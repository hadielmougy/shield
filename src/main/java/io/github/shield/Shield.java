package io.github.shield;


import io.github.shield.internal.DefaultProxyFactoryProvider;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Shield<T> {

  private static ProxyFactoryProvider proxyFactoryProvider;

  private final List<FilterFactory> filterFactories;
  private final ProxyFactory proxyFactory;

  private Shield(final Class<T> type, final Object obj) {
    this.proxyFactory = new DefaultProxyFactoryProvider().forObject(type, obj);
    this.filterFactories = new LinkedList<>();
  }


  /**
   * @param filterFactory
   * @return current shield object
   */
  public Shield<T> filter(final FilterFactory filterFactory) {
    this.filterFactories.add(Objects.requireNonNull(filterFactory,
        "filter can't be null"
    ));
    return this;
  }


  /**
   * Creates new shield object that wraps the target object.
   *
   * @param targetObject
   * @return new instance of shield
   */
  public static <T> Shield<T> forObject(final Class<T> type, final Object targetObject) {
    return new Shield<>(type, targetObject);
  }


  /**
   * Create proxy of the given type around the target object.
   *
   * @return proxy of the type that is passed as a parameter to this method
   */
  public T build() {

    if (filterFactories.isEmpty()) {
      throw new IllegalStateException(
          "At least one filter must be provided"
      );
    }

    List<Filter> filters = filterFactories
        .stream()
        .map(FilterFactory::build)
        .collect(Collectors.toList());

    return proxyFactory.create(sort(filters));
  }


  private List<Filter> sort(final List<Filter> list) {
    return list.stream()
        .sorted()
        .collect(Collectors.toList());
  }

}
