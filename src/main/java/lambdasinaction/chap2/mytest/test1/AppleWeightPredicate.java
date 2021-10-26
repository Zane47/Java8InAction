package lambdasinaction.chap2.mytest.test1;


public class AppleWeightPredicate implements ApplePredicate {
    @Override
    public boolean test(Apple apple) {
        return apple.getWeight() > 150;
    }
}
