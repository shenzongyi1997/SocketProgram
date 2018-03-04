package BlockQueue;

import java.util.concurrent.*;

/**
 * Created by Administrator on 2017/12/9.
 */
public class TimeOut {
    static Runnable task = new Runnable() {
        @Override
        public void run() {
            int count = 7;
            while (count > 0) {
                count--;
                System.out.println(8-count);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // 退出执行
                    System.out.println("interrupt, then quit");
                    return;
                }
                count--;
            }
        }
    };
    public static void main(String[] args) throws InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(1);
        es.execute(task);
        // 关闭线程池
        es.shutdown();
        // 阻塞操作，等待5s
        boolean finished = es.awaitTermination(5, TimeUnit.SECONDS);
        // 如果过了5s线程还没有完成, 强制关闭, interrupt Runnable 线程,  进入 InterruptedException 处理流程
        if (!finished) {
            es.shutdownNow();
        }
        System.out.println("task 3");
    }
}