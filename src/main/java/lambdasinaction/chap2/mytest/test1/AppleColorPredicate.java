package lambdasinaction.chap2.mytest.test1;


public class AppleColorPredicate implements ApplePredicate {
    @Override
    public boolean test(Apple apple) {
        return "green".equals(apple.getColor());
    }
}
