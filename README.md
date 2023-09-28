# shield

Fault tolerance library for java

## Usage

```java
    final IComponent comp=Shield.forObject(componentObj)
        .filter(Filter.retry())
        .delayMillis(500)
        .maxRetries(3)
        .backoff()
        .onException(IllegalStateException.class)
        .onException(AnotherException.class)
        .as(IComponent.class);

        comp.doSomething();
```

## Supported Filters

* Throttler
* Rate limit
* Timeout
* Fire and forget
* Retry
* Circuit-breaker

## TODO

* Metrics

