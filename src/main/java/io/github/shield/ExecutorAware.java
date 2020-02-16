package io.github.shield;


public interface ExecutorAware {

    void configureExecutor(ExecutorProvider executorProvider);

}
