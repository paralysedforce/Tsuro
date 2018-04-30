import pytest

from admin import Player, TsuroGame
from board import Board, Position
from deck import Deck, PathTile

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


def test_play_a_turn():
    placed_tile = PathTile([(0, 5)])
    p0 = Player('p0', P(0, 0, 0), tiles=[placed_tile])

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
