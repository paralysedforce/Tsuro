from .map_card import MapCard
from .path import Path

PATH_DESCS = [
                (0,1),
                (3,7),
             ]       

def test_init():
    map_card = MapCard([
        (0,1)
        ])
    print(map_card)
    assert Path(0,1) in map_card._paths

def test_rotate_clockwise():
    map_card = MapCard(PATH_DESCS)
    map_card.rotate(True)

    paths = [Path(*x) for x in PATH_DESCS]
    for path in paths:
        path.rotate(True)
        print(path)
        print(map_card)
        assert path in map_card._paths 


def test_rotate_counterclockwise():    
    map_card = MapCard(PATH_DESCS)
    map_card.rotate(False)

    paths = [Path(*x) for x in PATH_DESCS]
    for path in paths:
        path.rotate(False)
        print(path)
        print(map_card)
        assert path in map_card._paths 

def test_equal():
    card1 = MapCard(PATH_DESCS)
    card2 = MapCard(PATH_DESCS)
    assert card1 == card2

def test_equal_after_rotate():
    card1 = MapCard(PATH_DESCS)
    card2 = MapCard(PATH_DESCS)
    card1.rotate(True)
    card2.rotate(True)
    assert card1 == card2

def test_not_equal():
    card1 = MapCard(PATH_DESCS)
    card2 = MapCard(PATH_DESCS)
    card2.rotate(True)
    assert not card1 == card2

def test_not_equal_lengths():
    card1 = MapCard(PATH_DESCS)
    # Leave out first path
    card2 = MapCard(PATH_DESCS[1:])
    assert not card1 == card2
