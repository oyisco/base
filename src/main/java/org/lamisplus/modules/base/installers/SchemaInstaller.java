package org.lamisplus.modules.base.installers;

import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.installers.AcrossLiquibaseInstaller;
import org.springframework.core.annotation.Order;

@Order(1)
@Installer(name = "schema-installer", description = "Installs the required database tables", version = 1)
public class SchemaInstaller extends AcrossLiquibaseInstaller
{
	public SchemaInstaller() {
		super( "classpath:installers/lamis-base/schema/initial_schema.xml" );
	}
}
