from token_spot import TokenSpot
from enum import Enum


class Side(Enum):
    TOP = 0
    RIGHT = 1
    BOTTOM = 2
    LEFT = 3


class MapSquare:
    """A square on the map.

    Attributes:
        _spots (None | TokenSpot[])
    """
    def __init__(self, token_spots=None):
        """Create a MapSquare that contains TokenSpots and can receive a MapCard.

        Args:
            token_spots (None | TokenSpot[])
        """
        if token_spots is None:
            self._spots = [
                TokenSpot(),
                TokenSpot(),
                TokenSpot(),
                TokenSpot(),
                TokenSpot(),
                TokenSpot(),
                TokenSpot(),
                TokenSpot(),
            ]
        else:
            self._spots = token_spots

        # Set token parents as self
        for token_spot in self._spots:
            token_spot.set_parent(self)

        self._map_card = None

    def place_card(self, map_card):
        """Place a card in the MapSquare, connecting TokenSpots as appropriate.

        Args:
            map_card (MapCard)
        """
        assert self._map_card is None

        self._map_card = map_card

        for path in map_card.get_paths():
            start = self._spots[path.get_start()]
            end = self._spots[path.get_end()]

            TokenSpot.pair_path(start, end)

            # If either have a token, send it along
            occupant_start = start.get_occupant()
            occupant_end = end.get_occupant()
            if occupant_start is not None:
                end.arrive_via_path(occupant_start)
            if occupant_end is not None:
                start.arrive_via_path(occupant_end)

    def set_adjacent(self, other, side):
        """Binds the TokenSpots on the borders of adjacent MapSquares.

        Args:
            other (MapSquare): MapSquare that is adjacent to self.
            side (Side): Side showing on which side of self other lies.
        """
        # Sadly, switch doesn't exist :(

        if side == Side.TOP:
            # Set the 0th and 1st nodes of self to 5th and 4th nodes of other.
            TokenSpot.pair_adjacent(self._spots[0], other._spots[5])
            TokenSpot.pair_adjacent(self._spots[1], other._spots[4])
        elif side == Side.RIGHT:
            # Set the 2nd and 3rd nodes of self to 8th and 7th nodes of other.
            TokenSpot.pair_adjacent(self._spots[2], other._spots[7])
            TokenSpot.pair_adjacent(self._spots[3], other._spots[6])
        elif side == Side.BOTTOM:
            # Set the 4th and 5th nodes of self to 1st and 0th nodes of other.
            TokenSpot.pair_adjacent(self._spots[4], other._spots[1])
            TokenSpot.pair_adjacent(self._spots[5], other._spots[0])
            pass
        elif side == Side.LEFT:
            # Set the 6th and 7th nodes of self to 3rd and 2nd nodes of other.
            TokenSpot.pair_adjacent(self._spots[6], other._spots[3])
            TokenSpot.pair_adjacent(self._spots[7], other._spots[2])
        else:
            # Improper imput, don't mess anything up by doing anything
            raise ValueError("Invalid side argument: {0}".format(side))

    def set_terminal(self, side):
        """Set all TokenSpots on the indicated side as terminal.

        Args:
            side (Side): The side on which the TokenSpot terminates.
        """
        if side == Side.TOP:
            # Set 0 and 1 to terminal
            self._spots[0].set_terminal(True)
            self._spots[1].set_terminal(True)
        elif side == Side.RIGHT:
            # Set 2 and 3 to terminal
            self._spots[2].set_terminal(True)
            self._spots[3].set_terminal(True)
        elif side == Side.BOTTOM:
            # Set 4 and 5 to terminal
            self._spots[4].set_terminal(True)
            self._spots[5].set_terminal(True)
        elif side == Side.LEFT:
            # Set 6 and 7 to terminal
            self._spots[6].set_terminal(True)
            self._spots[7].set_terminal(True)
        else:
            # Improper imput, don't mess anything up by doing anything
            raise ValueError("Invalid side argument: {}".format(side))
