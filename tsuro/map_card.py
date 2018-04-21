from copy import deepcopy
from typing import List

from path import Path


class MapCard:
    """Creates a MapCard from a list of tuples

    Args:
        card_desc (int):  List of tuples that each represents a path on this card.
    """
    def __init__(self, card_desc):
        self._paths = []
        for path_desc in card_desc:
            self._paths.append(Path(*path_desc))

    def rotate(self, is_clockwise: bool):
        """Rotates the MapCard by rotating each of the paths.

        Args:
            is_clockwise (bool)
        """
        for path in self._paths:
            path.rotate(is_clockwise)

    def get_paths(self) -> List[Path]:
        """Gets a copy of the list of paths on this card.

        Returns:
            List[Path]
        """
        # TODO: Is this deepcopy necessary? Do we mutate Paths?
        return deepcopy(self._paths)

    def __eq__(self, other):
        same_cards = all([p in other._paths for p in self._paths])
        same_length = len(self._paths) == len(other._paths)
        return same_cards and same_length

    def __str__(self):
        return str([str(path) for path in self._paths])
