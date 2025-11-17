import java.io.*;
import java.net.*;

public class Client {
    //creating a new socket from javas socket class
    private Socket socket;
    //initializing in to be a BufferedReader to read from inputstreamreader
    private BufferedReader in;
    //initializing out to be a printwriter to write to outputstream of the socket we will be operating on.
    private PrintWriter out;

    // need a construct that takes in a host, like local host, and some port number
    //needs to throw an Ioexception for any potential input output stream issues we have.
    public Client(String host, int port) throws IOException {
        //socket will be connected on whatever host and port we are using
        socket = new Socket(host, port);
        //out will be a type of printwriter that will write to sockets output stream, autoflush is true to make sure there is no buffers.
        out = new PrintWriter(socket.getOutputStream(), true);
        // in is a bufferedreader that will read from sockets inputstream coming from the port and host we connect to.
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
    //handshake will write 12345 to say that we are ok to be on this socket.
    public void handshake() throws IOException {
        out.println("12345");
    }

    //write out the message to the socket, and read the response from the socket.
    public String request(String message) throws IOException {
        out.println(message);
        return in.readLine();
    }
    //returns whatever socket we are on
    public Socket getSocket() {
        return socket;
    }
    //closes in, out, and our socket connection
    //checks if in out and socket are null because if they are null it can throw a nullpointerexception.
    public void disconnect() throws IOException {
        if (in != null) in.close();
        if (out != null) out.close();
        if (socket != null) socket.close();
    }
}