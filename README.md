# shield

Fault tolerance library for java

## Usage

```java
    final IComponent comp = Shield.forObject(componentObj)
                .filter(Filter.retry()
                        .delayMillis(500)
                        .maxRetries(3)
                        .onException(IllegalStateException.class)
                        .build())
                .as(IComponent.class);

    comp.doSomething();
```