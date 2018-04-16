from .admin import Administrator


def test_one():
    admin = Administrator()
    print(admin)
    assert 1