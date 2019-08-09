package com.xitu.app.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class Cache {

	private static final Logger logger = LoggerFactory.getLogger(Cache.class);
	private final Map<String, String> enties = new HashMap<>();
	
	@Autowired
    private CacheManager cacheManager;

    @Cacheable(cacheNames = "user")
    public String get(String id) {
        // 记录数据产生的时间，用于测试对比
        long time = new Date().getTime();
        // 打印使用到的cacheManager
        logger.info("The cacheManager is" + cacheManager);
        // 当数据不是从cache里面获取时，打印日志
        logger.info("Get value by id=" + id + ", The time is " + time);
        return enties.get(id);
    }

    @CacheEvict(cacheNames = "user")
    public String delete(String id) {
        return enties.remove(id);
    }

    @CachePut(cacheNames = "user", key = "#id")
    public String save(String id, String value) {        
        logger.info("save value " + value + " with key " + id);
        enties.put(id, value);
        return value;
    }

    @CachePut(cacheNames = "user", key = "#id")
    public String update(String id, String value) {
        return enties.put(id, value);
    }

}
