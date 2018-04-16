TEST_PATH = .

init :
	pip install -r requirements.txt

init-pytest :
	pip install -U pytest

test :
	pytest --verbose --color=yes --rootdir=. $(TEST_PATH)

default :
	python game
