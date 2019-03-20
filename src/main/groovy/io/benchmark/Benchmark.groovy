package io.benchmark

import com.codahale.metrics.ConsoleReporter
import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.SharedMetricRegistries

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class Benchmark {

    private final int[] threads = [1, 2, 4, 8, 16, 32, 64, 128, 256]
    private MetricRegistry metrics
    private ConsoleReporter reporter

    private Bench<Object> bench

    Benchmark(Bench<?> bench) {
        super()
        this.bench = (Bench<Object>) bench
    }

    void run(String[] args) throws InterruptedException {
        int threads = 64
        int iteration = 100000
        int connections = 10
        String host = "redis://127.0.0.1:6379"
        if (args.length > 0) {
            threads = Integer.valueOf(args[0]) //64;
            iteration = Integer.valueOf(args[1])//100;
            connections = Integer.valueOf(args[2])//10
            host = args[3]
        }
        run(threads, iteration, connections, host);
    }

    void run(int threads, final int iterations, int connections, String host) throws InterruptedException {

        for (final int threadsAmount : this.threads) {
            final Object client = bench.createInstance(connections, host)
            ExecutorService e = Executors.newFixedThreadPool(threadsAmount)

            metrics = SharedMetricRegistries.getOrCreate("redisson")

            reporter = ConsoleReporter.forRegistry(metrics)
                    .convertRatesTo(TimeUnit.SECONDS)
                    .convertDurationsTo(TimeUnit.MICROSECONDS)
                    .build()

            for (threadNumber in 1..threadsAmount) {
                e.execute(new Runnable() {
                    @Override
                    void run() {
                        (1..iterations).each { bench.executeOperation("${it}", client, threadNumber, it, metrics) }
                    }

                })
            }

            SharedMetricRegistries.remove("redisson")

            e.shutdown()
            if (e.awaitTermination(30, TimeUnit.MINUTES)) {
                println("Total threads: " + threadsAmount)
                reporter.report()
            }

            bench.shutdown(client)
        }
    }

}
