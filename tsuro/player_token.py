# { position:TokenSpot, associatedPlayer: Player }

class Token:

    """ Initializes a token for the given player.

        Args:
            player -- that the Token is associated with
    """
    def __init__(self, player):
        self._player = player
        player.set_token(self)
        self._location = None

    def set_location(self, spot):
        self._location = spot
    
    def get_location(self):
        return self._location

    """ Eliminates the token from the board,
        which also triggers elimination of the associated player.
    """
    def eliminate(self):
        self._player.eliminate()