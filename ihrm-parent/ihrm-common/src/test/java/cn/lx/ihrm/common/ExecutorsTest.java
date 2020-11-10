package cn.lx.ihrm.common;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * cn.lx.ihrm.common
 *
 * @Author Administrator
 * @date 10:04
 */
public class ExecutorsTest implements Runnable{

    public static void main(String[] args) throws InterruptedException {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            /**
             * Constructs a new {@code Thread}.  Implementations may also initialize
             * priority, name, daemon status, {@code ThreadGroup}, etc.
             *
             * @param r a runnable to be executed by new thread instance
             * @return constructed thread, or {@code null} if the request to
             * create a thread is rejected
             */
            @Override
            public Thread newThread(Runnable r) {

                Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName("test11234");
                return thread;
            }
        });

        scheduledExecutorService.scheduleAtFixedRate(new ExecutorsTest(),1000,1000, TimeUnit.MILLISECONDS);

        Thread.sleep(10000);
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        System.out.println("打印我");
    }
}
