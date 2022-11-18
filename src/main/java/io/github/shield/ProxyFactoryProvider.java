package io.github.shield;

public interface ProxyFactoryProvider {

  /**
   * Creates proxy factory for the given object.
   *
   * @param obj target object
   * @return proxy factory for given obj instance
   * @see ProxyFactory
   */
  ProxyFactory forObject(Object obj);
}
