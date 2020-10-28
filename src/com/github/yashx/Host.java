package com.github.yashx;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

abstract class Host {
    protected final Socket socket;
    protected final String talkingTo;
    protected final DataInputStream dataInput;
    protected final DataOutputStream dataOutput;

    public Host(Socket socket, String talkingTo) throws IOException {
        this.socket = socket;
        this.talkingTo = talkingTo;
        this.dataInput = new DataInputStream(socket.getInputStream());
        this.dataOutput = new DataOutputStream(socket.getOutputStream());
    }

    public void send(String message) throws IOException {
        dataOutput.writeUTF(message);
        dataOutput.flush();
    }

    public String read() throws IOException {
        String message = null;
        try {
            message = dataInput.readUTF();
        }
        catch (EOFException e){
            System.exit(0);
        }
        return message;
    }

    public String getTalkingTo() {
        return talkingTo;
    }

    public void close() throws IOException {
        System.out.print("Closing Node");
        dataInput.close();
        dataOutput.close();
        socket.close();
    }
}

class Client extends Host{
    public Client(String ip, int port) throws IOException {
        super(new Socket(ip,port), "Server");
    }
}

class Server extends Host{
    public Server(int port) throws IOException {
        super(getSocketFromServerPort(port), "Client");
    }

    private static Socket getSocketFromServerPort(int port) throws IOException {
        System.out.println("Waiting for client ....");
        return new ServerSocket(port).accept();
    }
}
