package io.benchmark.redissonpro

import io.benchmark.Bench
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.client.codec.StringCodec
import org.redisson.config.Config
import org.redisson.config.PerformanceMode

abstract class RedissonProBench implements Bench<RedissonClient> {

    @Override
    RedissonClient createInstance(int connections, String host) {
        Config c = new Config()
        c.useSingleServer()
                .setTimeout(10000000)
                .setAddress(host)
                .setConnectionPoolSize(connections).setConnectionMinimumIdleSize(connections)
        c.performanceMode = PerformanceMode.HIGHER_THROUGHPUT
        c.setCodec(StringCodec.INSTANCE)

        RedissonClient r = Redisson.create(c)
        r.getKeys().flushdb()
        return r
    }

    @Override
    void shutdown(RedissonClient instance) {
        instance.shutdown()
    }

}
