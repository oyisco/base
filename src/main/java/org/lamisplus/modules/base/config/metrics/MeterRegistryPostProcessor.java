package org.lamisplus.modules.base.config.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties;

import java.util.Collections;
import java.util.List;

class MeterRegistryPostProcessor implements BeanPostProcessor {

    private final ObjectProvider<List<MeterBinder>> meterBinders;

    private final ObjectProvider<List<MeterFilter>> meterFilters;

    private final ObjectProvider<List<MeterRegistryCustomizer<?>>> meterRegistryCustomizers;

    private final ObjectProvider<MetricsProperties> metricsProperties;

    private volatile MeterRegistryConfigurer configurer;

    MeterRegistryPostProcessor(ObjectProvider<List<MeterBinder>> meterBinders,
                               ObjectProvider<List<MeterFilter>> meterFilters,
                               ObjectProvider<List<MeterRegistryCustomizer<?>>> meterRegistryCustomizers,
                               ObjectProvider<MetricsProperties> metricsProperties) {
        this.meterBinders = meterBinders;
        this.meterFilters = meterFilters;
        this.meterRegistryCustomizers = meterRegistryCustomizers;
        this.metricsProperties = metricsProperties;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        if (bean instanceof MeterRegistry) {
            getConfigurer().configure((MeterRegistry) bean);
        }
        return bean;
    }

    private MeterRegistryConfigurer getConfigurer() {
        if (this.configurer == null) {
            this.configurer = new MeterRegistryConfigurer(
                    this.meterBinders.getIfAvailable(Collections::emptyList),
                    this.meterFilters.getIfAvailable(Collections::emptyList),
                    this.meterRegistryCustomizers.getIfAvailable(Collections::emptyList),
                    this.metricsProperties.getObject().isUseGlobalRegistry());
        }
        return this.configurer;
    }

}
