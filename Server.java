import java.util.*;
import java.io.*;
import java.net.*;
import java.time.*;

public class Server {
    //serversocket is used by a server to listen for connections from incoming clients
    private ServerSocket serverSocket;
    //need a list of clients
    private List<ClientHandler> clients = new ArrayList<>();
    //using this because we need to know the date and time from the system clock, and for times when each clients connect.
    private List<LocalDateTime> connectedTimes = new ArrayList<>();
    //need a server constructor, takes in port because it needs to know what port on the local machine to open and focus on.
    //serversocket(port) connects to local network interfaces
    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    //serve takes in a number of clients it is expecting to serve in the run.
    
    public void serve(int n) {
        for (int i = 0; i < n; i++) {
            try {
                //for every client, accept returns a socket representing the client connection.
                Socket clientSocket = serverSocket.accept();
                //A new thread is created for the client, it handles the handshake, reading client requests, the number factorization, and sending responses back.
                ClientHandler handler = new ClientHandler(clientSocket);
                //this keeps track of the thread so the system can disconnect or interrupt the clients
                clients.add(handler);
                //we start the handler so it runs along with the server.
                //it allows the server to go back and accept the next client, instead of waiting for this one to finish.
                handler.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {
        try {
            //for everything in client handler that are clients, interrupt and then close.
            for (ClientHandler c : clients) {
                c.interrupt();
                c.close();
            }
            // we then close the serversocket.
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //this method is used to get the connected times of each client.
    public ArrayList<LocalDateTime> getConnectedTimes() {
        synchronized (connectedTimes) {
            ArrayList<LocalDateTime> copy = new ArrayList<>(connectedTimes);
            Collections.sort(copy);
            return copy;
        }
    }
    //clienthandler is what we are using for the list.
    private class ClientHandler extends Thread {
        //new socket sock will be used
        private Socket sock;
        //bufferedreader to read inputs
        private BufferedReader in;
        //printwriter to write to outstream.
        private PrintWriter out;
        //constructor for clienthandler, for every client in clienthandler will have a timeout of 5000 to make outputs easier to see
        public ClientHandler(Socket sock) {
            this.sock = sock;
            try {
                sock.setSoTimeout(5000);
                // sets this specific out to a new socket called sock to write out to outputstream
                this.out = new PrintWriter(sock.getOutputStream(), true);
                //this in will be a bufferedreader to read inputs from socket sock.
                this.in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // we read the input from the bufferedreader on sock socket.
        public void run() {
            try {
                String handshake = in.readLine();
                // if we dont read the correct handshake from the client we say that we couldnt handshake
                if (!"12345".equals(handshake)) {
                    out.println("couldn't handshake");
                    return;
                }
                //we add the new localdatetime added to the connectedtimes list.
                synchronized (connectedTimes) {
                    connectedTimes.add(LocalDateTime.now());
                }

                //checks if the thread was not interrupted and the current message in the reader isnt null
                String msg;
                while (!Thread.interrupted() && (msg = in.readLine()) != null) {
                    try {
                        // we then parse the msg.
                        long num = Long.parseLong(msg);
                        //if num is invalid then we say there was an exception.
                        if (num > Integer.MAX_VALUE || num < 1) {
                            out.println("There was an exception on the server");
                            //else we will run countfactors on num
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
        // calculate number of factors for a given number
        private long countFactors(long n) {
            long count = 0;
            for (long i = 1; i * i <= n; i++) {
                if (n % i == 0) {
                    count += (i * i == n) ? 1 : 2;
                }
            }
            return count;
        }
        // closing the socket, input, and output.
        public void close() {
            try {
                if (sock != null) sock.close();
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (Exception ignored) {}
        }
    }
}