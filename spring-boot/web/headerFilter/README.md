# 過濾掉不需要的 Response Header

<br><br>

---

<br><br>

當 restful 回應 Header 中出現了不想要的 Header，可以透過實現一個 `WebFilter` 來過濾掉 Response Header:

<br><br>

```java
@Component
@Order(0) // 數字越小，Filter 順序月靠前，這個 HeaderFilter 在第一個執行之後，往後的 Response Header 都會套用我設定的 HttpServletResponseWrapper
public class VaryHeaderFilter implements Filter{

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, new HttpServletResponseWrapper((HttpServletResponse) response) {
            @Override
            public void addHeader(String name, String value) {
                // VARY Header 不要
                if (!HttpHeaders.VARY.equals(name)) {
                    super.addHeader(name, value);
                }
            }

            @Override
            public void setHeader(String name, String value) {
                if (!HttpHeaders.VARY.equals(name)) {
                    super.setHeader(name, value);
                }
            }
        });
    }
}
```