import pytest

from board import Board, BoardSquare, NoPathTileError, PathTile,    Position


def test_board_edge_positions():
    b = Board(1, 1)
    expected = [
        Position(0, 0, 0),
        Position(0, 0, 1),
        Position(0, 0, 2),
        Position(0, 0, 3),
        Position(0, 0, 4),
        Position(0, 0, 5),
        Position(0, 0, 6),
        Position(0, 0, 7),
    ]
    assert b.edge_positions == expected

    b = Board(2, 2)
    expected = [
        Position(0, 0, 0),
        Position(0, 0, 1),
        Position(0, 1, 0),
        Position(0, 1, 1),

        Position(0, 1, 2),
        Position(0, 1, 3),
        Position(1, 1, 2),
        Position(1, 1, 3),

        Position(1, 0, 4),
        Position(1, 0, 5),
        Position(1, 1, 4),
        Position(1, 1, 5),

        Position(0, 0, 6),
        Position(0, 0, 7),
        Position(1, 0, 6),
        Position(1, 0, 7),
    ]
    assert expected == b.edge_positions


def test_board_place_tile():
    tile = PathTile([(0, 1), (2, 3), (4, 5), (6, 7)])

    b = Board(3, 3)
    b.place_tile(1, 1, tile)
    assert b._board[1][1].path_tile == tile

    with pytest.raises(IndexError):
        b.place_tile(-1, 0, tile)
        b.place_tile(3, 0, tile)


def test_board_traverse_one_tile():
    b = Board(3, 3)

    with pytest.raises(IndexError):
        b.traverse_path(Position(-1, 0, 0))
        b.traverse_path(Position(3, 0, 0))

    assert b.traverse_path(Position(0, 0, 0)) == [], 'empty list if no pathtile'


def test_board_square():
    square = BoardSquare()

    # Raises exception before a PathTile is placed.
    with pytest.raises(NoPathTileError):
        square.next(0)

    assert not square.has_tile()
    square.path_tile = PathTile([(0, 1), (2, 3), (4, 5), (6, 7)])
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
    # invalid TileSpot
    with pytest.raises(ValueError):
        PathTile([(9, 10)])

    # non-unique paths
    with pytest.raises(AssertionError):
        PathTile([(0, 1), (0, 2)])

    # indexing
    tile = PathTile([(0, 1)])
    assert tile[0] == 1
    assert tile[1] == 0

    tile = PathTile([(0, 1), (2, 3)])
    assert tile[0] == 1
    assert tile[1] == 0
    assert tile[2] == 3
    assert tile[3] == 2

    # string representation
    tile = PathTile([(0, 1)])
    assert str(tile) == '0 <-> 1'


def test_path_tile_equality():
    assert PathTile([(0, 1)]) == PathTile([(0, 1)])
    assert PathTile([(0, 1)]) == PathTile([(1, 0)])
    assert not PathTile([(0, 1)]) == PathTile([(0, 2)])
    assert not PathTile([(0, 1)]) == PathTile([(0, 1), (2, 3)])
    assert PathTile([(2, 3), (0, 1)]) == PathTile([(0, 1), (2, 3)])
