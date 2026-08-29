# shield

Fault tolerance library for java

## Usage

```xml
<dependency>
    <groupId>io.github.hadielmougy</groupId>
    <artifactId>shield</artifactId>
    <version>0.1.2</version>
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
### Adaptive throttler

Concurrency throttler whose limit adapts to call outcomes (AIMD): it shrinks the limit
multiplicatively when admitted calls fail and grows it back one permit per success, staying within
`[minRequests, requests]`.

```java

    final Supplier<Void> throttler = Shield.decorate(target)
        .with(Interceptor.adaptiveThrottler()
            .requests(10)          // maximum (and starting) concurrency
            .minRequests(1)        // floor the limit never drops below
            .backoffRatio(0.5)     // multiply the limit by this on each failure
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

### Lifecycle

`build()` returns a `ShieldedSupplier`, which is a `Supplier` that is also `AutoCloseable`.
The timeout and rate-limiter interceptors allocate thread pools, so long-lived decorated
suppliers should be closed when no longer needed to release those resources:

```java

    try (ShieldedSupplier<Void> decorated = Shield.decorate(() -> ...)
            .with(Interceptor.timeout().waitMillis(1000))
            .build()) {
        decorated.get();
    }
```

`close()` is idempotent and is a no-op for chains that hold no executors (e.g. circuit breaker,
retry, throttler).

