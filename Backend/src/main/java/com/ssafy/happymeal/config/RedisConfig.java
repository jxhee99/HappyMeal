package com.ssafy.happymeal.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ssafy.happymeal.domain.food.entity.Food; // Food 엔티티/DTO 경로
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer; // 변경된 시리얼라이저
import org.springframework.data.redis.serializer.StringRedisSerializer;
import java.util.List;

@Configuration
public class RedisConfig {

    /**
     * 애플리케이션 전반에서 사용될 수 있는 ObjectMapper를 Spring Bean으로 등록합니다.
     * JavaTimeModule을 등록하여 Java 8+ 날짜/시간 타입을 지원하고,
     * 날짜/시간을 타임스탬프 대신 ISO-8601 문자열 형식으로 직렬화하도록 설정합니다.
     * @Primary 어노테이션을 사용하여 다른 ObjectMapper 빈보다 우선적으로 사용되도록 합니다.
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 역직렬화 시 구체적인 타입을 알 수 있도록 기본 타이핑 활성화 (선택 사항, 보안 고려 필요)
        // GenericJackson2JsonRedisSerializer가 타입을 잘 처리한다면 필요 없을 수 있습니다.
        // 필요에 따라 아래 주석을 해제하고 특정 패키지 등으로 범위를 제한하는 것이 좋습니다.
        // PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
        // .allowIfSubType("com.ssafy.happymeal") // 허용할 패키지 지정 (보안 강화)
        // .allowIfSubType("java.util.ArrayList")
        // .build();
        // objectMapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL);

        return objectMapper;
    }

    @Bean
    public RedisTemplate<String, List<Food>> redisTemplateListFood(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) { // Spring 컨텍스트에서 ObjectMapper 빈 주입
        RedisTemplate<String, List<Food>> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 키 직렬화: StringRedisSerializer 사용
        template.setKeySerializer(new StringRedisSerializer());

        // 값 직렬화: GenericJackson2JsonRedisSerializer 사용 및 주입된 ObjectMapper 전달
        // GenericJackson2JsonRedisSerializer는 생성자에서 ObjectMapper를 받으므로,
        // 우리가 설정한 JavaTimeModule 등이 포함된 ObjectMapper가 확실하게 사용됩니다.
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        template.setValueSerializer(genericJackson2JsonRedisSerializer);

        // 해시 키/값 직렬화 방식도 동일하게 설정
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(genericJackson2JsonRedisSerializer);

        template.afterPropertiesSet();
        return template;
    }
}