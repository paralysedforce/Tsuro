from collections import deque
from random import shuffle
from typing import List

from map_card import MapCard

DEFAULT_CARDS = [
    [(0, 1), (2, 3), (4, 5), (6, 7)],
    [(0, 1), (2, 4), (3, 6), (5, 7)],
    [(0, 6), (1, 5), (2, 4), (3, 7)],
    [(0, 5), (1, 4), (2, 7), (3, 6)],
    [(0, 2), (1, 4), (3, 7), (5, 6)],
    [(0, 4), (1, 7), (2, 3), (5, 6)],
    [(0, 1), (2, 6), (3, 7), (4, 5)],
    [(0, 2), (1, 6), (3, 7), (4, 5)],
    [(0, 4), (1, 5), (2, 6), (3, 7)],
    [(0, 1), (2, 7), (3, 4), (5, 6)],
    [(0, 2), (1, 7), (3, 4), (5, 6)],
    [(0, 3), (1, 5), (2, 7), (4, 6)],
    [(0, 4), (1, 3), (2, 7), (5, 6)],
    [(0, 3), (1, 7), (2, 6), (4, 5)],
    [(0, 1), (2, 5), (3, 6), (4, 7)],
    [(0, 3), (1, 6), (2, 5), (4, 7)],
    [(0, 1), (2, 7), (3, 5), (4, 6)],
    [(0, 7), (1, 6), (2, 3), (4, 5)],
    [(0, 7), (1, 2), (3, 4), (5, 6)],
    [(0, 2), (1, 4), (3, 6), (5, 7)],
    [(0, 7), (1, 3), (2, 5), (4, 6)],
    [(0, 7), (1, 5), (2, 6), (3, 4)],
    [(0, 4), (1, 5), (2, 7), (3, 6)],
    [(0, 1), (2, 4), (3, 5), (6, 7)],
    [(0, 2), (1, 7), (3, 5), (4, 6)],
    [(0, 7), (1, 5), (2, 3), (4, 6)],
    [(0, 4), (1, 3), (2, 6), (5, 7)],
    [(0, 6), (1, 3), (2, 5), (4, 7)],
    [(0, 1), (2, 7), (3, 6), (4, 5)],
    [(0, 3), (1, 2), (4, 6), (5, 7)],
    [(0, 3), (1, 5), (2, 6), (4, 7)],
    [(0, 7), (1, 6), (2, 5), (3, 4)],
    [(0, 2), (1, 3), (4, 6), (5, 7)],
    [(0, 5), (1, 6), (2, 7), (3, 4)],
    [(0, 5), (1, 3), (2, 6), (4, 7)],
]


class Deck:
    def __init__(self, possible_cards=DEFAULT_CARDS):
        """Creates a deck from the list of known paths."""
        self._cards = deque()
        for card_desc in possible_cards:
            self._cards.append(MapCard(card_desc))

    def shuffle(self):
        """Re-order the cards in the deck."""
        shuffle(self._cards)

    def draw(self):
        # type: () -> MapCard
        """Return the top card from the deck.

        Returns:
            MapCard | None
        """
        try:
            return self._cards.popleft()
        except IndexError:
            return None

    def replace_cards(self, cards):
        # type: (List[MapCard]) -> None
        """Return cards to the bottom of the deck.

        Args:
            cards: (list of MapCard)
        """
        for card in cards:
            self._cards.append(card)

    def get_size(self):
        # type: () -> int
        """Return the number of cards currently in the deck."""
        return len(self._cards)

    def __eq__(self, other):
        if isinstance(other, Deck):
            for card in self._cards:
                if card not in other._cards:
                    return False

            return len(self._cards) == len(other._cards)
        else:
            return NotImplemented

    def __str__(self):
        return str([str(card) for card in self._cards])
