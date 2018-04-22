from enum import Enum
from typing import Tuple, List, Optional, Dict

from map_card import MapCard
from player import Player

DEFAULT_WIDTH = 5
DEFAULT_HEIGHT = 5


def check_bounds(function):
    """A decorator that checks input bounds."""
    def decorator(i, j, *args, **kwargs):
        pass
    pass


class Board:
    """
    Attributes:
        _board (List[List[MapSquare]])
    """
    def __init__(self):
        self._board = [[MapSquare() for _ in range(DEFAULT_WIDTH)] for _ in range(DEFAULT_HEIGHT)]
        pass

    def place_token_start(self, loc, token):
        """Place a token in one of the 2x6x4 starting spots.

        Args:
            loc (int): The location around the edge clockwise. [0,2x6x4)
        """
        # TODO: make this actually work. Currently only works for loc=0
        self._board[0][0]._spots[1].receive_token_via_adjacent(token)

    def place_tile(self, i: int, j: int, path_tile: 'PathTile'):
        self._board[i][j].place_tile(path_tile)


class Side(Enum):
    TOP = 0
    RIGHT = 1
    BOTTOM = 2
    LEFT = 3


# TODO: Use an enum to represent the 8 possible TileSpots?
# TODO: Use an enum to represent the 4 possible Rotations?


class NoPathTileError(Exception):
    """Raised when indexing into a MapSquare missing a path tile."""
    pass


class MapSquare:
    """A square on the map.

    Attributes:
        _path_tile: Optional[PathTile]
        _rotation: int
    """
    def __init__(self):
        # This is an example of the Strategy pattern.
        self._path_tile = None
        self._rotation = 0

    def place_tile(self, path_tile: 'PathTile') -> None:
        self._path_tile = path_tile

    def get_player(self) -> Optional[Tuple[Player, int]]:
        """Return the players on the square as a list of (player, position)."""
        return None

    def get_offset(self, key: int) -> Tuple[int, int]:
        """Return a tuple indicating the side of MapSquare.

        Args:
            key: int

        Returns:
            Tuple[int, int]
                This approach is like an offset matrix in physics applications.
        """
        if not self._path_tile:
            raise NoPathTileError

        # This isn't the cleanest, but type hints require a return statement that
        # is guaranteed to be reached.
        key = self._path_tile[key]

        # Top
        if key in (0, 1):
            offset = (0, 1)
        # Right
        elif key in (2, 3):
            offset = (1, 0)
        # Bottom
        elif key in (4, 5):
            offset = (0, -1)
        # Left
        elif key in (6, 7):
            offset = (-1, 0)

        return offset


class PathTile:
    """A tile with path connections.

           0   1
        +---------+
    7   |         |  2
        |         |
    6   |         |  3
        +---------+
           5   4
    """
    def __init__(self, connections: List[Tuple[int, int]]) -> None:
        # TODO: Assert that PathTile must be created with 4 tuples? This should be asserted by type.
        if not all([0 <= c0 < 8 and 0 <= c1 < 8 for c0, c1 in connections]):
            raise ValueError('Path spots must be values in the range 0-7.')

        self._paths = PathTile.create_paths_dict(connections)  # type: Dict[int, int]
        self._connections = connections

    @staticmethod
    def create_paths_dict(connections: List[Tuple[int, int]]) -> Dict[int, int]:
        paths = {}
        for c0, c1 in connections:
            paths[c0] = c1
            paths[c1] = c0
        return paths

    # TODO: Create a type that constrains this to 0 - 7.
    def __getitem__(self, key: int) -> int:
        """Given an key, return the connecting path."""
        return self._paths[key]

    def __str__(self):
        return "\n".join("{} <-> {}".format(c0, c1) for c0, c1 in self._connections)


class Token:
    """Marks a player's location on the board.

    Attributes:
        _player (Player)
        _location (TokenSpot)
    """

    def __init__(self, player):
        """Initialize a token for the given player.

        Args:
            player (Player)
        """
        self._player = player
        player.set_token(self)
        self._location = None

    def set_location(self, spot):
        self._location = spot

    def get_location(self):
        return self._location

    def eliminate(self):
        """Eliminate the token and its associated player from the board."""
        self._player.eliminate()
