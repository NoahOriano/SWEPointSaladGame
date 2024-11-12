import network.Client;
import network.Server;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TestServerClientConnection {
    @Test
    public void testServerClientConnection() {
        // First create a client and expect an error to be thrown since the server is not running
        Assert.assertThrows(IOException.class, () -> new Client("127.0.0.1", 4048));
        // Uses a different port than gameplay to avoid conflicts
        Server server = new Server(4048, 1, 2, null, null);
        assertEquals(0, server.getAcceptedSockets().size());
        // Create a new thread to run the server
        // When run the thread will call startServer() which will add sockets to the acceptedSockets list
        Thread thread = new Thread(server::startServer);
        thread.start();
        // Create a client socket to connect to the server
        // This will add a socket to the acceptedSockets list
        Client client;
        // Try to connect to the server
        System.out.println("Attempting to connect to server...");
        boolean connected = false;
        int attempts = 0;
        while (!connected && attempts < 1000) {
            try {
                // Construct a client with the server's IP address and port
                // Client constructor will throw an exception if it cannot connect
                client = new Client("127.0.0.1", 4048);
                connected = true;
            } catch (IOException e) {
                // Do nothing
            }
            attempts++;
        }
        System.out.println("Connected to server in " + attempts + " attempts.");
    }
}