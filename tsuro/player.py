from typing import Optional

from deck import Deck
from map_card import MapCard

HAND_SIZE = 3


class Player:
    """Agent playing the game.

    Attributes:
        _deck (Deck):
        _is_active (bool): Whether this player is currently in the game (not eliminated).
        _dragon_card (DragonCard): The dragon card that the player may pick up.
    """
    def __init__(self, deck: Deck, dragon_card) -> None:
        """Initialize an active player with a hand of HAND_SIZE cards

        Args:
            deck (Deck): Used for drawing and replacing cards.
            dragon_card: Picked up when the deck is empty.
                token: The Token that corresponds to this player.
        """
        self._deck = deck
        self._is_active = True
        self._dragon_card = dragon_card
        self._hand = [self.draw_card() for _ in range(HAND_SIZE)]

    def is_active(self) -> bool:
        return self._is_active

    def draw_card(self) -> Optional[MapCard]:
        """Directs this player to draw a card from its _deck."""
        card = self._deck.draw()
        # If there are no cards remaining in the deck, acquire the dragon card instead.
        if not card and not self._dragon_card.is_held():
            self._dragon_card.set_holder(self)
        return card

    def take_turn(self, placement_square):
        """Chooses a card from the hand and returns it as it's turn

        Args:
            placement_square (MapSquare): Where the chosen card will
            eventually be played.

        Returns:
            A MapCard oriented in the way that it should be placed.
        """
        pass  # TODO

    def eliminate(self):
        """Deactivates the player, returns tiles to deck."""
        self._is_active = False
        self._deck.replace_cards(self._hand)

        if self is self._dragon_card.holder:
            self._dragon_card.relinquish()
