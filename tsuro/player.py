from enum import Enum
from typing import List, Optional  # noqa: F401

from attr import attrib, attrs

from _stateful import State
from board import PathTile, Position


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
