import pytest

from adaptors import AdminAdaptor
from admin import GameState, TsuroGame
from board import BoardState, PathTile, Position, TilePlacement
from player import Color, Player


# TODO: don't copy this from admin_test
@pytest.fixture
def initial_state():
    """Build a consistent game state for testing.

    A: Player('A', Position((0, 0), 0), [], GRAY))
    B: Player('B', Position((0, 0), 6), [], GREEN))
    C: Player('C', Position((0, 2), 0), [], RED))


            +---------++---------++---------+
            | A       ||         || C       |
            |         ||         ||         |
            |B        ||         ||         |
            +---------++---------++---------+
            |         ||         ||         |
            |         ||         ||         |
            |         ||         ||         |
            +---------++---------++---------+
            |         ||         ||         |
            |         ||         ||         |
            |         ||         ||         |
            +---------++---------++---------+

    """
    return GameState(
        active_players=[
            Player('A', Position((0, 0), 0), [], Color.GRAY),
            Player('B', Position((0, 0), 6), [], Color.GREEN),
            Player('C', Position((0, 2), 0), [], Color.RED),
        ],
        eliminated_players=[],
        dragon_holder=None,
        board_state=BoardState(
            tile_placements=[],
            height=3,
            width=3,
        ),
        deck_state=[],
    )


# NOTE: `initial_state` is a test fixture containing an the initial testing state for TsuroGame
def test_administrator_adaptor_move_from_edge(initial_state):
    # Build the "class's" representation of the input
    deck = list(initial_state.deck_state)
    active_players = initial_state.active_players
    eliminated_players = initial_state.eliminated_players
    board = initial_state.board_state

    placement = TilePlacement(
        tile=PathTile([(0, 5), (6, 3)]),
        coordinate=(0, 2),
        rotation=0
    )
    active_players[0].tiles.append(placement.tile)

    aa = AdminAdaptor()
    result = aa.play_a_turn(deck, active_players, eliminated_players, board, placement)
    (deck, active_players, eliminated_players, board, end_indicator) = result
