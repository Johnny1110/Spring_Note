# 實作 Web 前後端分離登入（使用 JWT）

<br>

學習 vue.js 的時候使用前後端分離技術，後端理所應當要使用 JWT 進行跨域登入與授權。於是便出現了這篇筆記。

---

<br>

## 目錄

一. [認識 JWT](#1)

二. [後端實作](#2)

三. [實際測試](#3)

<br>

---
<br>

<tag id="1"/>

## 一. 認識 JWT (JSON Web Token)

1. 使用 JWT 進行認證的流程如下圖: (圖片取自官方:https://jwt.io/introduction/)

    ![client-credentials-grant](./imgs/client-credentials-grant.png)

    step 1. client 送出一個 login 請求給 server

    step 2. server 回傳一組 JWT 給 client

    step 3. client 在之後向 server 端的請求 header 中，加入得到的 JWT 供 server 辨識身份。

<br/>

2. JWT 結構:

    JWT 全部是以 base64 進行加密，其結構如下所示:

    base64url_encode(Header) + '.' + base64url_encode(Claims) + '.' + base64url_encode(Signature)

    分有 3 個主要部份:

    * Header : 存放 meta 資料，包括 token 類型及加密方法。例如
        
            {
                "typ" : "JWT",
                "alg" : "HS256"
            }

    <br>

    * Claims: 存放 token 主要訊息。JWT 標準規定了一些標準規範如下

        *   iss : token 是给誰的
        *   sub : token 主題
        *   exp : token 過期時間
        *   issiat : token 建立時間
        *   jti : JWT ID

        除了標準規範，也可以自己加入東西進去

            {
                "iss": "google.com",
                "exp": 1435055117,
                "username": Johnny,
                "authorize": ["USER", "ADMIN"]
            }

    <br>

    * Signature: 簽名主要驗證 token 是否有效，是否被竄改。


<br>
<br>

---

<br>
<br>

<tag id="2"/>

## 二. 後端實作

<br>

1. 首按照慣例先配置好 [pom.xml](./pom.xml) 依賴:

    * 實作 jwt 最重要的3個依賴:

        ```xml
            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-api</artifactId>
                <version>0.10.7</version>
            </dependency>

            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-impl</artifactId>
                <version>0.10.7</version>
            </dependency>

            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-jackson</artifactId>
                <version>0.10.7</version>
            </dependency>
        ```

    * spring security starter 依賴:

        ```xml
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-security</artifactId>
                <version>2.2.1.RELEASE</version>
            </dependency>
        ```

    其餘不再一一列舉。

    <br>

2. Spring Security 基本 UserDetails 與 UserDetailService 實作:

    這邊不多做解釋，屬於 spring security 的基本設定。

    *   UserDetails 實作 : [SysUser.java](./entity/SysUser.java)
    *   UserRole 物件 : [SysRole.java](./entity/SysRole.java)
    *   UserDetailService 介面 : [UserService.java](./service/UserService.java)
    *   UserDetailService 實作 : [UserServiceImpl.java](./service/impl/UserServiceImpl.java)

<br>

3. [WebSecurityConfig](./config/WebSecurityConfig.java) 設定:

    有幾點要注意:

    <br>

    1.  首先有一個註解

            @EnableGlobalMethodSecurity(prePostEnabled = true)
        
        開啟這個註解之後就可以在 Controller 裡用註解控制訪問權。

        <br>
        <br>

    2. @Configuration 不可以用 @Autowired 注入 Bean，因為使用 AutoScan 的 Bean 還沒被初始化。這邊要用的 Bean 需自行定義。

        ```java
        @Bean
        UserService userService(){
            return new UserServiceImpl();
        }

        @Bean
        AuthenticationProvider customAuthenticationProvider() {
            return new CustomAuthenticationProvider();
        }
        ```

    <br>
    <br>

    3. CustomAuthenticationProvider 實作 spring security 官方的介面 AuthenticationProvider，這樣就可以自行定義帳密認證流程。具體時做細節在後面說明（[點這裡前往](#provider)）。

        在 config 中 Override 方法來添加 Provider:

        ```java
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            // 使用自定義的驗證
            auth.authenticationProvider(customAuthenticationProvider());
        }
        ```
    
    <br>
    <br>

    4.  在認證核心方法 configure(HttpSecurity http) 中，有兩個地方要特別注意 :

        ```java
        .addFilterBefore(new LoginFilter("/login", authenticationManager()), UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(new JWTAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        ```

        這邊定義了 2 個 Filter 分別為 LoginFilter 與 JWTAuthenticationFilter，用來作為登入邏輯處理與請求身份認證處理。
        實作細節後面會提到（[點這裡前往](#filter)）。
        
        使用 addFilterBefore( ) 添加 Filter 到請求進入點。

    <br>
    <br>

<tag id="provider">

4.  [CustomAuthenticationProvider](./config/provider/CustomAuthenticationProvider.java) 實作細節 :

    1. 實作介面 AuthenticationProvider 定義方法 Authentication authenticate(Authentication authentication) :

        *   這個方法定義了如何檢查帳密，搭配前面建立了 UserService 做 UserDetails 查詢。

        *   記得當帳密邏輯檢查錯誤時要拋出 AuthenticationException。

        * 當檢察通過之後就產生一個 Authentication 並回傳。

        ```java
        return new UsernamePasswordAuthenticationToken(username, password, user.getAuthorities());
        ```

<br>
<br>

<tag id="filter">

5. 自訂義 Filter 類別 :

    1.  [LoginFilter](./config/filter/LoginFilter.java) 實作細節 : 

        <br>

        LoginFilter 繼承抽象類別 AbstractAuthenticationProcessingFilter 並 Override 3 個方法 : 
        
        <br>

        1. ```attemptAuthentication(req, resp)```:

            用這個方法直接取出 request 的 username 與 password 參數，使用父類別的方法做登入驗證:

            ```java
            getAuthenticationManager()
                .authenticate(
                new UsernamePasswordAuthenticationToken(username,password)
            );
            ```

            getAuthenticationManager( ) 會取出我們在之前註冊的 [CustomAuthenticationProvider](#provider) 進行驗證。

            當驗證 <strong style="color:green">成功</strong> 之後則會把請求轉交給 ```successfulAuthentication(req, resp, chain, auth)``` 方法處理。


            當驗證 <strong style="color:red">失敗</strong> 之後則會把請求轉交給 ```unsuccessfulAuthentication(req, resp, chain, auth)``` 方法處理。

            <br>

        2. ```successfulAuthentication(req, resp, chain, auth)```:

            直接把東西包一包給 [JwtUtil](#jwt) 處理，我們後面再講解:

            ```java
            @Override
            protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse resp, FilterChain chain, Authentication auth) throws IOException, ServletException {
                // 登入成功，將token透過JwtUtil放到 response 中
                JwtUtil.addAuthentication(resp, auth);
            }
            ```

            <br>

        3. ```unsuccessfulAuthentication(req, resp, chain, auth)```:

            認證失敗就包一個 status-code : 401 的 JSON 給客戶端

            ```java
            @Override
            protected void unsuccessfulAuthentication(HttpServletRequest req, HttpServletResponse resp, AuthenticationException failed) throws IOException, ServletException {
                resp.setContentType("application/json");
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                JSONObject object = new JSONObject();
                object.append("status",401);
                resp.getOutputStream().println(object.toString());
            }
            ```

    <br><br><br>

    2.  [JWTAuthenticationFilter](./config/filter/JWTAuthenticationFilter.java) 實作細節 : 

        

        *   這個 Filter 用來處理 Header 帶 Token 的請求

        <br>


        *  繼承 GenericFilterBean 複寫 doFilter 方法:

            ```doFilter(req, resp, chain)```:

            ```java
            // 每次發送的請求都解析 Header 的 Authentication Token 資訊。
            @Override
            public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
                Authentication authentication = JwtUtil.getAuthentication((HttpServletRequest) req);
                SecurityContextHolder.getContext().setAuthentication(authentication); // 把認證放入上下文
                chain.doFilter(req,resp);
            }
            ```

    <br><br><br>

    <tag id="jwt">

    6.  [JwtUtil](./utils/JwtUtil.java) 實作細節 : 

        * 先定義幾組常數

            ```java
            static final long EXPIRATIONTIME = 432_000_000;     // 5天
            static final String HEADER_STRING = "Authorization";// 存放Token的Header Key
            static final Key key = MacProvider.generateKey();    //給定一組密鑰，用來解密以及加密使用
            ```

        <br>

        * addAuthentication( ) 用來產生帳號的 JWT 並包成 JSONObject 塞給 response

            ```java
            public static void addAuthentication(HttpServletResponse response, Authentication user) {
                Map<String, Object> auth = new HashMap<>();
                auth.put("authorize", user.getAuthorities());
                String jwts = Jwts.builder()
                        .setClaims(auth)
                        .setSubject(user.getName()) // setClaims() 一定要在 setSubject() 上面，不然會被覆蓋。
                        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
                        .signWith(key)
                        .compact();
                try {
                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getOutputStream().println(generateJsonResponse(user, jwts));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ```


        <br>

        * getAuthentication() 用來取出 request header 中的 token 資訊，解析後包裝成一個 Authentication 物件再回傳。

            ```java
            public static Authentication getAuthentication(HttpServletRequest request) {
                String token = request.getHeader(HEADER_STRING);
                if (token != null){
                    try{
                        Claims claims = Jwts.parser()
                                .setSigningKey(key)
                                .parseClaimsJws(token)
                                .getBody();
                        String user = claims.getSubject();
                        List<GrantedAuthority> authorities = generateGrantedAuthorityList(claims.get("authorize"));
                        return user != null ? new UsernamePasswordAuthenticationToken(user, null, authorities) : null;
                    }catch (JwtException ex){
                        System.out.println(ex);
                    }
                }
                return null;
            }
            ```

    <br><br><br>

    7.  Controller 部份

        * Controller 部份就很隨意了，直接看 code : 

        ```java
        @RestController
        public class NormalController {
            @PreAuthorize("hasAuthority('ADMIN')") // 只有 ADMIN 才進得來
            @RequestMapping("/sayHello")
            public String forwardToIndex(){
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String username = authentication.getName();
                System.out.println(username);
                authentication.getAuthorities().forEach(System.out::println);
                return username;
            }
        }
        ```

<br><br><br>

---

<br>

## 三. 實際測試

* 為了方便，我這邊直接用 python 發請求給 server。

    1. post : "http://localhost:8080/login"

        帶上 post 資料

        ```python
        post_data = {
            'username': 'Johnny'
            'password': 'letmein'
        }
        ```

        然後會取得 response，把 token 資訊放到 header 的 dicts 中。

        ```python
        header = {
            'Authorization': 'the token'
        }
        ```

        <br>

    2.  get : "http://localhost:3000/sayHello"

        帶上 header 資訊，結果可以得到 server 回傳的 username : Johnny

