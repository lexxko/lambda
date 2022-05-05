package org.my.config;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.credential.TokenCredential;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.spring.data.cosmos.config.AbstractCosmosConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CosmosDbCfg extends AbstractCosmosConfiguration {
    @Value("${azure.cosmos.dbName}")
    private String dbName;
    @Value("${azure.cosmos.key}")
    private String key;
    @Value("${azure.cosmos.uri}")
    private String uri;

    @Override
    protected String getDatabaseName() {
        return dbName;
    }

    @Bean
    CosmosClientBuilder cosmosClientBuilder() {
        return new CosmosClientBuilder()
                .endpoint(uri)
                .credential(new AzureKeyCredential(key));
    }
}
