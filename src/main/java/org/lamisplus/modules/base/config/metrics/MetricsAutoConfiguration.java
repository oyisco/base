package org.lamisplus.modules.base.config.metrics;

import com.codahale.metrics.MetricRegistry;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.dropwizard.DropwizardConfig;
import io.micrometer.core.instrument.dropwizard.DropwizardMeterRegistry;
import io.micrometer.core.instrument.util.HierarchicalNameMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties;
import org.springframework.boot.actuate.autoconfigure.metrics.PropertiesMeterFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;

import java.util.List;

@Slf4j
@Configuration
@EnableConfigurationProperties(MetricsProperties.class)
public class MetricsAutoConfiguration {

    @Bean
    @Primary
    public CompositeMeterRegistry compositeMeterRegistry(Clock clock,
                                                         List<MeterRegistry> registries) {
        return new CompositeMeterRegistry(clock, registries);
    }

    @Bean
    public MeterRegistry consoleLoggingRegistry(MetricRegistry dropwizardRegistry) {
        DropwizardConfig dropwizardConfig = new DropwizardConfig() {
            @Override
            public String prefix() {
                return "console";
            }

            @Override
            public String get(String key) {
                return null;
            }
        };

        return new DropwizardMeterRegistry(dropwizardConfig, dropwizardRegistry, HierarchicalNameMapper.DEFAULT, Clock.SYSTEM) {
            @Override
            protected Double nullGaugeValue() {
                return null;
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public Clock micrometerClock() {
        return Clock.SYSTEM;
    }

    @Bean
    public static MeterRegistryPostProcessor meterRegistryPostProcessor(
            ObjectProvider<List<MeterBinder>> meterBinders,
            ObjectProvider<List<MeterFilter>> meterFilters,
            ObjectProvider<List<MeterRegistryCustomizer<?>>> meterRegistryCustomizers,
            ObjectProvider<MetricsProperties> metricsProperties) {
        return new MeterRegistryPostProcessor(meterBinders, meterFilters,
                meterRegistryCustomizers, metricsProperties);
    }

    @Bean
    @Order(0)
    public PropertiesMeterFilter propertiesMeterFilter(MetricsProperties properties) {
        return new PropertiesMeterFilter(properties);
    }

    @Configuration
    @ConditionalOnProperty(value = "management.metrics.binders.jvm.enabled",
            matchIfMissing = true)
    static class JvmMeterBindersConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public JvmGcMetrics jvmGcMetrics() {
            return new JvmGcMetrics();
        }

        @Bean
        @ConditionalOnMissingBean
        public JvmMemoryMetrics jvmMemoryMetrics() {
            return new JvmMemoryMetrics();
        }

        @Bean
        @ConditionalOnMissingBean
        public JvmThreadMetrics jvmThreadMetrics() {
            return new JvmThreadMetrics();
        }

        @Bean
        @ConditionalOnMissingBean
        public ClassLoaderMetrics classLoaderMetrics() {
            return new ClassLoaderMetrics();
        }

    }

    @Configuration
    static class MeterBindersConfiguration {

        @Bean
        @ConditionalOnProperty(value = "management.metrics.binders.uptime.enabled",
                matchIfMissing = true)
        @ConditionalOnMissingBean
        public UptimeMetrics uptimeMetrics() {
            return new UptimeMetrics();
        }

        @Bean
        @ConditionalOnProperty(value = "management.metrics.binders.processor.enabled",
                matchIfMissing = true)
        @ConditionalOnMissingBean
        public ProcessorMetrics processorMetrics() {
            return new ProcessorMetrics();
        }

        @Bean
        @ConditionalOnProperty(name = "management.metrics.binders.files.enabled",
                matchIfMissing = true)
        @ConditionalOnMissingBean
        public FileDescriptorMetrics fileDescriptorMetrics() {
            return new FileDescriptorMetrics();
        }
    }
}

