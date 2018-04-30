from typing import Dict, List, NamedTuple, Tuple

# A type for representing one of the 8 possible positions on a tile.
"""
           0   1
        +---------+
    7   |         |  2
        |         |
    6   |         |  3
        +---------+
           5   4
"""
TileSpot = int


class Position(NamedTuple):
    """A unique position on the board.

    A position on the board is defined by the index of its BoardSquare (x, y),
    and the TileSpot on that BoardSquare. This is an immutable algebraic datatype.
    """
    i: int
    j: int
    tile_spot: TileSpot

    def __repr__(self):
        return 'Position({}, {}, {})'.format(self.i, self.j, self.tile_spot)


class Board:
    """The Tsuro game board.

         0  1  2  3
       +------------+
    0  |            |
    1  |            |
    2  |            |
    3  | x (3, 0)   |
       +------------+

    In our game design, board doesn't have any knowledge of Players. The responsibility of
    tracking player positions goes to the the Admin/Controller. Board has the responsibility of
    providing paths and checking the validity of moves.

    Attributes:
        _board (List[List[BoardSquare]])
        _width (int)
        _height (int)
        _edge_positions (List[Position])
    """
    # The (row, column) offset needed to get to the connecting square, given a TileSpot.
    TILE_SPOT_OFFSET = {
        # top
        0: (-1, 0),
        1: (-1, 0),
        # right
        2: (0, 1),
        3: (0, 1),
        # bottom
        4: (1, 0),
        5: (1, 0),
        # left
        6: (0, -1),
        7: (0, -1),
    }

    # The adjacent tilespot on the connecting square.
    ADJACENT_TILE_SPOT = {
        0: 5,
        1: 4,
        2: 7,
        3: 6,
        4: 1,
        5: 0,
        6: 3,
        7: 2,
    }

    def __init__(self, height: int, width: int) -> None:
        self._board = [[BoardSquare() for _ in range(width)] for _ in range(height)]
        self._width = width
        self._height = height
        self._edge_positions = []  # type: List[Position]

    @property
    def edge_positions(self) -> List[Position]:
        # Memoized, since it only needs to be calculated once.
        if not self._edge_positions:
            posns = []  # type: List[Position]
            w = self._width
            h = self._height
            posns += [Position(0, j, ts)     for j in range(w) for ts in (0, 1)]  # top edge     # noqa: E272
            posns += [Position(i, w - 1, ts) for i in range(h) for ts in (2, 3)]  # right edge   # noqa: E272
            posns += [Position(h - 1, j, ts) for j in range(w) for ts in (4, 5)]  # bottom edge  # noqa: E272
            posns += [Position(i, 0, ts)     for i in range(h) for ts in (6, 7)]  # left edge    # noqa: E272
            self._edge_positions = posns

        return self._edge_positions

    def place_tile(self, i: int, j: int, path_tile: 'PathTile'):
        self._check_bounds(i, j)
        self._board[i][j].path_tile = path_tile

    def traverse_path(self, p: Position) -> List[Position]:
        self._check_bounds(p.i, p.j)

        if not self._board[p.i][p.j].has_tile():
            return []

        path = []
        while True:
            i, j, tile_spot = p
            next_ts_within_square = self._board[i][j][tile_spot]

            # First, append the movement within the square.
            path.append(p)
            path.append(Position(p.i, p.j, next_ts_within_square))

            # Now, traverse to the next square if possible.
            i_offset, j_offset = self.TILE_SPOT_OFFSET[next_ts_within_square]
            next_i = p.i + i_offset
            next_j = p.j + j_offset

            if not self._in_bounds(next_i, next_j):
                break

            next_p = Position(next_i, next_j, self.ADJACENT_TILE_SPOT[next_ts_within_square])
            if not self._board[next_i][next_j].has_tile():
                path.append(next_p)
                break

            p = next_p

        return path

    def is_on_edge(self, p: Position) -> bool:
        """Return True if the position is on the edge of the board, False otherwise.

        Use to check if a path leads off the board.
        """
        return p in self.edge_positions

    def _check_bounds(self, i: int, j: int):
        if not self._in_bounds(i, j):
            raise IndexError('({},{}) is out of bounds.'.format(i, j))

    def _in_bounds(self, i: int, j: int) -> bool:
        return 0 <= i < self._height and 0 <= j < self._width

    def __getitem__(self, index):
        return self._board[index]


# TODO: Use an enum to represent the 8 possible TileSpots?
# TODO: Use an enum to represent the 4 possible Rotations?


class BoardSquare:
    """A square on the map.

    Attributes:
        path_tile: Optional[PathTile]
        _rotation: int
    """
    def __init__(self):
        self.path_tile = None  # Using the Strategy Pattern with path tiles.
        # self._rotation = 0

    def has_tile(self) -> bool:
        return self.path_tile is not None

    def next(self, tile_spot: TileSpot) -> TileSpot:
        """Given a entering tile spot, return the exiting TileSpot.

        Args:
            tile_spot: TileSpot

        Returns:
            TileSpot
        """
        if not self.path_tile:
            raise ValueError('No path tile present in the board square.')

        tile_spot = self.path_tile[tile_spot]
        # TODO: Rotate.

        return tile_spot

    def __getitem__(self, tile_spot):
        return self.next(tile_spot)


class PathTile:
    """A tile that connects TileSpots."""
    def __init__(self, connections: List[Tuple[TileSpot, TileSpot]]) -> None:
        if not all([0 <= c0 < 8 and 0 <= c1 < 8 for c0, c1 in connections]):
            raise ValueError('Tile spots must be values in the range 0-7.')

        self._paths = PathTile.create_paths_dict(connections)  # type: Dict[int, int]
        self._connections = connections

    @staticmethod
    def create_paths_dict(connections: List[Tuple[TileSpot, TileSpot]]) -> Dict[TileSpot, TileSpot]:
        paths = {}  # type: Dict[TileSpot, TileSpot]
        for c0, c1 in connections:
            assert (c0 not in paths) and (c1 not in paths), 'TileSpots can only have 1 connection.'
            paths[c0] = c1
            paths[c1] = c0
        return paths

    def __getitem__(self, tile_spot: TileSpot) -> TileSpot:
        """Given a tile_spot, return the connecting path."""
        return self._paths[tile_spot]

    def __eq__(self, other):
        return self._paths == other._paths

    def __str__(self):
        return "\n".join("{} <-> {}".format(c0, c1) for c0, c1 in self._connections)
