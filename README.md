# shield

Fault tolerance library for java

## Usage

```xml
<dependency>
    <groupId>io.github.hadielmougy</groupId>
    <artifactId>shield</artifactId>
    <version>0.1</version>
</dependency>
```

```java

    final Retry retry = Interceptor.retry()
            .delayMillis(500)
            .maxRetries(3)
            .onException(IllegalArgumentException.class);

    final Supplier<Void> decorated = Shield.decorate(() -> ...)
            .with(retry)
            .build();
    
    decorated.get();
```

## Supported Interceptors

* Throttler
* Rate limit
* Timeout
* Retry
* Circuit-breaker

