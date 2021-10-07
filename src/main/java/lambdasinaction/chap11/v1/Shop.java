package lambdasinaction.chap11.v1;

import static lambdasinaction.chap11.Util.delay;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class Shop {

    private final String name;
    private final Random random;

    public Shop(String name) {
        this.name = name;
        random = new Random(name.charAt(0) * name.charAt(1) * name.charAt(2));
    }

    public double getPrice(String product) {
        return calculatePrice(product);
    }

    /**
     * 同步方法, 为等待同步事件完成而等待1秒钟, 所有商店都要重复这种操作, 需要修改 -> Async
     *
     * @param product product
     * @return price
     */
    private double calculatePrice(String product) {
        delay();
        return random.nextDouble() * product.charAt(0) + product.charAt(1);
    }

    /**
     * 异步方法 -> AsyncShop.getPrice(), 使用工厂方法
     *
     * @param product product
     * @return price
     */
    public Future<Double> getPriceAsync(String product) {
        // 创建CompletableFuture对象， 它会包含计算的结果
        CompletableFuture<Double> futurePrice = new CompletableFuture<>();
        // 在另一个线程中以异步方式执行计算
        new Thread(() -> {
            double price = calculatePrice(product);
            // 需长时间计算的任务结束并得出结果时，设置Future的返回值
            futurePrice.complete(price);
        }).start();

        // 无需等待还没结束的计算，直接返回Future对象
        return futurePrice;
    }

    /** 如果价格计算过程中产生了错误会怎样呢？
     * 非常不幸，这种情况下你会得到一个相当糟糕的结果：
     * 用于提示错误的异常会被限制在试图计算商品价格的当前线程的范围内，最终会杀死该线程，
     * 而这会导致等待get方法返回结果的客户端永久地被阻塞
     *
     * @param product
     * @return
     */
    public Future<Double> getPriceAsyncWithExceptionHandler(String product) {
        // 创建CompletableFuture对象， 它会包含计算的结果
        CompletableFuture<Double> futurePrice = new CompletableFuture<>();
        // 在另一个线程中以异步方式执行计算
        new Thread(() -> {
            // 如果价格计算正常结束，完成Future操作并设置商品价格
            // 否则就抛出导致失败的异常，完成这次Future操作
            try {
                double price = calculatePrice(product);
                // 需长时间计算的任务结束并得出结果时，设置Future的返回值
                futurePrice.complete(price);
            } catch (Exception ex) {
                futurePrice.completeExceptionally(ex);
            }

        }).start();

        // 无需等待还没结束的计算，直接返回Future对象
        return futurePrice;
    }

    public String getName() {
        return name;
    }

}
