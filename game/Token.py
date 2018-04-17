# { position:TokenSpot, associatedPlayer: Player }

class Token:
    def __init__(self, player):
        self._player = player

    def eliminate(self):
        self._player.eliminate()