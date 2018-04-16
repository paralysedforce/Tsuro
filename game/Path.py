# { startPosn: uint < 8, endPosn: uint < 8, rotate(isClockwise: bool) }

class Path:

    """ Creates a Path from a tuple of points.

    """
    def __init__(self, path_desc):
        self._start_posn = path_desc[0]
        self.end_posn = path_desc[1]

    def rotate(self, isClockwise):
        pass