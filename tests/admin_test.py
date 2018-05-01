import pytest

from admin import Player, TsuroGame, GameState
from board import Board, Position, TilePlacement
from deck import Deck, PathTile
from default_config import DEFAULT_WIDTH, DEFAULT_HEIGHT

# def test_illegal_move():
#     player = Player(Deck([[(0, 1), (2, 3), (4, 5), (6, 7), ]]), DragonCard())
#     token = Token(player)

#     board = Board()
#     board.place_token_start(0, token)

#     assert not (Administrator.is_legal_play(board, player._hand[0], player))


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


def create_simple_game_state() -> GameState:
    p0 = Player('p0', P(0, 0, 0), tiles=[])
    game = TsuroGame([p0])
    return game.state()

def create_simple_placement() -> TilePlacement:
    tile = PathTile([(0, 5)])
    placement = TilePlacement(
        tile=tile,
        coordinate=(0, 0),
        rotation=0,
        )
    return placement

# making a move from the edge
def test_move_from_edge():
    placement = create_simple_placement()
    initial_state = create_simple_game_state()
    (after_state, winners) = TsuroGame.play_a_turn(initial_state, placement)

    game = TsuroGame.from_state(after_state)

    p0_expected_position = P(1, 0, 0)
    assert p0_expected_position == game.players[0].position

# making a move that causes a token to cross multiple tiles
def test_move_accross_multiple():
    placement0 = create_simple_placement()
    placement1 = create_simple_placement()
    # Update placement0 to be not adjacent to the player
    placement0 = TilePlacement(placement0[0], (1, 0), placement0[2])
    initial_state = create_simple_game_state()
    (mid_state, _) = TsuroGame.play_a_turn(initial_state, placement0)
    (final_state, _) = TsuroGame.play_a_turn(mid_state, placement1)

    p0_expected_position = P(2, 0, 0)
    assert final_state.active_players[0].position == p0_expected_position

# making a move where multiple players move at once
def test_move_multiple_players():
    pass

# making a move where multiple players are eliminated
def test_eliminate_multiple():
    pass

# making a move where the tile is not placed in its original position (i.e., it is rotated)
def test_test_place_rotation():
    pass

# making an illegal move, specifically where the move is an elimination move, but there are non-elimination moves available
def test_elimination_move():
    pass


# moving where no player has the dragon tile before or after
def test_never_dragon():
    before_state = create_simple_game_state()
    placement = create_simple_placement()

    # To make sure the test is actually testing the proper initial state
    assert before_state.dragon_holder is None
    (after_state, _) = TsuroGame.play_a_turn(before_state, placement)
    assert after_state.dragon_holder is None


# moving where one player has the dragon tile before and no one gets any new tiles
def test_no_new_tiles():
    pass

# moving where the player that has the dragon tile makes a move that causes an elimination (of another player)
def test_dragon_player_eliminates_other():
    pass

# moving where a player that does not have the dragon tile makes a move and it causes an elimination of the player that has the dragon tile
def test_dragon_player_eliminated():
    pass

# moving where the player that has the dragon tile makes a move that causes themselves to be eliminated
def test_dragon_player_self_elimination():
    pass

