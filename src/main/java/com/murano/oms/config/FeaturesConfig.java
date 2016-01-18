package com.murano.oms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.togglz.core.Feature;
import org.togglz.core.manager.TogglzConfig;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.repository.jdbc.JDBCStateRepository;
import org.togglz.core.repository.util.DefaultMapSerializer;
import org.togglz.core.user.NoOpUserProvider;
import org.togglz.core.user.UserProvider;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import com.murano.oms.base.feature.SupportedFeature;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FeaturesConfig implements TogglzConfig {
    private static final String PRODUCT_FEATURES_TABLE_POSTFIX = "_features";

    @Value("${product.id:default}")
    private String productId;

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void init() {
        log.info("Active product id is {}, feature table {} is going to be used ", productId, productFeaturesTableName());
    }

    @Override
    public Class<? extends Feature> getFeatureClass() {
        return SupportedFeature.class;
    }

    @Override
    public StateRepository getStateRepository() {
        JDBCStateRepository.Builder builder = JDBCStateRepository.newBuilder(dataSource);
        builder.createTable(false).tableName(productFeaturesTableName())
                .noCommit(true).serializer(DefaultMapSerializer.singleline());
        return builder.build();
    }

    @Override
    public UserProvider getUserProvider() {
        return new NoOpUserProvider();
    }

    private String productFeaturesTableName() {
        return productId + PRODUCT_FEATURES_TABLE_POSTFIX;
    }
}
