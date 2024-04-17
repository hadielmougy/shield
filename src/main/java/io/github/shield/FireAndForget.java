package io.github.shield;

import io.github.shield.internal.FireAndForgetInterceptor;

public interface FireAndForget extends InterceptorBuilder {

  class Config implements FireAndForget {


    @Override
    public Interceptor build() {
      return new FireAndForgetInterceptor();
    }
  }

}
