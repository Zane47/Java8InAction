package lambdasinaction.chap10;


import java.util.Optional;

public class TestDemo {

    private static Car car;

    private static Person person;

    private static Insurance insurance;

    public static void main(String[] args) {
        test1();
        test2();
    }

    /**
     * OptionalMain.getCarInsuranceName()
     * p210
     * flatmap避免嵌套式optional结构
     *
     */
    private static void test2() {
        Optional<Person> optPerson = Optional.of(TestDemo.person);

        // 无法通过编译, optPerson是Optional<Person>类型的变量， 调用map方法应该没有问题。
        // 但getCar返回的是一个Optional<Car>类型的对象（如代码清单10-4所示），
        // 这意味着map操作的结果是一个Optional<Optional<Car>>类型的对象。

        // 因此，它对getInsurance的调用是非法的，
        // 因为最外层的optional对象包含了另一个optional对象的值，
        // 而它当然不会支持getInsurance方法。图10-3说明了你会遭遇的嵌套式optional结构
        // Optional<String> name = optPerson.map(Person::getCar).map(Car::getInsurance).map(Insurance::getName);



        // 使用flatMap
        // 由于Insurance.getName()方法的返回类型为String，这里就不再需要进行flapMap操作了。
        String name = optPerson.flatMap(Person::getCar).flatMap(Car::getInsurance)
            .map(Insurance::getName).orElse("Unknown");

        System.out.println(name);
    }

    private static void test1() {
        Optional<Insurance> optInsurance = Optional.ofNullable(TestDemo.insurance);
        Optional<String> name = optInsurance.map(Insurance::getName);
        System.out.println(name);
    }


}
