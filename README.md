# Shield

A lightweight, composable fault-tolerance library for Java. Shield lets you wrap any
`Supplier<T>` with one or more resilience **interceptors** — circuit breaker, retry, timeout,
throttling, and rate limiting — and compose them into a single decorated `Supplier`.

- **Zero heavy dependencies** — only `jcip-annotations` at runtime.
- **Composable** — stack interceptors in any combination.
- **Java 11+**.

## Table of contents

- [Installation](#installation)
- [Quick start](#quick-start)
- [Core concepts](#core-concepts)
  - [Decorating a supplier](#decorating-a-supplier)
  - [The `ShieldedSupplier` handle](#the-shieldedsupplier-handle)
  - [Composition and ordering](#composition-and-ordering)
  - [Exception semantics](#exception-semantics)
  - [Lifecycle and resource management](#lifecycle-and-resource-management)
- [Interceptors](#interceptors)
  - [Circuit breaker](#circuit-breaker)
  - [Retry](#retry)
  - [Timeout](#timeout)
  - [Throttler](#throttler)
  - [Adaptive throttler](#adaptive-throttler)
  - [Rate limiter](#rate-limiter)
- [Exception reference](#exception-reference)
- [Thread safety](#thread-safety)
- [Building from source](#building-from-source)

## Installation

Gradle:

```groovy
implementation 'io.github.hadielmougy:shield:0.1.3'
```

Maven:

```xml
<dependency>
    <groupId>io.github.hadielmougy</groupId>
    <artifactId>shield</artifactId>
    <version>0.1.3</version>
</dependency>
```

## Quick start

```java
import io.github.shield.Shield;
import io.github.shield.Interceptor;
import io.github.shield.ShieldedSupplier;

import java.time.Duration;

// Wrap a call with a circuit breaker and a retry.
ShieldedSupplier<String> call = Shield.decorate(() -> httpClient.get("/orders"))
        .with(Interceptor.circuitBreaker()
                .failureRateThreshold(50)
                .slidingWindowSize(10))
        .with(Interceptor.retry()
                .maxRetries(3)
                .delayMillis(200))
        .build();

String body = call.get();
```

## Core concepts

### Decorating a supplier

Everything starts from `Shield.decorate(Supplier<T>)`. You attach interceptors with `.with(...)`
and finish with `.build()`:

```java
ShieldedSupplier<T> decorated = Shield.decorate(target)
        .with(interceptorA)
        .with(interceptorB)
        .build();

T result = decorated.get();
```

`build()` throws `IllegalStateException` if no interceptor was added.

### The `ShieldedSupplier` handle

`build()` returns a `ShieldedSupplier<T>`, which is:

- a `java.util.function.Supplier<T>` — call `.get()` to execute the decorated invocation;
- an `AutoCloseable` — call `.close()` to release resources (see
  [Lifecycle](#lifecycle-and-resource-management)).

Because it is a `Supplier`, existing code that expects `Supplier<T>` keeps working:

```java
Supplier<T> asSupplier = Shield.decorate(target).with(...).build();
```

### Composition and ordering

Interceptors execute in the order they are added, outermost first, wrapping inward toward the
target:

```
get() → interceptorA → interceptorB → ... → target supplier
```

For example, `decorate(target).with(timeout).with(retry)` produces
`timeout( retry( target ) )`: the timeout bounds the *entire* retry sequence, while
`decorate(target).with(retry).with(timeout)` retries around *each* timed call. Choose the order
deliberately.

### Exception semantics

Shield interceptors **propagate** the target's own exception rather than swallowing it. Each
interceptor may additionally throw its own control-flow exception (for example
`CircuitBreakerOpenException` when the breaker is open). See the
[Exception reference](#exception-reference) for the full list. A call that fails therefore throws;
it never silently returns `null`.

### Lifecycle and resource management

The **timeout** and **rate limiter** interceptors allocate thread pools internally. If you build
long-lived decorated suppliers (or many of them dynamically), close them when done to release those
pools:

```java
try (ShieldedSupplier<Void> decorated = Shield.decorate(target)
        .with(Interceptor.timeout().waitMillis(1000))
        .build()) {
    decorated.get();
}
```

`close()` is idempotent and is a no-op for interceptors that hold no executors (circuit breaker,
retry, throttler, adaptive throttler).

## Interceptors

All interceptors are created from static factory methods on `Interceptor`.

### Circuit breaker

Stops calling a failing dependency once its failure rate crosses a threshold, giving it time to
recover. Implements the classic **closed → open → half-open** state machine.

```java
import io.github.shield.CircuitBreaker;
import java.time.Duration;

// Count-based window
ShieldedSupplier<Void> comp = Shield.decorate(target)
        .with(Interceptor.circuitBreaker()
                .slidingWindowType(CircuitBreaker.WindowType.COUNT_BASED)
                .slidingWindowSize(10)                       // evaluate over the last 10 calls
                .failureRateThreshold(50)                    // open at >= 50% failures
                .waitDurationInOpenState(Duration.ofSeconds(2))
                .permittedNumberOfCallsInHalfOpenState(3))   // trial calls before re-closing
        .build();

// Time-based window
ShieldedSupplier<Void> comp2 = Shield.decorate(target)
        .with(Interceptor.circuitBreaker()
                .slidingWindowType(CircuitBreaker.WindowType.TIME_BASED)
                .slidingWindowSize(5)                        // 5-second window
                .minimumNumberOfCalls(20)                    // need >= 20 calls before evaluating
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(2)))
        .build();
```

**Options**

| Method | Default | Meaning |
|--------|---------|---------|
| `slidingWindowType(WindowType)` | `COUNT_BASED` | `COUNT_BASED` or `TIME_BASED`. |
| `slidingWindowSize(int)` | `100` | Count-based: number of calls in the window. Time-based: window length **in seconds**. |
| `failureRateThreshold(int)` | `50` | Percentage of failures (0–100) at which the breaker opens. |
| `waitDurationInOpenState(Duration)` | `1s` | How long the breaker stays open before trialing again. Sub-second durations are honored. |
| `permittedNumberOfCallsInHalfOpenState(int)` | `0` | Trial calls allowed in half-open. `0` means the breaker goes straight back to closed after the wait. |
| `minimumNumberOfCalls(int)` | `100` | **Time-based only.** Minimum calls in the window before the failure rate is evaluated. |
| `recordExceptions(Class...)` | *(all)* | If set, **only** these exception types count as failures. |
| `ignoreExceptions(Class...)` | *(none)* | These exception types are not counted as failures. |

**Behavior**

- **Closed** — calls pass through and are recorded. After each call the failure rate is
  recomputed; if the window is full/elapsed and the rate is at or above the threshold, the breaker
  opens. The call that trips the breaker still returns its own result or exception; only
  subsequent calls are rejected.
- **Open** — every call is rejected immediately with `CircuitBreakerOpenException` for
  `waitDurationInOpenState`, then the breaker transitions to half-open (if
  `permittedNumberOfCallsInHalfOpenState > 0`) or closed.
- **Half-open** — up to `permittedNumberOfCallsInHalfOpenState` trial calls are admitted (one at a
  time). If they all succeed the breaker closes; if any fails it re-opens.
- **Exception matching** — `recordExceptions` / `ignoreExceptions` inspect the thrown exception and
  its cause chain. `recordExceptions` takes precedence: when set, anything not listed is treated as
  a success.

Throws: `io.github.shield.internal.CircuitBreakerOpenException` while open.

### Retry

Re-invokes the target on failure, up to a maximum number of attempts, with a configurable delay
policy.

```java
import java.util.concurrent.TimeUnit;

ShieldedSupplier<Void> comp = Shield.decorate(target)
        .with(Interceptor.retry()
                .maxRetries(4)                       // total attempts
                .delayMillis(200)                    // delay between attempts
                .backOff()                           // exponential delay
                .onException(IOException.class))     // only retry these
        .build();
```

**Options**

| Method | Default | Meaning |
|--------|---------|---------|
| `maxRetries(int)` | `3` | Maximum **total** attempts (not additional retries). |
| `delayMillis(long)` | `1000` | Delay between attempts, in milliseconds. |
| `delaySeconds(long)` | — | Delay between attempts, in seconds. |
| `fixed()` | *(default)* | Constant delay between attempts. |
| `backOff()` | — | Exponential delay: each wait is double the previous one. |
| `onException(Class)` | *(all)* | Restrict retries to this exception type (call repeatedly to add more). If never called, all exceptions are retried. |

**Behavior**

- If a call succeeds, its result is returned immediately.
- If a call throws an exception that is **not** retryable (per `onException`), that exception is
  rethrown as-is.
- If all attempts are exhausted, throws `RetriesExhaustedException` wrapping the last failure.
- `onException` matching considers the exception type, its supertypes, and its cause chain.

Throws: `io.github.shield.internal.RetriesExhaustedException` when attempts are exhausted; the
original exception if it is not retryable.

### Timeout

Runs the target on a separate thread and abandons it if it does not complete within the deadline.

```java
ShieldedSupplier<Void> comp = Shield.decorate(target)
        .with(Interceptor.timeout().waitMillis(1000))
        .build();

// or
Interceptor.timeout().waitSeconds(2);
```

**Options**

| Method | Meaning |
|--------|---------|
| `waitMillis(long)` | Timeout in milliseconds. |
| `waitSeconds(long)` | Timeout in seconds. |

**Behavior**

- Completes normally and returns the value if the call finishes in time.
- If the deadline is exceeded, the worker is interrupted and `TimeoutExceededException` is thrown.
- If the target throws, that exception is propagated unchanged (not swallowed).
- Uses an internal thread pool — **close the supplier** to release it (see
  [Lifecycle](#lifecycle-and-resource-management)).

Throws: `io.github.shield.TimeoutExceededException` on timeout;
`io.github.shield.InvocationCancelledException` if the waiting thread is interrupted.

### Throttler

A **concurrency limiter**: caps the number of calls executing at the same time using a fair
semaphore.

```java
ShieldedSupplier<Void> comp = Shield.decorate(target)
        .with(Interceptor.throttler()
                .requests(5)             // at most 5 concurrent calls
                .maxWaitMillis(500))     // wait up to 500ms for a slot
        .build();
```

**Options**

| Method | Default | Meaning |
|--------|---------|---------|
| `requests(int)` | `10` | Maximum number of concurrent in-flight calls. |
| `maxWaitMillis(long)` | `500` | How long a call waits to acquire a slot before being rejected. |

**Behavior**

- Acquires a permit before the call and releases it after (success or failure).
- If no permit becomes available within `maxWaitMillis`, the call is rejected with
  `InvocationCancelledException`.

Throws: `io.github.shield.InvocationCancelledException` when a slot cannot be acquired in time.

### Adaptive throttler

A concurrency limiter whose limit **adapts to call outcomes** using an additive-increase /
multiplicative-decrease (AIMD) policy — the same family as TCP congestion control. It sheds load
when the downstream is struggling and recovers as it heals.

```java
ShieldedSupplier<Void> comp = Shield.decorate(target)
        .with(Interceptor.adaptiveThrottler()
                .requests(20)           // maximum (and starting) concurrency
                .minRequests(2)         // floor the limit never drops below
                .backoffRatio(0.5)      // multiply the limit by this on each failure
                .maxWaitMillis(500))    // wait up to 500ms for a slot
        .build();
```

**Options**

| Method | Default | Meaning |
|--------|---------|---------|
| `requests(int)` | `10` | Maximum and initial concurrency limit. |
| `minRequests(int)` | `1` | Lower bound for the adaptive limit. Must be `<= requests`. |
| `backoffRatio(double)` | `0.5` | Factor applied to the current limit on each failure, in `(0, 1)`. |
| `maxWaitMillis(long)` | `500` | How long a call waits to acquire a slot before being rejected. |

**Behavior**

- Starts at `requests` and is never allowed above it.
- **On failure** (an admitted call throws), the limit is multiplied by `backoffRatio` and floored
  at `minRequests`.
- **On success**, the limit grows back by one, up to `requests`.
- Local rejections (a call that cannot acquire a slot in time) do **not** change the limit — that
  is the limiter working as intended. Only the outcome of admitted calls adjusts it.
- The target's exception is propagated after the limit is adjusted.

Throws: `io.github.shield.InvocationCancelledException` when a slot cannot be acquired in time.

### Rate limiter

Caps the number of calls admitted **per second** using a fixed window that refills every second.

```java
ShieldedSupplier<Void> comp = Shield.decorate(target)
        .with(Interceptor.rateLimiter().rate(100))   // up to 100 calls/second
        .build();
```

**Options**

| Method | Default | Meaning |
|--------|---------|---------|
| `rate(int)` | `10` | Maximum number of calls admitted per one-second window. |

**Behavior**

- Each call consumes one permit; permits are refilled to `rate` at the start of every second.
- A call waits briefly to acquire a permit; if none is available it is rejected with
  `InvocationCancelledException`.
- Uses an internal scheduled thread pool — **close the supplier** to release it (see
  [Lifecycle](#lifecycle-and-resource-management)).

Throws: `io.github.shield.InvocationCancelledException` when no permit is available.

## Exception reference

| Exception | Package | Thrown by | Meaning |
|-----------|---------|-----------|---------|
| `CircuitBreakerOpenException` | `io.github.shield.internal` | Circuit breaker | The breaker is open (or half-open and saturated); the call was rejected. |
| `RetriesExhaustedException` | `io.github.shield.internal` | Retry | All retry attempts failed; wraps the last failure as its cause. |
| `TimeoutExceededException` | `io.github.shield` | Timeout | The call did not complete within the deadline. |
| `InvocationCancelledException` | `io.github.shield` | Throttler, Adaptive throttler, Rate limiter, Timeout | The call could not be admitted (no slot/permit) or the waiting thread was interrupted. |

All of these extend `RuntimeException`, so they are unchecked.

## Thread safety

Decorated suppliers are safe to share and invoke from multiple threads. The throttler, adaptive
throttler, and rate limiter use semaphores to coordinate concurrent access; the circuit breaker
guards state transitions internally. The timeout interceptor executes each call on a pooled worker
thread and copies the invocation context to it.

## Building from source

The project builds with [Gradle](https://gradle.org/) via the wrapper (no local Gradle install
required):

```bash
./gradlew build
```

Run the tests:

```bash
./gradlew test
```

## Releasing

Releases are published to [Maven Central](https://central.sonatype.com/) through the Sonatype
Central Portal, using the [`com.vanniktech.maven.publish`](https://vanniktech.github.io/gradle-maven-publish-plugin/)
plugin. Artifacts (main jar, sources jar, javadoc jar) are GPG-signed.

**One-time setup**

1. **Central Portal token** — generate a user token at central.sonatype.com (*Account → Generate
   User Token*) for the account that owns the verified `io.github.hadielmougy` namespace, and add it
   to `~/.gradle/gradle.properties`:

   ```properties
   mavenCentralUsername=<token-username>
   mavenCentralPassword=<token-password>
   ```

   (Alternatively, export `ORG_GRADLE_PROJECT_mavenCentralUsername` /
   `ORG_GRADLE_PROJECT_mavenCentralPassword`.) Gradle does **not** read `~/.m2/settings.xml`.

2. **Signing** — the build signs with your local GPG key via `gpg` (`signing { useGpgCmd() }`), so
   an unlocked key in your GnuPG keyring is all that is needed.

**Cut a release**

1. Set the release version in `build.gradle` (`version = '…'`).
2. Publish and auto-release:

   ```bash
   ./gradlew publishAndReleaseToMavenCentral
   ```

   Use `./gradlew publishToMavenCentral` instead to upload to a staging repository and release
   manually from the Portal UI.
3. Tag the release and push:

   ```bash
   git tag v<version>
   git push origin v<version>
   ```
