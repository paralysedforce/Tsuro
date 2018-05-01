import pytest

from admin import Player, TsuroGame, GameState
from board import Board, BoardState, Position, TilePlacement
from deck import Deck, PathTile
from default_config import DEFAULT_WIDTH, DEFAULT_HEIGHT


# def test_illegal_move():
#     player = Player(Deck([[(0, 1), (2, 3), (4, 5), (6, 7), ]]), DragonCard())
#     token = Token(player)

#     board = Board()
#     board.place_token_start(0, token)

#     assert not (Administrator.is_legal_play(board, player._hand[0], player))


def start_game_state():
    """Build a consistent game state for testing.

    1: Player('Eric', Position((0, 0), 0), [])
    2: Player('Will', Position((0, 0), 6), [])
    3: Player('John', Position((0, 2), 0), [])


            +---------++---------++---------+
            | 1       ||         || 3       |
            |         ||         ||         |
            |2        ||         ||         |
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
            Player('Eric', Position((0, 0), 0), []),
            Player('Will', Position((0, 0), 6), []),
            Player('John', Position((0, 2), 0), []),
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


def P(i, j, tile_spot):
    return Position((i, j), tile_spot)


class TsuroGameOneCard(TsuroGame):
    def deck_factory(self):
        return Deck([PathTile([(0, 1)])])


# def admin_base(deck: Deck=TsuroGameOneCard.deck_factory()):
#     """Creates a game with 4 players on the 4 corners of the board"""
#     player1 = Player(name='Robby', position=P(i=0, j=0, tile_spot=0), tiles=[])
#     player2 = Player(name='Will', position=P(i=4, j=0, tile_spot=2), tiles=[])
#     player3 = Player(name='Eric', position=P(i=4, j=4, tile_spot=4), tiles=[])
#     player4 = Player(name='Jerry', position=P(i=0, j=4, tile_spot=6), tiles=[])

#     game = TsuroGame([player1, player2, player3, player4])
#     game.deck = deck


def test_deal_to():
    player = Player(name='Robby', position=None, tiles=[])
    game = TsuroGameOneCard([player])

    assert player.tiles == [], 'start with no tiles'
    game.deal_to(player)
    assert player.tiles == [PathTile([(0, 1)])], 'deal one tile'
    game.deal_to(player)
    assert player.tiles == [PathTile([(0, 1)])], 'deal from empty deck'


def test_dragon_tile():
    p0 = Player(name='Eric', position=None, tiles=[])
    p1 = Player(name='Will', position=None, tiles=[])

    game = TsuroGameOneCard([p0, p1])
    assert game.dragon_tile_holder is None, "dragon tile isn't held at start of game"

    game.deal_to(p0)
    assert game.dragon_tile_holder is None, "if a card is dealt, don't assign dragon card"

    game.deal_to(p0)
    assert game.dragon_tile_holder is p0, "assign dragon card once deck is empty"

    game.deal_to(p1)
    assert game.dragon_tile_holder is p0, "don't reassign dragon card if it's held"


def test_place_tile():
    # (0, 0, 0) -> (0, 0, 5)
    p0 = Player(name='Eric', position=P(0, 0, 0), tiles=[])
    game = TsuroGame([p0])
    tile = PathTile([(0, 5)])
    assert game.peek_path(player=p0, path_tile=tile) == [P(0, 0, 0), P(0, 0, 5), P(1, 0, 0)]


def test_place_two_tiles():
    # Place a tile at (0,1) and peek path at (0,0)
    tile0 = PathTile([(0, 5)])
    tile1 = PathTile([(0, 5)])

    p0 = Player('p0', P(0, 0, 0), [])

    game = TsuroGame([p0])
    game.board.place_tile((1, 0), tile0)

    expected_path = [P(0, 0, 0), P(0, 0, 5), P(1, 0, 0), P(1, 0, 5), P(2, 0, 0)]
    assert game.peek_path(player=p0, path_tile=tile1) == expected_path
    assert not game.board[0][1].has_tile(), 'does not modify state of the board'


# def test_play_a_turn():
#     placed_tile = PathTile([(0, 5)])
#     p0 = Player('p0', P(0, 0, 0), tiles=[placed_tile])

    # game = TsuroGameOneCard([p0])

    # deck: Deck,
    # active_players: List[Player],
    # elim_players: List[Player],
    # board: Board,
    # new_tile: PathTile,
    # i: int,
    # j: int
    # -> Tuple[Deck, List[Player], List[Player], Board, Optional[List[Player]]]
    # (new_deck, active_players, eliminated_players, board, winners_or_false) = game.play_a_turn(game.deck, [p0], [], game.board, t0, 0, 0)

    # assert not new_deck
    # assert active_players == [Player('p0', P(0, 1, 0), [PathTile([(0, 1)])])]
    # assert not eliminated_players
    # assert board._board[0][0].path_tile == placed_tile
    # assert not winners_or_false

# making a move from the edge
def test_move_from_edge():
    initial_state = start_game_state()
    placement = TilePlacement(
        tile=PathTile([(0, 5), (6, 3)]),
        coordinate=(0, 2),
        rotation=0
    )
    (final_state, _) = TsuroGame.play_a_turn(initial_state, placement)

    assert final_state.active_players[1].position == P(1, 2, 0)

# making a move that causes a token to cross multiple tiles
def test_move_accross_multiple():
    initial_state = start_game_state()

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

    (mid_state, _) = TsuroGame.play_a_turn(initial_state, placement0)
    (final_state, _) = TsuroGame.play_a_turn(mid_state, placement1)

    assert final_state.active_players[0].position == P(2, 2, 0)

# making a move where multiple players move at once
def test_move_multiple_players():
    initial_state = start_game_state()
    placement = TilePlacement(
        tile=PathTile([(0, 5), (6, 3)]),
        coordinate=(0,0),
        rotation=0
    )
    (final_state, _) = TsuroGame.play_a_turn(initial_state, placement)

    # Assert that the two players have moved
    assert final_state.active_players[2].position == P(1, 0, 0)
    assert final_state.active_players[0].position == P(0, 1, 6)


# making a move where multiple players are eliminated
def test_eliminate_multiple():
    pass

# making a move where the tile is not placed in its original position (i.e., it is rotated)
def test_test_place_rotation():
    # TODO: Implement some sort of rotation scheme.
    pass

# making an illegal move, specifically where the move is an elimination move, but there are non-elimination moves available
def test_elimination_move():
    # This test is covered in test_eliminate_multiple. The player should detect
    # if its own move will eliminate itself before playing.
    pass


# moving where no player has the dragon tile before or after
def test_never_dragon():
    initial_state = start_game_state()
    # Update the deck state so that nobody takes the dragon
    initial_state = initial_state.update(deck_state=[
        PathTile([(6, 0)]),
        PathTile([]),
        PathTile([]),
        PathTile([]),
        PathTile([(6, 0)]),
        PathTile([]),
        PathTile([]),
        PathTile([]),
        PathTile([(6, 0)]),
    ])
    assert len(initial_state.deck_state) > 0
    game = TsuroGame.from_state(initial_state)
    for player in game.players:
        game.deal_to(player)
        game.deal_to(player)
        game.deal_to(player)
    initial_state = game.state()

    placement = TilePlacement(
        tile=PathTile([(0, 1), (6, 7)]),
        coordinate=(0,0),
        rotation=0
    )

    # To make sure the test is actually testing the proper initial state
    assert initial_state.dragon_holder is None
    (after_state, _) = TsuroGame.play_a_turn(initial_state, placement)
    assert after_state.dragon_holder is None

# moving where one player has the dragon tile before and no one gets any new tiles
def test_no_new_tiles():
    initial_state = start_game_state()

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

    (mid_state, _) = TsuroGame.play_a_turn(initial_state, placement0)
    # Now the last player should be the dragon holder
    assert mid_state.dragon_holder == 2
    (final_state, _) = TsuroGame.play_a_turn(mid_state, placement1)
    # All that has changed is that the holder is further up in the queue
    assert final_state.dragon_holder == 1


# moving where the player that has the dragon tile makes a move that causes an elimination (of another player)
def test_dragon_player_eliminates_other():
    pass

# moving where a player that does not have the dragon tile makes a move and it causes an elimination of the player that has the dragon tile
def test_dragon_player_eliminated_other_dragon_tile_behavior():
    state = start_game_state()
    # Player John is the dragon holder, and Player D comes right after John
    additional_player = state.active_players + [Player('D', Position((3, 3), 3), [])]
    state = state.update(
        active_players=additional_player,
        dragon_holder=2,
    )

    placement = TilePlacement(
        tile=PathTile([(0, 1)]),
        coordinate=(0, 2),
        rotation=0,
    )

    assert state.active_players[state.dragon_holder].name == 'John', 'Player John is the original dragon tile holder'

    new_state, _ = TsuroGame.play_a_turn(state, placement)
    assert new_state.active_players[new_state.dragon_holder].name == 'D', 'Player D is now the dragon tile holder'
    assert new_state.dragon_holder == 1, 'Player Eric plays a turn that eliminates Player John, then requeues, leaving D in index 1'


# moving where the player that has the dragon tile makes a move that causes themselves to be eliminated
def test_dragon_player_self_elimination_dragon_tile_behavior():
    state = start_game_state()
    state = state.update(dragon_holder=0)

    eliminate_player_A = TilePlacement(
        tile=PathTile([(0, 1), (6, 5)]),
        coordinate=(0, 0),
        rotation=0,
    )

    new_state, _ = TsuroGame.play_a_turn(state, eliminate_player_A)
    assert new_state.active_players[0].name == 'Will', 'Eric was eliminated'
    assert new_state.dragon_holder == 0, 'Will is now the dragon tile holder'

def test_dragon_player_self_elimination_deck_behavior():
    state = start_game_state()

    eliminate_player_A = TilePlacement(
        tile=PathTile([(0, 1), (6, 5)]),
        coordinate=(0, 0),
        rotation=0,
    )

    tile0 = PathTile([(0, 1)])
    tile1 = PathTile([(2, 3)])
    tile2 = PathTile([(4, 5)])

    full_deck = state.update(
        active_players= [
            Player('Eric', Position((0, 0), 0), [tile0, tile0]),
            Player('Will', Position((0, 0), 6), [tile0, tile0]),
            Player('John', Position((0, 2), 0), [tile0, tile0, tile0]),
            ],
        dragon_holder=0,
    )
    new_state, _ = TsuroGame.play_a_turn(full_deck, eliminate_player_A)
    # Eric's tiles should have returned to the deck, to have one of them delt
    # to Will.
    assert len(new_state.deck_state) == 1
    assert len(new_state.active_players[0].tiles) == 3

    full_deck = state.update(
        active_players= [
            Player('Eric', Position((0, 0), 0), [tile0]),
            Player('Will', Position((0, 0), 6), [tile0, tile0]),
            Player('John', Position((0, 2), 0), [tile0, tile0]),
            ],
        dragon_holder=0,
    )
    new_state, _ = TsuroGame.play_a_turn(full_deck, eliminate_player_A)
    # This case came about because Eric picked up the dragon tile, a whole round
    # passed, and then Eric eliminated himself. The new tile should go to Will
    # And John should become the next dragon holder.
    assert len(new_state.deck_state) == 0
    assert len(new_state.active_players[0].tiles) == 3
    assert new_state.dragon_holder == 1


