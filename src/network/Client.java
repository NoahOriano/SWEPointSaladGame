package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * Client class for the game networking. Connects to the server and sends and receives messages.
 * The run method will run the game loop, receiving messages from the server and sending responses.
 * Due to the text based nature of the game, this class is usable for any game that uses turns and text input.
 */
public class Client {
    ObjectOutputStream outToServer;
    ObjectInputStream inFromServer;

    public Client(String ipAddress) throws IOException {
        //Connect to server
        Socket socket = new Socket(ipAddress, 2048);
        outToServer = new ObjectOutputStream(socket.getOutputStream());
        inFromServer = new ObjectInputStream(socket.getInputStream());
    }

    public Client(String ipAddress, int port) throws IOException {
        //Connect to server
        Socket socket = new Socket(ipAddress, port);
        outToServer = new ObjectOutputStream(socket.getOutputStream());
        inFromServer = new ObjectInputStream(socket.getInputStream());
    }

    public void run() throws Exception {
        Message nextMessage = (Message) inFromServer.readObject();
        Scanner scan = new Scanner(System.in);
        while (!nextMessage.gameOver()) {
            nextMessage = (Message) inFromServer.readObject();
            System.out.println(nextMessage.message());
            if (nextMessage.actionExpected()) {
                outToServer.writeObject(new Message(scan.nextLine(), Message.MESSAGE_TYPE.ACTION_REQUESTED));
                outToServer.flush();
            }
        }
        scan.close();
    }
}
