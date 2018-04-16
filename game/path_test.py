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
