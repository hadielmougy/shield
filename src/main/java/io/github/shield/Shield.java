package io.github.shield;


import io.github.shield.internal.SupplierWrapper;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class Shield<T> {

  private final List<FilterFactory> filterFactories;
  private final Supplier<T> supplier;

  public Shield(Supplier<T> supplier) {
    this.supplier = supplier;
    this.filterFactories = new LinkedList<>();
  }


  /**
   * @param filterFactory
   * @return current shield object
   */
  public Shield<T> with(final FilterFactory filterFactory) {
    this.filterFactories.add(Objects.requireNonNull(filterFactory,
        "filter can't be null"
    ));
    return this;
  }


  /**
   * Creates new shield object that wraps the target object.
   *
   * @param supplier
   * @return new instance of shield
   */
  public static <T> Shield<T> decorate(Supplier<T> supplier) {
    return new Shield<>(supplier);
  }


  /**
   * Create proxy of the given type around the target object.
   *
   * @return proxy of the type that is passed as a parameter to this method
   */
  public Supplier<T> build() {

    if (filterFactories.isEmpty()) {
      throw new IllegalStateException(
          "At least one filter must be provided"
      );
    }

    List<Filter> filters = filterFactories
        .stream()
        .map(FilterFactory::build)
        .collect(Collectors.toList());

    return new SupplierWrapper<>(supplier, sort(filters));
  }


  private List<Filter> sort(final List<Filter> list) {
    return list.stream()
        .sorted()
        .collect(Collectors.toList());
  }

}
