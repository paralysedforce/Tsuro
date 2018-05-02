from typing import NamedTuple, List

import attr


class Stateful:
    @classmethod
    def from_state(cls):
        raise NotImplementedError

    def state(self):
        raise NotImplementedError


@attr.s(frozen=True)  # frozen=True makes the class immutable
class State(object):
    def update(self, **replacement_attrs):
        """Return a new instance of State with the arguments overwritten, shallow copying all mutable members."""
        cls = type(self)
        new_attrs = self.__dict__  # extract the members of the current class
        for name, val in replacement_attrs.items():
            if name not in new_attrs:
                raise ValueError('{} is not a member of {}'.format(name, cls))
            new_attrs[name] = val
        return cls(**new_attrs)
