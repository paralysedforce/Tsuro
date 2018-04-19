# { startPosn: uint < 8, endPosn: uint < 8, rotate(isClockwise: bool) }

class Path:

    """ Creates a Path from a tuple of points.

        Args:
            start -- number representing one end point of the path
            end -- number representing the other end point of the path
    """
    def __init__(self, start, end):
        self._start_posn = start
        self._end_posn = end

    """ Rotates the path as if it were on the card.
        Adds 2, mod 8 to start and end position if clockwise,
        subtracts 2, mod 8 if counterclockwise.

        Args:
            is_clockwise -- True to rotate the card clockwise, 
                False to rotate the card counterclockwise
    """
    def rotate(self, is_clockwise):
        if is_clockwise:
            self._start_posn = (self._start_posn + 2) % 8
            self._end_posn = (self._end_posn + 2) % 8
        else:
            # Add 8 to ensure positive number and proper wrapping around
            self._start_posn = (self._start_posn + 8 - 2) % 8
            self._end_posn = (self._end_posn + 8 - 2) % 8


    """ Gets the end position of this path 
    
        return -- number representing the start position of this path
    """
    def get_start(self):
        return self._start_posn

    """ Gets the end position of this path 
    
        return -- number representing the end position of this path
    """
    def get_end(self):
        return self._end_posn

    """ Reverses the direction of this path """
    def inverse(self):
        tmp = self._start_posn
        self._start_posn = self._end_posn
        self._end_posn = tmp

    """ Compares two Paths

        Two paths are considered equivalent if both the start points and
        end points are the same or both the start point of one and
        the end point of the other are the same and the end point of one
        and the start point of the other are the same.
    """
    def __eq__(self, other):
        if isinstance(other, Path):
            output = (
            (self._start_posn == other._start_posn
                and
                self._end_posn == other._end_posn)
            or
            (self._start_posn == other._end_posn
                and
                self._end_posn == other._start_posn))
            return output
        else:
            return NotImplemented

    def __str__(self):
        return "Path: (start: " + str(self._start_posn) + " end: " + str(self._end_posn) + ")"
