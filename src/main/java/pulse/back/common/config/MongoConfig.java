package pulse.back.common.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;


@Configuration
@EnableReactiveMongoRepositories(basePackages = "pulse.back", reactiveMongoTemplateRef = "simpleReactiveMongoTemplate")
@RequiredArgsConstructor
public class MongoConfig {

    private final MongoMappingContext mongoMappingContext;

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create("mongodb+srv://ssddo:0524@cluster.0mtmr.mongodb.net/pulse?retryWrites=true&w=majority");
    }

    @Bean
    public ReactiveMongoDatabaseFactory reactiveMongoDatabaseFactory(MongoClient mongoClient) {
        return new SimpleReactiveMongoDatabaseFactory(mongoClient, "pulse");
    }

    @Bean
    public MappingMongoConverter reactiveMappingMongoConverter() {
        MappingMongoConverter converter = new MappingMongoConverter(ReactiveMongoTemplate.NO_OP_REF_RESOLVER,
                mongoMappingContext);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return converter;
    }

    @Bean
    public ReactiveMongoTemplate simpleReactiveMongoTemplate(ReactiveMongoDatabaseFactory reactiveMongoDatabaseFactory,
                                                             MappingMongoConverter reactiveMappingMongoConverter) {
        return new ReactiveMongoTemplate(reactiveMongoDatabaseFactory, reactiveMappingMongoConverter);
    }
}
