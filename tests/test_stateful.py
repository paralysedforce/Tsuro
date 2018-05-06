from typing import List

import attr
import pytest
from attr import attrib, attrs

from _stateful import ImmutableMixin, State, StatefulInterface


@attrs
class SomeState(State):
    x: int = attrib()
    y: int = attrib()


def test_state():
    t = SomeState(1, 2)
    assert t == SomeState(1, 2), '__eq__ is derived'
    assert t.to_dict() == {'x': 1, 'y': 2}, 'to_dict() converts the attributes to a dictionary'
    t.x = 99
    assert t.x == 99, 'fields are mutable'


def test_immutable_mixin():

    @attrs
    class SomeImmutableState(State, ImmutableMixin):
        x: int = attrib()
        y: int = attrib()

    t = SomeImmutableState(1, 2)
    # attempting to mutate an immutable state throws
    with pytest.raises(attr.exceptions.FrozenInstanceError):
        t.x = 99
    t.update(x=99) == SomeImmutableState(99, 2), 'update() returns a new instance of self'

    @attrs
    class SomeImmutableStateWithMutableAttr(State, ImmutableMixin):
        x: List = attrib(default=[])
        y: int = attrib(default=0)

    original_list = [1, 2, 3]
    t0 = SomeImmutableStateWithMutableAttr(original_list)
    assert t0.x == [1, 2, 3]
    assert t0.x is original_list, 'state maintains a reference to the original list'

    t1 = t0.update(y=2)
    assert t1.x is not original_list, 'update() performs a deep copy of any mutable members'


def test_stateful():

    class SomeStatefulClass(StatefulInterface):
        pass

    ssc = SomeStatefulClass()

    with pytest.raises(NotImplementedError):
        ssc.state()

    with pytest.raises(NotImplementedError):
        ts = SomeState(1, 2)
        SomeStatefulClass.from_state(ts)
