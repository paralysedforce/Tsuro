from .map_card import MapCard
from .path import Path


def test_init():
    map_card = MapCard([
        (0,1)
        ])
    print(map_card)
    assert Path(0,1) in map_card._paths

def test_rotate_clockwise():
    path_descs = [
        (0,1),
        (3,7),
        ]
    
    map_card = MapCard(path_descs)
    map_card.rotate(True)

    paths = [Path(*x) for x in path_descs]
    for path in paths:
        path.rotate(True)
        print(path)
        print(map_card)
        assert path in map_card._paths 


def test_rotate_counterclockwise():
    path_descs = [
        (0,1),
        (3,7),
        ]
    
    map_card = MapCard(path_descs)
    map_card.rotate(False)

    paths = [Path(*x) for x in path_descs]
    for path in paths:
        path.rotate(False)
        print(path)
        print(map_card)
        assert path in map_card._paths 
