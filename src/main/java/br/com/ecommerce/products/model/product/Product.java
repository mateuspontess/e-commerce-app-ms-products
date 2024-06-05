package br.com.ecommerce.products.model.product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.ecommerce.products.model.manufacturer.Manufacturer;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@ToString
@Entity(name = "Product")
@Table(name = "products")
public class Product {

	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String description;
	private BigDecimal price;
	
	@Enumerated(EnumType.STRING)
	private Category category;
	
	@Embedded
	private Stock stock;
	
	@Setter
	@ManyToOne 
	@JoinColumn(name = "manufacturer_id")
	private Manufacturer manufacturer;
	
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductSpec> specs = new ArrayList<>();


	public Product(String name, String description, BigDecimal price, Category category, Stock stock, Manufacturer manufacturer, List<ProductSpec> specs) {
		this.checkNotBlank(name, "name");
		this.checkNotBlank(description, "description");
		this.checkPrice(price);
		this.checkNotNull(category, "category");
		this.checkNotNull(stock, "stock");
		this.checkNotNull(manufacturer, "manufacturer");
		this.checkNotNull(specs, "specs");
	}
	
	public void update(Product data) {
		if(data != null) {
			if (data.getName() != null && !data.getName().isBlank())
				this.name = data.getName();
			
			if (data.getDescription() != null && !data.getDescription().isBlank())
				this.description = data.getDescription();
			
			if (data.getPrice() != null && data.getPrice().compareTo(BigDecimal.ZERO) > 0)
				this.price = data.getPrice();
			
			if (data.getCategory() != null) 
				this.category = data.getCategory();
			
			if(data.getManufacturer() != null)
				this.setManufacturer(data.getManufacturer());
		}
	}

	public void updateStock(int value) {
		this.stock.update(value);
	}

	private void checkNotBlank(String attribute, String attributeName) {
		if (attribute == null || attribute.isEmpty()) 
			throw new IllegalArgumentException("Cannot be blank: " + attributeName);
	}
	private void checkNotNull(Object attribute, String attributeName) {
		if (attribute == null) 
			throw new IllegalArgumentException("Cannot be null: " + attributeName);
	}
	private void checkPrice(BigDecimal price) {
		this.checkNotNull(price, "paymentAmount");
		if (price.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Price must be a positive value");
		}
	}
}