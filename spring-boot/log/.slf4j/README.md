# 配置 Slf4J

<br>

---

<br>

Spring Boot 配置 `@Slf4j` 看這邊 ：

 <br>

 ## pom.xml


 <br>

 套件依賴

 <br>

 ```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
    <version>2.7.2</version>
</dependency>

<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
    <version>1.18.24</version>
</dependency>
 ```

<br>

plugin 依賴：

<br>

```xml
<plugin>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok-maven-plugin</artifactId>
    <version>1.18.20.0</version>
</plugin>
```

<br>
<br>
<br>
<br>


## src/main/resources/application.yml

<br>

```yml
logging:
  config: classpath:logback-spring.xml
  level:
    org.springframework.boot.autoconfigure: info
```

<br>
<br>
<br>
<br>

## src/main/resources/logback-spring.xml

<br>

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration scan="true" scanPeriod="60 seconds" debug="false">
    <contextName>logback</contextName>
    <property name="log.path" value="../"/>

    <!--輸出到 console -->
    <appender name="console" class="ch.qos.logback.core.ConsoleAppender">
        <!-- <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
             <level>ERROR</level>
         </filter>-->
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %magenta(%-5level) %green([%-50.50class]) >>> %cyan(%msg) %n
            </pattern>
        </encoder>
    </appender>

    <!--輸出到 file -->
    <appender name="file" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${log.path}/example.%d{yyyy-MM-dd}.log</fileNamePattern>
        </rollingPolicy>
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} %contextName [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="info">
        <appender-ref ref="console"/>
        <appender-ref ref="file"/>
    </root>

    <logger name="com.example"/>
    <logger name="com.example.finalversion" level="info" additivity="false">
        <appender-ref ref="console"/>
        <appender-ref ref="file"/>
    </logger>

</configuration>
```

<br>
<br>
<br>
<br>

## 使用 `@Slf4j`

<br>

```java
@Slf4j
@Service
public class DemoService {

    public void doSomething() {
        log.info("do Something.")
    }

    ppublic void throwException() {
        try {
            throw new RuntimeException("some error.");
        }catch (RuntimeExeption ex){
            log.error("error", ex)
        }
    }

}
```