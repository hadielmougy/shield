package io.github.shield;

import java.util.Objects;

public class DefaultProxyFactoryProvider implements ProxyFactoryProvider {

  /**
   * {@inheritDoc}
   */
  @Override
  public ProxyFactory forObject(final Object obj) {
    return new JdkProxyFactory(Objects.requireNonNull(obj));
  }
}
