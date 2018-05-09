from collections import deque
from typing import Deque, Dict, List, NamedTuple, Optional, Tuple  # noqa: F401

from attr import attrib, attrs

import defaults
from _stateful import ImmutableMixin, State, StatefulInterface
from board import Board, BoardState, PathTile, TilePlacement
from deck import Deck
from player import Player


@attrs
class GameState(State, ImmutableMixin):
    """The serialized state of the game.

    Returned by TsuroGame.state(). Reconstruct Tsuro games using this object.

    Example:
        >>> state = GameState(
                active_players=[
                    Player('A', Position((0, 0), 0), [], Color.GRAY),
                    Player('B', Position((0, 0), 6), [], Color.GREEN),
                ],
                eliminated_players=[],
                dragon_holder=None,
                board_state=BoardState(
                    tile_placements=[],
                    height=3,
                    width=3,
                ),
                deck_state=[],
            )
        >>> game = TsuroGame.from_state(state)
        >>> game.state()
        GameState(active_players=[Player(name='A', position=Position(coordinate=(0, 0)...
    """
    active_players: List[Player]      = attrib()
    eliminated_players: List[Player]  = attrib()
    dragon_holder: Optional[int]      = attrib()
    board_state: BoardState           = attrib()
    deck_state: List[PathTile]        = attrib()


class TsuroGame(StatefulInterface):
    """Controller / administrator for Tsuro.

    Attributes:
        board: Board
        deck: Deck
        dragon_tile_holder: Optional[Player]  # TODO: Match this representation of dragon tile holder with GameState.
        players: Deque[Player]
        positions: Dict[Player, Position]
    """

    def __init__(self, players: List[Player]) -> None:
        self.board = self.board_factory()  # type: Board
        self.deck = self.deck_factory()    # type: Deck
        self.dragon_tile_holder = None     # type: Optional[Player]
        self.players = deque(players)      # type: Deque[Player]
        self.eliminated_players = list()   # type: List[Player]

    def play_turn(self, tile_placement: TilePlacement):
        """Given a tile placement, play a turn,
            removing the tile from the player's hand,
            and moving/eliminating players as needed."""
        # Todo: Assert that the playe is legal.
        # Todo: Remove the tile from the player's hand.
        self.board.place_tile(tile_placement)
        self._move_players(tile_placement.coordinate)

        current_player = self.players[0]
        if current_player not in self._to_eliminate():
            self.deal_to(current_player)
            self.players.append(self.players.popleft())

        for player in self._to_eliminate():
            self._eliminate_player(player)

    def ended(self) -> bool:
        """Return a boolean indicated if the game has ended."""
        one_player_left = len(self.players) < 2
        no_cards_left = len(self.deck) == (self.board._height * self.board._width) - 1
        return one_player_left or no_cards_left

    def deal_to(self, player: Player):
        """Deal from the deck to a player. Assign the dragon card if needed."""
        tile = self.deck.draw()
        if tile:
            player.tiles.append(tile)
        elif not self.dragon_tile_holder:
            self.dragon_tile_holder = player

    def _move_players(self, coordinate: Tuple[int, int]):
        """Move the players on a certain square on the map."""
        for player in self.players:
            if player.position.coordinate == coordinate:
                path = self.board.traverse_path(player.position)
                player.position = path[-1]
                player.has_moved = True

    def _to_eliminate(self) -> List[Player]:
        """Return a list of players to be eliminated.

        A player is to be eliminated if it is on the edge of the board, and it has moved.
        """
        return [p for p in self.players if self.board.is_on_edge(p.position) and p.has_moved]

    def _eliminate_player(self, player: Player):
        """Eliminate a player.

        Return the player's card to the deck, reset dragon tile holder, and
        deal cards to remaining players if needed.
        """
        self.deck.replace_tiles(player.tiles)
        player.tiles = []

        player_index = self.players.index(player)
        self.players.remove(player)

        if self.dragon_tile_holder is player:
            self.dragon_tile_holder = None
            candidate = self.players[(player_index) % len(self.players)]  # TODO: + 1 to index
            if len(candidate.tiles) < 3:
                self.dragon_tile_holder = candidate

        # Let all players draw until dragon card is held again, starting with dragon holder.
        if self.dragon_tile_holder is not None:
            player_index = self.players.index(self.dragon_tile_holder)
            self.dragon_tile_holder = None
            i = 0
            while True:
                drawer = self.players[(player_index + i) % len(self.players)]
                if self.dragon_tile_holder is not None or len(drawer.tiles) == 3:
                    break
                self.deal_to(drawer)
                i += 1

        self.eliminated_players.append(player)

    def board_factory(self) -> Board:
        return Board(defaults.DEFAULT_WIDTH, defaults.DEFAULT_HEIGHT)

    def deck_factory(self) -> Deck:
        return Deck.from_connections(defaults.DEFAULT_CARDS)

    def state(self) -> GameState:
        return GameState(
            active_players=list(self.players),
            eliminated_players=self.eliminated_players,
            dragon_holder=self.players.index(self.dragon_tile_holder) if self.dragon_tile_holder else None,
            board_state=self.board.state(),
            deck_state=self.deck.state(),
        )

    @classmethod
    def from_state(cls, game_state: GameState):
        game = cls([])
        game.players = deque(game_state.active_players)
        game.eliminated_players = game_state.eliminated_players
        game.deck = Deck.from_state(game_state.deck_state)
        game.board = Board.from_state(game_state.board_state)

        if game_state.dragon_holder is None:
            game.dragon_tile_holder = None
        else:
            holder_index = game_state.dragon_holder
            game.dragon_tile_holder = game.players[holder_index]

        return game

    def unsafe_does_eliminate_player(self, placement: TilePlacement, player: Player) -> bool:
        """Checks to see if placing the placement will eliminate the player.
            NOTE: This will mutate game state, so create a copy of the game before
            calling this."""
        self.board.place_tile(placement)
        self._move_players(placement.coordinate)
        return player in self._to_eliminate()

    def is_placement_legal(self, placement: TilePlacement, player: Player) -> bool:
        """Checks that the tile placement is legal according to the following rules
            WITHOUT mutating game state:
            - The tile is in the player's hand
            - The tile does not already exist on the board
            - The placement does not lead to the player's elimination if the
                player has other legal tiles to place.
        """
        # Check if is tile of player
        if placement.tile not in player.tiles:
            return False

        # Check if the move eliminates the player
        state = self.state()
        copy = TsuroGame.from_state(state)
        # Check elimination move
        if copy.unsafe_does_eliminate_player(placement, player):
            # Check if all moves other would eliminate the player.
            for tile in player.tiles:
                for rotation in range(4):
                    placement_attempt = TilePlacement(tile, placement.coordinate, rotation)
                    copy = TsuroGame.from_state(state)
                    if not copy.unsafe_does_eliminate_player(placement_attempt, player):
                        # Other move doesn't eliminate, so this move is illegal.
                        return False
        return True








