# Spring Boot 熱部屬配置

<br>

使用 Spring Boot 開發 Web 專案時，一定會遇到需要熱部屬的問題，去網路上查有時解法也缺斤少兩，這邊直接寫一個彙整後的完整的解決方案

<br>

---

<br>

## 目錄

* ### [一. pom.xml 配置及依賴](#pom)

* ### [二. properties.yml 設定](#yml)

<br>

---

<br>


## 實作

<div id="pom"></div>

### 一. pom.xml 配置及依賴 [（看完整）](./pom.xml)

* 熱部屬 devtools 依賴 

    ```xml
    <!-- spring boot devtool 開發階段熱部屬自動配置依賴 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <version>${spring.boot.version}</version>
        <optional>true</optional>
    </dependency>
    ```

<br/>

* Spring Boot Plugin 配置

    ```xml
    <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
        <version>${spring.boot.version}</version>
        <configuration>
            <addResources>true</addResources>
            <mainClass>${start-class}</mainClass>
            <layout>ZIP</layout>
        </configuration>
        <executions>
            <execution>
                <goals>
                    <goal>repackage</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
    ```

    addResources 屬性一定要加，方便靜態資源自動更新。

<br/>

---

<br/>

<div id="yml"></div>

### 二. properites.yml 配置 [（看完整）](./properites.yml)

* properites.yml 配置屬性

    ```yml
    # spring 配置
    spring:
    devtools:
        restart:
            enabled: true

    ## thymeleaf 模板資源必須如此配置才可以支援部屬 war
    thymeleaf:
        prefix: classpath:templates/
        suffix: .html
        cache: false
        enabled: true
        encoding: UTF-8
        mode: LEGACYHTML5

    ## 靜態資源必須如此配置才可以支援部屬 war
    resources:
        static-locations: classpath:static/
        cache:
        cachecontrol:
            no-cache: true



    # tomcat 配置
    server:
    port: 8080
    ```
