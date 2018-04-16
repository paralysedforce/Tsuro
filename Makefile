TEST_PATH = ./tests

init :
	pip install -r requirements.txt

test : init
	pytest --verbose --color=yes $(TEST_PATH)


default :
	python game
