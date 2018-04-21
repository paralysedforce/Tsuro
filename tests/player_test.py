from deck import DEFAULT_CARDS, Deck
from dragon_card import DragonCard
from player import HAND_SIZE, Player


def test_player_inactive_inital():
    assert Player(Deck(), DragonCard()).is_active()


def test_player_has_dragon_after_draw():
    dragon = DragonCard()
    deck = Deck([])

    player = Player(deck, dragon)

    assert dragon.holder == player


def test_player_cannot_steal_dragon():
    dragon = DragonCard()
    deck = Deck([])

    player = Player(deck, dragon)
    player2 = Player(deck, dragon)

    assert dragon.holder == player and dragon.holder != player2


def test_eliminated_not_active():
    dragon = DragonCard()
    deck = Deck([])

    player = Player(deck, dragon)
    player.eliminate()
    assert not player.is_active()


def test_eliminated_relinquishes_dragon():
    dragon = DragonCard()
    deck = Deck([])

    player = Player(deck, dragon)
    player.eliminate()
    assert not dragon.is_held()


def test_eliminated_returns_cards():
    dragon = DragonCard()
    deck = Deck()

    player = Player(deck, dragon)
    assert len(deck) == (len(DEFAULT_CARDS) - HAND_SIZE)

    player.eliminate()
    assert len(deck) == len(DEFAULT_CARDS)
