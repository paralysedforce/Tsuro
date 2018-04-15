

#{ board: Board, players: Player[?], dragon: DragonCard, deck: DrawPile }


class Administrator:

    """ Default constructor
    Sets all fields to None
    """
    def __init__(self):
        self.board = None
        self.players = None
        self.dragon = None
        self.deck = None