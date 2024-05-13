package br.com.ecommerce.products.model.manufacturer;

import java.util.ArrayList;
import java.util.List;

import br.com.ecommerce.products.model.product.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
@Entity(name = "Manufacturer")
@Table(name = "manufacturers", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
public class Manufacturer {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	@OneToMany(mappedBy = "manufacturer", fetch = FetchType.LAZY)
	private List<Product> products = new ArrayList<>();;
	
	
	public Manufacturer(String name) {
		this.name = name.toUpperCase();
	}
	
	public void update(String name) {
		if (name != null && !name.isBlank())
			this.name = name.toUpperCase();
	}
	
	@Override
	public String toString() {
		return String.format("Manufacturer(id=%d, name=%s)", this.id, this.name);
	}
}