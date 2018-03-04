package ServerProgram;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Administrator on 2017/11/5.
 */
public class Server {
    public static void main(String args[]){
        int portNumber=13134;
        try {
            ServerSocket serverSocket=new ServerSocket(portNumber);
            Socket client=serverSocket.accept();
            System.out.println("客户端已经连上了，他的IP地址是："+client.getInetAddress());
            BufferedReader receiver=new BufferedReader(new InputStreamReader(client.getInputStream()));//得到客户端的输入流，
            String inputLine = null;
            while ((inputLine = receiver.readLine()) != null) {
                System.out.println("recv from client : " + inputLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
