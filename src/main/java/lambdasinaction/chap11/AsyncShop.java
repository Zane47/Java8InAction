package lambdasinaction.chap11;

import static lambdasinaction.chap11.Util.delay;
import static lambdasinaction.chap11.Util.format;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class AsyncShop {

    private final String name;
    private final Random random;

    public AsyncShop(String name) {
        this.name = name;
        random = new Random(name.charAt(0) * name.charAt(1) * name.charAt(2));
    }

    /**使用工厂方法supplyAsync创建CompletableFuture
     *
     * @param product
     * @return
     */
    public Future<Double> getPrice(String product) {
/*
        CompletableFuture<Double> futurePrice = new CompletableFuture<>();
        new Thread( () -> {
                    try {
                        double price = calculatePrice(product);
                        futurePrice.complete(price);
                    } catch (Exception ex) {
                        futurePrice.completeExceptionally(ex);
                    }
        }).start();
        return futurePrice;
*/

        // supplyAsync方法接受一个生产者（ Supplier）作为参数，
        // 返回一个CompletableFuture对象，该对象完成异步执行后会读取调用生产者方法的返回值。

        // 生产者方法会交由ForkJoinPool池中的某个执行线程（ Executor）运行，
        // 但是你也可以使用supplyAsync方法的重载版本，传递第二个参数指定不同的执行线程执行生产者方法。
        return CompletableFuture.supplyAsync(() -> calculatePrice(product));
    }

    private double calculatePrice(String product) {
        delay();
        if (true) {
            throw new RuntimeException("product not available");
        }
        return format(random.nextDouble() * product.charAt(0) + product.charAt(1));
    }

}