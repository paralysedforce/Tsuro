from typing import Dict, List, NamedTuple, Tuple

import attr

from _stateful import State, ImmutableMixin, StatefulInterface


# A type alias representing one of the 8 possible positions on a tile.
TileSpot = int
"""
           0   1
        +---------+
    7   |         |  2
        |         |
    6   |         |  3
        +---------+
           5   4
"""


class Position(NamedTuple):
    """A unique position on the board.

    A position on the board is defined by the index of its BoardSquare (x, y),
    and the TileSpot on that BoardSquare. This is an immutable algebraic datatype.

    Example:
        >>> p = Position(coordinate=(0, 0), tile_spot=8)
    """
    coordinate: Tuple[int, int]
    tile_spot: TileSpot


class TilePlacement(NamedTuple):
    """The placement of a tile.

    TilePlacement consists of a tile and the intended coordinates/rotation for its placement.
    """
    tile: 'PathTile'
    coordinate: Tuple[int, int]
    rotation: int


@attr.s
class BoardState(State, ImmutableMixin):
    """The state of the board.

    A unique board state is defined by a list of TilePlacements and the
    dimensions of the board.
    """
    tile_placements: List[TilePlacement]  = attr.ib()
    height: int                           = attr.ib()
    width: int                            = attr.ib()


class Board(StatefulInterface):
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

    Example:
        >>> board = Board(3, 3)
        >>> tile = PathTile([(0, 5)])
        >>> board.place_tile((0, 0), tile)
        >>> board.traverse_path(Position(coordinate=(0, 0), tile_spot=0))
        [Position((0, 0), 0), Position((0, 0), 5), Position((1, 0), 0)]
    """
    # The (row, column) offset needed to get to the connecting square, given a TileSpot.
    TILE_SPOT_OFFSET = {
        # top
        0: (-1, 0), 1: (-1, 0),
        # right
        2: (0, 1), 3: (0, 1),
        # bottom
        4: (1, 0), 5: (1, 0),
        # left
        6: (0, -1), 7: (0, -1),
    }

    # The adjacent tilespot on the connecting square.
    ADJACENT_TILE_SPOT = {
        0: 5, 1: 4, 2: 7, 3: 6,
        4: 1, 5: 0, 6: 3, 7: 2,
    }

    def __init__(self, height: int, width: int) -> None:
        self._board = [[BoardSquare() for _ in range(width)] for _ in range(height)]
        self._width = width
        self._height = height

    @property
    def tile_placements(self):
        """The list of tile placements existing on the board."""
        # TODO: Once place_tile() is refactored to take a TilePlacement, we'll just maintain this as a list.
        tile_placements = []
        for i in range(self._width):
            for j in range(self._height):
                square = self._board[i][j]
                if square.has_tile():
                    tp = TilePlacement(square.path_tile, (i, j), square.rotation)
                    tile_placements.append(tp)
        return tile_placements

    @property
    def edge_positions(self) -> List[Position]:
        """The list of Positions along the board's edge."""
        # Memoized, since it only needs to be calculated once.
        if not hasattr(self, '_edge_positions'):
            posns = []  # type: List[Position]
            w = self._width
            h = self._height
            posns += [Position((0, j), ts)     for j in range(w) for ts in (0, 1)]  # top edge     # noqa: E272
            posns += [Position((i, w - 1), ts) for i in range(h) for ts in (2, 3)]  # right edge   # noqa: E272
            posns += [Position((h - 1, j), ts) for j in range(w) for ts in (4, 5)]  # bottom edge  # noqa: E272
            posns += [Position((i, 0), ts)     for i in range(h) for ts in (6, 7)]  # left edge    # noqa: E272
            self._edge_positions = posns

        return self._edge_positions

    def place_tile(self, coordinate: Tuple[int, int], path_tile: 'PathTile'):
        i, j = coordinate
        if not self._in_bounds(i, j):
            raise IndexError('({},{}) is out of bounds.'.format(i, j))
        self._board[i][j].path_tile = path_tile

    def traverse_path(self, p: Position) -> List[Position]:
        i, j = p.coordinate
        if not self._in_bounds(i, j):
            raise IndexError('({},{}) is out of bounds.'.format(i, j))

        if not self._board[i][j].has_tile():
            return []

        path = []
        while True:
            (i, j), tile_spot = p
            next_ts_within_square = self._board[i][j][tile_spot]

            # First, append the movement within the square.
            path.append(p)
            path.append(Position((i, j), next_ts_within_square))

            # Now, traverse to the next square if possible.
            i_offset, j_offset = self.TILE_SPOT_OFFSET[next_ts_within_square]
            next_i = i + i_offset
            next_j = j + j_offset

            if not self._in_bounds(next_i, next_j):
                break

            next_p = Position((next_i, next_j), self.ADJACENT_TILE_SPOT[next_ts_within_square])
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

    def _in_bounds(self, i: int, j: int) -> bool:
        return 0 <= i < self._height and 0 <= j < self._width

    def __getitem__(self, index):
        return self._board[index]

    # implement the StatefulInterface interface
    def state(self) -> BoardState:
        return BoardState(
            tile_placements=self.tile_placements,
            height=self._height,
            width=self._width,
        )

    @classmethod
    def from_state(cls, state: BoardState) -> 'Board':
        board = cls(state.height, state.width)
        for tile, coordinate, rotation in state.tile_placements:
            board.place_tile(coordinate, tile)
        return board


