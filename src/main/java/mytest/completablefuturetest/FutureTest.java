package mytest.completablefuturetest;


import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FutureTest {

    public static void main(String[] args) throws Exception {

        ThreadPoolExecutor executor =
            new ThreadPoolExecutor(3, 6, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));
        Future<?> future = executor.submit(() -> {
            // delay
            Thread.sleep(5000);
            return new Random().nextInt(100);
        });
        // 即使异步任务等待了5秒，也依然先于消息输出，由此证明get方法是阻塞的。
        // Future只是个接口，实际上返回的类是FutureTask
        System.out.println(future.get());
        System.out.println("如果get是阻塞的，则此消息在数据之后输出");
        executor.shutdown();
    }
}
