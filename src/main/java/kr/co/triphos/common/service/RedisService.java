package kr.co.triphos.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
	private final RedisTemplate<String, String> redisTemplate;

	@Autowired
	public RedisService(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void saveMapData(String key, Map<String, String> mapData) {
		redisTemplate.opsForHash().putAll(key, mapData);
	}

	public void saveMapData(String key, Map<String, String> mapData, long timeout) {
		redisTemplate.opsForHash().putAll(key, mapData);
		redisTemplate.expire(key, timeout, TimeUnit.MILLISECONDS); // TTL 설정 추가
	}

	public void saveData(String key, String value) {
		redisTemplate.opsForValue().append(key, value);
	}

	public void saveData(String key, String value, long timeout) {
		redisTemplate.opsForValue().append(key, value);
		redisTemplate.expire(key, timeout, TimeUnit.MILLISECONDS); // TTL 설정 추가
	}

	public Map<Object, Object> getMapData(String key) {
		return redisTemplate.opsForHash().entries(key);
	}

	public String getData(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	public boolean delData(String key) {
		return Boolean.TRUE.equals(redisTemplate.delete(key));
	}
}
