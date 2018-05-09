all: test typecheck lint

test:
	pytest -v

typecheck:
	MYPYPATH=tsuro mypy tsuro --ignore-missing-imports

lint:
	isort -c tsuro/*.py tests/*.py -v
