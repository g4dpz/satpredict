package com.badgersoft.satpredict.config;

import com.badgersoft.satpredict.service.TleUpdateService;
import com.badgersoft.satpredict.service.TleUpdateServiceImpl;
import com.badgersoft.satpredict.utils.Cache;
import com.badgersoft.satpredict.utils.Clock;
import com.badgersoft.satpredict.utils.UTCClock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by davidjohnson on 26/07/2016.
 */
@Configuration
@ComponentScan(basePackages = "com.badgersoft")
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Bean
    public Clock clock() {
        return new UTCClock();
    }

    @Bean
    public TleUpdateService tleUpdateService() { return new TleUpdateServiceImpl(); }

    @Bean
    public Cache cache() {
        return new Cache(clock());
    }

}
