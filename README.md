# Resilience4j Spring Boot 예제

이 프로젝트는 Spring Boot와 [Resilience4j](https://resilience4j.readme.io/)를 활용하여 Circuit Breaker(회로차단기)와 Retry(재시도) 패턴을 적용한 예제입니다.

## 주요 기능

-   **Circuit Breaker**: 서비스 장애 발생 시 자동으로 회로를 차단하여 장애 전파를 방지합니다.
-   **Retry**: 실패한 요청을 지정한 횟수만큼 재시도합니다.
-   REST API 엔드포인트를 통해 동작을 확인할 수 있습니다.

## 빌드 및 실행 방법

```bash
./gradlew build
./gradlew bootRun
```

## REST API 엔드포인트

| 메서드 | 경로   | 설명                   |
| ------ | ------ | ---------------------- |
| GET    | /hello | Circuit Breaker 테스트 |
| GET    | /test1 | Circuit Breaker 테스트 |
| GET    | /test2 | Circuit Breaker 테스트 |
| GET    | /retry | Retry 테스트           |

## 주요 코드 설명

-   `HelloRestController`: REST API 엔드포인트 제공
-   `HelloService`: Circuit Breaker, Retry 어노테이션 적용
-   `application.yml`: Resilience4j 설정값 정의

## Resilience4j 설정 예시 (`src/main/resources/application.yml`)

```yaml
resilience4j:
    circuitbreaker:
        configs:
            default:
                failureRateThreshold: 50
                slidingWindowSize: 10
                waitDurationInOpenState: 10000
        instances:
            hello:
                baseConfig: default
    retry:
        configs:
            default:
                maxRetryAttempts: 3
                waitDuration: 3s
        instances:
            helloRetry:
                baseConfig: default
```

## 참고

-   [Resilience4j 공식 문서](https://resilience4j.readme.io/docs/getting-started-3)
-   [Spring Cloud Circuit Breaker](https://cloud.spring.io/spring-cloud-static/spring-cloud-circuitbreaker/current/reference/html/)
