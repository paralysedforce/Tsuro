from abc import ABC, abstractmethod, abstractclassmethod
from enum import Enum
from typing import List, Optional  # noqa: F401

import attr

from board import PathTile, Position, TilePlacement
from _stateful import State


class Color(Enum):
    GRAY = 0
    GREEN = 1
    RED = 2
    ORANGE = 3
    BLUE = 4
    WHITE = 5
    YELLOW = 6
    BLACK = 7


@attr.s
class PlayerABC(State, ABC):
    name: str                     = attr.ib()
    position: Optional[Position]  = attr.ib()
    tiles: List[PathTile]         = attr.ib()
    color: Color                  = attr.ib(default=Color.GRAY)
    has_moved: bool               = attr.ib(default=False)

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
