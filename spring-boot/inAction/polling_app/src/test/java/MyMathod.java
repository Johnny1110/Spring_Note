public class MyMathod {
    public void sayHelloToUser(String name, MyInterface<String, String> mapper){
        String ans = mapper.apply(name);
        System.out.println(ans);
    }
}
