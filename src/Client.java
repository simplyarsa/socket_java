import java.io.*;
import java.net.*;
import java.sql.Timestamp;

public class Client {

    public static StringBuffer encrypt(String text, int s)
    {
        StringBuffer result= new StringBuffer();
 
        for (int i=0; i<text.length(); i++)
        {
            if (Character.isUpperCase(text.charAt(i)))
            {
                char ch = (char)(((int)text.charAt(i) +
                                  s - 65) % 26 + 65);
                result.append(ch);
            }
            else
            {
                char ch = (char)(((int)text.charAt(i) +
                                  s - 97) % 26 + 97);
                result.append(ch);
            }
        }
        return result;
    }
    public static void main(String[] args) {
        String serverAddress = "localhost"; // Change this to the server's address
        int serverPort = 5000; // Change this to the server's port

        try (Socket socket = new Socket(serverAddress, serverPort);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in)) ) {
            String response;
            String clientNum = in.readLine();
            System.out.println("Client No: " + clientNum);
            boolean flag = true;
           while(true){
            while(!(response = in.readLine()).equals(clientNum)){
            while(!(response.equals("_"))) {
                System.out.println(response);
                response =  in.readLine();
                 if(response.equals(clientNum)){
                break;
            }
            }
            if(response.equals(clientNum)){
                break;
            }
            }
            if(response.equals(clientNum)) {
            while(flag) {
                System.out.print("Enter a number: ");
                String numberStr = userInput.readLine();
                out.println(numberStr);

                response = in.readLine();
                if( response.equals("##123##")) {
                    flag = false;
                    break;
                }
                
                System.out.println("Server response: " + response);
            }
            String message = "";
            while(!(response = in.readLine()).equals("_")) {
                System.out.println(response);
            }
           
                System.out.println("Enter client no. : ");
                message = userInput.readLine();
                out.println(message);
                response = in.readLine();
                System.out.println(response);
                message = userInput.readLine();
                String msg = encrypt(message, 3).toString();
                 Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                msg = msg + "time :" + timestamp;
                out.println(msg);
            
        }
    }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}