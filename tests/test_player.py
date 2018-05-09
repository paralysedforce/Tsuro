import pytest

from board import Board, BoardState, PathTile, TilePlacement
from player import MostSymmetricStrategy, LeastSymmetricStrategy, num_symmetric_rotations, sort_tiles_by_symmetry


@pytest.fixture
def tiles_by_symmetry():
    """Three pathtiles, sorted from least to most symmetric."""
    single_rotation = PathTile([(0, 1)])
    two_rotations = PathTile([(0, 1), (4, 5)])
    three_rotations = PathTile([(0, 1), (2, 3), (4, 5), (6, 7)])
    return [single_rotation, two_rotations, three_rotations]


@pytest.fixture
def board():
    """A dummy board."""
    return Board(3, 3)


def test_num_symmetric_rotations(tiles_by_symmetry):
    assert num_symmetric_rotations(tiles_by_symmetry[0]) == 0, 'pathtile with single path has no symmetric rotations'
    assert num_symmetric_rotations(tiles_by_symmetry[1]) == 1, 'one possible rotation'
    assert num_symmetric_rotations(tiles_by_symmetry[2]) == 3, 'entire pathtile is symmetric'


def test_num_symmetric_rotations(tiles_by_symmetry):
    one_rot, two_rot, three_rot = tiles_by_symmetry
    path_tiles = [three_rot, one_rot, two_rot]
    assert sort_tiles_by_symmetry(path_tiles) == tiles_by_symmetry, 'sort in ascending order'


def test_least_symmetric(board, tiles_by_symmetry):
    strategy = LeastSymmetricStrategy()
    strategy.choose_move(board, tiles_by_symmetry) == tiles_by_symmetry[0], 'choose the least symmetric tile'

    # throws when called on a full board
    with pytest.raises(ValueError):
        full_board = Board.from_state(
            BoardState(
                tile_placements=TilePlacement(tile=tiles_by_symmetry[0], coordinate=(0, 0)),
                height=3,
                width=3,
            )
        )
        strategy.choose_move(full_board, tiles_by_symmetry)


def test_most_symmetric(board, tiles_by_symmetry):
    strategy = MostSymmetricStrategy()
    strategy.choose_move(board, tiles_by_symmetry) == tiles_by_symmetry[-1], 'choose the most symmetric tile'
