package io.github.shield.internal;

import io.github.shield.Filter;
import io.github.shield.ProxyFactory;
import java.util.List;

/**
 *
 */
public final class JdkProxyFactory<T> implements ProxyFactory {

  /**
   *
   */
  private final Object targetObjecct;
  private final Class<T> type;

  /**
   * JDK proxy factory.
   *
   * @param obj
   */
  public JdkProxyFactory(final Class<T> type, final Object obj) {
    this.targetObjecct = obj;
    this.type = type;
  }


  /**
   * Create proxy for the given type.
   *
   * @param filters
   * @param <T>
   * @return proxy
   */
  public <T> T create(final List<Filter> filters) {
    return (T) java.lang.reflect.Proxy.newProxyInstance(
        targetObjecct.getClass().getClassLoader(),
        new Class[]{type},
        new InvocationInterceptor(targetObjecct, filters));
  }


}
