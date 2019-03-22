package io.benchmark.redissonpro

import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.Timer
import io.benchmark.Bench
import io.benchmark.Benchmark
import org.redisson.api.RSet
import org.redisson.api.RedissonClient

class SetAddBenchmark {

    static void main(String[] args) throws InterruptedException {
        Bench<RedissonClient> bench = new RedissonProBench() {
            @Override
            void executeOperation(String data, RedissonClient benchInstance, int threadNumber, MetricRegistry metrics) {
                RSet<String> set = benchInstance.getSet("set_${threadNumber}")
                Timer.Context time = metrics.timer("set").time()
                set.add(data)
                time.stop()
            }
        }
        Benchmark benchmark = new Benchmark(bench)
        benchmark.run(args)
    }

}
