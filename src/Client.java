import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable {
    private static Socket client;
    private static BufferedReader in;
    private static PrintWriter out;
    private static boolean done;

    private static final String localhost = "127.0.01";
    private static final int port = 9_999;

    @Override
    public void run() {
        try {
            client = new Socket(localhost, port);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            InputHandler inHandler = new InputHandler();
            Thread t = new Thread(inHandler);
            t.start();

            String inMessage;
            while ((inMessage = in.readLine()) != null) {
                System.out.println(inMessage);
            }

        } catch (IOException e) {
            shutdown();
        }
    }

    public static void shutdown() {
        done = true;
        try {
            in.close();
            out.close();
            if (!client.isClosed()) {
                client.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class InputHandler implements Runnable {
        @Override
        public void run() {
            BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
            while (!done) {
                String message = null;
                try {
                    message = inReader.readLine();
                    out.println(message);
                    if (message.equals("/quit")) {
                        inReader.close();
                        shutdown();
                    }
                } catch (IOException e) {
                    shutdown();
                }
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }
}
