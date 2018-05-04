from board import PathTile, Position


def P(i, j, tile_spot):
    # Just for readability.
    return Position((i, j), tile_spot)


def Tile(i, j):
    """Create a PathTile with a single connection."""
    return PathTile([(i, j)])
