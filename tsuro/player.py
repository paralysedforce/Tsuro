from dragon_card import DragonCard

# { hand: MapCard[3], isActive: bool }

HAND_SIZE = 3

class Player:

    """ Initializes an active player with a hand of HAND_SIZE cards

        Args:
            deck -- instance of Deck for drawing and replacing cards
            dragon_card -- instance of the dragon_card for picking up when the deck is empty
            token=None -- Token that corresponds to this player
    """
    def __init__(self, deck, dragon_card, token=None):
        self._deck = deck

        self._is_active = True
        self._dragon_card = dragon_card

        self._hand = []
        for _ in range(HAND_SIZE):
            self.draw_card()

        self._token = token

    def get_token(self):
        return self._token

    def set_token(self, token):
        self._token = token

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

    """ Chooses a card from the hand and returns it as it's turn

        Args:
            placement_square -- where the chosen card will end up going

        return -- a MapCard oriented in the way that it should be placed
    """
    def take_turn(self, placement_square):
        pass # TODO

    """ Deactivates the player, returns tiles to deck """
    def eliminate(self):
        self._is_active = False
        self._deck.replace_cards(self._hand)

        if self == self._dragon_card.get_holder():
            # Relinquish the dragon card
            self._dragon_card.relinquish()


    # __eq__ is not necessary because no players are equivalent unless
    # they are the exact same object

