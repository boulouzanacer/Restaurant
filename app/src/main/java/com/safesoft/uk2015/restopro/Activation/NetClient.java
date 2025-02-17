package com.safesoft.uk2015.restopro.Activation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by UK2015 on 05/12/2016.
 */

public class NetClient {
    /**
     * Maximum size of buffer
     */
    public static final int BUFFER_SIZE = 2048;
    private Socket socket = null;
    private PrintWriter out = null;
    private BufferedReader in = null;

    private String host = null;
    private String macAddress = null;
    private int port = 7999;


    /**
     * Constructor with Host, Port and MAC Address
     * @param host
     * @param port
     */
    public NetClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private void connectWithServer() {
        try {
            if (socket == null) {
                SocketAddress sockaddr = new InetSocketAddress(host, port);
                Socket socket = new Socket();
                socket.connect(sockaddr, 1500);

              //  socket = new Socket(host, port);
                out = new PrintWriter(socket.getOutputStream());
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disConnectWithServer() {
        if (socket != null) {
            if (socket.isConnected()) {
                try {
                    in.close();
                    out.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendDataWithString(String message) {
        if (message != null) {
            connectWithServer();
            out.write(message);
            out.flush();
        }
    }

    public String receiveDataFromServer() {
        try {

            String message = "";
            int charsRead = 0;
            char[] buffer = new char[BUFFER_SIZE];
            // charsRead = in.read(buffer);
            if ((charsRead=in.read(buffer)) != -1) {
                message += new String(buffer).substring(0, charsRead);
            }
            /*
            while ((charsRead = in.read(buffer)) != 0) {
                message += new String(buffer).substring(0, charsRead);
            }*/

            disConnectWithServer(); // disconnect server
            return message;
        } catch (IOException e) {
            return "Error receiving response:  " + e.getMessage();
        }
    }
}
