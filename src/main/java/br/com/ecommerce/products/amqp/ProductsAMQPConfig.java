package br.com.ecommerce.products.amqp;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductsAMQPConfig {

	@Bean
	RabbitAdmin createRabbitAdmin(ConnectionFactory conn) {
		return new RabbitAdmin(conn);
	}
	@Bean
	ApplicationListener<ApplicationReadyEvent> inicializaRabbitadmin(RabbitAdmin admin) {
		return event -> admin.initialize();
	}
	@Bean
	Jackson2JsonMessageConverter messageConverter() {
		return new Jackson2JsonMessageConverter();
	}
	@Bean
	RabbitTemplate criaTemplate(ConnectionFactory conn, Jackson2JsonMessageConverter messageConverter){
		RabbitTemplate template = new RabbitTemplate(conn);
		template.setMessageConverter(messageConverter);
		
		return template;
	}
	
	// Receiver configs
	static class Receiver {
		@Bean
		DirectExchange discoverExchangePedidos() {
			return ExchangeBuilder.directExchange("orders.create.ex").build();
		}
		@Bean
		Queue filaStockProdutos() {
			return QueueBuilder.nonDurable("products.stock-orders").build();
		}
		@Bean
		Binding bindPedidos() {
			return BindingBuilder
					.bind(this.filaStockProdutos())
					.to(this.discoverExchangePedidos())
					.with("stock");
		}
	}
}