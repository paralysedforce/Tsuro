from .path import Path
from copy import deepcopy

# { paths: Path[4], rotate(isClockwise: bool) }

class MapCard:

    """ Creates a MapCard from a list of tuples

        Args:
            card_desc -- List of tuples that each represents a path 
                on this card.
    """
    def __init__(self, card_desc):
        self._paths = []
        for path_desc in card_desc:
            self._paths.append(Path(*path_desc))

    """ Rotates the MapCard by rotating each of the paths.

        Args:
            is_clockwise -- True to rotate the card clockwise,
                False to rotate the card counterclockwise.
    """
    def rotate(self, is_clockwise):
        for path in self._paths:
            path.rotate(is_clockwise)
        

    """ Gets a copy of the list of paths on this card.

        returns -- list of Path 
    """
    def get_paths(self):
        return deepcopy(self._paths)

    def __eq__(self, other):
        if isinstance(other, MapCard):
            # Check that paths are the same
            for path in self._paths:
                if path not in other._paths:
                    return False
            
            # Only return true if paths are the same length
            return len(self._paths) == len(other._paths)
        else:
            return NotImplemented


    def __str__(self):
        return str([str(path) for path in self._paths])