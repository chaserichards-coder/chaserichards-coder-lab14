import java.util.*;
import java.io.*;
import java.net.*;
import java.time.*;

public class Server {
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();
    private List<LocalDateTime> connectedTimes = new ArrayList<>();

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void serve(int n) {
        for (int i = 0; i < n; i++) {
            try {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket);
                clients.add(handler);
                handler.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {
        try {
            for (ClientHandler c : clients) {
                c.interrupt();
                c.close();
            }
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<LocalDateTime> getConnectedTimes() {
        synchronized (connectedTimes) {
            ArrayList<LocalDateTime> copy = new ArrayList<>(connectedTimes);
            Collections.sort(copy);
            return copy;
        }
    }

    private class ClientHandler extends Thread {
        private Socket sock;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket sock) {
            this.sock = sock;
            try {
                sock.setSoTimeout(5000);
                this.out = new PrintWriter(sock.getOutputStream(), true);
                this.in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                String handshake = in.readLine();

                if (!"12345".equals(handshake)) {
                    out.println("couldn't handshake");
                    return;
                }

                synchronized (connectedTimes) {
                    connectedTimes.add(LocalDateTime.now());
                }

                String msg;
                while (!Thread.interrupted() && (msg = in.readLine()) != null) {
                    try {
                        long num = Long.parseLong(msg);

                        if (num > Integer.MAX_VALUE || num < 1) {
                            out.println("There was an exception on the server");
                        } else {
                            long factors = countFactors(num);
                            out.println("The number " + num + " has " + factors + " factors");
                        }
                    } catch (NumberFormatException e) {
                        out.println("There was an exception on the server");
                    }
                }
            } catch (java.net.SocketTimeoutException e) {
                // Timeout - exit gracefully
            } catch (Exception e) {
                // Client disconnected
            } finally {
                close();
            }
        }

        private long countFactors(long n) {
            long count = 0;
            for (long i = 1; i * i <= n; i++) {
                if (n % i == 0) {
                    count += (i * i == n) ? 1 : 2;
                }
            }
            return count;
        }

        public void close() {
            try {
                if (sock != null) sock.close();
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (Exception ignored) {}
        }
    }
}