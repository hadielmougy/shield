package io.github.shield.metrics;

public interface Measured {
    int arrivalCount();
    double arrivalRate();
    int serviceRate();
    double throughput();
    double avgProcessingTime();
    int waitCount();
    double avgWaitTime();
    int inProcessingCount();
    int currentTotalCount();
    
}