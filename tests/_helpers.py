from board import PathTile, Position, TilePlacement


# Aliases for readability.


def PositionAlias(i, j, tile_spot):
    return Position((i, j), tile_spot)


def TileAlias(i, j):
    """Create a PathTile with a single connection."""
    return PathTile([(i, j)])


def TilePlacementAlias(tile, i, j, rotation=0):
    return TilePlacement(tile=tile, coordinate=(i, j), rotation=rotation)
