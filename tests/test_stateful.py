from typing import List

import attr
import pytest
from attr import attrib, attrs

from _stateful import ImmutableMixin, State, StatefulInterface


@attrs
class TestState(State):
    x: int = attrib()
    y: int = attrib()


def test_state():
    t = TestState(1, 2)
    assert t == TestState(1, 2), '__eq__ is derived'
    assert t.to_dict() == {'x': 1, 'y': 2}, 'to_dict() converts the attributes to a dictionary'
    t.x = 99
    assert t.x == 99, 'fields are mutable'


@attrs
class TestImmutableState(State, ImmutableMixin):
    x: int = attrib()
    y: int = attrib()


@attrs
class TestImmutableStateWithMutableAttr(State, ImmutableMixin):
    x: List = attrib(default=[])
    y: int = attrib(default=0)


def test_immutable_mixin():
    t = TestImmutableState(1, 2)

    # attempting to mutate an immutable state throws
    with pytest.raises(attr.exceptions.FrozenInstanceError):
        t.x = 99

    t.update(x=99) == TestImmutableState(99, 2), 'update() returns a new instance of self'

    original_list = [1, 2, 3]
    t0 = TestImmutableStateWithMutableAttr(original_list)
    assert t0.x == [1, 2, 3]
    assert t0.x is original_list, 'state maintains a reference to the original list'

    t1 = t0.update(y=2)
    assert t1.x is not original_list, 'update() performs a deep copy of any mutable members'


class TestStatefulClass(StatefulInterface):
    pass


def test_stateful():
    tsc = TestStatefulClass()

    with pytest.raises(NotImplementedError):
        tsc.state()

    with pytest.raises(NotImplementedError):
        ts = TestState(1, 2)
        TestStatefulClass.from_state(ts)
