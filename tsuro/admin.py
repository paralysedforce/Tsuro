from collections import deque
from typing import Dict, List, NamedTuple, Optional, Tuple  # noqa: F401

import attr

import defaults
from _stateful import ImmutableMixin, State, StatefulInterface
from board import Board, BoardState, PathTile, TilePlacement
from deck import Deck
from player import Player


@attr.s
class GameState(State, ImmutableMixin):
    """The state of the game.

    Example:
        >>> game.state()
    """
    active_players: List[Player]      = attr.ib()
    eliminated_players: List[Player]  = attr.ib()
    dragon_holder: Optional[int]      = attr.ib()
    board_state: BoardState           = attr.ib()
    deck_state: List[PathTile]        = attr.ib()


class TsuroGame(StatefulInterface):
    """Controller / administrator for Tsuro.

    Attributes:
        board: Board
        deck: Deck
        # TODO: Match this representation of dragon tile holder with GameState.
        dragon_tile_holder: Optional[Player]
        players: Deque[Player]
        positions: Dict[Player, Position]
    """

    def __init__(self, players: List[Player]) -> None:
        """Default constructor.

        Args:
            num_players (int): Count players to instantiate the Admin with.
        """
        self.board = self.board_factory()  # type: Board
        self.deck = self.deck_factory()    # type: Deck
        self.dragon_tile_holder = None     # type: Optional[Player]
        self.players = deque(players)
        self.eliminated_players = list()   # type: List[Player]

    def deal_to(self, player: Player):
        """Deal from the deck to a player. Assign the dragon card if needed."""
        tile = self.deck.draw()
        if tile:
            player.tiles.append(tile)
        elif not self.dragon_tile_holder:
            self.dragon_tile_holder = player

    def move_players(self, coordinate: Tuple[int, int]):
        """Move the players on a certain square on the map."""
        for player in self.players:
            if player.position.coordinate == coordinate:
                path = self.board.traverse_path(player.position)
                player.position = path[-1]
                player.has_moved = True

    def to_eliminate(self) -> List[Player]:
        """Return a list of players to be eliminated.

        A player is to be eliminated if it has not moved, and if it is on the edge.
        """
        return [p for p in self.players if self.board.is_on_edge(p.position) and p.has_moved]

    def eliminate_player(self, player: Player):
        """Eliminate a player.

        Return the player's card to the deck, reset dragon tile holder, and
        deal cards to remaining players if needed.
        """
        self.deck.replace_tiles(player.tiles)
        player.tiles = []

        player_index = self.players.index(player)
        self.players.remove(player)
        self.eliminated_players.append(player)

        if self.dragon_tile_holder is player:
            self.dragon_tile_holder = None
            candidate = self.players[(player_index) % len(self.players)]
            if len(candidate.tiles) < 3:
                self.dragon_tile_holder = candidate

        if self.dragon_tile_holder is not None:
            player_index = self.players.index(self.dragon_tile_holder)
            self.dragon_tile_holder = None
            # Let all players draw until dragon card is held again, starting with dragon holder.
            i = 0
            while True:
                drawer = self.players[(player_index + i) % len(self.players)]
                if self.dragon_tile_holder is not None or len(drawer.tiles) == 3:
                    break
                self.deal_to(drawer)
                i += 1

    def ended(self) -> bool:
        # Game ends if there are no more active players,
        # or all of the tiles have been placed
        one_player_left = len(self.players) < 2
        no_cards_left = len(self.deck) == (self.board._height * self.board._width) - 1
        return one_player_left or no_cards_left

    def play_turn(self, tile_placement: TilePlacement):
        # Place the tile and move the players
        self.board.place_tile(tile_placement.coordinate, tile_placement.tile)
        self.move_players(tile_placement.coordinate)
        to_eliminate = self.to_eliminate()

        # If the current player did not eliminate itself, deal & put it last in line
        current_player = self.players[0]
        if current_player not in to_eliminate:
            self.deal_to(current_player)
            self.players.append(self.players.popleft())

        # Eliminate players on the edge
        for player in to_eliminate:
            self.eliminate_player(player)

    @staticmethod
    def play_a_turn(state: GameState, tile_placement: TilePlacement) -> Tuple[GameState, Optional[List[Player]]]:
        """Compute the state of the game."""
        game = TsuroGame.from_state(state)
        game.play_turn(tile_placement)

        if game.ended():
            return (game.state(), list(game.players))
        else:
            return (game.state(), None)

    @staticmethod
    def legal_play(player: Player, board: Board, placement: TilePlacement) -> bool:
        pass

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
