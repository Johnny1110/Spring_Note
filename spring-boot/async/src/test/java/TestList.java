import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TestList {

    public static void main(String[] args) {

        Person p1 = new Person();
        Person p2 = new Person();
        Person p3 = new Person();

        p1.setName("Johnny");
        p1.setAge(24);
        p1.setLevel(1);

        p2.setName("May");
        p2.setAge(21);
        p2.setLevel(1);

        p3.setName("Layla");
        p3.setAge(18);
        p3.setLevel(1);

        List<Person> personList = new ArrayList<>();
        personList.add(p1);
        personList.add(p2);
        personList.add(p3);

        List<Person> greaterThen20 = personList.stream().filter(p -> p.getAge() > 20).collect(Collectors.toList());
        for (Person person : greaterThen20) {
            person.setLevel(3);
        }

        personList.forEach(p -> {
            System.out.println("---");
            System.out.println(p.getName());
            System.out.println(p.getLevel());
        });
    }

    public static class Person{
        private String name;
        private int age;
        private int level;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }
    }

}
