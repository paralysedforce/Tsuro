from collections import deque
from typing import Dict, List, NamedTuple, Optional, Tuple, Union  # noqa: F401

from dataclasses import dataclass

import default_config
from board import Board, PathTile, Position
from deck import Deck


# A dataclass is a "mutable NamedTuple".
@dataclass
class Player:
    name: str
    position: Optional[Position]
    tiles: List[PathTile]


def peek_path(player: Player, board: Board, tile: PathTile) -> List[Position]:
    """Return the path from a tile placement, given a board state and a position."""
    i, j, _ = player.position
    board.place_tile(i, j, tile)
    path = board.traverse_path(player.position)
    board._board[i][j].path_tile = None  # This 'undoing' isn't the cleanest.
    return path


def move_eliminates_player(player: Player, board: Board, tile: PathTile) -> bool:
    path = peek_path(player, board, tile)
    landing_position = path[-1]
    return board.is_on_edge(landing_position)


def legal_play(player: Player, board: Board, tile: PathTile) -> bool:
    # not valid if player doesn't hold the tile being placed
    if tile not in player.tiles:
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

    def next_player(self) -> Player:
        p = self.players.popleft()
        self.players.append(p)
        return p

    def deal_to(self, player: Player):
        """Deal from the deck to a player. Assign the dragon card if needed."""
        tile = self.deck.draw()
        if tile:
            player.tiles.append(tile)
        elif not self.dragon_tile_holder:
            self.dragon_tile_holder = player

    def peek_path(self, player: Player, path_tile: PathTile) -> List[Position]:
        """Return the resulting path from a certain tile placement.

        Does not mutate the board.
        """
        i, j, _ = player.position
        self.board.place_tile(i, j, path_tile)
        path = self.board.traverse_path(player.position)
        self.board._board[i][j] = None
        return path

    def peek_path_list(self, players: List[Player], path_tile: PathTile) -> List[Tuple[Player, List[Position]]]:
        pass

    @staticmethod
    def play_a_turn(
        deck: Deck,
        active_players: List[Player],
        elim_players: List[Player],
        board: Board,
        new_tile: PathTile,
        i: int,
        j: int
    ) -> Tuple[Deck, List[Player], List, Board, Union[List[Player], bool]]:
        """Compute the state of the game."""
        # moving_player = active_players[0]

        # square = moving_player.get_token().get_location().get_parent()

        # # Place tile in the square
        # square.place_card(placement_tile)
        # moving_player.draw_card()

        # # Remove inactive players
        # for player in active_players:
        #     if not player.is_active():
        #         active_players.remove(player)

        # # Still need to move to the end of the list
        # if moving_player.is_active():
        #     active_players.remove(moving_player)
        #     active_players.append(moving_player)

        # sum_hands = 0
        # for player in active_players:
        #     sum_hands += len(player._hand)

        # game_over = len(active_players) < 2 or sum_hands == 0

        # if game_over:
        #     return (draw_pile, active_players, eliminated_players, board, active_players)
        # else:
        #     return (draw_pile, active_players, eliminated_players, board, False)
        pass

    def board_factory(self) -> Board:
        return Board(default_config.DEFAULT_WIDTH, default_config.DEFAULT_HEIGHT)

    def deck_factory(self) -> Deck:
        return Deck.from_connections(default_config.DEFAULT_CARDS)
