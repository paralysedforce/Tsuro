from enum import Enum
from typing import List, Optional  # noqa: F401

from attr import attrib, attrs

from _stateful import State
from board import PathTile, Position


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
