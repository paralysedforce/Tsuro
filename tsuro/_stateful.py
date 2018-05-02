from typing import NamedTuple, List

import attr
import json


@attr.s
class State:
    """A State is a serializable representation of some object.

    The @attr.s class decorator derives setters, getters, and other operators
    to simulate working with a tuple.

    Example:

        @attr.s
        class Point(State):
            '''An example state representing a point in a 2D plane.'''
            x: int = attr.ib()
            y: int = attr.ib()

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


@attr.s(frozen=True)
class ImmutableMixin:
    """A mixin to add immutability to any State.

    Prevents inplace modifying of fields and adds an update() method.

    Example:

        @attr.s
        class IPoint(State, ImmutableMixin):
            x: int = attr.ib()
            y: int = attr.ib()

        >>> p = IPoint(1, 2)
        >>> p
        IPoint(x=1, y=2)
        >>> p.x = 99
        "attr.exceptions.FrozenInstanceError"
        >>> p.update(x=99)
        IPoint(x=99, y=2)
    """
    def update(self, **replacement_attrs):
        """Return a new instance of State with the arguments overwritten, shallow copying all mutable members."""
        cls = type(self)
        new_attrs = self.__dict__  # extract the members of the current class
        for name, val in replacement_attrs.items():
            if name not in new_attrs:
                raise ValueError('{} is not a member of {}'.format(name, cls))
            new_attrs[name] = val
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
