from player import Player


class DragonCard:
    """The Dragon Card.

    Attributes:
        _holding_player (Player)
    """
    def __init__(self):
        self._holding_player = None

    def is_held(self):
        # type: () -> bool
        """Return a boolean indicating if the card is held."""
        return self._holding_player is not None

    def set_holder(self, player):
        # type: (Player) -> None
        """Set the current holder of the card.

        Args:
            player (Player): The player to take possession of the card.
        """
        self._holding_player = player

    def get_holder(self):
        # type: () -> Player
        """Return a reference to the card's holder.

        Returns:
            Player | None
        """
        return self._holding_player

    def relinquish(self):
        """Removes the player holding the card."""
        self._holding_player = None

    def __str__(self):
        return "Dragon card held by " + str(self._holding_player)
