package org.lamisplus.modules.base.config;

import org.lamisplus.modules.base.domain.BaseDomain;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackageClasses = BaseDomain.class)
public class DomainConfiguration {
}
