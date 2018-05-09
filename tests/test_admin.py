import pytest

from _helpers import PositionAlias as P
from admin import GameState, RulesViolatedError, TsuroGame
from board import BoardState, Position, TilePlacement
from deck import PathTile
from player import Color, Player


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


@pytest.fixture
def game(initial_state):
    """An instance of TsuroGame, instantiated with inital_state. For testing."""
    return TsuroGame.from_state(initial_state)


def test_move_from_edge(game):
    placement = TilePlacement(
        tile=PathTile([(0, 5), (6, 3)]),
        coordinate=(0, 2),
        rotation=0
    )
    game.players[0].tiles.append(placement.tile)
    game.play_turn(placement)
    assert game.state().active_players[1].position == P(1, 2, 0)


def test_move_across_multiple(game):
    """making a move that causes a token to cross multiple tiles"""
    placement0 = TilePlacement(
        tile=PathTile([(0, 5), (6, 3)]),
        coordinate=(1, 2),
        rotation=0
    )

    placement1 = TilePlacement(
        tile=PathTile([(0, 5), (6, 3)]),
        coordinate=(0, 2),
        rotation=0
    )

    game.players[0].tiles.append(placement0.tile)
    game.play_turn(placement0)
    game.players[0].tiles.append(placement1.tile)
    game.play_turn(placement1)

    state = game.state()
    assert state.active_players[0].position == P(2, 2, 0)


def test_move_multiple_players(game):
    """making a move where multiple players move at once"""
    placement = TilePlacement(
        tile=PathTile([(0, 5), (6, 3)]),
        coordinate=(0, 0),
        rotation=0
    )
    game.players[0].tiles.append(placement.tile)
    game.play_turn(placement)
    state = game.state()

    # Assert that the two players have moved
    assert state.active_players[2].position == P(1, 0, 0)
    assert state.active_players[0].position == P(0, 1, 6)


def test_self_elimination_other_options_illegal(initial_state):
    straight_edge = PathTile([(0, 5), (1, 4), (2, 7), (3, 6)])
    self_eliminate = PathTile([(0, 1), (2, 3), (4, 5), (6, 7)])

    state = initial_state.update(
        active_players=[Player('A', Position((0, 0), 0), [straight_edge, self_eliminate], Color.GRAY)],
    )
    game = TsuroGame.from_state(state)

    # throws because Player A plays a self-elimination with other options available
    with pytest.raises(RulesViolatedError):
        placement = TilePlacement(tile=self_eliminate, coordinate=(0, 0), rotation=0)
        player = game.players[0]
        game.is_placement_legal(placement, player)

    state = initial_state.update(
        active_players=[Player('A', Position((0, 0), 0), [self_eliminate], Color.GRAY)],
    )

    game = TsuroGame.from_state(state)
    player = game.players[0]
    placement = TilePlacement(tile=self_eliminate, coordinate=(0, 0), rotation=0)
    game.is_placement_legal(placement, player)  # does not throw


def test_card_not_in_hand_illegal():
    straight_edge = PathTile([(0, 5), (1, 4), (2, 7), (3, 6)])
    state = initial_state()
    placement = TilePlacement(tile=straight_edge, coordinate=(0, 0), rotation=0)
    game = TsuroGame.from_state(state)
    player = game.players[0]

    with pytest.raises(RulesViolatedError):
        game.is_placement_legal(placement, player)


def test_eliminate_multiple():
    """making a move where multiple players are eliminated"""
    pass


def test_test_place_rotation():
    """making a move where the tile is not placed in its original position (i.e., it is rotated)"""
    # TODO: Implement some sort of rotation scheme.
    pass


def test_elimination_move():
    """making an illegal move where the move is an elimination move, but there are non-elimination moves available"""
    # This test is covered in test_eliminate_multiple. The player should detect
    # if its own move will eliminate itself before playing.
    pass


def test_never_dragon(initial_state):
    """moving where no player has the dragon tile before or after"""
    # Update the deck state so that nobody takes the dragon
    game = TsuroGame.from_state(initial_state.update(deck_state=[
        PathTile([(0, 1), (6, 7)]),
        PathTile([(0, 1), (6, 7)]),
        PathTile([(0, 1), (6, 7)]),
        PathTile([(0, 1), (6, 7)]),
        PathTile([(0, 1), (6, 7)]),
        PathTile([(0, 1), (6, 7)]),
        PathTile([(0, 1), (6, 7)]),
        PathTile([(0, 1), (6, 7)]),
        PathTile([(0, 1), (6, 7)]),
    ]))

    for player in game.players:
        game.deal_to(player)
        game.deal_to(player)
        game.deal_to(player)

    placement = TilePlacement(
        tile=PathTile([(0, 1), (6, 7)]),
        coordinate=(0, 0),
        rotation=0
    )

    assert game.state().dragon_holder is None
    game.play_turn(placement)
    assert game.state().dragon_holder is None


