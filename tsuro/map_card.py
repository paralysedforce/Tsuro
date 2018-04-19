from path import Path
from copy import deepcopy


class MapCard:
    """Creates a MapCard from a list of tuples

    Args:
        card_desc (int):  List of tuples that each represents a path on this card.
    """
    def __init__(self, card_desc):
        self._paths = []
        for path_desc in card_desc:
            self._paths.append(Path(*path_desc))

    def rotate(self, is_clockwise):
        """Rotates the MapCard by rotating each of the paths.

        Args:
            is_clockwise (bool)
        """
        for path in self._paths:
            path.rotate(is_clockwise)

    def get_paths(self):
        """Gets a copy of the list of paths on this card.

        Returns:
            list of Path
        """
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
