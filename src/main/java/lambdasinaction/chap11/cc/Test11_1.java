package lambdasinaction.chap11.cc;


import java.util.concurrent.*;

public class Test11_1 {

    public static void main(String[] args) {
        // 创建ExecutorService，通过它你可以向线程池提交任务
        ExecutorService executor = Executors.newCachedThreadPool();
        // 向ExecutorService提交一个Callable对象
        Future<Double> future = executor.submit(new Callable<Double>() {
            @Override
            public Double call() throws Exception {
                // 以异步方式在新的线程中执行耗时的操作
                // dosomething()
                return null;
            }
        });
        // 异步操作进行的同时，你可以做其他的事情
        // dosomethingelse()I

        try {
            // 获取异步操作的结果，如果最终被阻塞，无法得到结果，那么在最多等待1秒钟之后退出
            Double result = future.get(1, TimeUnit.SECONDS);
        } catch (ExecutionException ee) {
        // 计算抛出一个异常
        } catch (InterruptedException ie) {
        // 当前线程在等待过程中被中断
        } catch (TimeoutException te) {
        // 在Future对象完成之前超过已过期
        }


    }


}
