package network;

import game.bot.Bot;
import game.state.State;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Server class for creating server on a specific port. Creates a server and handles bot logic.
 * The server will handle the game loop for the given game state and use the given bot to determine bot actions.
 * The server class is designed to be used for any game that is turn based and requires text input.
 */
public class Server {
    private ServerSocket serverSocket;
    private final List<Socket> acceptedSockets = new ArrayList<>();
    private final List<ObjectOutputStream> acceptedOutputStreams = new ArrayList<>();
    private final List<ObjectInputStream> acceptedInputStreams = new ArrayList<>();
    private final Bot botBehavior;
    private final int port;
    private final int numClients;
    private final int numBots;
    private final State game;

    public Server(int port, int numClients, int numBots, State game, Bot botBehavior) {
        this.botBehavior = botBehavior;
        this.port = port;
        this.numClients = numClients;
        this.numBots = numBots;
        this.game = game;
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(port);
            // Accept all clients
            for (int i = 0; i < numClients; i++) {
                Socket clientSocket = serverSocket.accept();
                acceptedSockets.add(clientSocket);
            }
            for (int i = 0; i < numBots; i++) {
                acceptedSockets.add(null);
            }
            // Shuffle the sockets so that the order of players is random
            Collections.shuffle(acceptedSockets);
            for (Socket acceptedSocket : acceptedSockets) {
                if (acceptedSocket == null) {
                    acceptedOutputStreams.add(null);
                    acceptedInputStreams.add(null);
                } else {
                    acceptedOutputStreams.add(new ObjectOutputStream(acceptedSocket.getOutputStream()));
                    acceptedInputStreams.add(new ObjectInputStream(acceptedSocket.getInputStream()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts the game loop and handles player actions
     * This functions by getting and sending messages to/from game state.
     * Works be for any game that is turned based with single player turns
     */
    public void startGame() {
        // Main game loop
        while (!game.isGameOver()) {
            Message generalMessage = game.getPublicMessage();
            // Sends the game state to each player so they can plan
            if (generalMessage != null) {
                sendToAllPlayers(generalMessage);
            }
            // Then, finds the player from who action is expected and awaits their response
            for (int i = 0; i < numClients + numBots; i++) {
                Message playerMessage = game.getPlayerMessage(i);
                if (playerMessage != null) {
                    sendToPlayer(i, playerMessage);
                    if (playerMessage.actionExpected()) {
                        String response = awaitPlayerResponse(i);
                        // If the player action is invalid, await a new response
                        while (!game.handlePlayerAction(i, response)) {
                            playerMessage = game.getPlayerMessage(i);
                            sendToPlayer(i, playerMessage);
                            response = awaitPlayerResponse(i);
                        }
                    }
                }
            }
        }

        // Send final message to all players
        Message generalMessage = game.getPublicMessage();
        if (generalMessage != null) {
            sendToAllPlayers(generalMessage);
        }
        for (int i = 0; i < numClients + numBots; i++) {
            Message playerMessage = game.getPlayerMessage(i);
            if (playerMessage != null) {
                sendToPlayer(i, playerMessage);
            }
        }
    }

    public List<Socket> getAcceptedSockets() {
        return acceptedSockets;
    }

    /**
     * Disconnects all clients from the server by closing the sockets
     */
    public void disconnectAllClients() {
        System.out.println("Disconnecting connected Clients");
        for (Socket socket : acceptedSockets) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        acceptedSockets.clear();
    }

    /**
     * Sends a message to a specific player. Should only be called for non-bot players.
     */
    public void sendToPlayer(int playerIndex, Message message) {
        if (acceptedSockets.get(playerIndex) != null) {
            try {
                ObjectOutputStream stream = acceptedOutputStreams.get(playerIndex);
                stream.writeObject(message);
                stream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Waits for a message from a specific player. Returns response for both bots and players the same way.
     */
    public String awaitPlayerResponse(int playerIndex) {
        if (acceptedInputStreams.get(playerIndex) == null || acceptedOutputStreams.get(playerIndex) == null) {
            return botBehavior.getAction(playerIndex);
        } else {
            try {
                Message response = (Message) acceptedInputStreams.get(playerIndex).readObject();
                return response.message();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public void sendToAllPlayers(Message message) {
        for (int i = 0; i < acceptedSockets.size(); i++) {
            if (acceptedSockets.get(i) != null) {
                sendToPlayer(i, message);
            }
        }
    }
}
