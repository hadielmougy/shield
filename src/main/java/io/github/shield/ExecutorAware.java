package io.github.shield;

import java.util.concurrent.ExecutorService;

public interface ExecutorAware {

    void configureExecutor(final ExecutorProvider executorProvider);

    void setExecutorService(final ExecutorService executorService);
}
