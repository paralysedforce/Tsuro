## Tsuro

[![Build Status](https://travis-ci.com/chang/Tsuro.svg?token=Vs959weLwfA54UrbgMsc&branch=master)](https://travis-ci.com/chang/Tsuro)
[![codecov](https://codecov.io/gh/chang/Tsuro/branch/master/graph/badge.svg?token=7k8GIzcEI9)](https://codecov.io/gh/chang/Tsuro)

### Authors
- William Stogin: wstogin@u.northwestern.edu
- Eric Chang: ericchang2017@u.northwestern.edu

### Setup

`Tsuro` is written using type hints, which is a language feature available in Python 3.5 and onwards. We're supporting Python 3.6.

```bash
virtualenv env  # --python=/path/to/python3.6/interpreter
source env/bin/activate
pip install -r requirements.txt
```

### Static Type Checking

As a dynamically typed language, Python doesn't allow us to enforce contracts using typing. This lack of type safety tends to cause problems as a codebase grows.

To mitigate this, we made the design decision to add static type checking to `Tsuro`. We do this using `mypy` along with the standard library's `typing` module. This doesn't affect the program's functionality at runtime, but allows us to do type checking using static analysis tools. Type checking is done in our continuous integration builds.

```python
def add(x, y):
    return x + y

def add_typed(x: int, y: int) -> int:
    return x + y

add(1, 0.5)
add_typed(1, 0.5)  # example.py:8: error: Argument 2 to "add_typed" has incompatible type "float"; expected "int"
```

To perform type checking:

```bash
mypy tsuro tests
```

To generate type annotations:

```bash
source scripts/pyannotate.sh
```

### Tests

Unit tests are under the `/tests` module and use the [pytest](http://pytest.org) framework.

```bash
pytest
```
