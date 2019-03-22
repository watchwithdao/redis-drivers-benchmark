package io.benchmark

import com.codahale.metrics.ConsoleReporter
import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.SharedMetricRegistries

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class Benchmark {

    private final int[] totalThreadPools = [1, 2, 4, 8, 16, 32, 64]
    private MetricRegistry metrics
    private ConsoleReporter reporter
    private Bench<Object> bench

    Benchmark(Bench<?> bench) {
        super()
        this.bench = (Bench<Object>) bench
    }

    void run(String[] args) throws InterruptedException {
        String metricName = 'Redisson'
        int totalIterations = 100000
        int totalConnections = 10
        String host = 'redis://127.0.0.1:6379'
        if (args) {
            metricName = args[0]
            totalIterations = Integer.valueOf(args[1])
            totalConnections = Integer.valueOf(args[2])
            host = args[3]
        }
        run(metricName, totalIterations, totalConnections, host)
    }

    void run(String metricName, final int totalIterations, int totalConnections, String host) throws InterruptedException {
        for (final int threads : this.totalThreadPools) {
            final Object client = bench.createInstance(totalConnections, host)
            ExecutorService e = Executors.newFixedThreadPool(threads)
            metrics = SharedMetricRegistries.getOrCreate(metricName)
            reporter = ConsoleReporter.forRegistry(metrics)
                    .convertRatesTo(TimeUnit.SECONDS)
                    .convertDurationsTo(TimeUnit.MICROSECONDS)
                    .build()
            for (thread in 1..threads) {
                e.execute(new Runnable() {
                    @Override
                    void run() {
                        (1..totalIterations).each { bench.executeOperation("${it}", client, thread, metrics) }
                    }
                })
            }
            SharedMetricRegistries.remove(metricName)
            e.shutdown()
            if (e.awaitTermination(30, TimeUnit.MINUTES)) {
                println("Total threads: " + threads)
                reporter.report()
            }
            bench.shutdown(client)
        }
    }

}
