from .deck import Deck
from .player import Player
from .dragon_card import DragonCard

from collections import deque

#{ board: Board, players: Player[?], dragon: DragonCard, deck: DrawPile }


class Administrator:

    """ Default constructor

        Args:
            num_players=3 -- number of players to instantiate the Admin with
    """
    def __init__(self, num_players=3):
        self._board = None
        self._dragon = DragonCard()
        self._deck = Deck()
        self._players = deque()
        for _ in range(num_players):
            self._players.append(Player(self._deck, self._dragon))



    """ Determines whether it is legal for a given player to place a given tile on a given board.

        There are two ways a tile placement can be illegal:

        1. The placement of the tile is an elimination move for the player
            (unless all of the possible moves are elimination moves).
        2. The tile is not (a possibly rotated version of) one of the tiles of the player.
    """
    @staticmethod
    def isLegalPlay(board, tile, player):
        pass


    """ Computes the state of the game after the completion of a turn given the
         state of the game before the turn.
    """
    def playATurn(draw_pile, active_players, eliminated_players2, board, placement_tile):
        moving_player = active_players[0]

        moving_player