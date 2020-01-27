import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Tester {
    @Test
    public void testObjectMapper() throws JsonProcessingException {
        Student student = new Student();
        student.setmID("A01");
        student.setmEmail("JJJ@gmail.com");
        student.setmPasswd("fff");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(student);
        System.out.println(json);
    }

    @Test
    public void testObjectMapperToList() throws JsonProcessingException {
        Student student = new Student();
        student.setmID("A01");
        student.setmEmail("JJJ@gmail.com");
        student.setmPasswd("fff");
        ObjectMapper mapper = new ObjectMapper();
        List<Student> users = new ArrayList<Student>();
        users.add(student);
        String jsonlist = mapper.writeValueAsString(users);
        System.out.println(jsonlist);

    }

    @Test
    public void testMacPovider(){
        System.out.println(MacProvider.generateKey(SignatureAlgorithm.HS512).getEncoded());
    }


    @Test
    public void testStream(){
        People man1 = new People(1, "Johnny", new Address("Taiwan", "Taipei"));
        People man2 = new People(2, "Ben", new Address("China", "Beijing"));
        List<People> mans = new ArrayList<>();
        mans.add(man1);
        mans.add(man2);List<Integer> ids = mans.stream()
                .map(People::getId)
                .collect(Collectors.toList());
        ids.forEach(System.out::println);

        List<String> mansInCity = mans.stream()
                .map(People::getAddress)
                .map(Address::getCity)
                .collect(Collectors.toList());
        mansInCity.forEach(System.out::println);

    }

    @Test
    public void testFunctionalInterface(){
        MyMathod method = new MyMathod();
        method.sayHelloToUser("Wang", (name)->{
            return "hello, welcome to myMethod, Mr." + name;
        });
    }

    public static class People{
        private int id;
        private String name;
        private Address address;

        public People(int id, String name, Address addr) {
            this.id = id;
            this.name = name;
            this.address = addr;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Address {
        private String country;
        private String city;

        public Address(String country, String city) {
            this.country = country;
            this.city = city;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }
    }

}
