package main.server;

import game.bot.PointSaladBot;
import game.state.PointSaladState;
import network.Server;

import java.util.Scanner;

/**
 * Hosts a game of Point Salad with the specified number of players and bots.
 * The server will wait for all players to connect before starting the game.
 */
public class HostPointSaladGame {
    public static final int PORT = 2048;

    public static void main(String[] args) {
        int numPlayers = 0;
        int numBots = 0;
        if(args.length > 3 || args.length == 1) {
            throw new IllegalArgumentException("Usage: java HostPointSaladGame [numPlayers] [numBots]");
        }
        else if(args.length == 2 || args.length == 3) {
            numPlayers = Integer.parseInt(args[0]);
            numBots = Integer.parseInt(args[1]);
        }
        else {
            // First, set up and host the server with specified number of bots and players
            Scanner scan = new Scanner(System.in);
            System.out.println("Enter the number of players: ");
            numPlayers = scan.nextInt();
            System.out.println("Enter the number of bots: ");
            numBots = scan.nextInt();
            scan.close();
        }
        if (numBots + numPlayers < 2) {
            System.out.println("You need at least 2 players to play the game.");
            return;
        }
        if (numBots + numPlayers > 6) {
            System.out.println("You can only have up to 6 players.");
            return;
        }
        int port = PORT;
        if(args.length == 3) {
            port = Integer.parseInt(args[2]);
        }
        System.out.println("Server is running on port " + port + "...");
        if (numPlayers > 0) {
            System.out.println("Waiting for " + numPlayers + " players to join...");
        }

        // Now initialize the game and server
        PointSaladState game = new PointSaladState(numPlayers + numBots);
        PointSaladBot bot = new PointSaladBot(game);
        // Creating the server blocks until all players have connected
        Server server = new Server(port, numPlayers, numBots, game, bot);
        server.startServer();
        server.startGame();
    }
}
