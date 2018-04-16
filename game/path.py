# { startPosn: uint < 8, endPosn: uint < 8, rotate(isClockwise: bool) }

class Path:

    """ Creates a Path from a tuple of points.

    """
    def __init__(self, path_desc):
        self._start_posn = path_desc[0]
        self._end_posn = path_desc[1]

    def rotate(self, isClockwise):
        if isClockwise:
            self._start_posn = (self._start_posn + 2) % 8
            self._end_posn += (self._end_posn + 2) % 8
        else:
            # Add 8 to ensure positive number and proper wrapping around
            self._start_posn = (self._start_posn + 8 - 2) % 8
            self._end_posn += (self._end_posn + 8 - 2) % 8


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

            if output == False:
                print("Output is false.")
                print(self)
                print(other)
            return output
        else:
            print("Other is not a path")
            return NotImplemented