def test_no_new_tiles(game):
    """moving where one player has the dragon tile before and no one gets any new tiles"""
    placement0 = TilePlacement(
        tile=PathTile([(0, 5), (6, 3)]),
        coordinate=(1, 2),
        rotation=0
    )
    placement1 = TilePlacement(
        tile=PathTile([(0, 5), (6, 3)]),
        coordinate=(0, 2),
        rotation=0
    )

    game.players[0].tiles.append(placement0.tile)
    game.play_turn(placement0)
    assert game.state().dragon_holder == 2, 'the last player should be the dragon holder'
    game.players[0].tiles.append(placement1.tile)
    game.play_turn(placement1)
    assert game.state().dragon_holder == 1, 'All that has changed is that the holder is further up in the queue'


def test_dragon_player_eliminates_other():
    """moving where the player that has the dragon tile makes a move that causes an elimination (of another player)"""
    pass


def test_dragon_player_eliminated_other_dragon_tile_behavior(initial_state):
    """moving where a player that does not have the dragon tile makes a move, causes an elimination of the player that has the dragon tile"""
    # Player C is the dragon holder, and Player D comes right after C
    additional_player = initial_state.active_players + [Player('D', Position((3, 3), 3), [])]

    game = TsuroGame.from_state(initial_state.update(
        active_players=additional_player,
        dragon_holder=2,
    ))

    eliminate_A = TilePlacement(
        tile=PathTile([(0, 1)]),
        coordinate=(0, 2),
        rotation=0,
    )

    state = game.state()
    assert state.active_players[state.dragon_holder].name == 'C', 'Player C is the original dragon tile holder'

    game.players[0].tiles.append(eliminate_A.tile)
    game.play_turn(eliminate_A)
    new_state = game.state()
    assert new_state.active_players[new_state.dragon_holder].name == 'D', 'Player D is now the dragon tile holder'
    assert new_state.dragon_holder == 1, 'Player A plays a turn that eliminates Player C, then requeues, leaving D in index 1'


def test_dragon_player_self_elimination_dragon_tile_behavior(initial_state):
    """moving where the player that has the dragon tile makes a move that causes themselves to be eliminated"""
    game = TsuroGame.from_state(initial_state.update(dragon_holder=0))

    eliminate_player_A = TilePlacement(
        tile=PathTile([(0, 1), (6, 5)]),
        coordinate=(0, 0),
        rotation=0,
    )

    game.players[0].tiles.append(eliminate_player_A.tile)
    game.play_turn(eliminate_player_A)
    assert game.state().active_players[0].name == 'B', 'A was eliminated'
    assert game.state().dragon_holder == 0, 'B is now the dragon tile holder'


def test_dragon_player_self_elimination_deck_behavior(initial_state):
    eliminate_player_A = TilePlacement(
        tile=PathTile([(0, 1), (6, 5)]),
        coordinate=(0, 0),
        rotation=0,
    )

    tile0 = PathTile([(0, 1)])
    tile1 = PathTile([(2, 3)])
    tile2 = PathTile([(4, 5)])

    one_card = TsuroGame.from_state(initial_state.update(dragon_holder=0, deck_state=[tile0]))
    two_cards = TsuroGame.from_state(initial_state.update(dragon_holder=0, deck_state=[tile0, tile1]))
    three_cards = TsuroGame.from_state(initial_state.update(dragon_holder=0, deck_state=[tile0, tile1, tile2]))
    one_card.players[0].tiles.append(eliminate_player_A.tile)
    two_cards.players[0].tiles.append(eliminate_player_A.tile)
    three_cards.players[0].tiles.append(eliminate_player_A.tile)

    one_card.play_turn(eliminate_player_A)
    assert one_card.state().active_players[0].tiles == [tile0], 'Player to move draws the single tile from the deck'

    two_cards.play_turn(eliminate_player_A)
    assert two_cards.state().active_players[0].tiles == [tile0]
    assert two_cards.state().active_players[1].tiles == [tile1], 'Next player in queue draws second tile'

    three_cards.play_turn(eliminate_player_A)
    assert three_cards.state().active_players[1].tiles == [tile1]
    assert three_cards.state().active_players[0].tiles == [tile0, tile2], 'Card drawing rotates to front of queue'
