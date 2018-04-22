from board import PathTile

import pytest


def test_pathtile_creation():
    # Raise an exception on an invalid TileSpot input.
    with pytest.raises(ValueError):
        PathTile([(9, 10)])


def test_pathtile_indexing():
    tile = PathTile([(0, 1)])
    assert tile[0] == 1
    assert tile[1] == 0

    tile = PathTile([(0, 1), (2, 3)])
    assert tile[0] == 1
    assert tile[1] == 0
    assert tile[2] == 3
    assert tile[3] == 2
