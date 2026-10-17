# Resilience4j Spring Boot 예제

이 프로젝트는 Spring Boot와 [Resilience4j](https://resilience4j.readme.io/)를 활용하여 Circuit Breaker(회로차단기)와 Retry(재시도) 패턴을 적용한 예제입니다.

## 기술 스택

| 구분        | 버전                                       |
| ----------- | ------------------------------------------ |
| Java        | 25                                         |
| Spring Boot | 4.1.1 (Spring Framework 7, Tomcat 11)      |
| Resilience4j| 2.4.0 (`resilience4j-spring-boot4`)        |
| Gradle      | 9.8.1 (Wrapper 포함)                       |
| Lombok      | 1.18.48                                    |

## 주요 기능

-   **Circuit Breaker**: 서비스 장애 발생 시 자동으로 회로를 차단하여 장애 전파를 방지합니다.
-   **Retry**: 실패한 요청을 지정한 횟수만큼 재시도합니다.
-   REST API 엔드포인트를 통해 동작을 확인할 수 있습니다.

## 사전 준비

JDK 25가 필요합니다. 빌드는 Gradle Toolchain(`JavaLanguageVersion.of(25)`)으로 Java 25를 요구하므로, 아래 중 한 가지 방법으로 JDK 25를 지정하세요.

-   `JAVA_HOME`을 JDK 25 경로로 설정

    ```powershell
    # PowerShell
    $env:JAVA_HOME = "C:\java\jdk-25.0.4.1"
    ```

    ```bash
    # Bash
    export JAVA_HOME=/path/to/jdk-25
    ```

-   또는 `~/.gradle/gradle.properties`에 설치 경로 등록 (다른 JDK로 Gradle을 실행해도 Toolchain이 JDK 25를 찾음)

    ```properties
    org.gradle.java.installations.paths=C:/java/jdk-25.0.4.1
    ```

## 빌드 및 실행 방법

```bash
./gradlew build     # 컴파일 + 테스트
./gradlew bootRun   # 애플리케이션 실행 (http://localhost:8080)
```

Windows에서는 `gradlew.bat build`, `gradlew.bat bootRun`을 사용할 수 있습니다.

## REST API 엔드포인트

| 메서드 | 경로   | 설명                                          |
| ------ | ------ | --------------------------------------------- |
| GET    | /hello | Circuit Breaker 테스트 (`hello` 인스턴스)     |
| GET    | /test1 | Circuit Breaker 테스트 (`test1` 인스턴스)     |
| GET    | /test2 | Circuit Breaker 테스트 (`test2` 인스턴스)     |
| GET    | /retry | Retry 테스트 (`retry` 인스턴스)               |

각 서비스 메서드는 80% 확률로 `RuntimeException`을 발생시키며, 실패 시 fallback 메서드의 응답이 반환됩니다.
실패율이 임계치(50%)를 넘으면 회로가 열리고(OPEN), 이후 요청은 `CallNotPermittedException`으로 즉시 fallback 처리됩니다.

```bash
curl http://localhost:8080/hello
# hello world!
# 또는 hello : fallback invoked! exception type : class java.lang.RuntimeException
# 회로 OPEN 시 hello : fallback invoked! exception type : class io.github.resilience4j.circuitbreaker.CallNotPermittedException
```

## 주요 코드 설명

-   `HelloRestController`: REST API 엔드포인트 제공
-   `HelloService`: `@CircuitBreaker`, `@Retry` 어노테이션 적용
-   `application.yml`: Resilience4j 설정값 정의

> 참고: 어노테이션의 `name`과 일치하는 인스턴스가 `application.yml`의 `instances`에 없으면 `default` 설정이 적용됩니다.
> 현재 `test1`, `test2`(Circuit Breaker), `retry`(Retry)는 `instances`에 정의되어 있지 않으므로 `default` 설정으로 동작합니다.

## Resilience4j 설정 (`src/main/resources/application.yml`)

숫자만 적은 Duration 값은 밀리초(ms)로 해석되며, `3s`처럼 단위를 붙일 수도 있습니다.

```yaml
resilience4j:
  circuitbreaker:
    configs:
      default:
        failureRateThreshold: 50  #실패 비율이 이 값을 넘어가면 회로를 차단합니다.
        slowCallRateThreshold: 100
        slowCallDurationThreshold: 60000
        permittedNumberOfCallsInHalfOpenState: 4 #회로가 반-개방 상태일 때, 받아들일 요청의 개수를 지정합니다.
        maxWaitDurationInHalfOpenState: 1000 #회로가 반-개방 상태일 때, 처리하는 요청의 최대 타임아웃입니다.
        slidingWindowType: COUNT_BASED
        slidingWindowSize: 10
        minimumNumberOfCalls: 10
        waitDurationInOpenState: 10000
      test1:
        slidingWindowSize: 50
        permittedNumberOfCallsInHalfOpenState: 10
      test2:
        slidingWindowSize: 10
        permittedNumberOfCallsInHalfOpenState: 10
    instances:
      hello:
        baseConfig: default
      backendA:
        baseConfig: test1
        waitDurationInOpenState: 5000
      backendB:
        baseConfig: test2

  retry:
    configs:
      default:
        maxRetryAttempts: 3
        waitDuration: 3s
        ignoreExceptions:
          - java.util.NoSuchElementException
    instances:
      helloRetry:
        baseConfig: default
        maxRetryAttempts: 3
```

## 참고

-   [Resilience4j 공식 문서](https://resilience4j.readme.io/)
-   [Resilience4j Spring Boot 가이드](https://resilience4j.readme.io/docs/getting-started-3)
-   [Spring Boot 4.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide)
