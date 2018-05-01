from collections import deque
from typing import Dict, List, NamedTuple, Optional, Tuple, Union  # noqa: F401

from dataclasses import dataclass

import default_config
from board import Board, PathTile, Position, TilePlacement, BoardState
from deck import Deck


# A dataclass is a "mutable NamedTuple".
@dataclass
class Player:
    name: str
    position: Optional[Position]
    tiles: List[PathTile]


class GameState(NamedTuple):
    active_players: List[Player]
    eliminated_players: List[Player]
    dragon_holder: Optional[int]
    board_state: BoardState
    deck_state: List[PathTile]


def peek_path(player: Player, board: Board, tile: PathTile) -> List[Position]:
    """Return the path from a tile placement, given a board state and a position."""
    (i, j), _ = player.position
    board.place_tile((i, j), tile)
    path = board.traverse_path(player.position)
    board._board[i][j].path_tile = None  # This 'undoing' isn't the cleanest.
    return path

def move_players(active_players: List[Player], board: Board, square: Tuple[int, int]) -> List[Player]:
    """Moves the active_players that are in square along their paths and returns
        the players that should be eliminated"""
    to_eliminate = []
    for player in active_players:
        if player.position.coordinate == square:
            path = board.traverse_path(player.position)
            player.position = path[-1]
            if board.is_on_edge(player.position):
                to_eliminate.append(player)
    return to_eliminate


def move_eliminates_player(player: Player, board: Board, tile: PathTile) -> bool:
    path = peek_path(player, board, tile)
    landing_position = path[-1]
    return board.is_on_edge(landing_position)


def legal_play(player: Player, board: Board, placement: TilePlacement) -> bool:
    tile = placement.tile

    # Not valid if attempting to place where the player is not
    if not placement.coordinate == player.position.coordinate:
        return False

    # not valid if placement of the tile eliminates the player, unless all tiles eliminate player
    all_moves_eliminate_player = all([move_eliminates_player(player, board, tile) for tile in player.tiles])
    if move_eliminates_player(player, board, tile) and not all_moves_eliminate_player:
        return False

    return True


class TsuroGame:
    """Controller / administrator for Tsuro.

    Attributes:
        board: Board
        deck: Deck
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

    def peek_path(self, player: Player, path_tile: PathTile) -> List[Position]:
        """Return the resulting path from a certain tile placement.

        Does not mutate the board.
        """
        (i, j), _ = player.position
        self.board.place_tile((i, j), path_tile)
        path = self.board.traverse_path(player.position)
        self.board._board[i][j] = None
        return path

    def peek_path_list(self, players: List[Player], path_tile: PathTile) -> List[Tuple[Player, List[Position]]]:
        pass

    @staticmethod
    def play_a_turn(
        state: GameState,
        tile_placement: TilePlacement
    ) -> (GameState, Union[List[Player], bool]):
        """Compute the state of the game."""
        # Assume the tile to be placed has already been removed from the
        # player's hand and is being placed in the proper loction.

        game = TsuroGame.from_state(state)

        # Place the tile and move the players
        game.board.place_tile(tile_placement.coordinate, tile_placement.tile)
        to_eliminate = move_players(game.players, game.board, tile_placement.coordinate)

        # Deal to the current player and put it last in line
        current_player = game.players.popleft()
        game.deal_to(current_player)
        game.players.append(current_player)

        # Eliminate players on the edge
        for player in to_eliminate:
            if game.board.is_on_edge(player.position):
                # Eliminate the player
                game.players.remove(player)
                game.eliminated_players.append(player)

                # Return cards to deck
                game.deck.replace_tiles(player.tiles)

                if game.dragon_tile_holder is not None:
                    # If the recently-eliminated player was the holder, pass it
                    # along. (note: if next player has full hand, everyone does)
                    if game.dragon_tile_holder == player:
                        game.dragon_tile_holder = None
                        player_index = game.players.index(player)
                        candidate = game.players[(player_index + 1) % len(game.players)]
                        if len(candidate.tiles) < 3:
                            game.dragon_tile_holder = candidate

                # Draw cards if dragon card is held
                if game.dragon_tile_holder is not None:
                    player_index = game.players.index(game.dragon_tile_holder)
                    game.dragon_tile_holder = None
                    # Let all players draw until dragon card is held again,
                    # starting with dragon holder.
                    for i in range(len(game.players)):
                        drawer = game.players[(player_index + i) % len(game.players)]
                        if game.dragon_tile_holder is not None or len(drawer.tiles) == 3:
                            break
                        game.deal_to(drawer)

        # Game ends if there are no more active players,
        # or all of the tiles have been placed
        end_state = game.state()
        game_did_end = False
        game_did_end = game_did_end or len(end_state.active_players) == 0
        game_did_end = game_did_end or len(end_state.deck_state) ==\
            (end_state.board_state.height * end_state.board_state.width)-1

        if game_did_end:
            return (end_state, end_state.active_players)
        else:
            return (game.state(), False)

    def board_factory(self) -> Board:
        return Board(default_config.DEFAULT_WIDTH, default_config.DEFAULT_HEIGHT)

    def deck_factory(self) -> Deck:
        return Deck.from_connections(default_config.DEFAULT_CARDS)
