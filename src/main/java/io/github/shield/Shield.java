package io.github.shield;


import io.github.shield.internal.SupplierWrapper;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class Shield<T> {

  private final List<InterceptorBuilder> interceptorBuilders;
  private final Supplier<T> supplier;

  public Shield(Supplier<T> supplier) {
    this.supplier = supplier;
    this.interceptorBuilders = new LinkedList<>();
  }

  public Shield<T> with(final InterceptorBuilder interceptorBuilder) {
    this.interceptorBuilders.add(Objects.requireNonNull(interceptorBuilder,
        "filter can't be null"
    ));
    return this;
  }

  public static <T> Shield<T> decorate(Supplier<T> supplier) {
    return new Shield<>(supplier);
  }


  public Supplier<T> build() {

    if (interceptorBuilders.isEmpty()) {
      throw new IllegalStateException(
          "At least one filter must be provided"
      );
    }

    List<Interceptor> interceptors = interceptorBuilders
        .stream()
        .map(InterceptorBuilder::build)
        .collect(Collectors.toList());

    return new SupplierWrapper<>(supplier, sort(interceptors));
  }


  private List<Interceptor> sort(final List<Interceptor> list) {
    return list.stream()
        .sorted()
        .collect(Collectors.toList());
  }

}
