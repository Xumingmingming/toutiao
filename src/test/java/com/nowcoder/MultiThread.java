package com.nowcoder;

import com.nowcoder.util.JedisAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

class MyThread extends Thread{
   public int tid;
   public MyThread(int tid){
       this.tid=tid;
   }

    @Override
    public void start() {
        try{

            for (int i=0;i<10;i++){
                //每隔一秒
                //Thread.sleep(1000);
                System.out.println(String.format("T%d:%d", tid, i));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

public class MultiThread
{
    public static void testThread(){
        for (int i=0;i<10;i++){
           // new MyThread(i).start();
        }

        for (int i=0;i<10;i++){
            final int tid = i;
            new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        for (int i = 0; i < 10; ++i) {
                            Thread.sleep(1000);
                            System.out.println(String.format("T2:%d:%d", tid, i));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public static Object obj=new Object();
    public static void testSynchronized1(){

        synchronized (obj){
            try {
                for (int i = 0; i < 10; ++i) {
                    Thread.sleep(1000);
                    System.out.println(String.format("T3:%d", i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static void testSynchronized2(){

        synchronized (new Object()){
            try {
                for (int i = 0; i < 10; ++i) {
                    Thread.sleep(1000);
                    System.out.println(String.format("T4:%d", i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static void testSynchronized() {
        for (int i = 0; i < 10; ++i) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    testSynchronized1();
                    testSynchronized2();
                }
            }).start();
        }
    }
    public static void testBlockingQueue(){
        BlockingQueue<String> q = new ArrayBlockingQueue<String>(10);
        new Thread(new Producer(q)).start();
        new Thread(new Consumer(q), "Consumer1").start();
        new Thread(new Consumer(q), "Consumer2").start();


    }
    private static int counter=0;
    private static AtomicInteger atomicInteger=new AtomicInteger(0);
    public static void sleep(int mills) {
        try {
            //Thread.sleep(new Random().nextInt(mills));
            Thread.sleep(mills);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void testWithAtomic() {
        for (int i = 0; i < 10; ++i) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //sleep(1000);
                    for (int j = 0; j < 10; ++j) {
                        System.out.println(atomicInteger.incrementAndGet());
                    }
                }
            }).start();
        }
    }

    public static void testWithoutAtomic() {
        for (int i = 0; i < 10; ++i) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sleep(1000);
                    for (int j = 0; j < 10; ++j) {
                        counter++;
                        System.out.println(counter);
                    }
                }
            }).start();
        }
    }

    public static void testAtomic() {
        testWithAtomic();
        //testWithoutAtomic();
    }

    private static ThreadLocal<Integer> threadLocalUserIds = new ThreadLocal<>();
    private static int userId;
    public static void testThreadLocal() {
        for (int i = 0; i < 10; ++i) {
            userId = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    threadLocalUserIds.set(userId);
                    sleep(1000);
                    System.out.println("ThreadLocal: " + threadLocalUserIds.get());
                }
            }).start();
        }

        for (int i = 0; i < 10; ++i) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //userId = finalID;
                    sleep(1000);
                    System.out.println("NonThreadLocal: " + userId +" userId:"+ userId);
                }
            }).start();
        }

    }

  public static void testExecutor(){
         // ExecutorService service= Executors.newSingleThreadExecutor();
      ExecutorService service=Executors.newFixedThreadPool(2);
      service.submit(new Runnable() {
          @Override
          public void run() {
              for (int i=0;i<10;i++){
                  sleep(1000);
                  System.out.println("Executor1: "+i);
              }
          }
      });
      //把任务抛进来
      service.submit(new Runnable() {
          @Override
          public void run() {
              for (int i = 0; i < 10; ++i) {
                  sleep(1000);
                  System.out.println("Execute2 " + i);
              }
          }
      });
      //关掉
      service.shutdown();
      while(!service.isTerminated()){
          sleep(1000);
          System.out.println("Wait for termination.");
      }
  }
    private static final Logger logger = LoggerFactory.getLogger(MultiThread.class);

    public static void testFuture(){
      ExecutorService service= Executors.newSingleThreadExecutor();
      Future<Integer> future=service.submit(new Callable<Integer>() {
          @Override
          public Integer call() throws Exception {
              sleep(1000);
              return 1;
          }
      });

      service.shutdown();
      try {
          //System.out.println(future.get());
          System.out.println(future.get(100,TimeUnit.MILLISECONDS));
      } catch (Exception e) {

          logger.error("超时！"+e.getMessage());
      }
  }
    public static void testSleep(){
        try {
            //Thread.currentThread().interrupt();
            Thread.sleep(1000);
            Thread.currentThread().interrupt();
            //System.out.println("hahahah");
        } catch (Exception e) {
            System.out.println("Interruted when sleep!");
            e.printStackTrace();
        }

    }
    public static void main(String[] args) {

        //testThread();
        //testSynchronized();
        //testBlockingQueue();
        //testAtomic();
        //testThreadLocal();
        //testExecutor();
        //testFuture();
        testSleep();
    }

}
class Producer implements Runnable{
    private BlockingQueue<String> q;
    public Producer(BlockingQueue<String> q) {
        this.q = q;
    }
    public void run() {
        try {
            for (int i = 0; i < 100; ++i) {
                Thread.sleep(10);
                q.put(String.valueOf(i));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
class Consumer implements Runnable{
    private BlockingQueue<String> q;
    public Consumer(BlockingQueue<String> q) {
        this.q = q;
    }
    public void run() {
        try {
            while(true){
                System.out.println(Thread.currentThread().getName()+":" + q.take());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}