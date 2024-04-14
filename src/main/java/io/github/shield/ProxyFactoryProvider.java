package io.github.shield;

public interface ProxyFactoryProvider {

  /**
   * Creates proxy factory for the given object.
   *
   * @param obj target object
   * @return proxy factory for given obj instance
   * @see ProxyFactory
   */
  <T> ProxyFactory forObject(final Class<T> type, Object obj);
}
