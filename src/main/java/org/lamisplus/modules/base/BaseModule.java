package org.lamisplus.modules.base;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.context.configurer.ComponentScanConfigurer;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;

@AcrossDepends(required = {
        AcrossHibernateJpaModule.NAME
})
public class BaseModule extends AcrossModule {
    public static final String NAME = "LAMISBaseModule";

    public BaseModule() {
        super();
        addApplicationContextConfigurer(new ComponentScanConfigurer(getClass().getPackage().getName() + ".service",
                getClass().getPackage().getName() + ".web", getClass().getPackage().getName() + ".tenant",
                getClass().getPackage().getName() + ".module", "org.springframework.web.socket"
        ));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return "Module containing LAMIS base entities and services";
    }
}
