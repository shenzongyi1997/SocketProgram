package BlockQueue;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Administrator on 2017/12/2.
 */
public class test {
    static final BlockingQueue<String> bq = new ArrayBlockingQueue<String>(3);
    static Random random = new Random();
    static  Runnable producerRunnable = new Runnable()
    {
        public void run()
        {
            while (true)
            {
                try
                {
                    if(bq.offer(" ")) {
                        System.out.println("我生产了一个!" );
                    }
                    else
                    {
                        System.out.println("队列已满，无法继续生产！");
                    }
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    };
    static Runnable customerRunnable = new Runnable()
    {
        public void run()
        {
            while (true)
            {
                try
                {
                    Integer sleeptime = random.nextInt(5000)+1000;
                    if(bq.remove(" ")){
                        System.out.print("我消费了一个!" );
                        System.out.println("这个消费者要休眠"+sleeptime/1000+"秒！");
                    }
                    else{
                        System.out.println("队列为空，无法消费！");
                    }

                    Thread.sleep(sleeptime);

                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    };
    static  Runnable monitorRunnable = new Runnable()
    {
        int i = 0;
        public void run()
        {
            while (true)
            {
                try
                {
                    System.out.println("阻塞队列剩余空间:"+bq.remainingCapacity());
                    Thread.sleep(3000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    };
    public static void main(String[] args)
    {

        Thread producerThread = new Thread(producerRunnable);
        Thread customer1Thread = new Thread(customerRunnable);
        Thread customer2Thread = new Thread(customerRunnable);
        Thread customer3Thread = new Thread(customerRunnable);
        Thread customer4Thread = new Thread(customerRunnable);
        Thread monitorThread = new Thread(monitorRunnable);
        producerThread.start();
        customer1Thread.start();
        customer2Thread.start();
        customer3Thread.start();
        customer4Thread.start();
        monitorThread.start();

    }
}
