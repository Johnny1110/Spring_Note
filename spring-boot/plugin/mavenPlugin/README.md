# Spring Boot Maven 打包工具配置

<br>

使用 Maven 做 Spring Boot 應用開發，需要的專案打包 plugin 配置。

<br>

------------------------

<br>

## 目錄

* 一. [pom.xml 配置](#pom)

* 二. [Maven 操作 Spring Boot plugin 指令](#cmd)

<br>

---

<br>

## 實作

<div id="pom">

### 一. pom.xml 配置 　[（完整看這裡）](./pom.xml)

* 首先在 pom.xml 開頭先表明好專案要打包成甚麼類型。

    ```xml
    <groupId>com.frizo.learn</groupId>
    <artifactId>spring-boot-plugin</artifactId>
    <version>1.0-SNAPSHOT</version>
    <name>Learn Spring Boot Plugin</name>
    <!-- 在這裡也可以指定為 war -->
    <packaging>jar</packaging>

    ...
    ```


<br>
<br>

* 然後在 pom 的 build plugins 區塊添加如下配置資訊：

    ```xml
    <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
        <version>${spring.boot.version}</version>
        <configuration>
            <addResources>true</addResources>
            <mainClass>com.frizo.learn.vue.VueServerApplication</mainClass>
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

    解釋 :

    * configuration :

        * addResources: 開啟後當程式運行時可以即時同步靜態資源
        
        * mainClass : Spring Boot 應用的 Main Class 的相對位址

        * layout : 打包時順便把 binary code 包一個 ZIP 檔在 target 資料夾裡，也可以設定為 WAR 或 JAR 或 NONE

    * executions.execution :

        * goals.goal : 指定要運行的 plugin 目標

<br><br>

---

<br><br>

<div id="cmd">

### 二. Maven 操作 Spring Boot plugin 指令

* 標準啟動指令 : 

    ```bash
    mvn clean spring-boot:run
    ```

* 帶參數啟動指定 profile

    ```bash
    mvn clean spring-boot:run -Dspring-boot.run.profiles=xxx
    ```

* 打包指令

    ```bash
    mvn clean
    mvn package
    mvn spring-boot:repackage
    ```

    