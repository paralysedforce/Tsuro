from board import Token

HAND_SIZE = 3


class Player:
    """Agent playing the game.

        Attributes:
            _deck (Deck):
            _is_active: Whether this player is currently in the game
                (not eliminated).
            _dragon_card: The dragon card that the player may pick up.
    """

    def __init__(self, deck, dragon_card, token=None):
        """Initializes an active player with a hand of HAND_SIZE cards

            Args:
                deck (Deck): Used for drawing and replacing cards.
                dragon_card: Picked up when the deck is empty.
                token: The Token that corresponds to this player.
        """
        self._deck = deck

        self._is_active = True
        self._dragon_card = dragon_card

        self._hand = []
        for _ in range(HAND_SIZE):
            self.draw_card()

        self._token = token

    def get_token(self):
        # type: () -> Token
        return self._token

    def set_token(self, token):
        # type: (Token) -> None
        self._token = token

    def is_active(self):
        # type: () -> bool
        return self._is_active

    def draw_card(self):
        """Directs this player to draw a card from its _deck."""
        card = self._deck.draw()
        if card is not None:
            self._hand.append(card)
        else:
            # No more cards, get dragon card instead
            if not self._dragon_card.is_held():
                # Claim the dragon card
                self._dragon_card.set_holder(self)

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

        if self == self._dragon_card.get_holder():
            # Relinquish the dragon card
            self._dragon_card.relinquish()
