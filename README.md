# shield

Fault tolerance library for java

## Usage

```java
Shield shield = new Shield();

shield.addFilter(Filter.retry()
                    .timeUnit(TimeUnit.MILLISECONDS)
                    .withDelay(500)
                    .withMaxRetries(3)
                    .onException(IllegalArgumentException.class)
                    .build());


final ComponentInterface comp = shield.forObject(componentObj).as(ComponentInterface.class);

comp.doSomething();
```