package io.github.shield.internal;

import io.github.shield.ProxyFactory;
import io.github.shield.ProxyFactoryProvider;
import java.util.Objects;

public class DefaultProxyFactoryProvider implements ProxyFactoryProvider {


  public DefaultProxyFactoryProvider() {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> ProxyFactory forObject(final Class<T> type, final Object obj) {
    return new JdkProxyFactory(type, Objects.requireNonNull(obj));
  }
}
