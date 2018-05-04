from abc import ABC, abstractmethod
from enum import Enum
from typing import List, Optional  # noqa: F401

from attr import attrib, attrs

from _stateful import State
from board import Board, PathTile, Position, TilePlacement


class Color(Enum):
    GRAY = 0
    GREEN = 1
    RED = 2
    ORANGE = 3
    BLUE = 4
    WHITE = 5
    YELLOW = 6
    BLACK = 7


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


class MoveStrategy(ABC):
    """Encapsulation of a player's automatic move strategy."""
    @abstractmethod
    def choose_move(board: Board, tiles: List[PathTile]) -> TilePlacement:
        pass


class RandomStrategy(MoveStrategy):
    def choose_move(board: Board, tiles: List[PathTile]) -> TilePlacement:
        pass


class LeastSymmetricStrategy(MoveStrategy):
    def choose_move(board: Board, tiles: List[PathTile]) -> TilePlacement:
        pass


class MostSymmetricStrategy(MoveStrategy):
    def choose_move(board: Board, tiles: List[PathTile]) -> TilePlacement:
        pass
