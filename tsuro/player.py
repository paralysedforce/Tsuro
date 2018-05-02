from enum import Enum
from typing import List, Optional  # noqa: F401

import attr

from board import PathTile, Position
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
class Player(State):
    """Representation of a Player.

    We treat Players as mutable named tuples.

    Example:
        >>>
    """
    name: str                     = attr.ib()
    position: Optional[Position]  = attr.ib()
    tiles: List[PathTile]         = attr.ib()
    color: Color                  = attr.ib(default=Color.GRAY)
    has_moved: bool               = attr.ib(default=False)
