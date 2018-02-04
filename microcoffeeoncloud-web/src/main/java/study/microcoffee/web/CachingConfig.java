package study.microcoffee.web;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Configuration class of Spring cache infrastructure.
 */
@Configuration
@EnableCaching
@EnableScheduling
public class CachingConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("serviceUrls");
    }

    @Scheduled(fixedDelayString = "${app.cache.serviceUrls.ttl}")
    @CacheEvict(value = "serviceUrls", allEntries = true)
    public void flushCache() {
    }
}
