# { position:TokenSpot, associatedPlayer: Player }


class Token:
    """Marks a player's location on the board.

        Attributes:
            _player (Player): who owns this token.
            _location (TokenSpot): where this token currently is.
    """

    """ Initializes a token for the given player.

        Args:
            player (Player): who owns this token
    """

    def __init__(self, player):
        self._player = player
        player.set_token(self)
        self._location = None

    def set_location(self, spot):
        self._location = spot

    def get_location(self):
        return self._location

    def eliminate(self):
        """Eliminates the token from the board,
            triggering elimination of the associated player."""
        self._player.eliminate()
