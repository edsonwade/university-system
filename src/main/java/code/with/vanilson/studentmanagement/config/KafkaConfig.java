package code.with.vanilson.studentmanagement.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic billingTopic() {
        return TopicBuilder.name("billing-events")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic notificationTopic() {
        return TopicBuilder.name("notification-events")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
