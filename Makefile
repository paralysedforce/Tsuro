all: setup test typecheck lint

nosetup: test typecheck lint

setup:
	virtualenv -p python3 env && \
	source env/bin/activate && \
	pip install -r requirements.txt

test:
	pytest -v

typecheck:
	MYPYPATH=tsuro mypy tsuro --ignore-missing-imports -v

lint:
	isort -c tsuro/*.py tests/*.py -v
