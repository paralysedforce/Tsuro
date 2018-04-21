class Path:
    def __init__(self, start, end):
        """Create a Path from a tuple of points.

        Args:
            start (int): number representing one end point of the path
            end (int): number representing the other end point of the path
        """
        self.start = start
        self.end = end

    def rotate(self, is_clockwise: bool):
        """Rotate the path as if it were on the card.

        If clockwise, add 2, mod 8 to start and end position.
        If counterclockwise, subtract 2, mod 8.

        Args:
            is_clockwise (bool)
        """
        if is_clockwise:
            self.start = (self.start + 2) % 8
            self.end = (self.end + 2) % 8
        else:
            # Add 8 to ensure positive number and proper wrapping around
            self.start = (self.start + 8 - 2) % 8
            self.end = (self.end + 8 - 2) % 8

    def inverse(self):
        """Reverse the direction of the path."""
        self.start, self.end = self.end, self.start

    def __eq__(self, other):
        # Two paths are considered equivalent if both the start points and
        # end points are the same or both the start point of one and
        # the end point of the other are the same and the end point of one
        # and the start point of the other are the same.
        if not isinstance(other, Path):
            raise ValueError('Cannot compare Path with type: {}'.format(type(other)))
        return (self.start == other.start and self.end == other.end) or \
               (self.start == other.end and self.end == other.start)

    def __str__(self):
        return "Path: (start: " + str(self.start) + " end: " + str(self.end) + ")"
