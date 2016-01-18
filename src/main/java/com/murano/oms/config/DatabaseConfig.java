package com.murano.oms.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import java.beans.PropertyVetoException;
import java.util.Properties;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig implements TransactionManagementConfigurer {
    @Value(value = "${jdbc.driverClassName:org.postgresql.Driver}")
    private String jdbcDriverClass;

    @Value(value = "${jdbc.url:jdbc:postgresql://localhost:5432/maxxdb}")
    private String jdbcUrl;

    @Value(value = "${jdbc.user.name:maxxsys}")
    private String jdbcUserName;

    @Value(value = "${jdbc.password:Flex123!}")
    private String jdbcPassword;

    @Value(value = "${jdbc.max.pool.size:50}")
    private int maxxPoolSize;

    @Bean(destroyMethod = "close")
    public DataSource createC3p0DataSource() {
        try {
            ComboPooledDataSource c3p0PooledDataSource = new ComboPooledDataSource();
            c3p0PooledDataSource.setDriverClass(jdbcDriverClass);
            c3p0PooledDataSource.setUser(jdbcUserName);
            c3p0PooledDataSource.setPassword(jdbcPassword);
            c3p0PooledDataSource.setMaxPoolSize(maxxPoolSize);
            c3p0PooledDataSource.setJdbcUrl(jdbcUrl);
            c3p0PooledDataSource.setAcquireIncrement(2);

            return c3p0PooledDataSource;
        } catch (PropertyVetoException e) {
            throw new RuntimeException(e);
        }

    }

    @Bean
    public LocalSessionFactoryBean createSessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        Properties hibernateProperties = new Properties();
        hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        hibernateProperties.put("hibernate.show_sql", true);
        hibernateProperties.put("hibernate.format_sql", true);
        sessionFactory.setPackagesToScan("flextrade.flexvision.fx.audit.pojo");
        sessionFactory.setDataSource(createC3p0DataSource());
        sessionFactory.setHibernateProperties(hibernateProperties);

        return sessionFactory;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(createC3p0DataSource());
    }

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return transactionManager();
    }
}
