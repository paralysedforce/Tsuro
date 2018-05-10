def precondition(expr):
    """A decorator that evaluates a stringified expression before the execution of a function.

    Example:

        @precondition('x > 0 and y > 0')
        def add_positive(x, y):
            return x + y

        >>> add_positive(1, 2)
        3
        >>> add_positive(-1, 2)
        AssertionError: Precondition "x > 0 and y > 0" is false
    """
    def decorator(func):
        def inner_decorator(*args, **kwargs):
            arguments = _get_func_args(func, args, kwargs)
            cond = eval(expr, None, arguments)  # set args dict to locals field, otherwise they'll be overwritten
            assert cond, 'Precondition "{}" is false'.format(expr)
            res = func(*args, **kwargs)
            return res
        return inner_decorator
    return decorator


def postcondition(expr):
    """A decorator that evaluates a stringified expression after the execution of a function.

    For now, can only assert postconditions on function arguments.

    Example:

        @postcondition('len(l) > 1')
        def append_to_list(l):
            l.append(1)
            return l

        >>> append_to_list([0])
        [0, 1]
        >>> append_to_list([])
        AssertionError: Postcondition "len(l) > 1" is false
    """
    def decorator(func):
        def inner_decorator(*args, **kwargs):
            arguments = _get_func_args(func, args, kwargs)
            res = func(*args, **kwargs)
            cond = eval(expr, None, arguments)
            assert cond, 'Postcondition "{}" is false'.format(expr)
            return res
        return inner_decorator
    return decorator


# NOTE: for some reason, *args is getting overwritten in the decorated function
# using pos_args instead

def _get_func_args(func, pos_args, kwargs):
    """Get the arguments of a function as a dict of named values."""
    var_names = func.__code__.co_varnames
    pos_args_dict = {k: v for k, v in zip(var_names[0:len(pos_args)], pos_args)}
    args_dict = {**pos_args_dict, **kwargs}
    return args_dict
