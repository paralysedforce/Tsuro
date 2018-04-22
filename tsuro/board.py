from enum import Enum

from map_card import MapCard

BOARD_HEIGHT = 6
BOARD_WIDTH = 6


class Board:
    def __init__(self):
        # init squares
        self._squares = [[MapSquare() for _ in range(BOARD_WIDTH)] for _ in range(BOARD_HEIGHT)]

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

    def set_terminal(self, is_terminal: bool):
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


# TODO: Use an enum to represent the 8 possible TileSpots?
# TODO: Use an enum to represent the 4 possible Rotations?


class NoPathTileError(Exception):
    """Raised when indexing into a MapSquare missing a path tile."""
    pass


class MapSquare:
    """A square on the map.

    Attributes:
        _path_tile: Optional[PathTile]
        _rotation: int
    """
    def __init__(self):
        # This is an example of the Strategy pattern.
        self._path_tile = None
        self._rotation = 0

    def place_tile(self, path_tile: 'PathTile') -> None:
        self._path_tile = path_tile

    def get_player(self) -> Optional[Tuple[Player, int]]:
        """Return the players on the square as a list of (player, position)."""
        return None

    def get_offset(self, key: int) -> Tuple[int, int]:
        """Return a tuple indicating the side of MapSquare.

        Args:
            key: int

        Returns:
            Tuple[int, int]
                This approach is like an offset matrix in physics applications.
        """
        if not self._path_tile:
            raise NoPathTileError

        # This isn't the cleanest, but type hints require a return statement that
        # is guaranteed to be reached.
        key = self._path_tile[key]

        # Top
        if key in (0, 1):
            offset = (0, 1)
        # Right
        elif key in (2, 3):
            offset = (1, 0)
        # Bottom
        elif key in (4, 5):
            offset = (0, -1)
        # Left
        elif key in (6, 7):
            offset = (-1, 0)

        return offset


class PathTile:
    """A tile with path connections.

           0   1
        +---------+
    7   |         |  2
        |         |
    6   |         |  3
        +---------+
           5   4
    """
    def __init__(self, connections: List[Tuple[int, int]]) -> None:
        # TODO: Assert that PathTile must be created with 4 tuples? This should be asserted by type.
        if not all([0 <= c0 < 8 and 0 <= c1 < 8 for c0, c1 in connections]):
            raise ValueError('Path spots must be values in the range 0-7.')

        self._paths = PathTile.create_paths_dict(connections)  # type: Dict[int, int]
        self._connections = connections

    @staticmethod
    def create_paths_dict(connections: List[Tuple[int, int]]) -> Dict[int, int]:
        paths = {}
        for c0, c1 in connections:
            paths[c0] = c1
            paths[c1] = c0
        return paths

    # TODO: Create a type that constrains this to 0 - 7.
    def __getitem__(self, key: int) -> int:
        """Given an key, return the connecting path."""
        return self._paths[key]

    def __str__(self):
        return "\n".join("{} <-> {}".format(c0, c1) for c0, c1 in self._connections)


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
