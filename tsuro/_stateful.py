import json
from copy import deepcopy
from typing import List, NamedTuple

import attr
from attr import attrs


@attrs
class State:
    """A State is a serializable representation of some object.

    The @attrs class decorator derives setters, getters, and other operators
    to simulate working with a tuple.

    Example:

        @attrs
        class Point(State):
            '''An example state representing a point in a 2D plane.'''
            x: int = attrib()
            y: int = attrib()

        >>> p = Point(1, 2)
        >>> p
        Point(x=1, y=2)
        >>> x, y = p            # tuple unpacking
        >>> p.to_dict()         # dictionary representation
        {'x': 1, 'y': 2}
        >>> print(p.to_json())  # json serialization
        {
            "x": 1,
            "y": 2
        }
    """

    def to_dict(self):
        return attr.asdict(self)

    def to_json(self):
        return json.dumps(self.to_dict(), indent=4)


@attrs(frozen=True)
class ImmutableMixin:
    """A mixin to add immutability to any State.

    Prevents inplace modifying of fields and adds an update() method.

    Example:

        @attrs
        class IPoint(State, ImmutableMixin):
            x: int = attrib()
            y: int = attrib()

        >>> p = IPoint(1, 2)
        >>> p
        IPoint(x=1, y=2)
        >>> p.x = 99
        "attr.exceptions.FrozenInstanceError"
        >>> p.update(x=99)
        IPoint(x=99, y=2)
    """

    def update(self, deep_copy=True, **replacement_attrs):
        """Return a new instance of State with the arguments overwritten, shallow copying all members."""
        cls = type(self)
        new_attrs = self.__dict__  # extract the members of the current class
        for name, val in replacement_attrs.items():
            if name not in new_attrs:
                raise ValueError('{} is not a member of {}'.format(name, cls))
            new_attrs[name] = val

        # TODO: Profile this. May result in serious inefficiencies.
        if deep_copy:
            for name, val in new_attrs.items():
                new_attrs[name] = deepcopy(val)

        return cls(**new_attrs)


class StatefulInterface:
    """An interface that an object must implement to be Stateful.

    To be stateful, an object must be able to return its state and reconstruct itself from a state.
    """

    @classmethod
    def from_state(cls, state):
        raise NotImplementedError

    def state(self):
        raise NotImplementedError
