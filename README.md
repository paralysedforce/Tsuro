# Tsuro

This project was a networked implementation of Tsuro for EECS 397 at Northwestern University in Spring 2018. It's no longer being actively maintained, but I'm making this public now. It was written in Java 8 in IntelliJ and includes all of its dependencies.

To compile this project, just run ```make```.
This will produce the following two files:

- run.sh: can be run against test-play-a-turn
- tournamentPlayer.sh: can be run to connect a player to a tournament

To add a tournament player to a running tournament, run
```
tournamentPlayer.sh <host address> <port number>
```

Authors:

  - Vyas Alwar (VyasAlwar2018@u.northwestern.edu)
  - William Stogin (wstogin@u.northwestern.edu)
