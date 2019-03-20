package io.benchmark

import com.codahale.metrics.MetricRegistry

interface Bench<T> {

    T createInstance(int connections, String host)

    void executeOperation(String data, T benchInstance, int threadNumber, int iteration, MetricRegistry metrics)

    void shutdown(T instance)

}