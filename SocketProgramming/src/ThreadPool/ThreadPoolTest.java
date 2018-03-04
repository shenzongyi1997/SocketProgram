package ThreadPool;

import ServerProgram.ServerReceiveFile;
import ServerProgram.ServerWithHeartbeatDIY;

import java.io.*;
import java.math.RoundingMode;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/12/9.
 */
public class ThreadPoolTest extends ServerSocket{
    static ExecutorService pool = Executors.newFixedThreadPool(10);
    ThreadPoolTest() throws Exception {
        super();
    }
    private static final int SERVER_PORT = 14413; // 服务端端口
    private static final int SERVER_HEARTBEAT_PORT = 12001;
    private static DecimalFormat df = null;//数字格式
    String pathname="F://download";//接受到的东西就放到这个目录下
    static {
        // 设置数字格式，保留一位有效小数
        df = new DecimalFormat("#0.0");
        df.setRoundingMode(RoundingMode.HALF_UP);
        df.setMinimumFractionDigits(1);
        df.setMaximumFractionDigits(1);
    }//设置数据格式
    public class Task implements Runnable {

        private Socket socket;

        private DataInputStream dis;

        private FileOutputStream fos;

        public Task(Socket socket) {
            this.socket = socket;
        }
        @Override
        public void run() {
            try {
                dis = new DataInputStream(socket.getInputStream());

                // 文件名和长度
                String fileName = dis.readUTF();
                long fileLength = dis.readLong();
                File directory = new File(pathname);
                if(!directory.exists()) {
                    directory.mkdir();
                }
                File file = new File(directory.getAbsolutePath() + File.separatorChar + fileName);
                fos = new FileOutputStream(file);

                // 开始接收文件
                byte[] bytes = new byte[1024];
                int length = 0;
                while((length = dis.read(bytes, 0, bytes.length)) != -1) {
                    fos.write(bytes, 0, length);
                    fos.flush();
                }
                System.out.println("文件接收成功!  " + fileName + "   大小是" + getFormatFileSize(fileLength) );
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if(fos != null)
                        fos.close();
                    if(dis != null)
                        dis.close();
                    socket.close();
                } catch (Exception e) {}
            }
        }
    }
    String getFormatFileSize(long length) {
        double size = ((double) length) / (1 << 30);
        System.out.println("我就输出个东西，啥也不干");
        if(size >= 1) {
            return df.format(size) + "GB";
        }
        size = ((double) length) / (1 << 20);
        if(size >= 1) {
            return df.format(size) + "MB";
        }
        size = ((double) length) / (1 << 10);
        if(size >= 1) {
            return df.format(size) + "KB";
        }
        return length + "B";
    }
    class ClientWorker implements Runnable {
        // 为连入的客户端打开的套接口
        private Socket socket;
        //worker名称
        private String name=null;
        // 最后一次心跳时间
        private Date lastHeartbeat = new Date();
        ClientWorker(Socket socket) {
            this.socket = socket;
        }
        public Date getLastHeartbeat() {
            return lastHeartbeat;
        }
        public void setLastHeartbeat(Date lastHeartbeat) {
            this.lastHeartbeat = lastHeartbeat;
        }
        @Override
        public String toString(){
            return name;
        }
        public void close(){
            try {
                System.out.println(this+" close...");
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                // 打开sock输入输出流
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                while(true){
                    // 读取来自客户端的字符串并输出
                    String msg = reader.readLine();
                    if (msg==null){
                        break;
                    }
                    else if (name==null){
                        name = msg;
                    }
                    else if (msg.equals("I am OK.")){
                        //更新最后一次心跳
                        this.setLastHeartbeat(new Date());
                    }
                    System.out.println(msg+" ["+(new Date())+"]");
                }
            } catch (Exception e) {
                System.out.println("该客户端已经关闭！");
            } finally {
                try {
                    //当前worker退出
                    this.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    void work(ServerSocket server,ServerSocket serverSocket)
    {
        int a = 0;
        try {
            while (true) {


                    Socket socket2 = serverSocket.accept();
                    ClientWorker clientWorker = new ClientWorker(socket2);
                    pool.submit(clientWorker);


                    Socket socket1 = server.accept();
                    Task task = new Task(socket1);
                    pool.submit(task);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = null;
            serverSocket = new ServerSocket(SERVER_HEARTBEAT_PORT);
            ServerSocket server = new ServerSocket(SERVER_PORT); // 启动服务端
            ThreadPoolTest threadPoolTest=new ThreadPoolTest();
            threadPoolTest.work(server,serverSocket);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
