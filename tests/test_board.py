import pytest

from board import (Board, BoardSquare, BoardState, PathTile, Position,
                   TilePlacement)

from _helpers import PositionAlias as P
from _helpers import TileAlias as Tile
from _helpers import TilePlacementAlias as TP


@pytest.fixture
def tile():
    """A simple path tile for testing."""
    return PathTile([(0, 1), (2, 3), (4, 5), (6, 7)])


def test_board_edge_positions():
    b = Board(1, 1)
    expected = [
        P(0, 0, 0),
        P(0, 0, 1),
        P(0, 0, 2),
        P(0, 0, 3),
        P(0, 0, 4),
        P(0, 0, 5),
        P(0, 0, 6),
        P(0, 0, 7),
    ]
    assert b.edge_positions == expected, 'all positions on a 1x1 board are edge positions'

    b = Board(2, 2)
    expected = [
        P(0, 0, 0), P(0, 0, 1), P(0, 1, 0), P(0, 1, 1),
        P(0, 1, 2), P(0, 1, 3), P(1, 1, 2), P(1, 1, 3),
        P(1, 0, 4), P(1, 0, 5), P(1, 1, 4), P(1, 1, 5),
        P(0, 0, 6), P(0, 0, 7), P(1, 0, 6), P(1, 0, 7),
    ]
    assert expected == b.edge_positions, '2x2 test'


def test_board_open_squares(tile):
    b = Board(2, 2)
    assert b.open_squares == [(0, 0), (0, 1), (1, 0), (1, 1)], 'all squares are open at the start'

    b.place_tile(TP(tile, 0, 0))
    assert b.open_squares == [(0, 1), (1, 0), (1, 1)], 'occupied square at (0, 0) is excluded'


def test_board_place_tile(tile):
    b = Board(3, 3)
    b.place_tile(TP(tile, 1, 1))
    assert b._board[1][1].path_tile == tile

    with pytest.raises(IndexError):
        b.place_tile(TP(tile, -1, 0))
        b.place_tile(TP(tile, 3, 0))


def test_board_traverse_path_no_tiles():
    b = Board(3, 3)

    with pytest.raises(IndexError):
        b.traverse_path(P(-1, 0, 0))
        b.traverse_path(P(3, 0, 0))

    assert b.traverse_path(P(0, 0, 0)) == [], 'empty list if no pathtile'


def test_board_traverse_path_2x2():
    b = Board(2, 2)
    b.place_tile(TP(Tile(7, 2), 0, 0))
    assert b.traverse_path(P(0, 0, 7)) == [P(0, 0, 7), P(0, 0, 2), P(0, 1, 7)], 'path to middle'
    assert b.traverse_path(P(0, 0, 2)) == [P(0, 0, 2), P(0, 0, 7)], 'path to left edge'

    b.place_tile(TP(Tile(7, 2), 0, 1))
    assert b.traverse_path(P(0, 0, 7)) == [P(0, 0, 7), P(0, 0, 2), P(0, 1, 7), P(0, 1, 2)], 'path to right edge'


def test_board_traverse_path_three_tiles():
    b = Board(3, 3)
    b.place_tile(TP(Tile(7, 2), 0, 0))
    b.place_tile(TP(Tile(7, 2), 0, 1))

    start = P(0, 0, 7)
    expected = [P(0, 0, 7), P(0, 0, 2), P(0, 1, 7), P(0, 1, 2), P(0, 2, 7)]
    assert b.traverse_path(start) == expected, 'traverse in a straight line across the top of the board'

    # Connect a path straight across the top edge of the board.
    b.place_tile(TP(Tile(7, 2), 0, 2))

    expected = [P(0, 0, 7), P(0, 0, 2), P(0, 1, 7), P(0, 1, 2), P(0, 2, 7), P(0, 2, 2)]
    actual = b.traverse_path(start)
    assert actual == expected, 'stop when the edge is reached'
    assert b.is_on_edge(actual[-1]), 'the path terminates on the edge of the board'


def test_board_state():
    b = Board(2, 2)
    tile0 = Tile(0, 1)
    b.place_tile(TP(tile0, 0, 0))
    assert b.state() == BoardState(
        [TilePlacement(tile=Tile(0, 1), coordinate=(0, 0), rotation=0)],
        2,
        2,
    )

    tile1 = Tile(2, 3)
    b.place_tile(TP(tile1, 1, 1))
    assert b.state() == BoardState(
        tile_placements=[
            TilePlacement(tile=Tile(0, 1), coordinate=(0, 0), rotation=0),
            TilePlacement(tile=Tile(2, 3), coordinate=(1, 1), rotation=0)
        ],
        height=2,
        width=2,
    )


def test_board_from_state():
    state = BoardState(
        tile_placements=[
            TP(Tile(0, 1), 0, 0),
            TP(Tile(2, 3), 1, 1)
        ],
        height=2,
        width=2,
    )

    b = Board.from_state(state)
    assert b._board[0][0].path_tile == Tile(0, 1)
    assert b._board[1][1].path_tile == Tile(2, 3)


def test_board_square(tile):
    square = BoardSquare()

    # Raises exception before a PathTile is placed.
    with pytest.raises(ValueError):
        square.next(0)

    assert not square.has_tile()
    square.path_tile = tile
    assert square.has_tile()

    # top
    assert square.next(0) == 1
    assert square.next(1) == 0
    # right
    assert square.next(2) == 3
    assert square.next(3) == 2
    # bottom
    assert square.next(4) == 5
    assert square.next(5) == 4
    # left
    assert square.next(6) == 7
    assert square.next(7) == 6


def test_path_tile():
    # providing invalid TileSpot throws
    with pytest.raises(IndexError):
        Tile(9, 10)

    # providing non-unique paths throws
    with pytest.raises(AssertionError):
        PathTile([(0, 1), (0, 2)])

    # indexing
    tile = PathTile([(0, 1)])
    assert tile[0] == 1
    assert tile[1] == 0

    # indexing using an invalid TileSpot throws
    with pytest.raises(IndexError):
        tile[-1]

    tile = PathTile([(0, 1), (2, 3)])
    assert tile[0] == 1
    assert tile[1] == 0
    assert tile[2] == 3
    assert tile[3] == 2


def test_path_tile_equality():
    assert PathTile([(0, 1)]) == PathTile([(0, 1)])
    assert PathTile([(0, 1)]) == PathTile([(1, 0)])
    assert not PathTile([(0, 1)]) == PathTile([(0, 2)])
    assert not PathTile([(0, 1)]) == PathTile([(0, 1), (2, 3)])
    assert PathTile([(2, 3), (0, 1)]) == PathTile([(0, 1), (2, 3)])


def test_rotate_tile_symmetric():
    tile1 = PathTile([(0, 1), (2, 3), (4, 5), (6, 7)])
    tile2 = PathTile([(0, 1), (2, 3), (4, 5), (6, 7)])
    tile2.rotate()
    assert tile1 == tile2


def test_rotate_tile_asymmetric():
    tile1 = PathTile([(0, 1), (2, 5), (3, 4), (6, 7)])
    tile2 = PathTile([(0, 1), (2, 5), (3, 4), (6, 7)])
    tile2.rotate()
    assert not (tile1 == tile2)


def test_symmetric_tile():
    tile = PathTile([(0, 1), (2, 3), (4, 5), (6, 7)])
    assert tile.unique_rotations() == 1


def test_asymmetric_tile():
    tile = PathTile([(0, 5), (1, 3), (2, 6), (4, 7)])
    assert tile.unique_rotations() == 4
