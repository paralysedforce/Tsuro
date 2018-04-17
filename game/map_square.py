from .token_spot import TokenSpot

# { spots: TokenSpot[8] }

class MapSquare:
    def __init__(self):
        self.spots = [TokenSpot()] * 8
        self._occupant = None

    