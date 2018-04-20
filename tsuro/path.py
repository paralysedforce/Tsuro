class Path:
    def __init__(self, start, end):
        """Create a Path from a tuple of points.

        Args:
            start (int): number representing one end point of the path
            end (int): number representing the other end point of the path
        """
        self._start_posn = start
        self._end_posn = end

    def rotate(self, is_clockwise: bool):
        """Rotate the path as if it were on the card.

        If clockwise, add 2, mod 8 to start and end position.
        If counterclockwise, subtract 2, mod 8.

        Args:
            is_clockwise (bool)
        """
        if is_clockwise:
            self._start_posn = (self._start_posn + 2) % 8
            self._end_posn = (self._end_posn + 2) % 8
        else:
            # Add 8 to ensure positive number and proper wrapping around
            self._start_posn = (self._start_posn + 8 - 2) % 8
            self._end_posn = (self._end_posn + 8 - 2) % 8

    def get_start(self) -> int:
        return self._start_posn

    def get_end(self) -> int:
        return self._end_posn

    def inverse(self):
        """Reverse the direction of the path."""
        tmp = self._start_posn
        self._start_posn = self._end_posn
        self._end_posn = tmp

    def __eq__(self, other):
        # Two paths are considered equivalent if both the start points and
        # end points are the same or both the start point of one and
        # the end point of the other are the same and the end point of one
        # and the start point of the other are the same.
        if isinstance(other, Path):
            output = ((self._start_posn == other._start_posn and self._end_posn == other._end_posn) or
                      (self._start_posn == other._end_posn and self._end_posn == other._start_posn))
            return output
        else:
            return NotImplemented

    def __str__(self):
        return "Path: (start: " + str(self._start_posn) + " end: " + str(self._end_posn) + ")"
