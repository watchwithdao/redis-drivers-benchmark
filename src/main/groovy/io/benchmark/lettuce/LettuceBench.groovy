package io.benchmark.lettuce

import io.benchmark.Bench
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.codec.Utf8StringCodec

abstract class LettuceBench implements Bench<StatefulRedisConnection> {

    RedisClient redisClient
    StatefulRedisConnection<String, String> connection

    @Override
    StatefulRedisConnection createInstance(int totalConnections, String host) {

        redisClient = RedisClient.create(RedisURI.create(host))
        connection = redisClient.connect(new Utf8StringCodec())
        connection.sync().flushdb()
        return connection
    }

    @Override
    void shutdown(StatefulRedisConnection instance) {
        instance.close()
        redisClient.shutdown()
    }

}
