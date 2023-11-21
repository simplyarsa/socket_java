import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.util.ArrayList;

public class Server {
    protected static ArrayList<Socket> clientSockets = new ArrayList<>();

    public static void main(String[] args) {
        int port = 5000;
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientSockets.add(clientSocket);
                System.out.println("Client connected: " + clientSocket);
                if(clientSockets.size() ==1 ){
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    out.println(1);
                }
                // Handle each client in a new thread
                Thread clientHandler = new Thread(new ClientHandler(clientSocket,clientSockets.size()));
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null)
                    serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Broadcast a message to all connected clients
    public static void broadcast(String message) {
        for (Socket clientSocket : clientSockets) {
            try {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void listClients( Socket client ) {
        try{
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            int pos = 1;
                for (Socket clientSocket : clientSockets) {
                    out.println(pos + ". " + clientSocket);
                    pos++;
                }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendToClients( Socket client, int[] arr, String message) {
        try{
            for (int pos : arr) {
                PrintWriter out = new PrintWriter(clientSockets.get(pos-1).getOutputStream(), true);
                out.println(message);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

     public static void fetchNextClient( Socket client, int clientNo) {
        try{
               int  cNo = clientNo;
                PrintWriter out = new PrintWriter(clientSockets.get((cNo-1)%clientSockets.size()).getOutputStream(), true);
                out.println(cNo);
                System.out.println("Allowing "+ cNo + "to send message");
            
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private boolean enabled;
    private int clientNo;
 
    public ClientHandler(Socket clientSocket,int clientNum) {
        this.clientSocket = clientSocket;// Add the client socket to the list
        this.enabled = false;
        this.clientNo = clientNum;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            // sending the clienNo to let the client know their number
            out.println(clientNo);

            String inputLine;
          
            while (!enabled && (inputLine = in.readLine()) != null) {
                int number = Integer.parseInt(inputLine);
                if (isPrime(number)) {
                    System.out.println("Received a prime number: " + number);
                    // out.println("Prime number: " + number);
                    out.println("##123##");
                    enabled = true;
                } else {
                    System.out.println("Received a non prime number: " + number);
                    out.println("Non Prime number: " + number);
                }
            }

            out.println("Select the name of clients: ");
            Server.listClients(clientSocket);
            out.println("Enter the output with a blank. Eg. 1 2 5 ...");
            out.println("_");
            
            while(enabled) {
                String clientNum = in.readLine();
                System.out.println(clientNum);
                String[] parts = clientNum.split(" ");
                int[] pos = new int[parts.length];
                for(int i = 0; i<parts.length; i++) {
                    pos[i] = Integer.parseInt(parts[i]);
                }
                out.println("Enter the message");
                String msg = in.readLine();
                System.out.println("Ecrypted Msg : "+ msg);
                String s[] =  msg.split("time");
                String decString = decrypt(s[0], 3).toString();

                if(checkPalindrome(decString)){
                    decString += " is a palindrome";
                }
                else{
                    decString += " is not a palindrome";
                }
                System.out.println("Broadcast the message : \'" + decString + "\' to " + clientNum);
                Server.sendToClients(clientSocket, pos, decString + " time :" + s[1]);
                Server.fetchNextClient(clientSocket,  clientNo+1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
                Server.clientSockets.remove(clientSocket);
                System.out.println("Client disconnected: " + clientSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static StringBuffer decrypt(String text, int s)
    {
        StringBuffer result= new StringBuffer();
 
        for (int i=0; i<text.length(); i++)
        {
            if (Character.isUpperCase(text.charAt(i)))
            {
                char ch = (char)(((int)text.charAt(i) -
                                  s - 65) % 26 + 65);
                result.append(ch);
            }
            else
            {
                char ch = (char)(((int)text.charAt(i) -
                                  s - 97) % 26 + 97);
                result.append(ch);
            }
        }
        return result;
    }
    private boolean checkPalindrome(String s){
        int n = s.length();
        for(int i = 0; i < n/2; i++){
            if(s.charAt(i) != s.charAt(n-i-1)){
                return false;
            }
        }
        return true;
    }
    private boolean isPrime(int number) {
        if (number <= 1) {
            return false;
        }
        if (number <= 3) {
            return true;
        }
        if (number % 2 == 0 || number % 3 == 0) {
            return false;
        }
        for (int i = 5; i * i <= number; i += 6) {
            if (number % i == 0 || number % (i + 2) == 0) {
                return false;
            }
        }
        return true;
    }
}

/*
 * Algorithm for Caesar Cipher: 
*Input: Choose a shift value between 1 and 25.
*Write down the alphabet in order from A to Z.
*Create a new alphabet by shifting each letter of the original alphabet by the shift value. For example, if the shift value is 3, the new alphabet would be:
*A B C D E F G H I J K L M N O P Q R S T U V W X Y Z
*D E F G H I J K L M N O P Q R S T U V W X Y Z A B C
*Replace each letter of the message with the corresponding letter from the new alphabet. For example, if the shift value is 3, the word “hello” would become “khoor”.
*To decrypt the message, shift each letter back by the same amount. For example, if the shift value is 3, the encrypted message “khoor” would become “hello”.
 */