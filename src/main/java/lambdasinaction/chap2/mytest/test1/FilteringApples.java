package lambdasinaction.chap2.mytest.test1;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilteringApples {
    public static void main(String[] args) {
        List<Apple> appleList = Arrays.asList(
            new Apple(150, "red"),
            new Apple(200, "greed"));


        List<Apple> heavyList = filter(appleList, new AppleRedAndHeavyPredicate());

    }

    private static List<Apple> filter(List<Apple> appleList, ApplePredicate predicate) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple : appleList) {
            if (predicate.test(apple)) {
                result.add(apple);
            }
        }

        return result;
    }

}
