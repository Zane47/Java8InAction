package mytest.completablefuturetest;


import java.util.Random;
import java.util.concurrent.*;

public class CompletableFutureTest {

    public static void main(String[] args) {
        try {
            // 1.test
            test1();

            // 2. 处理(thenApply / thenApplyAsync)
            //test2();

            // 3. 消费(thenAccept / thenRun / thenAcceptBoth)
            //test3();

            // 4. 组合任务（thenCombine / thenCompose）
            //test4();

            // 5. 快者优先（applyToEither / acceptEither）
            // test5();

            // 6. 异常处理（exceptionally / whenComplete / handle）
            test6();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 6. 异常处理（exceptionally / whenComplete / handle）
     * <p>
     * public CompletionStage<T> exceptionally(Function<Throwable, ? extends T> fn);
     * <p>
     * public CompletionStage<T> whenComplete(BiConsumer<? super T, ? super Throwable> action);
     * public CompletionStage<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action);
     * public CompletionStage<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action, Executor executor);
     * <p>
     * public <U> CompletionStage<U> handle(BiFunction<? super T, Throwable, ? extends U> fn);
     * public <U> CompletionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn);
     * public <U> CompletionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn,Executor executor);
     * <p>
     * 综上，如果单纯要处理异常，那就用exceptionally；
     * 如果还想处理结果（没有异常的情况），那就用handle，比whenComplete友好一些，handle不仅能处理异常还能返回一个异常情况的默认值。
     * <p>
     * whenComplete对异常情况不是特别友好。
     */
    private static void test6() throws ExecutionException, InterruptedException {
        // exceptionally
        //test6_1();

        // whenComplete
        // test6_2();

        // handle
        test6_3();
    }

    // handle
    private static void test6_3() throws ExecutionException, InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 6, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));

        CompletableFuture<String> future = CompletableFuture
            .supplyAsync(() -> {
                if (true){
                    throw new RuntimeException("Error!!!");
                }
                return "Hello";
            }, executor)
            // 处理上一步发生的异常
            .handle((result,ex) -> {
                System.out.println("上一步结果：" + result);
                System.out.println("处理异常：" + ex.getMessage());
                return "Value When Exception Occurs";
            });

        System.out.println(future.get());

        executor.shutdown();
    }

    // whenComplete
    // 运行后可以看到whenComplete对异常情况不是特别友好。
    private static void test6_2() throws ExecutionException, InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 6, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));

        CompletableFuture<String> future = CompletableFuture
            .supplyAsync(() -> {
                if (true) {
                    throw new RuntimeException("Error!!!");
                }
                return "Hello";
            }, executor)
            // 处理上一步发生的异常
            .whenComplete((result, ex) -> {
                // 这里等待为了上一步的异常输出完毕
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("上一步结果：" + result);
                System.out.println("处理异常：" + ex.getMessage());
            });

        System.out.println(future.get());

        executor.shutdown();
    }

    // exceptionally
    private static void test6_1() throws ExecutionException, InterruptedException {

        ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 6, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));

        CompletableFuture<String> future = CompletableFuture
            .supplyAsync(() -> {
                if (true) {
                    throw new RuntimeException("Error!!!");
                }
                return "Hello";
            }, executor)
            // 处理上一步发生的异常
            .exceptionally(e -> {
                System.out.println("处理异常：" + e.getMessage());
                return "处理完毕!";
            });

        System.out.println(future.get());

        executor.shutdown();
    }

    /**
     * 快者优先（applyToEither / acceptEither）
     * 场景: 如果我们有多条渠道去完成同一种任务，那么我们肯定选择最快的那个。
     * <p>
     * public <U> CompletionStage<U> applyToEither(CompletionStage<? extends T> other, Function<? super T, U> fn)
     * public <U> CompletionStage<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn)
     * public <U> CompletionStage<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn, Executor executor)
     * <p>
     * public CompletionStage<Void> acceptEither(CompletionStage<? extends T> other, Consumer<? super T> action)
     * public CompletionStage<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action)
     * public CompletionStage<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action, Executor executor)
     * <p>
     * 这两种区别：仅仅是一个有返回值，一个没有（Void）
     */
    private static void test5() throws ExecutionException, InterruptedException {
        // applyToEither
        //test5_1();
        // acceptEither
        test5_2();
    }

    // acceptEither
    private static void test5_2() throws ExecutionException, InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 6, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));

        CompletableFuture<String> otherFuture = CompletableFuture
            .supplyAsync(() -> {
                int result = new Random().nextInt(100);
                System.out.println("执行者A：" + result);
                try {
                    // 故意A慢了一些
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "执行者A【" + result + "】";
            }, executor);

        CompletableFuture<Void> future = CompletableFuture
            .supplyAsync(() -> {
                int result = new Random().nextInt(100);
                System.out.println("执行者B：" + result);
                return "执行者B【" + result + "】";
            }, executor)
            .acceptEither(otherFuture, (faster) -> {
                System.out.println("谁最快：" + faster);
            });

        System.out.println(future.get());

        executor.shutdown();
    }

    // applyToEither
    private static void test5_1() throws ExecutionException, InterruptedException {

        ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 6, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));

        CompletableFuture<String> otherFuture = CompletableFuture
            .supplyAsync(() -> {
                int result = new Random().nextInt(100);
                System.out.println("执行者A：" + result);
                try {
                    // 故意A慢了一些
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "执行者A【" + result + "】";
            }, executor);

        CompletableFuture<String> future = CompletableFuture
            .supplyAsync(() -> {
                int result = new Random().nextInt(100);
                System.out.println("执行者B：" + result);
                return "执行者B【" + result + "】";
            }, executor)
            .applyToEither(otherFuture, (faster) -> {
                System.out.println("谁最快：" + faster);
                return faster;
            });

        System.out.println(future.get());

        executor.shutdown();
    }


    /**
     * 组合两个任务呢？组合任务（thenCombine / thenCompose）
     * public <U,V> CompletionStage<V> thenCombine(CompletionStage<? extends U> other, BiFunction<? super T,? super U,? extends V> fn)
     * public <U,V> CompletionStage<V> thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super T,? super U,? extends V> fn)
     * public <U,V> CompletionStage<V> thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super T,? super U,? extends V> fn, Executor executor)
     * <p>
     * public <U> CompletionStage<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> fn)
     * public <U> CompletionStage<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn)
     * public <U> CompletionStage<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn, Executor executor)
     * <p>
     * 这两种区别：主要是返回类型不一样。
     * thenCombine：至少两个方法参数，一个为其它stage，一个为用户自定义的处理函数，函数返回值为结果类型。
     * thenCompose：至少一个方法参数即处理函数，函数返回值为stage类型。
     */
    private static void test4() throws ExecutionException, InterruptedException {
        // thenCombine
        test4_1();
        // thenCompose
        //test4_2();
    }

    // thenCompose
    private static void test4_2() throws ExecutionException, InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 6, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));

        CompletableFuture<Integer> future = CompletableFuture
            // 执行异步任务
            .supplyAsync(() -> {
                int result = new Random().nextInt(100);
                System.out.println("任务A：" + result);
                return result;
            }, executor)
            .thenComposeAsync((current) -> {
                return CompletableFuture.supplyAsync(() -> {
                    int b = new Random().nextInt(100);
                    System.out.println("任务B：" + b);
                    int result = b + current;
                    System.out.println("组合两个任务的结果：" + result);
                    return result;
                }, executor);
            });

        System.out.println(future.get());

        executor.shutdown();
    }

    /**
     * thenCombine
     */
    private static void test4_1() throws ExecutionException, InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 6, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));

        CompletableFuture<Integer> otherFuture = CompletableFuture
            // 执行异步任务
            .supplyAsync(() -> {
                int result = new Random().nextInt(100);
                System.out.println("任务A：" + result);
                return result;
            }, executor);

        CompletableFuture<Integer> future = CompletableFuture
            // 执行异步任务
            .supplyAsync(() -> {
                int result = new Random().nextInt(100);
                System.out.println("任务B：" + result);
                return result;
            }, executor)
            .thenCombineAsync(otherFuture, (current, other) -> {
                int result = other + current;
                System.out.println("组合两个任务的结果：" + result);
                return result;
            });

        System.out.println(future.get());

        executor.shutdown();
    }


    /**
     * 消费而不影响最终结果（thenAccept / thenRun / thenAcceptBoth）
     * <p>
     * public CompletableFuture<Void> thenAccept(Consumer<? super T> action)
     * public CompletableFuture<Void> thenAcceptAsync(Consumer<? super T> action)
     * public CompletableFuture<Void> thenAcceptAsync(Consumer<? super T> action, Executor executor)
     * <p>
     * public CompletableFuture<Void> thenRun(Runnable action)
     * public CompletableFuture<Void> thenRunAsync(Runnable action)
     * public CompletableFuture<Void> thenRunAsync(Runnable action, Executor executor)
     * <p>
     * public <U> CompletableFuture<Void> thenAcceptBoth(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action)
     * public <U> CompletableFuture<Void> thenAcceptBothAsync(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action)
     * public <U> CompletableFuture<Void> thenAcceptBothAsync(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action, Executor executor)
     * <p>
     * <p>
     * 这三种的区别是：
     * thenAccept：能够拿到并利用执行结果
     * thenRun：不能够拿到并利用执行结果，只是单纯的执行其它任务
     * thenAcceptBoth：能传入另一个stage，然后把另一个stage的结果和当前stage的结果作为参数去消费。
     */
    private static void test3() throws ExecutionException, InterruptedException {
        ThreadPoolExecutor executor =
            new ThreadPoolExecutor(3, 6, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));

        CompletableFuture<Integer> future = CompletableFuture
            // 执行异步任务
            .supplyAsync(() -> {
                return new Random().nextInt(100);
            }, executor)
            // 对上一步结果进行处理
            .thenApplyAsync(n -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int res = new Random().nextInt(100);
                System.out.println(String.format("如果是同步的，这条消息应该先输出。上一步结果：%s，新加：%s", n, res));
                return n + res;
            });

        // 单纯的消费执行结果，注意这个方法是不会返回计算结果的——CompletableFuture<Void>
        CompletableFuture<Void> voidCompletableFuture = future.thenAcceptAsync(n -> {
            System.out.println("单纯消费任务执行结果：" + n);
        });
        // 这个无法消费执行结果，没有传入的入口，只是在当前任务执行完毕后执行其它不相干的任务
        future.thenRunAsync(() -> {
            System.out.println("我只能执行其它工作，我得不到任务执行结果");
        }, executor);

        // 这个方法会接受其它CompletableFuture返回值和当前返回值
        future.thenAcceptBothAsync(CompletableFuture.supplyAsync(() -> {
            return "I'm Other Result";
        }), (current, other) -> {
            System.out.println(String.format("Current：%s，Other:%s", current, other));
        });

        System.out.println("我等了你2秒");
        System.out.println(future.get());

        executor.shutdown();
    }

    /**
     * 利用计算结果进一步处理 -> 结果转换(thenApply / thenApplyAsync)
     * <p>
     * // 同步转换
     * public <U> CompletableFuture<U> thenApply(Function<? super T,? extends U> fn)
     * // 异步转换，使用默认线程池
     * public <> CompletableFuture<U> thenApplyAsync(Function<? super T,? extends U> fn)
     * // 异步转换，使用指定线程池
     * public <U> CompletableFuture<U> thenApplyAsync(Function<? super T,? extends U> fn, Executor executor)
     */
    private static void test2() throws Exception {
        ThreadPoolExecutor executor =
            new ThreadPoolExecutor(3, 6, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));

        CompletableFuture<Integer> future = CompletableFuture
            .supplyAsync(() -> {
                return new Random().nextInt(100);
            }, executor)
            // 对上一步的结果进行处理
            .thenApply(num -> {
                try {
                    // delay
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int res = new Random().nextInt(100);
                System.out.println(String.format("如果是同步的，这条消息应该先输出。上一步结果：%s，新加：%s", num, res));
                return num + res;
            });
        /*CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
                return new Random().nextInt(100);
            }, executor)
            // 对上一步的结果进行处理
            .thenApplyAsync(num -> {
                try {
                    // delay
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int res = new Random().nextInt(100);
                System.out.println(String.format("如果是同步的，这条消息应该先输出。上一步结果：%s，新加：%s", num, res));
                return num + res;
            });*/
        // 先输出
        System.out.println("我等了你2秒");
        System.out.println(future.get());

        executor.shutdown();
    }


    /**
     * 使用CompletableFuture输出随机数字
     *
     * @throws Exception
     */
    private static void test1() throws Exception {
        ThreadPoolExecutor executor =
            new ThreadPoolExecutor(3, 6, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));

        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            return new Random().nextInt(100);
        }, executor);
        System.out.println(future.get());

        executor.shutdown();

    }

}
