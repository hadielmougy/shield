# shield

Fault tolerance library for java

## Usage

```java

    final Retry retry = Filter.retry()
            .delayMillis(500)
            .maxRetries(3)
            .onException(IllegalArgumentException.class);

    final Supplier<Void> decorated = Shield.decorate(() -> ...)
            .with(retry)
            .build();
    
    decorated.get();
```

## Supported Filters

* Throttler
* Rate limit
* Timeout
* Fire and forget
* Retry
* Circuit-breaker

