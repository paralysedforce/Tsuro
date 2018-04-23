import pytest

from board import MapSquare, NoPathTileError, PathTile


def test_map_square():
    square = MapSquare()

    # Raises exception before a PathTile is placed.
    with pytest.raises(NoPathTileError):
        square.get_offset(0)

    square.place_tile(PathTile([(0, 1), (2, 3), (4, 5), (6, 7)]))

    # top
    assert square.get_offset(0) == (0, 1)
    assert square.get_offset(1) == (0, 1)
    # right
    assert square.get_offset(2) == (1, 0)
    assert square.get_offset(3) == (1, 0)
    # bottom
    assert square.get_offset(4) == (0, -1)
    assert square.get_offset(5) == (0, -1)
    # left
    assert square.get_offset(6) == (-1, 0)
    assert square.get_offset(7) == (-1, 0)

    assert square.get_players() is None


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


def test_top_border_terminal():
    pass

def test_bottom_border_terminal():
    pass

def test_left_border_terminal():
    pass

def test_right_border_terminal():
    pass

def test_horizontal_connections():
    pass

def test_vertical_connections():
    pass
