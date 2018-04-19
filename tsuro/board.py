from map_square import MapSquare
from map_square import Side

# { tokenSpots:TokenSpot[8][8], placementSquares: Dim2(4,4) }

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
        for square in self._squares[BOARD_HEIGHT-1]:
            square.set_terminal(Side.BOTTOM)
        for squares in self._squares:
            squares[0].set_terminal(Side.LEFT)
            squares[BOARD_WIDTH-1].set_terminal(Side.RIGHT)

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
                if len(prev_row) > 0: # This was necessary because
                    # subscripting prev_row was throwing errors
                    square.set_adjacent(prev_row[i], Side.TOP)

            prev_row = row

    """ Places a token in one of the 2x6x4 starting spots

        Args:
            loc -- [0,2x6x4) location around the edge clockwise
    """
    def place_token_start(self, loc, token):
        # TODO: make this actually work. Currently only works for loc=0
        self._squares[0][0]._spots[1].arrive_via_adjacent(token)

