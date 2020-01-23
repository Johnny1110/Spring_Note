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

    ## 熱部屬配置
    devtools:
        restart:
        enabled: true

    ## thymeleaf 模板資源必須如此配置才可以支援 spring boot devtools 熱部屬
    thymeleaf:
        prefix: file:src/main/resources/templates/
        cache: false
        enabled: true
        encoding: UTF-8

    ## 靜態資源必須如此配置才可以支援 spring boot devtools 熱部屬
    resources:
        static-locations: file:src/main/resources/static/
        cache:
        cachecontrol:
            no-cache: true
    ```
