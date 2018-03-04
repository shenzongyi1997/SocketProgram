package ClientProgram;

/**
 * Created by Administrator on 2017/11/5.
 */
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
public class ClientSendFile extends Socket {
    private static final String SERVER_IP = "127.0.0.1"; // 服务端IP
    private static final int SERVER_PORT = 14413; // 服务端端口
    private Socket client;//客户端的socket
    private FileInputStream fis;//文件输入流
    private DataOutputStream dos;//数据输出流
    String pathName="F://upload/hello.txt";//传输的文件的路径，这儿我传输了一个视频文件
    /**
     * 直接通过构造函数连上服务器
     * 构造函数<br/>
     * 与服务器建立连接
     * @throws Exception
     */
    ClientSendFile() throws Exception {
        super(SERVER_IP, SERVER_PORT);
        this.client = this;
        System.out.println("客户端的端口是" + client.getLocalPort() + "，已经成功连接服务端");
    }
    /**
     * 向服务端传输文件
     * @throws Exception
     */
    public void sendFile() throws Exception {
        try {
            File file = new File(pathName);
            if(file.exists()) {
                fis = new FileInputStream(file);
                dos = new DataOutputStream(client.getOutputStream());
                // 文件名和长度
                dos.writeUTF(file.getName());
                dos.flush();
                dos.writeLong(file.length());
                dos.flush();

                // 开始传输文件
                System.out.println("开始传输");
                byte[] bytes = new byte[1024];
                int length = 0;
                long progress = 0;
                long temp=0;
                long ini=0;
                while((length = fis.read(bytes, 0, bytes.length)) != -1) {
                    dos.write(bytes, 0, length);
                    dos.flush();
                    progress += length;
                    temp=100*progress/file.length();
                    if(temp!=ini)
                    {
                        ini=temp;
                        System.out.print("    " + ini + "%    ");
                    }

                }
                System.out.println();
                System.out.println("传输完成！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(fis != null)
                fis.close();
            if(dos != null)
                dos.close();
            client.close();
        }
    }
    /**
     * 入口
     * @param args
     */
    public static void main(String[] args) {
        try {
            ClientSendFile client = new ClientSendFile(); // 启动客户端连
            client.sendFile(); // 传输文件
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}