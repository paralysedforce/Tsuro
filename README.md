## Tsuro

[![Build Status](https://travis-ci.com/chang/Tsuro.svg?token=Vs959weLwfA54UrbgMsc&branch=master)](https://travis-ci.com/chang/Tsuro)
[![codecov](https://codecov.io/gh/chang/Tsuro/branch/master/graph/badge.svg?token=7k8GIzcEI9)](https://codecov.io/gh/chang/Tsuro)

### Authors
- William Stogin: wstogin@u.northwestern.edu
- Eric Chang: ericchang2017@u.northwestern.edu

### Setup

`Tsuro` is written using type hints, which is a language feature available in Python 3.5 and onwards. We're supporting Python 3.6.

```bash
virtualenv env --python=/path/to/python3.6/interpreter
source env/bin/activate
pip install -r requirements.txt
```

### Static Type Checking

As a dynamically typed language, Python doesn't allow us to enforce contracts using typing. The lack of type safety tends to cause problems as a codebase grows in size.

To mitigate this, we made the design decision to use Python type hints with `Tsuro`. This doesn't affect the program at runtime, but allows us to do type checking using static analysis tools like [mypy](https://github.com/python/mypy). Type checking is done in our CI builds.

```python
def add(x: int, y: int) -> int:
    return x + y

add(1, 0)
add(1, 0.5)  # example.py:8: error: Argument 2 to "add" has incompatible type "float"; expected "int"
```

```bash
# To perform type checking:
MYPYPATH=tsuro mypy tsuro

# To generate type annotations using pyannotate (https://github.com/dropbox/pyannotate)
source scripts/pyannotate.sh
```

### Tests

Unit tests are under the `/tests` module and use the [pytest](http://pytest.org) framework.
