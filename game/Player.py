from .dragon_card import DragonCard

# { hand: MapCard[3], isActive: bool }

HAND_SIZE = 3

class Player:

    """ Initializes an active player with a hand of HAND_SIZE cards

        Args:
            deck -- instance of Deck for drawing and replacing cards
            dragon_card -- instance of the dragon_card for picking up when the deck is empty
    """
    def __init__(self, deck, dragon_card):
        self._deck = deck

        self._is_active = True
        self._dragon_card = dragon_card

        self._hand = []
        for _ in range(HAND_SIZE):
            self.draw_card()

    def is_active(self):
        return self._is_active

    def draw_card(self):
        card = self._deck.draw()
        if card is not None:
            self._hand.append(card)
        else:
            # No more cards, get dragon card instead
            if not self._dragon_card.is_held():
                # Claim the dragon card
                self._dragon_card.set_holder(self)


    # __eq__ is not necessary because no players are equivalent unless
    # they are the exact same object

