# shield

Fault tolerance library for java

## Usage

```xml
<dependency>
    <groupId>io.github.hadielmougy</groupId>
    <artifactId>shield</artifactId>
    <version>0.1.1</version>
</dependency>
```

## Supported Interceptors

### Throttler

```java

    final Supplier<Void> throttler = Shield.decorate(target)
        .with(Interceptor.throttler()
            .requests(1)
            .maxWaitMillis(500))
        .build();

   
```
### Rate limit

```java

    final Supplier<Void> limiter = Shield.decorate(target)
        .with(Interceptor.rateLimiter()
            .rate(1))
        .build();

```
### Timeout

```java

    final Supplier<Void> decorated = Shield.decorate(target)
            .with(Interceptor.timeout().waitMillis(1100))
            .with(Interceptor.retry().delayMillis(1000).maxRetries(5))
            .build();
```
### Retry

```java

    final Retry retry = Interceptor.retry()
            .delayMillis(500)
            .maxRetries(3)
            .onException(IllegalArgumentException.class);


```
### Circuit-breaker

```java

    // count based
    final Supplier<Void> comp = Shield.decorate( component)
                .with(Interceptor.circuitBreaker()
                        .failureRateThreshold(50)
                        .slidingWindowSize(4)
                        .waitDurationInOpenState(Duration.ofSeconds(1))
                        .slidingWindowType(CircuitBreaker.WindowType.COUNT_BASED))
                .build();

    // time based
    final Supplier<Void> comp = Shield.decorate(target)
                .with(Interceptor.circuitBreaker()
                        .failureRateThreshold(50)
                        .slidingWindowSize(1)
                        .waitDurationInOpenState(Duration.ofSeconds(1))
                        .slidingWindowType(CircuitBreaker.WindowType.TIME_BASED))
                .build();

```

### Assemble

```java

    final Supplier<Void> decorated = Shield.decorate(() -> ...)
            .with(retry)
            .with(...)
            .build();
    
    decorated.get();
```

