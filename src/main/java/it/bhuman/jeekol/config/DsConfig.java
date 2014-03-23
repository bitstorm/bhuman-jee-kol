package it.bhuman.jeekol.config;

import javax.annotation.sql.DataSourceDefinition;
import javax.ejb.Startup;
import javax.ejb.Stateless;

@DataSourceDefinition(
        className = "org.postgresql.ds.PGPoolingDataSource",
        name = "java:global/jdbc/MyDS",
        serverName="localhost",
        portNumber=5432,
        user = "postgres",
        password = "postgres",
        databaseName = "test"
)
@Startup
@Stateless
public class DsConfig {
	public void dummy(){ 
    }
}
