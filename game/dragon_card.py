# { isHeld: bool, holdingPlayer: Player }

class DragonCard:

    """ Default constructor
    Sets isHeld to False and holdingPlayer to none
    """
    def __init__(self):
       self._holding_player = None

    """ Indicates whether this card is held 

        returns -- True if held, False otherwise.
    """
    def is_held(self):
        return self._holding_player is not None

    """ Sets the current holder of this card

        Args:
            player -- instance of Player that is taking possession of the card.
    """
    def set_holder(self, player):
        self._holding_player = player

    """ Gets the current holder of this card

        returns -- Player instance if this card is held, None otherwise
    """
    def get_holder(self):
        return self._holding_player

    def __str__(self):
        return "Dragon card held by " + str(self._holding_player)