package br.com.ecommerce.products.model.product;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter 
@Setter
@ToString
@Embeddable
public class Stock {

	private Integer unit;
	
	public Stock(Integer quantity) {
		this.mustBePositive(quantity);
		this.unit = quantity;
	}
	public Stock(Stock stock) {
		this(stock.getUnit());
	}
	
	public void update(int value) {
		if ((this.unit + value) < 0) {
			this.unit = 0;
			return;
		}
		this.unit += value;
	}

	private void mustBePositive(int value) {
		if (value < 0) throw new IllegalArgumentException("The quantity sold must be positive");
	}
}