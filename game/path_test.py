from .path import Path

def test_creation():
    path = Path((0,1))
    assert path._start_posn == 0 and path._end_posn == 1


def test_comparison_same():
    path = Path((0,1))
    path2 = Path((0,1))
    assert path == path2

def test_comparison_different():
    path = Path((0,1))
    path2 = Path((1,5))
    assert not path == path2

def test_comparison_reversed():
    path = Path((0,1))
    path2 = Path((1,0))
    assert path == path2

def test_rotate_clockwise():
    path = Path((0,1))
    path.rotate(True)
    assert path._start_posn == 2 and path._end_posn == 3

def test_rotate_clockwise_reversed():
    path = Path((0,1))
    path.rotate(True)
    path2 = Path((3,2))
    assert path == path2

def test_rotate_counterclockwise():
    path = Path((0,1))
    path.rotate(False)
    assert path._start_posn == 6 and path._end_posn == 7

def test_rotate_counterclockwise_reversed():
    path = Path((0,1))
    path.rotate(False)
    path2 = Path((7,6))
    assert path == path2


