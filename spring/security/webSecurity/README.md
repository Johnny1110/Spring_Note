# 快速的建立一個 web security 應用

<br>

基於 spring boot 2.x 使用 spring boot starter security 快速建置一個 web 登入應用。

建置涉及到 spring security 標準介面 UserDetails 與 UserDetailsService。

<br>

---

<br>

## 閱讀指南

*   建議以 WebSecurityConfig 這個配置文件為核心，再做衍伸。
因為所有的配置都最後都是為了在配置文件上做編排。

*   Spring Boot 使用大量預設配置，這邊就依照 Spring Boot 的規範做演示。

*   前端使用 Thymeleaf 模板，由於不是這篇筆記的核心，所以除了 security tag 其他就不多做解釋。有需要可以到 [web](../web) 單元找找看 Thymeleaf 筆記教學。

<br><br>

---

<br><br>

## 目錄

一.  [pom.xml 依賴重點整理](#pom)

二.  [配置文件 WebSecurityConfig](#config)

三.  [自訂 User 資料來源](#user)

四.  [Controller 以及模板設計](#controller)

五.  [結語](#ending)

<br><br>

---

<br><br>

## 實作

<br>

<div id="pom"></div>

### 一. pom.xml 依賴重點整理 [（看完整）](./spring-web-security/pom.xml)

*   Spring Boot Security 核心依賴 : 

    ```xml
    <!-- spring boot security 自動配置依賴 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
            <version>${spring.boot.version}</version>
        </dependency>
    ```
*   Thymeleaf Security Tag 依賴 :

    ```xml
    <!-- thymeleaf security tag 依賴 -->
        <dependency>
            <groupId>org.thymeleaf.extras</groupId>
            <artifactId>thymeleaf-extras-springsecurity5</artifactId>
            <version>3.0.4.RELEASE</version>
        </dependency>
    ```

    加入 Thymeleaf Security Tag 之後就可以在 Thymeleaf 模板上使用專用的模板 Tags。例如 :

    ```html
    <html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity5">

        <!-- 認證登入後才看到 -->
        <div sec:authorize="isAuthenticated()" id="goHome">
            <span>login success!!!</span>
        </div>

    </html>
      ```

<br><br>

---

<br><br>
    

<div id="config"></div>


### 二. 配置文件 WebSecurityConfig [（看完整）](./spring-web-security/src/main/java/com/frizo/note/spring/web/security/config/WebSecurityConfig.java)

*   SpringWebSecurityConfig 繼承 WebSecurityConfigurerAdapter 並且使用類註解 @Configuration 這邊複寫其父類的 3 個核心方法用來定義本安全應用。

    1. configure(AuthenticationManagerBuilder auth) :

        * 加入自訂的 userLoginService 以及 passwordEncoder，這邊 passwordEncoder 必須要添加，因為是 Spring Security 官方指定要使用加密。

            ```java
                @Override
                protected void configure(AuthenticationManagerBuilder auth) throws Exception {
                    auth.userDetailsService(userLoginService())
                        .passwordEncoder(passwordEncoder());
                }
            ```

        *   UserDetailsService 被註冊成 Bean 供我們使用，具體實作細節稍後在說。[（傳送們）](#user)

        *   passwordEncoder 實作:

            ```java
            /**
             * Spring Security 預設必須要用 passwordEncoder，使用 PasswordEncoderFactories 建立的實例可以根據 db
             * 存有的密碼格式自動判別並進行加密解密。若使用 SHA-256 加密則 db 密碼欄位存碼格式舉例如下:
             * {SHA-256}ce5ca673d13b36118d54a7cf13aeb0ca012383bf771e713421b4d1fd841f539a
             **/
            @Bean
            PasswordEncoder passwordEncoder(){
                return PasswordEncoderFactories.createDelegatingPasswordEncoder();
            }
            ```

        <br>
        <br>

    2. configure(HttpSecurity http):

        *   Spring Secutiry HTTP 授權的核心定義

            ```java
            @Override
            protected void configure(HttpSecurity http) throws Exception {
                http.authorizeRequests()
                        .anyRequest().authenticated() //所有請求需登入才可訪問。
                        .and()
                        .formLogin()//使用 web 表單登入
                        .loginPage("/login") // 指定登入頁
                        .failureUrl("/login?error") // 登入失敗導向此頁
                        .permitAll() // "/login" 與 "/login?error" 可任意被訪問。
                        .and()
                        .rememberMe() // 允許使用勾選記住我
                        .tokenValiditySeconds(360000) //360000 秒過期
                        .key("my-Key") // token key
                        .and()
                        .logout()
                        .logoutSuccessUrl("/login") // 登出成功導向 "/login"
                        .permitAll()// 登出請求可以任意被訪問。
                        .and()
                        .csrf().disable(); // 停用防跨域攻擊(測試階段停用，不推薦)
            }
            ```

    <br><br>
    
    3. configure(WebSecurity web):

        * web 靜態資源配置，如果不設定那麼靜態資源無法被 Client 端訪問

            ```java
            @Override
            public void configure(WebSecurity web) throws Exception {
                web
                .ignoring().antMatchers("/css/**") // 訪問css/** 不擋
                .and()
                .ignoring().antMatchers("/js/**") // 訪問js/** 不擋
                .and()
                .ignoring().antMatchers("/images/**") // 訪問images/** 不擋
                .and()
                .ignoring().antMatchers("/favicon.ico"); // 訪問favicon.ico 不擋
            }
            ```

<br><br>

---

<br><br>

<div id="user">

### 三. 自訂 User 資料來源


* 要想自行定義系統 User 資料來源，需要實作 Spring Security 提供 2 個組介面 :

    *   <strong>UserDetails</strong> （定義　USER 的基本資料，包括 username， password，isEnable 等）

    *   <strong>UserDetailsService</strong>　（供框架呼叫 loadByUsername() 方法，取出 UserDetails 的資料）

    <br>

* UserDetails 實作類 ： SysUser [（看完整）](./spring-web-security/src/main/java/com/frizo/note/spring/web/security/entity/SysUser.java)

    ```java
    public class SysUser implements UserDetails, Serializable {
        private Long id;
        private String username;
        private String password;
        private boolean enabled;
        private boolean locked;
        private boolean expired;
        private List<SysRole> roles;

        // 輸出該 User 使用者權限
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            List<GrantedAuthority> auths = new ArrayList<>();
            List<SysRole> roles = this.getRoles();
            roles.forEach(role ->{
                auths.add(new SimpleGrantedAuthority(role.getName()));
            });
            return auths;
        }

        ... 其餘有 6 個方法需要實作，這裡不一一列舉，有需要請看 source code...
    }
    ```
    <br>

    另外 SysRole 的實作簡單列出如下 :
    
    ```java
    public class SysRole implements Serializable {
        private Long id;
        private String name;

        ...getter and setter...
    }
    ```

    <br><br>

*   UserDetailsService 實作類： UserLoginServiceImpl

    這邊的實作比較特殊，因為要符合寫 Spring 的習慣，這邊 Service 我們自己定義一個介面 UserLoginService 然後繼承 UserDetailsService ：

    UserLoginService ：[（看完整）](./spring-web-security/src/main/java/com/frizo/note/spring/web/security/service/UserLoginService.java)

    ```java
    import org.springframework.security.core.userdetails.UserDetailsService;

    public interface UserLoginService extends UserDetailsService {
    
    }
    ```

    <br>

    然後再寫一個 UserLoginService 的實現類 UserLoginServiceImpl：

    UserLoginServiceImpl：[（看完整）](./spring-web-security/src/main/java/com/frizo/note/spring/web/security/service/impl/UserLoginServiceImpl.java)

    ```java
    // 實作 UserLoginService (UserDetailsService)，實現自訂義找尋 USER 資訊
    @Service
    public class UserLoginServiceImpl implements UserLoginService {
        @Autowired
        UserLoginDao userLoginDao;

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            return userLoginDao.getByUsername(username);
        }
    }
    ```

    這邊 UserLoginDao 不過多做解釋，只是簡單的提供 getByUsername() 方法來尋找 User 資訊的持久層模擬類。

<br><br>

---

<br><br>

<div id="controller"></div>

### 四. Controller 以及模板設計

* 其實到這邊為止，一個基本的 Spring Security 應用已經配置完成了，剩下的就是編寫 Controller 與設計登入主頁的模板了。

* 模板部分由於並不是本章重點，所以只講解必要部分，若要看詳細部分，可以到專案的 [resources](./spring-web-security/src/main/resources) 資料夾查看 source code。

    <br>

    Controller 部分：[（看完整）](./spring-web-security/src/main/java/com/frizo/note/spring/web/security/controller/BaseController.java)

    ```java
    @Controller
    public class BaseController {

        @GetMapping("/login")
        public String getLoginPage(){
            return "login";
        }

        // login 成功後 url 會導向到 "/"
        @GetMapping(value = {"/", "/index"})
        public ModelAndView getIndex(){
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            ModelAndView mav = new ModelAndView();
            mav.setViewName("index");
            mav.addObject("componentName", "/components/welcome.html");
            mav.addObject("name", auth.getName());
            mav.addObject("auths", auth.getAuthorities());
            return mav;
        }
    }
    ```

    <br>

    login.html [（看完整）](./spring-web-security/src/main/resources/templates/login.html)

    這邊要注意的是表單的 ```<input/> name```，以及 ```<form/> action``` 的值，input name 必須是 username 與 password，而 action 目標則必須同設定檔中給定的 '/login' 一致 ：


    ```html
    <form name="form" th:action="@{/login}" action="/login" method="post">
        <div class="input-group mb-3">
            <div class="input-group-prepend">
                <span class="input-group-text" id="account">帳號</span>
            </div>
            <input type="text" class="form-control" placeholder="請輸入帳號" aria-label="account" aria-describedby="account" name="username"/>
        </div>

        <div class="input-group mb-3">
            <div class="input-group-prepend">
                <span class="input-group-text" id="passWord">密碼</span>
            </div>
            <input type="password" class="form-control" placeholder="請輸入密碼" aria-label="passWord" aria-describedby="passWord" name="password"/>
        </div>

        <input class="btn btn-primary" type="submit" value="登入" id="logInBtn">
    </form>
    ```

    <br>

    登入成功後 url 就可以重導向到 "/" 也就是 index.html 喽!!

<br>
<br>

---

<br>
<br>

<div id="ending"></div>

### 五. 結語

* 以上就是所有的配置信息，如有需要可以整份專案抓下來部屬執行看看。

    
* cmd 模式進到 spring-web-security 資料夾後執行 mvn spring-boot:run 指令，然後連到 localhost:8080 查看。

    預設提供 2 組帳號測試：

    1. 一般 USER

        
        * 帳號：user
        
        * 密碼：user

    <br/>

    1. 管理員 ADMIN


        * 帳號：admin

        * 密碼：admin
        










            


    