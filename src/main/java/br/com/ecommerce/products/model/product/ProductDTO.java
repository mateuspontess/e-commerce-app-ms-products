package br.com.ecommerce.products.model.product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.ecommerce.products.model.manufacturer.ManufacturerDTO;
import br.com.ecommerce.products.model.stock.StockDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class ProductDTO {
	
	@NotBlank
    private final String name;
	@NotBlank
	private final String description;
	@NotNull
	private final BigDecimal price;
	@NotNull
	private final Category category;
	@NotNull
    private final StockDTO stock; // cria junto com o objeto Product
    @NotNull
    private final ManufacturerDTO manufacturer; // espera-se que já esteja criado no banco de dados
    @NotNull
    private final List<ProductSpecDTO> specs = new ArrayList<>(); // cria junto com o objeto
}