# TODO: Use an enum to represent the 8 possible TileSpots?
# TODO: Use an enum to represent the 4 possible Rotations?


class BoardSquare:
    """A square on the map. This is a wrapper around PathTile implementing rotation.

    Attributes:
        path_tile: Optional[PathTile]
        _rotation: int

    Example:
        >>> bs = BoardSquare()
        >>> bs.path_tile = PathTile([(0, 1)])
        >>> bs[0]
        1
        >>> bs.rotation = 1
        >>> bs[2]
        3
    """
    def __init__(self):
        self.path_tile = None  # Using the Strategy Pattern with path tiles.
        self.rotation = 0

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
    """A object representing paths that connect TileSpots.

    Index into PathTiles to get the connecting TileSpots.

    Example:
        >>> p = PathTile([(0, 1), (2, 6)])
        >>> p[0]
        1
        >>> p[1]
        0
        >>> p[2]
        6
    """
    def __init__(self, connections: List[Tuple[TileSpot, TileSpot]]) -> None:
        for c0, c1 in connections:
            self._check_tile_spot(c0)
            self._check_tile_spot(c1)

        self._paths = self.create_paths_dict(connections)  # type: Dict[int, int]
        self._connections = connections

    @staticmethod
    def create_paths_dict(connections: List[Tuple[TileSpot, TileSpot]]) -> Dict[TileSpot, TileSpot]:
        paths = {}  # type: Dict[TileSpot, TileSpot]
        for c0, c1 in connections:
            assert (c0 not in paths) and (c1 not in paths), 'TileSpots can only have 1 connection.'
            paths[c0] = c1
            paths[c1] = c0
        return paths

    @staticmethod
    def _check_tile_spot(ts):
        if not 0 <= ts < 8:
            raise IndexError('Tile spots must be values from 0 through 7.')

    def __getitem__(self, tile_spot: TileSpot) -> TileSpot:
        """Given a tile_spot, return the connecting path."""
        self._check_tile_spot(tile_spot)
        return self._paths[tile_spot]

    def __eq__(self, other):
        return self._paths == other._paths

    def __repr__(self):
        paths = ', '.join(['({}, {})'.format(x, y) for x, y in self._connections])
        return "PathTile([{}])".format(paths)

    # TODO: This rotation information should be moved to BoardSquare since PathTile has no knowledge
    # of rotation, or we can refactor PathTile to handle all rotation. Right now rotation info is in two places.
    def rotate(self):
        """Rotates the tile clockwise once."""
        # Create a copy of _connections, but rotated clockwise once
        connections = [((path[0] + 2) %8, (path[1] + 2) %8) for path in self._connections]

        self._paths = PathTile.create_paths_dict(connections)
        self._connections = connections

    def unique_rotations(self) -> int:
        """Calculates the number of unique rotations the tile has"""
        copy = self.make_copy()
        rotations = []
        for _ in range(4):
            copy.rotate()
            rotation = copy.make_copy()
            if rotation not in rotations:
                rotations.append(rotation)

        return len(rotations)

    def make_copy(self):
        return PathTile(self._connections)