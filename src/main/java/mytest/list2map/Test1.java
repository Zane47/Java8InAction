package mytest.list2map;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * https://blog.csdn.net/linsongbin1/article/details/79801952
 * https://blog.csdn.net/YiQieFuCong/article/details/113073977
 */

public class Test1 {

    public static void main(String[] args) {
        List<Apple> appleList = new ArrayList<>();//存放apple对象集合

        Apple apple1 = new Apple(1, "苹果1", new BigDecimal("3.25"), 10);
        Apple apple12 = new Apple(1, "苹果2", new BigDecimal("1.35"), 20);
        Apple apple2 = new Apple(2, "香蕉", new BigDecimal("2.89"), 30);
        Apple apple3 = new Apple(3, "荔枝", new BigDecimal("9.99"), 40);
        Apple apple4 = new Apple(4, "荔枝", new BigDecimal("19.99"), 20);

        appleList.add(apple1);
        appleList.add(apple12);
        appleList.add(apple2);
        appleList.add(apple3);
        appleList.add(apple4);

        // list转map, id为key, 对象为value
        // 1. 循环转换
        // 2. stream
        /**
         * List -> Map
         * 需要注意的是：
         * toMap 如果集合对象有重复的key，会报错Duplicate key ....
         *  apple1,apple12的id都为1。
         *  可以用 (k1,k2)->k1 来设置，如果有重复的key,则保留key1,舍弃key2
         */
        Map<Integer, Apple> map = appleList.stream().collect(Collectors.toMap(Apple::getId, a -> a, (k1, k2) -> k1));


        // 有时候，希望得到的map的值不是对象，而是对象的某个属性，那么可以用下面的方式：
        Map<String, BigDecimal> maps = appleList.stream().collect(Collectors.toMap(Apple::getName, Apple::getMoney, (key1, key2) -> {
            if (key1.compareTo(key2) > 0) {
                return key1;
            } else {
                return key2;
            }
        }));


        // 3. 使用guava,

        /**
         * //依赖包
         * <dependency>
         *             <groupId>com.google.guava</groupId>
         *             <artifactId>guava</artifactId>
         *             <version>28.1-jre</version>
         *         </dependency>
         *
         *
         * //方法
         * private static Map<String,StudyObj>  repairMapTwo(List<StudyObj> studyObjs){
         *         Map<String, StudyObj> maps = Maps.uniqueIndex(studyObjs, new com.google.common.base.Function<StudyObj, String>() {
         *             @Override
         *             public String apply(StudyObj studyObj) {
         *                 return studyObj.getStudyCode();
         *             }
         *         });
         *         return maps;
         *     }
         *
         * // -------  源码了解
         *
         * //调用的Maps工具包源码对象 map
         * @CanIgnoreReturnValue
         *         public ImmutableMap.Builder<K, V> put(K key, V value) {
         *             this.ensureCapacity(this.size + 1);
         *             Entry<K, V> entry = ImmutableMap.entryOf(key, value);
         *             this.entries[this.size++] = entry;
         *             return this;
         *         }
         * //扩容方法
         * private void ensureCapacity(int minCapacity) {
         *             if (minCapacity > this.entries.length) {
         *                 this.entries = (Entry[])Arrays.copyOf(this.entries, com.google.common.collect.ImmutableCollection.Builder.expandedCapacity(this.entries.length, minCapacity));
         *                 this.entriesUsed = false;
         *             }
         *
         *         }
         */

        // 相同key值的对象在build时会抛异常。Multiple entries with same key
        Map<Integer, Apple> map1 = Maps.uniqueIndex(appleList, new Function<Apple, Integer>() {
            @Override
            public @Nullable Integer apply(@Nullable Apple apple) {
                return apple.getId();
            }
        });

        int a = 0;

    }


}
