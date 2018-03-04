package ClientProgram;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Administrator on 2017/11/5.
 */
public class Client {
    public static void main(String args[])
    {
        int portNumber=13134;
        Socket socket=null;
        try {
           socket=new Socket("localhost",portNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            PrintWriter writer=new PrintWriter(socket.getOutputStream(),true);//客户端自己的输出流
            writer.print("hello!Nice to meet you!");
            writer.print(System.in.read());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
