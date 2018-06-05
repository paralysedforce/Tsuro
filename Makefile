# TODO: make the classpath nicer
# TODO: replace ; with : for non-windows machines
# TODO: Fix the issues with Mockito when running from makefile

default: exec

exec: all
	@echo -en '#!/bin/sh\n\njava -cp ./out main/Game' > run.sh

all:
		javac -d out \
		-cp "lib/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar;lib/junit/junit/4.12/junit-4.12.jar;lib/org/mockito/mockito-all/1.10.19/mockito-all-1.10.19.jar" \
		src/main/Parser/*.java \
		src/main/Players/*.java \
		src/main/*.java \
		src/test/GameTests/*.java \
		src/test/ParserTests/*.java \
		src/test/PlayerTests/*.java


clean:
	rm -r out/*
	rm run.sh

run: all
	@cd out; \
	java -cp . main/main;

test: all
	java -cp "out;lib/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar;lib/junit/junit/4.12/junit-4.12.jar" org.junit.runner.JUnitCore test.AllTestSuite