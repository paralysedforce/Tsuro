# { next: TokenSpot, previous: TokenSpot, occupant: Token }

class TokenSpot:
    def __init__(self):
        self.next = None
        self.previous = None
        self.occupant = None