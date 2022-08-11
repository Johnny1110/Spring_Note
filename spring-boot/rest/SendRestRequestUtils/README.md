# 好用的 RestTemplate 輔助套件

<br>

---

<br>

開發專案過程中大量使用到 Spring 的 __RestTemplate__ 物件。因為總是需要把回傳結果映射成定義好的 Java 物件或者是 Java __List__，所以我寫了一個 __RestTemplate__ 的輔助套件。

<br>

## 依賴

<br>

```xml
<dependency>
    <groupId>org.json</groupId>
    <artifactId>json</artifactId>
    <version>20220320</version>
</dependency>

<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.8.5</version>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>2.7.2</version>
</dependency>

```

輔助套件使用 Gson 還有

<br>

```java
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.json.JSONArray;
import io.netty.util.internal.StringUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

public class SendRestRequestUtils {

  private static final HttpHeaders DEFAULT_HEADERS = new HttpHeaders();

  static {
    DEFAULT_HEADERS.setContentType(MediaType.APPLICATION_JSON);
    DEFAULT_HEADERS.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
  }

  /**
   * @param url
   * @param params
   * @param responseType 映射回傳類別
   *                     * 如果要回傳的是 List 資料結構，輸入範例: Type responseType = new TypeToken<ArrayList<YourClass>>(){}.getType();
   *                     * 如果是一般 Java 物件，輸入範例: YourClass.class
   * @param headers 客製化 headers
   * @param respJsonKey 回傳 Json 資料如果有需要解析的 key 就由此傳入
   *                    ex. 回傳格式 = {"d":{"msg": "success", "data": [...]}}
   *                    如果 "d" key 是不需要的，只要映射 {"msg": "success", "data": [...]} 而已，就可以把 "d" 當作參數放入，post() 方法會自動排除 "d"
   * @return
   * @param <T>
   * @throws JsonProcessingException
   */
  public static <T> T post(String url, Object params, Type responseType, HttpHeaders headers, String respJsonKey) throws JsonProcessingException {
    RestTemplate restTemplate = new RestTemplate();
    Gson gson = new Gson();
    HttpEntity<String> request = new HttpEntity<>(gson.toJson(params), headers);
    ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
    return mapJsonToReturnType(response.getBody(), respJsonKey, responseType);
  }


  /**
   * @param url
   * @param params 如果調用方自己把參數加入到 url 中，就不需要傳入這個參數。
   * @param responseType 映射回傳類別
   *                     * 如果要回傳的是 List 資料結構，輸入範例: Type responseType = new TypeToken<ArrayList<YourClass>>(){}.getType();
   *                     * 如果是一般 Java 物件，輸入範例: YourClass.class
   * @param headers 客製化 headers
   * @param respJsonKey 回傳 Json 資料如果有需要解析的 key 就由此傳入
   *                    ex. 回傳格式 = {"d":{"msg": "success", "data": [...]}}
   *                    如果 "d" key 是不需要的，只要映射 {"msg": "success", "data": [...]} 而已，就可以把 "d" 當作參數放入，post() 方法會自動排除 "d"
   * @return
   * @param <T>
   * @throws JsonProcessingException
   */
  public static <T> T get(String url, Object params, Type responseType, HttpHeaders headers, String respJsonKey) throws JsonProcessingException {
    RestTemplate restTemplate = new RestTemplate();
    Gson gson = new Gson();
    //  拼接參數到 url 中
    if (params != null){
      StringBuffer sb = new StringBuffer("?");
      Map<String, String> requestMap = new ObjectMapper().readValue(gson.toJson(params), new TypeReference<>() {});
      requestMap.forEach((k, v) -> {
        sb.append(k).append("=").append(v).append("&");
      });
      url = url.concat(sb.toString());
    }
    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
    return mapJsonToReturnType(response.getBody(), respJsonKey, responseType);

  }


  public static  <T> T post(String url, Object params, Type responseType, HttpHeaders headers) throws JsonProcessingException {
    return post(url, params, responseType, headers, null);
  }

  public static  <T> T post(String url, Object params, Type responseType) throws JsonProcessingException {
    return post(url, params, responseType, DEFAULT_HEADERS);
  }

  public static <T> T post(
      String url, Object params, Type responseType, String respJsonKey)
      throws JsonProcessingException {
    return post(url, params, responseType, DEFAULT_HEADERS, respJsonKey);
  }



  public static  <T> T get(String url, Object params, Type responseType, String respJsonKey) throws JsonProcessingException {
    return get(url, params, responseType, DEFAULT_HEADERS, respJsonKey);
  }

  public static <T> T get(String url, Type responseType, String respJsonKey)
      throws JsonProcessingException {
    return get(url, null, responseType, DEFAULT_HEADERS, respJsonKey);
  }

  public static <T> T get(String url, Type responseType, HttpHeaders headers)
          throws JsonProcessingException {
    return get(url, null, responseType, headers);
  }

  public static <T> T get(String url, Type responseType)
          throws JsonProcessingException {
    return get(url, null, responseType, DEFAULT_HEADERS);
  }

  public static  <T> T get(String url, Object params, Type responseType) throws JsonProcessingException {
    return get(url, params, responseType, DEFAULT_HEADERS);
  }

  public static  <T> T get(String url, Object params, Type responseType, HttpHeaders headers) throws JsonProcessingException {
    return get(url, params, responseType, headers, null);
  }




  /**
   * 這個方法負責把 JsonString 映射成 Java 物件，不開放給類別外調用者使用
   * @param body 回應資料
   * @param respJsonKey 回傳 Json 資料如果有需要解析的 key 就由此傳入
   * @param responseType 映射回傳類別
   * @return
   * @param <T>
   */
  private static <T> T mapJsonToReturnType(String body, String respJsonKey, Type responseType) {
    Object bodyData;
    try{
      // 先嘗試轉 JSONObject 處理
      JSONObject jsonObject = new JSONObject(body);
      bodyData = jsonObject;
      if (!StringUtil.isNullOrEmpty(respJsonKey)){
        // 如果回傳的 json 有 key 要解析，就在這裡完成
        bodyData = jsonObject.get(respJsonKey);
      }
    }catch (JSONException ex){
      // 出錯就當 JSONArray 處理
      bodyData = new JSONArray(body);
    }
    return new Gson().fromJson(bodyData.toString(), responseType);
  }
}
```


<br>

使用範例：

<br>

```java
public static void main(String[] args){
    RequestData req = new RequestData();
    req.setId(1);
    req.setName("Johnny");
    ResponseData resp = SendRestRequestUtils.post("http://POST_URL", req, ResponseData.class);
    ResponseData resp = SendRestRequestUtils.get("http://GET_URL", req, ResponseData.class);
    ResponseData resp = SendRestRequestUtils.get("http://GET_URL?id=1&name=Johnny", ResponseData.class);
}


static class RequestData {
    private int id;
    private int name;

    // Getter and setter
}

static class ResponseData {
    private String msg;
    private List<String> hobby;

    // Getter and setter
}
```