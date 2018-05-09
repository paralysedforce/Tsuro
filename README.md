## Tsuro

[![Build Status](https://travis-ci.com/chang/Tsuro.svg?token=Vs959weLwfA54UrbgMsc&branch=master)](https://travis-ci.com/chang/Tsuro)
[![codecov](https://codecov.io/gh/chang/Tsuro/branch/master/graph/badge.svg?token=7k8GIzcEI9)](https://codecov.io/gh/chang/Tsuro)

### Authors
- William Stogin: wstogin@u.northwestern.edu
- Eric Chang: ericchang2017@u.northwestern.edu

### Setup

```bash
cd Tsuro
virtualenv env --python=/path/to/python3.6/interpreter
source env/bin/activate
pip install -r requirements.txt
```

### Tests

To run all tests:

```bash
make
```

Otherwise:

```bash
make test       # run unit tests
make typecheck  # run type checking
make lint       # run import order checking (TODO: PEP8 linting)
```

### Static Type Checking

As a dynamically typed language, Python doesn't allow us to catch errors using typing. The lack of type safety tends to cause problems as a codebase grows in size.

To help with this issue, `Tsuro` is written using type hints, a language feature available in Python 3.5 and onwards. This doesn't affect the program at runtime, but lets us do type checking using static analysis tools like [mypy](https://github.com/python/mypy). Type checking is done as part of our tests and in our CI builds.

```python
def add(x: int, y: int) -> int:
    return x + y

add(1, 0)
add(1, 0.5)  # example.py:8: error: Argument 2 to "add" has incompatible type "float"; expected "int"
```
