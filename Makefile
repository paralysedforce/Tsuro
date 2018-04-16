TEST_PATH = .

init :
	pip install -r requirements.txt

test :
	python -m pytest --verbose --color=yes --rootdir=. $(TEST_PATH)

default :
	python game
