package ClientProgram;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class ClientHeartBeatDIY {
	static class HeartbeatWorker implements Runnable {
		Socket clientSocket;
		HeartbeatWorker(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}

		@Override
		public void run() {
			try {
				PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
				//while(true){
					//Thread.sleep(100000); //sleep 10秒
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						writer.print("I am OK.");
					}
				},0,100000);

				//}
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
	}
	
	public static void main(String[] args) throws IOException {
		String hostName = "localhost";
		int portNumber = 12001;
		Socket clientSocket = null;
		try {
			clientSocket = new Socket(hostName, portNumber);
			//打开sock输出流和console标准输入流
			PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader stdIn = new BufferedReader(new InputStreamReader(
					System.in));
			//启动心跳线程(改进：workerThread -> Timer  ->ScheduledExecutorService)
			(new Thread(new HeartbeatWorker(clientSocket))).start();
			for(;;){
				String msg = stdIn.readLine();
				if (msg.toLowerCase().equals("exit"))
					break;
				writer.println(msg);
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
}