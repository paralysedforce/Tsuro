from player import Player


class DragonCard:
    """The Dragon Card.

    Attributes:
        holder (Player)
    """
    def __init__(self):
        self.holder = None

    def is_held(self) -> bool:
        """Return a boolean indicating if the card is held."""
        return self.holder is not None

    def set_holder(self, player: Player):
        """Set the current holder of the card.

        Args:
            player (Player): The player to take possession of the card.
        """
        self.holder = player

    def relinquish(self):
        """Removes the player holding the card."""
        self.holder = None

    def __str__(self):
        return "DragonCard({})".format(self.holder)
