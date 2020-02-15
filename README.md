# shield

Fault tolerance library for java

## Usage

```java
    final IComponent comp = Shield.forObject(componentObj)
                .withFilter(Filter.retry()
                        .timeUnit(TimeUnit.MILLISECONDS)
                        .withDelay(500)
                        .withMaxRetries(3)
                        .onException(IllegalArgumentException.class)
                        .build())
                .as(IComponent.class);

    comp.doSomething();
```