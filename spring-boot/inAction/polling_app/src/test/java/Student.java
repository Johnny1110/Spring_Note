import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(
        value = {"email","id","passwd"},
        allowGetters = true
        )
public class Student {
    @JsonProperty(value="id")
    private String mID;

    @JsonProperty(value="passwd")
    private String mPasswd;

    @JsonProperty(value="email")
    private String mEmail;

    public Student() {
    }

    public void setmID(String mID) {
        this.mID = mID;
    }

    public void setmPasswd(String mPasswd) {
        this.mPasswd = mPasswd;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }
}
