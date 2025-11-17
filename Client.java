import java.util.*;
import java.io.*;
import java.net.*;
import java.time.*;

public class Client {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    public Client(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
    
    public Socket getSocket(){
        return(this.socket);
    }
    public void handshake(){
        out.println("12345");
        out.flush();

    }
    public String request(String msg)throws IOException{
        out.println(msg);
        out.flush();
        return in.readLine();
    }
    public void disconnect() throws IOException{
        in.close();
        out.close();
        socket.close();
    }


}
