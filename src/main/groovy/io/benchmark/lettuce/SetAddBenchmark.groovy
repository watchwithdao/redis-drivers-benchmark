package io.benchmark.lettuce

import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.Timer
import io.benchmark.Bench
import io.benchmark.Benchmark
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands

class SetAddBenchmark {

    static void main(String[] args) throws InterruptedException {
        Bench<StatefulRedisConnection> bench = new LettuceBench() {
            @Override
            void executeOperation(String data, StatefulRedisConnection benchInstance, int threadNumber, MetricRegistry metrics) {
                RedisCommands<String, String> sync = benchInstance.sync()
                Timer.Context time = metrics.timer('set').time()
                sync.sadd("set_${threadNumber}" as String, data)
                time.stop()
            }
        }
        Benchmark benchmark = new Benchmark(bench)
        benchmark.run(args)
    }

}
