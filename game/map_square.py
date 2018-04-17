from .token_spot import TokenSpot

# { spots: TokenSpot[8] }

class MapSquare:

    """ Creates a MapSquare that contains TokenSpots and can receive a MapCard

        Args:
            token_spots=None -- List of TokenSpots for use in testing
    """
    def __init__(self, token_spots=None):
        if token_spots is None:
            self._spots = [TokenSpot()] * 8
        else:
            self._spots = token_spots
        self._map_card = None

    """ Places a card in the MapSquare, connecting TokenSpots as appropriate.

        Args:
            map_card -- a MapCard representing the paths to be connected.
    """
    def place_card(self, map_card):
        assert self._map_card is None

        self._map_card = map_card

        for path in map_card.get_paths():
            start = self._spots[path.get_start()]
            end = self._spots[path.get_end()]

            start.pair_via_path(end)
            end.pair_via_path(start)

    

    