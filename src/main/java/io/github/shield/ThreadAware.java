package io.github.shield;

import java.util.concurrent.ExecutorService;

public interface ThreadAware {

    void configureExecutor(final ExecutorProvider executorProvider);


    void setExecutorService(ExecutorService executorService);
}
