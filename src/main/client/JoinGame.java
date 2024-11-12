package main.client;

import network.Client;

/**
 * Main class for the client side of the game. Connects to the server and runs the game loop.
 * Due to the generic nature of the client, this can be used for any game that uses turns and text input.
 */
public class JoinGame {
    public static void main(String[] args) throws Exception {
        if(args.length > 1) {
            throw new IllegalArgumentException("Usage: java JoinGame <ip address>");
        }
        else if(args.length == 0) {
            args = new String[]{"127.0.0.1"};
        }
        Client client = new Client(args[0]);
        client.run();
    }
}
