package io.github.shield;


import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Shield {

  private static ProxyFactoryProvider proxyFactoryProvider;

  private final List<FilterFactory> filterFactories;
  private final ProxyFactory proxyFactory;

  static {
    proxyFactoryProvider(new DefaultProxyFactoryProvider());
  }

  public static void proxyFactoryProvider(final ProxyFactoryProvider p) {
    Shield.proxyFactoryProvider = p;
  }


  private Shield(final Object obj) {
    this.proxyFactory = proxyFactoryProvider.forObject(obj);
    this.filterFactories = new LinkedList<>();
  }


  /**
   * @param filterFactory
   * @return current shield object
   */
  public Shield filter(final FilterFactory filterFactory) {
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
  public static Shield forObject(final Object targetObject) {
    return new Shield(targetObject);
  }


  /**
   * Create proxy of the given type around the target object.
   *
   * @param type interface type of target component
   * @param <T>  interface type
   * @return proxy of the type that is passed as a parameter to this method
   */
  public <T> T as(final Class<T> type) {

    if (filterFactories.isEmpty()) {
      throw new IllegalStateException(
          "At least one filter must be provided"
      );
    }

    List<Filter> filters = filterFactories
        .stream()
        .map(FilterFactory::build)
        .collect(Collectors.toList());

    return proxyFactory.create(type, sort(filters));
  }


  private List<Filter> sort(final List<Filter> list) {
    return list.stream()
        .sorted()
        .collect(Collectors.toList());
  }

}
