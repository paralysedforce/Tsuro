class Token:
    """Marks a player's location on the board.

    Attributes:
        _player (Player)
        _location (TokenSpot)
    """

    def __init__(self, player):
        """Initialize a token for the given player.

        Args:
            player (Player)
        """
        self._player = player
        player.set_token(self)
        self._location = None

    def set_location(self, spot):
        self._location = spot

    def get_location(self):
        return self._location

    def eliminate(self):
        """Eliminate the token and its associated player from the board."""
        self._player.eliminate()
