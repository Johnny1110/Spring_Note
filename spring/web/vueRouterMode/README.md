# Vue.js 前後端分離的 router mode : history 

<br>

Vue.js 做 SPA 時，預設的 url 連白癡一看都知道是 SPA，這無疑是直接暴露專案結構給人家知道

例如 : http://www.frizo.com/#/user/info

url 上加 \# 真的很醜，所以 vue.js 的 router 有一個 history 模式，簡單說就是可以把 url 模擬成正常 url

例如: http://www.frizo.com/user/info

vue.js 的部份就不多講了，看我們後端部份遇到的問題:

當 url 不加 # 時，瀏覽器會認為這是一般的 get 請求，於是把請求發送到 server 端。那 DispatcherServlet 當然不知道這個 url 是甚麼阿，因為 controller 是在前端（vue router），所以就會返回 404。

我們要做的就是在後端這邊處理 404，把請求轉發給 index.html 讓 vue 來處理頁面請求。

<br>

---

## 解法

寫一個 config 檔，添加 Bean => WebServerFactoryCustomizer。

注意 ! 這是 spring boot 2.x 以上的寫法，spring boot 1.x 不適用。

```java
@Configuration
public class VueRouterConfig {
    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer(){
        return new WebServerFactoryCustomizer<ConfigurableWebServerFactory>(){
            @Override
            public void customize(ConfigurableWebServerFactory factory) {
                ErrorPage errorPage404 = new ErrorPage(HttpStatus.NOT_FOUND, "/index.html");
                factory.addErrorPages(errorPage404);
            }
        };
    }
}
```