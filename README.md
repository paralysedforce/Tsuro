## Tsuro

[![Build Status](https://travis-ci.com/chang/Tsuro.svg?token=Vs959weLwfA54UrbgMsc&branch=master)](https://travis-ci.com/chang/Tsuro)
[![codecov](https://codecov.io/gh/chang/Tsuro/branch/master/graph/badge.svg?token=7k8GIzcEI9)](https://codecov.io/gh/chang/Tsuro)

#### Authors
- William Stogin: wstogin@u.northwestern.edu
- Eric Chang: ericchang2017@u.northwestern.edu

#### Setup

`Tsuro` is written using type hints, which is a language feature available in Python 3.5 and onwards. We're supporting Python 3.6.

```
virtualenv env  # --python=/path/to/python3.6/interpreter
source env/bin/activate
pip install -r requirements.txt
```

#### Tests

Unit tests are under the `/tests` module and use the [pytest](http://pytest.org) framework.

```
pytest
```
