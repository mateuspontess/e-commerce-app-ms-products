package br.com.ecommerce.products.amqp;

import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import br.com.ecommerce.products.model.stock.StockWriteOffDTO;
import br.com.ecommerce.products.service.ProductService;
import jakarta.transaction.Transactional;

@Component
public class ProductsListener {

	@Autowired
	private ProductService service;
	
	
	@RabbitListener(queues = "produtos.stock-pedidos")
	@Transactional
	public void receibeQueueMessageOrder(@Payload List<StockWriteOffDTO> dto) {
		service.updateStocks(dto);
	}
}