package pulse.back.common.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;

@Configuration
@EnableReactiveMongoRepositories(basePackages = "pulse.back", reactiveMongoTemplateRef = "simpleReactiveMongoTemplate")
public class MongoConfig {

    @Bean
    public MongoClient mongoClient() {
        // 방법 1: 최신 TLS 옵션 사용 (권장)
        return MongoClients.create("mongodb://ssddo:0524@cluster-shard-00-00.0mtmr.mongodb.net:27017,cluster-shard-00-01.0mtmr.mongodb.net:27017,cluster-shard-00-02.0mtmr.mongodb.net:27017/pulse?ssl=true&replicaSet=atlas-2baa9v-shard-0&authSource=admin&retryWrites=true&w=majority&tlsAllowInvalidHostnames=true");

        // 방법 2: MongoDB Atlas 표준 연결 문자열 (가장 안전)
        // return MongoClients.create("mongodb://ssddo:0524@cluster-shard-00-00.0mtmr.mongodb.net:27017,cluster-shard-00-01.0mtmr.mongodb.net:27017,cluster-shard-00-02.0mtmr.mongodb.net:27017/pulse?ssl=true&replicaSet=atlas-2baa9v-shard-0&authSource=admin&retryWrites=true&w=majority");

        // 방법 3: replica set 자동 발견
        // return MongoClients.create("mongodb://ssddo:0524@cluster-shard-00-00.0mtmr.mongodb.net:27017,cluster-shard-00-01.0mtmr.mongodb.net:27017,cluster-shard-00-02.0mtmr.mongodb.net:27017/pulse?ssl=true&authSource=admin&retryWrites=true&w=majority");
    }

    @Bean
    public ReactiveMongoDatabaseFactory reactiveMongoDatabaseFactory(MongoClient mongoClient) {
        return new SimpleReactiveMongoDatabaseFactory(mongoClient, "pulse");
    }

    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(Arrays.asList(
                new OffsetDateTimeToDateConverter(),
                new DateToOffsetDateTimeConverter()
        ));
    }

    @Bean
    public MongoMappingContext mongoMappingContext() {
        MongoMappingContext context = new MongoMappingContext();
        context.setSimpleTypeHolder(customConversions().getSimpleTypeHolder());
        return context;
    }

    @Bean
    public MappingMongoConverter reactiveMappingMongoConverter(ReactiveMongoDatabaseFactory factory,
                                                               MongoMappingContext context) {
        MappingMongoConverter converter = new MappingMongoConverter(ReactiveMongoTemplate.NO_OP_REF_RESOLVER, context);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        converter.setCustomConversions(customConversions());
        converter.afterPropertiesSet();
        return converter;
    }

    @Bean
    public ReactiveMongoTemplate simpleReactiveMongoTemplate(ReactiveMongoDatabaseFactory reactiveMongoDatabaseFactory,
                                                             MappingMongoConverter reactiveMappingMongoConverter) {
        return new ReactiveMongoTemplate(reactiveMongoDatabaseFactory, reactiveMappingMongoConverter);
    }

    @WritingConverter
    public static class OffsetDateTimeToDateConverter implements Converter<OffsetDateTime, Date> {
        @Override
        public Date convert(OffsetDateTime source) {
            return Date.from(source.toInstant());
        }
    }

    @ReadingConverter
    public static class DateToOffsetDateTimeConverter implements Converter<Date, OffsetDateTime> {
        @Override
        public OffsetDateTime convert(Date source) {
            return source.toInstant().atOffset(ZoneOffset.UTC);
        }
    }
}