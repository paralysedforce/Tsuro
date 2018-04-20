from collections import deque
from typing import List, Tuple, Union

from board import Board
from deck import Deck
from dragon_card import DragonCard
from map_card import MapCard
from player import Player


class Administrator:
    def __init__(self, num_players=3):
        """Default constructor.

        Args:
            num_players (int): Count players to instantiate the Admin with.
        """
        self._board = None
        self._dragon = DragonCard()
        self._deck = Deck()
        self._players = deque()
        for _ in range(num_players):
            self._players.append(Player(self._deck, self._dragon))

    @staticmethod
    def isLegalPlay(board: Board, tile: MapCard, player: Player) -> bool:
        """Return a bool indicating the legality of a tile placement.

        There are two ways a tile placement can be illegal:

        1. The placement of the tile is an elimination move for the player
            (unless all of the possible moves are elimination moves).
        2. The tile is not (a possibly rotated version of) one of the tiles of the player.
        """
        # Check that tile is one of the tiles of the player
        if tile not in player._hand:
            return False

        # Check that placement does not eliminate the player
        (draw_pile, active_players, eliminated_players, board, game_over_or_winners) = Administrator.playATurn(Deck([]), [player], [], board, tile)
        if len(active_players) > 0:
            # Player still active, valid move
            return True
        else:
            return False

    @staticmethod
    def playATurn(draw_pile: Deck,
                  active_players: List[Player],
                  eliminated_players: List,
                  board: Board,
                  placement_tile: MapCard,
                  ) -> Tuple[Deck, List[Player], List, Board, Union[List[Player], bool]]:
        """Compute the state of the game."""
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
            return (draw_pile, active_players, eliminated_players, board, active_players)
        else:
            return (draw_pile, active_players, eliminated_players, board, False)
