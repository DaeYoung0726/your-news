package project.yourNews.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.StatefulRetryOperationsInterceptor;
import project.yourNews.common.mail.mail.service.NewsMailService;

@Configuration
public class RabbitMqConfig {

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    /* Queue를 생성하는 Bean을 정의 */
    @Bean
    public Queue queue() {
        return new Queue(queueName, true);
    }

    /* 지정된 Exchange 이름으로 Direct Exchange Bean 을 생성 */
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchangeName);
    }

    /* Queue와 Exchange를 라우팅 키로 바인딩 */
    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

    /*  RabbitTemplate을 생성하는 Bean을 정의. 메시지 컨버터를 설정 */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    /* 메시지 리스너 컨테이너를 생성하는 Bean을 정의. 메시지를 큐에서 소비하는 역할 */
    @Bean
    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                                    MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setMessageListener(listenerAdapter);
        container.setConcurrentConsumers(5);
        container.setMaxConcurrentConsumers(10);
        return container;
    }

    /* 메시지 리스너 어댑터를 생성하는 Bean을 정의. 특정 메서드에 메시지를 위임 */
    @Bean
    public MessageListenerAdapter listenerAdapter(NewsMailService newsMailService) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(newsMailService, "sendMail");
        adapter.setMessageConverter(jackson2JsonMessageConverter());
        return adapter;
    }

    /* 메시지 처리 재시도 횟수 */
    @Bean
    public StatefulRetryOperationsInterceptor retryInterceptor() {
        return RetryInterceptorBuilder.stateful()
                .maxAttempts(5)         // 재시도 5번
                .backOffOptions(1000, 2.0, 10000)  // 사적 1초 / 2배씩 늘어남 / 최대 10초
                .recoverer(new RejectAndDontRequeueRecoverer())     // 재시도 실패 시 재큐하지 x
                .build();
    }

    /* 직렬화 */
    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
