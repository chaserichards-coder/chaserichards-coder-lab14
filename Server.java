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
public void serve(int clients){

}
public void disconnect(){

}
public List<LocalDateTime> getConnectedTimes() {
    return ConnectedTimes;
}
private class ClientHandler{

}
}
