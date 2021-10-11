package mytest.completablefuturetest;


import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FutureTest {
    public static void main(String[] args) throws Exception {

        /**
         * Future是Java 5添加的类，用来描述一个异步计算的结果。
         * 你可以使用isDone方法检查计算是否完成，
         * 或者使用get阻塞住调用线程，直到计算完成返回结果，
         * 你也可以使用cancel方法停止任务的执行。
         *
         * 虽然Future以及相关使用方法提供了异步执行任务的能力，
         * 但是对于结果的获取却是很不方便，
         * 只能通过阻塞或者轮询的方式得到任务的结果。
         * 阻塞的方式显然和我们的异步编程的初衷相违背，
         * 轮询的方式又会耗费无谓的CPU资源，而且也不能及时地得到计算结果，
         *
         */

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
