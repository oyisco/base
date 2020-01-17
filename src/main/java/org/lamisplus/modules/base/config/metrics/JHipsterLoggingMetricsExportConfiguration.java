package org.lamisplus.modules.base.config.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.dropwizard.DropwizardConfig;
import io.micrometer.core.instrument.dropwizard.DropwizardMeterRegistry;
import io.micrometer.core.instrument.util.HierarchicalNameMapper;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.base.config.JHipsterProperties;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@ConditionalOnProperty("jhipster.metrics.logs.enabled")
@Slf4j
public class JHipsterLoggingMetricsExportConfiguration {

    private final JHipsterProperties jHipsterProperties;

    /**
     * <p>Constructor for JHipsterLoggingMetricsExportConfiguration.</p>
     *
     * @param jHipsterProperties a {@link io.github.jhipster.config.JHipsterProperties} object.
     */
    public JHipsterLoggingMetricsExportConfiguration(JHipsterProperties jHipsterProperties) {
        this.jHipsterProperties = jHipsterProperties;
    }

    /**
     * <p>dropwizardRegistry.</p>
     *
     * @return a {@link com.codahale.metrics.MetricRegistry} object.
     */
    @Bean
    public MetricRegistry dropwizardRegistry() {
        return new MetricRegistry();
    }

    /**
     * <p>consoleReporter.</p>
     *
     * @param dropwizardRegistry a {@link com.codahale.metrics.MetricRegistry} object.
     * @return a {@link com.codahale.metrics.Slf4jReporter} object.
     */
    @Bean
    public Slf4jReporter consoleReporter(MetricRegistry dropwizardRegistry) {
        LOG.info("Initializing Metrics Log reporting");
        Marker metricsMarker = MarkerFactory.getMarker("metrics");
        final Slf4jReporter reporter = Slf4jReporter.forRegistry(dropwizardRegistry)
                .outputTo(LoggerFactory.getLogger("metrics"))
                .markWith(metricsMarker)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(jHipsterProperties.getMetrics().getLogs().getReportFrequency(), TimeUnit.SECONDS);
        return reporter;
    }

    // Needed to enable the Console reporter
    // https://github.com/micrometer-metrics/micrometer-docs/blob/9fedeb5/src/docs/guide/console-reporter.adoc
    /**
     * <p>consoleLoggingRegistry.</p>
     *
     * @param dropwizardRegistry a {@link com.codahale.metrics.MetricRegistry} object.
     * @return a {@link io.micrometer.core.instrument.MeterRegistry} object.
     */
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
}
