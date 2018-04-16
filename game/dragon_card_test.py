from .dragon_card import DragonCard

def test_not_held_initial():
    assert not DragonCard().is_held()
