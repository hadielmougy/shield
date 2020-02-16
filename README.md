# shield

Fault tolerance library for java

## Usage

```java
    final IComponent comp = Shield.forObject(componentObj)
                .withFilter(Filter.retry()
                        .delayMillis(500)
                        .maxRetries(3)
                        .onException(IllegalArgumentException.class)
                        .build())
                .as(IComponent.class);

    comp.doSomething();
```