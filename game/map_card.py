from .path import Path

# { paths: Path[4], rotate(isClockwise: bool) }

class MapCard:

    """ Creates a MapCard from a list of tuples

    """
    def __init__(self, card_desc):
        self._paths = []
        for path_desc in card_desc:
            self._paths.append(Path(path_desc))

    def rotate(self, is_clockwise):
        pass