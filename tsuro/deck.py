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
        self._cards = deque([MapCard(c) for c in possible_cards])

    def shuffle(self):
        """Re-order the cards in the deck."""
        shuffle(self._cards)

    def draw(self) -> MapCard:
        """Return the top card from the deck.

        Returns:
            Optional[MapCard]
        """
        if not self._cards:
            return None
        else:
            return self._cards.popleft()

    def replace_cards(self, cards: List[MapCard]):
        """Return cards to the bottom of the deck.

        Args:
            cards: (List[MapCard])
        """
        for card in cards:
            self._cards.append(card)

    def __len__(self):
        return len(self._cards)

    def __eq__(self, other):
        same_cards = all([c in other._cards for c in self._cards])
        same_len = len(self._cards) == len(other._cards)
        return same_cards and same_len

    def __str__(self):
        return str([str(card) for card in self._cards])
