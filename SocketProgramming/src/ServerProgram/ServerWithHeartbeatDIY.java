package ServerProgram;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


public class ServerWithHeartbeatDIY {

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

	class HeartbeatMonitor implements Runnable {
		//所有连入的客户端worker
		private Vector<ClientWorker> workers = null;//这个Vector就用来存放所有的ClientWorker,事实上对于每一个客户端来说都新建一个线程来管理

		HeartbeatMonitor(Vector<ClientWorker> workers) {
			this.workers = workers;
		}
		
		@Override
		public void run() {
			try {
				//while(true){
					try {
						Iterator<ClientWorker> iterWorker = workers.iterator();
						while(iterWorker.hasNext()){
							ClientWorker worker = iterWorker.next();
							long diffTime = (new Date()).getTime() - worker.getLastHeartbeat().getTime();//计算有多长时间没有收到客户端的信息了
							if (diffTime>=20000){
								System.out.println(worker+" heartbeat timeout...");
								worker.close();
								iterWorker.remove();//就删除自己
							}
						}
						//Thread.sleep(2000);//这里是原来的，让线程自己休眠
					} catch (Exception e) {
						e.printStackTrace();
					}
				//}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println("服务器端已经启动！");
		ServerWithHeartbeatDIY server = new ServerWithHeartbeatDIY();
		server.start();
	}

	void start() throws IOException {
		int portNumber = 12001;
		ServerSocket serverSocket = null;
		Timer timer = new Timer();
		try {
			serverSocket = new ServerSocket(portNumber);
			Vector<ClientWorker> workers = new Vector<ClientWorker>();
			(new Thread(new HeartbeatMonitor(workers))).start();
			while (true) {
				Socket clientSocket = serverSocket.accept();
				ClientWorker newWorker = new ClientWorker(clientSocket);
				workers.add(newWorker);//完成了一些工作
				Thread newWokerThread = new Thread(newWorker);//这儿是为了要把这个任务给搞进TimerTask里
				TimerTask timerTask = new TimerTask() {
					@Override
					public void run() {
						newWokerThread.run();
					}
				};
				timer.schedule(timerTask,0,10000);

			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			serverSocket.close();
		}
	}
}