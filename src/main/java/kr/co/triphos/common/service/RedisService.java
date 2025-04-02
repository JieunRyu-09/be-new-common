package kr.co.triphos.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

import java.util.Map;

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

	public Map<Object, Object> getMapData(String key) {
		return redisTemplate.opsForHash().entries(key);
	}
}
