# Point Salad Game

Welcome to the Point Salad Game! 
This is a simple card game that is played with a deck of cards. 
The deck of cards contains 108 cards, each with a unique combination of 6 different vegetables. 
The goal of the game is to collect cards that will give you the most points at the end of the game. 
The game is played in rounds, and each round, players will take cards and convert them.
The game ends when there are no more cards in the deck and garden. 
The player with the most points at the end of the game wins.

## Folder Structure

This workspace contains the following folders:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies
- `test`: the folder to maintain test files

Compiled output exists in the `out` folder.

## Running the Game

To run the game, go to the `main` folder under `src`. To host the game, run any of the HostGame files.
To join any kind of game, run the JoinGame file.
When hosting a game without args, you will be asked to input the number of players and the number of rounds.
When joining a game without args, it will default to the default server local IP, ideal for local testing.
When joining a game with args, the only arg is the server's IP address.
The game will always be hosted on port 2048.

### Running the Game Through Command Line

All commands must be run from the root directory of the project.
The client can be run through the command line by running the following command:
`java src/main/client/JoinGame.java`
The server can be run through the command line by running the following command:
`java -cp lib/org.json.jar src/main/server/HostPointSaladGame.java`

## Running the Tests

All tests are located in the `test` folder. 
Tests must be run through the IDE. The test.iml folder has the necessary configurations to run the tests.
To run the tests, right-click on the `test` folder and select `Run 'All Tests'`.
