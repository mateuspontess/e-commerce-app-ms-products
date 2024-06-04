package br.com.ecommerce.products.model.product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.ecommerce.products.model.manufacturer.ManufacturerDTO;
import br.com.ecommerce.products.model.stock.StockDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
	
	@NotBlank
    private String name;
	@NotBlank
	private String description;
	@NotNull
	private BigDecimal price;
	@NotNull
	private Category category;
	@NotNull
    private StockDTO stock; // cria junto com o objeto Product
    @NotNull
    private ManufacturerDTO manufacturer; // espera-se que já esteja criado no banco de dados
    @NotNull
    private List<ProductSpecDTO> specs = new ArrayList<>(); // cria junto com o objeto
}