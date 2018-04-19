from deck import Deck
from player import Player
from dragon_card import DragonCard

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
        # Check that tile is one of the tiles of the player
        if tile not in player._hand:
            return False

        # Check that placement does not eliminate the player
        (draw_pile, active_players, eliminated_players, board, game_over_or_winners ) = Administrator.playATurn(Deck([]), [player], [], board, tile)
        if len(active_players) > 0:
            # Player still active, valid move
            return True
        else:
            return False



    """ Computes the state of the game after the completion of a turn given the
         state of the game before the turn.
    """
    @staticmethod
    def playATurn(draw_pile, active_players, eliminated_players, board, placement_tile):
        moving_player = active_players[0]

        square = moving_player.get_token().get_location().get_parent()

        # Place tile in the square
        square.place_card(placement_tile)
        moving_player.draw_card()

        # Remove inactive players
        for player in active_players:
            if not player.is_active():
                active_players.remove(player)

        # Still need to move to the end of the list
        if moving_player.is_active():
            active_players.remove(moving_player)
            active_players.append(moving_player)

        sum_hands = 0
        for player in active_players:
            sum_hands += len(player._hand)

        game_over = len(active_players) < 2 or sum_hands == 0


        if game_over:
            return (draw_pile, active_players, eliminated_players, board, active_players )
        else:
            return (draw_pile, active_players, eliminated_players, board, False )