import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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

}
