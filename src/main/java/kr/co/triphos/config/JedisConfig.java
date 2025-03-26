package kr.co.triphos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class JedisConfig {
	@Bean
	public JedisPool jedisPoolConnection() {
		return new JedisPool("127.0.0.1", 6379);
	}
}
