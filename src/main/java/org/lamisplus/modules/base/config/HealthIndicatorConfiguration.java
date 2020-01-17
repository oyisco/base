package org.lamisplus.modules.base.config;

import com.foreach.across.core.annotations.Exposed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.health.HealthIndicatorProperties;
import org.springframework.boot.actuate.health.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@EnableConfigurationProperties({HealthIndicatorProperties.class})
@Slf4j
public class HealthIndicatorConfiguration {
    private final HealthIndicatorProperties properties;

    public HealthIndicatorConfiguration(HealthIndicatorProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean({HealthIndicator.class, ReactiveHealthIndicator.class})
    public ApplicationHealthIndicator applicationHealthIndicator() {
        return new ApplicationHealthIndicator();
    }

    @Bean
    @Exposed
    //@ConditionalOnMissingBean({HealthAggregator.class})
    public OrderedHealthAggregator healthAggregator() {
        OrderedHealthAggregator healthAggregator = new OrderedHealthAggregator();
        if (this.properties.getOrder() != null) {
            healthAggregator.setStatusOrder(this.properties.getOrder());
        }
        return healthAggregator;
    }

    /*@Bean
    @ConditionalOnMissingBean({HealthIndicatorRegistry.class})
    public HealthIndicatorRegistry healthIndicatorRegistry(ApplicationContext applicationContext) {
        return HealthIndicatorRegistryBeans.get(applicationContext);
    }*/
}
