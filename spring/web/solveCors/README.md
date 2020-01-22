# 跨源請求解決方法

<br>

跨源請求是一個經常讓我頭痛的問題，簡單說明一下甚麼情況會產生這個問題。

1. a.frizo.com ＞訪問＞ b.frizo.com    （主機位址不同）
2. a.frizo.com:80 ＞訪問＞ a.frizo.com:8080 （port 不同）
3. http://a.frizo.com ＞訪問＞ https://www.google.com （協定不同）

為甚麼產生:

資源由 server 端返回時，瀏覽器發現 response header 是跨源的，直接擋掉，防止一些安全問題。

如何解決:

解法有很多，這邊我們記錄一個就好（懶），我們從 server 端下手，讓 response header 添加一段允許跨源請求的訊息。

------------------------

<br />

## 解法

在 config 檔中添加一個 Bean => CorsFilter 即可。

```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        //放行哪些原始域
        config.addAllowedOrigin("*");
        //是否發送Cookie
        config.setAllowCredentials(true);
        //放行哪些原始域(請求方法)
        config.addAllowedMethod("*");
        //放行哪些原始域(Header信息)
        config.addAllowedHeader("*");

        //添加映射路徑
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", config);

        //返回新的 CorsFilter.
        return new CorsFilter(configSource);
    }
```
