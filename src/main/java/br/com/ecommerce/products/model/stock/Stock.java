package br.com.ecommerce.products.model.stock;

import br.com.ecommerce.products.model.product.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
@Entity(name = "Stock")
@Table(name = "stocks")
public class Stock {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "product_id") 
	private Product product;
	private Integer unit;
	
	
	public Stock(Product product, Integer quantity) {
		this.product = product;
		this.unit = quantity;
	}
	public Stock(Integer quantity) {
		this.unit = quantity;
	}
	
	public void updateStock(int value) {
		if(value < 0) throw new IllegalArgumentException("The quantity sold must be positive");
		if(this.unit < value) throw new IllegalArgumentException("The quantity sold cannot be greater than the stock");
		
		this.unit -= value;
	}

	@Override 
	public String toString() {
		return "Stock(id= " + this.id + ", quantity= " + this.unit + ")";
	}
}