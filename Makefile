TEST_PATH = .

init :
	pip install -r requirements.txt

init-pytest :
	pip install -U pytest

test :
	pytest --verbose --color=yes --rootdir=. $(TEST_PATH)
	MYPYPATH=tsuro mypy tsuro       # Static type checking
	isort -c tsuro/*.py tests/*.py  # Import order checking

