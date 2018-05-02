from abc import ABC, abstractmethod, abstractclassmethod
from enum import Enum
from dataclasses import dataclass
from typing import List, Optional  # noqa: F401

from board import PathTile, Position, TilePlacement


class Color(Enum):
    GRAY = 0
    GREEN = 1
    RED = 2
    ORANGE = 3
    BLUE = 4
    WHITE = 5
    YELLOW = 6
    BLACK = 7


@dataclass  # a dataclass is a "mutable NamedTuple"
class PlayerABC(ABC):
    name: str
    position: Optional[Position]
    tiles: List[PathTile]
    color: Color = Color.GRAY
    has_moved: bool = False

    @abstractclassmethod
    def initialize(cls, color):
        pass

    @abstractmethod
    def place_pawn(game: 'TsuroGame') -> Position:
        pass

    @abstractmethod
    def play_turn(game: 'TsuroGame') -> TilePlacement:
        pass

    @abstractmethod
    def end_game(game: 'TsuroGame'):
        pass
