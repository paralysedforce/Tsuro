from enum import Enum

BOARD_HEIGHT = 6
BOARD_WIDTH = 6


class Board:
    def __init__(self):
        # init squares
        self._squares = []
        for _ in range(BOARD_HEIGHT):
            row = []
            for _ in range(BOARD_WIDTH):
                square = MapSquare()
                row.append(square)
            self._squares.append(row)

        # terminate boundary squares
        for square in self._squares[0]:
            square.set_terminal(Side.TOP)
        for square in self._squares[BOARD_HEIGHT - 1]:
            square.set_terminal(Side.BOTTOM)
        for squares in self._squares:
            squares[0].set_terminal(Side.LEFT)
            squares[BOARD_WIDTH - 1].set_terminal(Side.RIGHT)

        # Bind adjacent squares
        prev_row = []
        for row in self._squares:
            prev_square = None
            for i in range(BOARD_WIDTH):
                square = row[i]

                # Bind left
                if prev_square is not None:
                    square.set_adjacent(prev_square, Side.LEFT)
                prev_square = square

                # Bind above
                if len(prev_row) > 0:  # This was necessary because
                    # subscripting prev_row was throwing errors
                    square.set_adjacent(prev_row[i], Side.TOP)

            prev_row = row

    def place_token_start(self, loc, token):
        """Place a token in one of the 2x6x4 starting spots.

        Args:
            loc (int): The location around the edge clockwise. [0,2x6x4)
        """
        # TODO: make this actually work. Currently only works for loc=0
        self._squares[0][0]._spots[1].receive_token_via_adjacent(token)


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

    def receive_token_via_adjacent(self, token):
        """ Receive a token that arrived via an adjacent spot.

            Args:
                token: The token that is arriving to this TokenSpot.
        """
        # No need to check if it is necessary to eliminate the token
        # because it did not arrive via card placement.

        # Pass the token along if possible.
        if self._next_spot is not None:
            self._next_spot.receive_token_via_path(token)
        else:
            self._occupant = token
            token.set_location(self)

    def receive_token_via_path(self, token):
        """ Receive a token that arrived via a path.

                Args:
                    token: The toke that is arriving to this TokenSpot.
        """
        # Check if the token should be eliminated.
        if self._is_terminal_spot:
            token.eliminate()

        # Pass the token along if possible.
        elif self._next_card is not None:
            self._next_card.receive_token_via_adjacent(token)

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
                end.receive_token_via_path(occupant_start)
            if occupant_end is not None:
                start.receive_token_via_path(occupant_end)

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
