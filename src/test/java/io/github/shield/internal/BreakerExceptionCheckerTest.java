package io.github.shield.internal;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link BreakerExceptionChecker}.
 *
 * <p>Matching is delegated to {@code ExceptionUtil.isClassFoundInStackTrace}, which inspects the
 * thrown throwable itself and then walks its cause chain. These tests cover both the thrown
 * exception and wrapped-cause matching.
 */
public class BreakerExceptionCheckerTest {

    @SafeVarargs
    private static Class<? extends Throwable>[] classes(Class<? extends Throwable>... cs) {
        return cs;
    }

    private static final Class<? extends Throwable>[] NONE = classes();

    @Test
    public void recordsEverythingByDefault() {
        BreakerExceptionChecker checker = new BreakerExceptionChecker(NONE, NONE);
        Assert.assertTrue(checker.shouldRecord(new RuntimeException()));
        Assert.assertTrue(checker.shouldRecord(new IllegalStateException(new NullPointerException())));
    }

    @Test
    public void ignoresExceptionMatchedInCauseChain() {
        BreakerExceptionChecker checker =
                new BreakerExceptionChecker(classes(IllegalStateException.class), NONE);
        Throwable wrapped = new RuntimeException(new IllegalStateException());
        Assert.assertFalse(checker.shouldRecord(wrapped));
    }

    @Test
    public void ignoreMatchesBySuperType() {
        // IllegalArgumentException is a subclass of RuntimeException -> assignable, so ignored
        BreakerExceptionChecker checker =
                new BreakerExceptionChecker(classes(RuntimeException.class), NONE);
        Throwable wrapped = new RuntimeException(new IllegalArgumentException());
        Assert.assertFalse(checker.shouldRecord(wrapped));
    }

    @Test
    public void recordsWhenIgnoredTypeIsNotInCauseChain() {
        BreakerExceptionChecker checker =
                new BreakerExceptionChecker(classes(IllegalStateException.class), NONE);
        Throwable wrapped = new RuntimeException(new NullPointerException());
        Assert.assertTrue(checker.shouldRecord(wrapped));
    }

    @Test
    public void recordsWhenRecordTypeMatchedInCauseChain() {
        BreakerExceptionChecker checker =
                new BreakerExceptionChecker(NONE, classes(IllegalStateException.class));
        Throwable wrapped = new RuntimeException(new IllegalStateException());
        Assert.assertTrue(checker.shouldRecord(wrapped));
    }

    @Test
    public void doesNotRecordWhenRecordTypeIsNotInCauseChain() {
        BreakerExceptionChecker checker =
                new BreakerExceptionChecker(NONE, classes(IllegalStateException.class));
        Throwable wrapped = new RuntimeException(new NullPointerException());
        Assert.assertFalse(checker.shouldRecord(wrapped));
    }

    @Test
    public void matchesTheThrownExceptionItselfWithoutACause() {
        // A raw exception whose own type is listed is matched directly.
        BreakerExceptionChecker recordChecker =
                new BreakerExceptionChecker(NONE, classes(IllegalStateException.class));
        Assert.assertTrue(recordChecker.shouldRecord(new IllegalStateException()));

        BreakerExceptionChecker ignoreChecker =
                new BreakerExceptionChecker(classes(IllegalStateException.class), NONE);
        // ignored (so not recorded) because the thrown type itself matches
        Assert.assertFalse(ignoreChecker.shouldRecord(new IllegalStateException()));
    }
}