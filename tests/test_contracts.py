import pytest

from _contracts import _get_func_args, postcondition, precondition


def test_precondition():

    @precondition('x > 0 and y > 0')
    def add_positive(x, y):
        return x + y

    assert add_positive(x=1, y=2) == 3, 'nothing happens if precondition is met'

    # throws an error upon negative input
    with pytest.raises(AssertionError):
        add_positive(-1, 2)


def test_postcondition():

    @postcondition('len(l) > 1')
    def append_to_list(l):
        l.append(1)

    # throws since argument l has length == 1 at the end of the function
    with pytest.raises(AssertionError):
        append_to_list([])

    append_to_list([0])  # does not throw


def test_mixed_precondition_postcondition():

    @precondition('x > 0')
    @postcondition('len(l) > 1')
    def append_positive_to_list(x, l):
        l.append(x)

    # append_positive_to_list(1, [0])  # doesn't throw since all conditions are met

    # with pytest.raises(AssertionError):
    #     append_positive_to_list(-1, [0])

    # with pytest.raises(AssertionError):
    #     append_positive_to_list(1, [])


def test_get_func_args():

    def args_decorator(func):
        """Decorated functions return the result from _get_func_args()."""
        def inner(*pos_args, **kwargs):
            return _get_func_args(func=func, pos_args=pos_args, kwargs=kwargs)
        return inner

    @args_decorator
    def add(x, y):
        return x + y

    expected = {'x': 1, 'y': 2}

    assert add(1, 2) == expected, 'positional args'
    assert add(x=1, y=2) == expected, 'keyword args'
    assert add(1, y=2) == expected, 'mixed'
    assert add(y=2, x=1) == expected, 'out of order kwargs'
