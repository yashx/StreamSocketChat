package com.github.yashx;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Choose one:");
        System.out.println("1. Server");
        System.out.println("2. Client");
        System.out.print("Input: ");

        int choice = Integer.parseInt(scanner.nextLine());
        try {
            Host host = getHostForChoice(choice);
            readMessagesToHost(host);
            sendMessagesFromHost(host);
        }

        catch (IOException e){
            e.printStackTrace();
        }
    }

    private static Host getHostForChoice(int choice) throws IOException {
        switch (choice){
            case 1:
                System.out.print("Enter port: ");
                int serverPort = Integer.parseInt(scanner.nextLine());
                return new Server(serverPort);
            case 2:
                System.out.print("Enter ip: ");
                String ip = scanner.nextLine();
                System.out.print("Enter port: ");
                int clientPort = Integer.parseInt(scanner.nextLine());
                return new Client(ip,clientPort);
        }
        System.out.println("Not a valid choice");
        System.exit(-1);
        return null;
    }

    private static void readMessagesToHost(Host host){
        Thread readerThread = new Thread(){
            @Override
            public void run() {
                while (true){
                    try {
                        String incomingMessage = host.read();
                        String toShow = String.format("\n%s: %s",host.getTalkingTo(),incomingMessage);
                        System.out.println(toShow);
                        if(incomingMessage.equalsIgnoreCase("exit")){
                            System.exit(0);
                        }
                        System.out.print("You: ");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        readerThread.start();
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                readerThread.stop();
                try {
                    host.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void sendMessagesFromHost(Host host) throws IOException {
        System.out.println("Write \"exit\" to quit");
        String str = "";
        do{
            System.out.print("You: ");
            str = scanner.nextLine();
            host.send(str);
        }while (!str.equalsIgnoreCase("exit"));
    }
}
