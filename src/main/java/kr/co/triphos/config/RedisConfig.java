package kr.co.triphos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisConfig {

	private final String redisHost;

	private final int redisPort;

	private final String redisPassword;

	public RedisConfig(
			@Value("${spring.redis.host}") String redisHost,
			@Value("${spring.redis.port}") Integer redisPort,
			@Value("${spring.redis.password}") String redisPassword
	) {
		this.redisHost = redisHost;
		this.redisPort = redisPort;
		this.redisPassword = redisPassword;
	}

	@Bean
	public JedisConnectionFactory jedisConnectionFactory() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisHost, redisPort);
		redisStandaloneConfiguration.setPassword(redisPassword);

		// Jedis 클라이언트 설정
		JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfig = JedisClientConfiguration.builder();
		jedisClientConfig.usePooling();

		return new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfig.build());
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);

		// key, hashKey, value 직렬화 설정 (String 방식)
		template.setKeySerializer(new StringRedisSerializer());
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new StringRedisSerializer());
		template.setHashValueSerializer(new StringRedisSerializer());

		template.afterPropertiesSet();
		return template;
	}
}
