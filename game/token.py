# { position:TokenSpot, associatedPlayer: Player }

class Token:

    """ Initializes a token for the given player.

        Args:
            player -- that the Token is associated with
    """
    def __init__(self, player):
        self._player = player

    """ Eliminates the token from the board,
        which also triggers elimination of the associated player.
    """
    def eliminate(self):
        self._player.eliminate()