# { isHeld: bool, holdingPlayer: Player }

class DragonCard:

    """ Default constructor
    Sets isHeld to False and holdingPlayer to none
    """
    def __init__(self):
       self._holding_player = None

    def is_held(self):
        return self._holding_player is not None

    def set_holder(self, player):
        self._holding_player = player

    def __str__(self):
        return "Dragon card held by " + str(self._holding_player)