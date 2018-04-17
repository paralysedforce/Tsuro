from .dragon_card import DragonCard
from .deck import Deck
from .player import Player

def test_not_held_initial():
    assert not DragonCard().is_held()

def test_set_holder_held():
    m_card = DragonCard()
    player = Player(Deck(), m_card)

    m_card.set_holder(player)
    assert m_card.is_held()

    
def test_get_holder_held():
    m_card = DragonCard()
    player = Player(Deck(), m_card)

    m_card.set_holder(player)
    assert m_card.get_holder() == player
    
def test_get_holder_not_held():
    assert DragonCard().get_holder() is None