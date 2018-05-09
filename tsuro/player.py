import random
from abc import ABC, abstractmethod
from enum import Enum
from typing import List, Optional  # noqa: F401

from attr import attrib, attrs

from _stateful import State
from board import Board, PathTile, Position, TilePlacement


class Color(Enum):
    GRAY = 1  # enum numbering starts at 1 so all values are truthy
    GREEN = 2
    RED = 3
    ORANGE = 4
    BLUE = 5
    WHITE = 6
    YELLOW = 7
    BLACK = 8


@attrs
class Player(State):
    """Representation of a Player.

    We treat Players as mutable named tuples.

    Example:
        >>>
    """
    name: str                     = attrib()
    position: Optional[Position]  = attrib()
    tiles: List[PathTile]         = attrib()
    color: Color                  = attrib(default=Color.GRAY)
    has_moved: bool               = attrib(default=False)


# TODO: Our current player is actually a PlayerState.
# class Player:
#     def __init__(self):
#         self.move_strategy = self.move_strategy_factory()

#     def play_turn(board):
#         return self.strategy.choose_move(board, self.tiles)

#     def move_strategy_factory(self):
#         return None


class MoveStrategyInterface(ABC):
    """Encapsulation of a player's automatic move strategy."""
    @abstractmethod
    def choose_move(self, board: Board, tiles: List[PathTile]) -> TilePlacement:
        pass


def validate_move_ability(func):
    def decorated(self, board, tiles):
        # Maybe this check should be in the Player class.
        if not board.open_squares:
            raise ValueError('Cannot choose move: Board is full.')
        if not tiles:
            raise ValueError('Cannot choose move: No tiles available.')
        return func(self, board, tiles)
    return decorated


def num_symmetric_rotations(path_tile: PathTile) -> int:
    """Return the number of rotations (0-3) that results in an identical path tile."""
    n = 0
    for i in range(1, 4):
        rotated = PathTile([((x + 2 * i) % 8, (y + 2 * i) % 8) for x, y in path_tile._connections])
        if rotated == path_tile:
            n += 1
    return n


def sort_tiles_by_symmetry(path_tiles: List[PathTile]) -> List[PathTile]:
    """Sort path tiles by symmetry in ascending order."""
    return sorted(path_tiles, key=lambda pt: num_symmetric_rotations(pt))


class RandomStrategy(MoveStrategyInterface):
    @validate_move_ability
    def choose_move(self, board: Board, tiles: List[PathTile]) -> TilePlacement:
        return TilePlacement(
            tile=random.choice(tiles),
            coordinate=random.choice(board.open_squares),
            rotation=random.choice(range(4)),
        )


class LeastSymmetricStrategy(MoveStrategyInterface):
    @validate_move_ability
    def choose_move(self, board: Board, tiles: List[PathTile]) -> TilePlacement:
        return sort_tiles_by_symmetry(tiles)[0]


class MostSymmetricStrategy(MoveStrategyInterface):
    @validate_move_ability
    def choose_move(self, board: Board, tiles: List[PathTile]) -> TilePlacement:
        return sort_tiles_by_symmetry(tiles)[-1]
