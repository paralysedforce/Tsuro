
# { next: TokenSpot, previous: TokenSpot, occupant: Token }

class TokenSpot:

    """ Initializes a TokenSpot.

        Args:
            is_terminal_spot=False -- whether this TokenSpot is terminal (edge of the board)
            next_card=None -- The TokenSpot adjacent to this one.
            next_spot=None -- The TokenSpot connected to this one via a path.
    """
    def __init__(self, is_terminal_spot=False, next_card=None, next_spot=None):
        self._next_spot = next_spot
        self._next_card = next_card
        self._is_terminal_spot = is_terminal_spot
        self._occupant = None

    """ Eliminates any tokens as necessary

        Args:
            token -- that is arriving to trigger this check

        return -- True if elimination occurred, False otherwis.
    """
    def _did_eliminate(self, token):
        # Eliminate player if terminal
        if self._is_terminal_spot:
            token._player.eliminate()
            return True

        # If another occupant, eliminate both
        elif self._occupant is not None:
            token._player.eliminate()
            self._occupant._player.eliminate()
            self._occupant = None
            return True
        
        return False

    """ Changes whether this TokenSpot is considered terminal

        Args:
            is_terminal -- boolean indicating whether this spot is terminal.
    """
    def set_terminal(self, is_terminal):
        self._is_terminal_spot = is_terminal

    """ Called to notify of a token's arrival via a path on the same card

        Args:
            token -- that is arriving to this TokenSpot
    """
    def arrive_via_adjacent(self, token):
        # Check if need to eliminate
        if self._did_eliminate(token):
            # Token eliminated, nothing else to do.
            pass

        # Pass along if possible
        elif self._next_spot is not None:
            self._next_spot.arrive_via_path(token)
        else:
            self._occupant = token

    """ Called to notify of a token's arrival via adjacence from a different card

        Args:
            token -- that is arriving to this TokenSpot
    """
    def arrive_via_path(self,token):
        # Check if need to eliminate
        if self._did_eliminate(token):
            # Token eliminated, nothing else to do.
            pass

        # Pass along if possible
        elif self._next_card is not None:
            self._next_card.arrive_via_adjacent(token)

        # Make token occupant
        else:
            self._occupant = token

    """ Pairs this TokenSpot to another via a path

        Args:
            other -- a TokenSpot connected via a path
    """
    def pair_via_path(self, other):
        self._next_spot = other
    
    """ Pairs this TokenSpot to another via adjacency from a different card 

        Args:
            other -- a TokenSpot representing the same space on the board
    """
    def pair_via_adjacency(self, other):
        self._next_card = other

    """ Retrieves the occupying token from this TokenSpot

        returns -- Token occupant of this TokenSpot
    """
    def get_occupant(self):
        return self._occupant

    """ Static function for pairing two adjacent spots in one go

        Args:
            spot1 -- TokenSpot adjacent to spot2
            spot2 -- TokenSpot adjacent to spot1
    """
    @staticmethod
    def pair_adjacent(spot1, spot2):
        spot1.pair_via_adjacency(spot2)
        spot2.pair_via_adjacency(spot1)

    """ Static function for pairing two path connected spots in one go

        Args:
            spot1 -- TokenSpot connected via a path to spot2
            spot2 -- TokenSpot connected via a path to spot1
    """
    @staticmethod
    def pair_path(spot1, spot2):
        spot1.pair_via_path(spot2)
        spot2.pair_via_path(spot1)
