from collections import deque
from typing import List, Optional, Tuple

from board import PathTile


class Deck:
    """A deck of PathTiles.

    Attributes:
        _tiles (Deque[PathTile])
    """

    def __init__(self, tiles: List[PathTile]) -> None:
        self._tiles = deque(tiles)

    @classmethod
    def from_connections(cls, connections: List[List[Tuple[int, int]]]):
        """A factory method to create Deck from a list of PathTile specifications."""
        path_tiles = [PathTile(spec) for spec in connections]
        return cls(path_tiles)

    def draw(self) -> Optional[PathTile]:
        """Return the top card from the deck. If deck is empty, return None."""
        if not self._tiles:
            return None
        else:
            return self._tiles.popleft()

    def replace_tiles(self, cards: List[PathTile]):
        """Return cards to the bottom of the deck.

        Args:
            cards: (List[PathTile])
        """
        for card in cards:
            self._tiles.append(card)

    def __len__(self):
        return len(self._tiles)

    def __eq__(self, other):
        if not len(self) == len(other):
            return False
        return all([c0 == c1 for c0, c1 in zip(self._tiles, other._tiles)])

    def __contains__(self, card):
        return card in self._tiles
