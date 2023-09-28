package io.github.shield;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;


/**
 *
 */
public class InvocationInterceptor implements InvocationHandler {


  /**
   *
   */
  private final List<Filter> filters;

  /**
   *
   */
  private final Object target;

  /**
   *
   */
  private final InvokerDispatcher dispatcher;
  /**
   *
   */
  private Filter firstFilter;


  /**
   * @param o
   * @param l
   */
  InvocationInterceptor(final Object o, final List<Filter> l) {
    this.target = o;
    this.filters = l;
    final TargetMethodInvoker method = new TargetMethodInvoker();
    final FallbackMethodInvoker fallback = new FallbackMethodInvoker();
    dispatcher = new InvokerDispatcher(method, fallback);
    reduceFilters();
  }


  /**
   * @param p
   * @param m
   * @param a
   * @return
   */
  @Override
  public Object invoke(final Object p, final Method m, final Object[] a) {
    InvocationContext ctx = new InvocationContext(firstFilter, target, m, a);
    setContext(ctx);
    return dispatcher.invoke(ctx);
  }


  private void setContext(final InvocationContext ctx) {
    for (Filter filter : filters) {
      filter.setContext(ctx);
    }
  }


  /**
   *
   */
  private void reduceFilters() {

    Deque<Filter> filtersDeque = new LinkedList<>();

    for (Filter filter : filters) {
      filtersDeque.addFirst(filter);
    }

    Filter curr = filtersDeque.pollFirst();

    while (true) {
      Filter next = filtersDeque.pollFirst();
      if (next == null) {
        break;
      } else {
        next.setNext(curr);
        curr = next;
      }
      break;
    }

    this.firstFilter = curr;
  }

}
