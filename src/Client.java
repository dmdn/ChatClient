import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class Client {
    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;

    private String userStatus;

    //Prompts the user of the nickname and organizes the exchange of messages with the server
    public Client() {
        Scanner scan = new Scanner(System.in);

        try {
            //Connect to the server and receive streams (in and out) for sending messages
            socket = new Socket(Const.IP, Const.PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Enter your nickname:");
            out.println(scan.nextLine());

            setUserStatus();

            //Start outputting all incoming messages to the console
            Resender resend = new Resender();
            resend.start();

            //While the user does not enter "exit", we send everything that was entered from the console to the server
            String str = "";
            while (!str.equals("exit")) {
                str = scan.nextLine() + " [" + userStatus + "]";
                //Sends data to the server
                out.println(str);
            }
            resend.setStop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }


    //Closes the input and output streams and socket
    private void close() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            System.err.println("The streams were not closed!");
        }
    }


    // The class in a separate thread forwards all messages from the server to the console
    // Works until the setStop () method is called
    private class Resender extends Thread {

        private boolean stoped;

        //Stop sending messages
        public void setStop() {
            stoped = true;
        }

        //Reads all messages from the server and prints them to the console.
        //Stopped by calling the setStop () method until the thread is stopped, it simply reads all the server messages and outputs them to the console.
        @Override
        public void run() {
            try {
                while (!stoped) {
                    //Reads the received data
                    String str = in.readLine();
                    System.out.println(str);
                }
            } catch (IOException e) {
                System.err.println("There was a problem retrieving the message.");
                e.printStackTrace();
            }
        }
    }


    private String setUserStatus() {
        Scanner in = new Scanner(System.in);
        System.out.println("Choose your status? (S(sleep)/E(eat)/W(work))");
        while (true) {
            char answer = Character.toLowerCase(in.nextLine().charAt(0));
            if (answer == 's') {
                return userStatus = "sleep";
            } else if (answer == 'e') {
                return userStatus = "eat";
            } else if (answer == 'w') {
                return userStatus = "work";
            } else {
                System.out.println("Incorrect input. Repeat.");
            }
        }
    }




}
