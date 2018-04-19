
class TokenSpot:
    """ Spot on the board where a Token may be.

        Attributes:
            _parent (MapSquare): The MapSquare that contains this TokensSpot.
            _next_spot (TokenSpot): The spot connected to this one via a path.
            _next_card (TokenSpot): The spotconnected to this one
                via parent adjacency.
            _is_terminal_spot: Whether this spot is on the edge of the board.
            _occupant (Token): The token located on this TokenSpot.
    """

    def __init__(self,
                 is_terminal_spot=False,
                 next_card=None,
                 next_spot=None,
                 parent_square=None):
        """ Initializes a TokenSpot.

            Args:
                is_terminal_spot=False: whether this TokenSpot is terminal
                    (on edge of the board).
                next_card (TokenSpot): The spot in the MapSquare adjacent
                    to the one that holds this Tokespot.
                next_spot: The TokenSpot connected to this one
                    via a path through the paret's square.
        """
        self._parent = parent_square
        self._next_spot = next_spot
        self._next_card = next_card
        self._is_terminal_spot = is_terminal_spot
        self._occupant = None

    def get_parent(self):
        """ Retrieve the parent MapSquare.

            Returns:
                MapSquare: The square that holds this spot.
        """
        return self._parent

    def set_parent(self, parent):
        """ Update the parent MapSquare.

            Args:
                paret (MapSquare): The square that contains this spot.
        """
        self._parent = parent

    def set_terminal(self, is_terminal):
        """ Changes whether this TokenSpot is considered terminal

                Args:
                    is_terminal: Whether this spot is terminal.
        """
        self._is_terminal_spot = is_terminal

    def arrive_via_adjacent(self, token):
        """ Receive a token that arrived via an adjacent spot.

            Args:
                token: The token that is arriving to this TokenSpot.
        """
        # No need to check if it is necessary to eliminate the token
        # because it did not arrive via card placement.

        # Pass the token along if possible.
        if self._next_spot is not None:
            self._next_spot.arrive_via_path(token)
        else:
            self._occupant = token
            token.set_location(self)

    def arrive_via_path(self, token):
        """ Receive a token that arrived via a path.

                Args:
                    token: The toke that is arriving to this TokenSpot.
        """
        # Check if the token should be eliminated.
        if self._is_terminal_spot:
            token.eliminate()

        # Pass the token along if possible.
        elif self._next_card is not None:
            self._next_card.arrive_via_adjacent(token)

        # Make token the occupant.
        else:
            self._occupant = token
            token.set_location(self)

    def pair_via_path(self, other):
        """ Pair this TokenSpot to another via a path.

            Args:
                other (TokenSpot): The spot to be connected to self via a path.
        """
        self._next_spot = other

    def pair_via_adjacency(self, other):
        """ Pairs this TokenSpot to another via adjacency from a different card

            Args:
                other (TokenSpot): Representing the same space on the board.
        """
        self._next_card = other

    def get_occupant(self):
        """ Retrieves the occupying token from this TokenSpot

            Return:
                The Token occupant of this TokenSpot
        """
        return self._occupant

    @staticmethod
    def pair_adjacent(spot1, spot2):
        """ Static function for pairing two adjacent spots in one go

            Args:
                spot1 (TokenSpot): adjacent to spot2
                spot2 (TokenSpot): adjacent to spot1
        """
        spot1.pair_via_adjacency(spot2)
        spot2.pair_via_adjacency(spot1)

    @staticmethod
    def pair_path(spot1, spot2):
        """ Static function for pairing two path connected spots in one go

            Args:
                spot1 (TokenSpot): connected via a path to spot2
                spot2 (TokenSpot): connected via a path to spot1
        """
        spot1.pair_via_path(spot2)
        spot2.pair_via_path(spot1)
