package com.xitu.app.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;

import static com.xitu.app.utils.StringUtil.isNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
public class DatasourceConfig {

    private String username;
    private String password;
    private String driverClass;
    private String url;
    private boolean readOnly = false;
    private String initSql;
    private String testSql = "select 1 from dual";
    private String databaseName;

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username.
     *
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets password.
     *
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets driver class.
     *
     * @return the driver class
     */
    public String getDriverClass() {
        return driverClass;
    }

    /**
     * Sets driver class.
     *
     * @param driverClass the driver class
     */
    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    /**
     * Gets url.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets url.
     *
     * @param url the url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Is read only boolean.
     *
     * @return the boolean
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Sets read only.
     *
     * @param readOnly the read only
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * Data source data source.
     *
     * @return the data source
     * @throws Throwable the throwable
     */
    private static final String DB_URL = "db_url";
    private static final String DB_HOST = "db_host";
    private static final String DB_PORT = "db_port";
    private static final String DB_NAME = "db_name";
    private static final String DB_USERNAME = "db_username";
    private static final String DB_PASSWORD = "db_password"; //noscansignature
    private static final Logger logger = LoggerFactory.getLogger(DatasourceConfig.class);

    @Bean
    @Primary
    public DataSource dataSource() throws Throwable {
        String url = System.getenv(DB_URL);
        String host = System.getenv(DB_HOST);
        String port = System.getenv(DB_PORT);
        String dbName = System.getenv(DB_NAME);
        String username = System.getenv(DB_USERNAME);
        String password = System.getenv(DB_PASSWORD);

        this.databaseName = isNull(dbName) ? this.databaseName : dbName;
        url = (isNull(host) || isNull(port)) ? url : String.format("jdbc:mysql://%s:%s/", host, port);

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setAutoCommit(true);
        hikariConfig.setReadOnly(readOnly);
        hikariConfig.setDriverClassName(driverClass);
        hikariConfig.setJdbcUrl(isNull(url) ? this.url : url);
        hikariConfig.setUsername(isNull(username) ? this.username : username);
        hikariConfig.setPassword(isNull(password) ? this.password : password);
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setMaxLifetime(1800000);
        hikariConfig.setIdleTimeout(600000);
        hikariConfig.setConnectionTimeout(30000);
        hikariConfig.setConnectionTestQuery(testSql);
        hikariConfig.setConnectionInitSql(testSql);
        DataSource dataSource = new HikariDataSource(hikariConfig);
        initDataBase(dataSource.getConnection());
        addSchema2Url(hikariConfig);
        dataSource = new HikariDataSource(hikariConfig);
        return dataSource;
    }

    private void initDataBase(Connection connection) throws Exception {
        try {
            String result = connection.nativeSQL(getInitSql());
            String[] sqls = result.split(";");
            Statement statement = connection.createStatement();
            for (String sql : sqls) {
                if (!isNull(sql.replaceAll("\\s", ""))) {
                    String sqlTmp = String.format("USE %s", this.databaseName);
                    try {
                        statement.execute(sqlTmp);
                        sqlTmp = sql;
                        statement.execute(sqlTmp);
                    } catch (SQLException ex) {
                        logger.error("execute sql fail . maybe {}", sqlTmp);
                        logger.error(ex.getMessage());
                    }
                }
            }
            if (!connection.getAutoCommit()) {
                connection.commit();
            }
        } catch (SQLException e) {
            throw new Exception("nativeSQL fail. pls check the initSql", e);
        }
    }

    private void addSchema2Url(HikariConfig hikariConfig) {
        if (!isNull(initSql) && !isNull(databaseName)) {
            if (url.contains("?")) {
                String[] urlSplit = url.split("\\?");
                if (!urlSplit[0].endsWith("/")) {
                    databaseName = "/" + databaseName;
                }
                url = url.replaceAll("\\?", databaseName + "?");
            } else if (url.endsWith("/")) {
                url += databaseName;
            } else {
                url += "/" + databaseName;
            }
            hikariConfig.setJdbcUrl(url);
        }
    }

    /**
     * Gets init sql.
     *
     * @return the init sql
     */
    public String getInitSql() {
        if (isNull(initSql)) {
            return "";
        } else {
            if (initSql.contains("\\") || initSql.contains("/")) {
                try {
                    Resource resource = new ClassPathResource(initSql);
                    BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), "UTF-8"));
                    StringBuilder stringBuilder = new StringBuilder();
                    br.lines().forEach(stringBuilder::append);
                    return stringBuilder.toString();
                } catch (IOException e) {
                    logger.error("get initSql fail.[%s]", initSql, e);
                    return "";
                }
            } else {
                return initSql;
            }
        }
    }

    /**
     * Sets init sql.
     *
     * @param initSql the init sql
     */
    public void setInitSql(String initSql) {
        this.initSql = initSql;
    }

    /**
     * Gets test sql.
     *
     * @return the test sql
     */
    public String getTestSql() {
        return testSql;
    }

    /**
     * Sets test sql.
     *
     * @param testSql the test sql
     */
    public void setTestSql(String testSql) {
        this.testSql = testSql;
    }

    /**
     * Gets database name.
     *
     * @return the database name
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * Sets database name.
     *
     * @param databaseName the database name
     */
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }
}

