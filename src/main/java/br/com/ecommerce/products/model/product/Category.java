package br.com.ecommerce.products.model.product;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Category {

	CPU("cpu"),
	GPU("gpu"),
	RAM("ram"),
	MOBO("mobo"),
	SSD("ssd"),
	HDD("hdd"),
	CASE("case"),
	POWER_SUPPLY("power_supply"),
	FAN("fan"),
	COOLER("cooler");
	
	private String type;
	
	Category(String type) {
		this.type = type.toUpperCase();
	}
	
	@JsonValue
	public String getType() {
		return type;
	}
	
	@JsonCreator
	public static Category fromString(String value) {
		for(Category category : Category.values()) {
			if(category.type.equalsIgnoreCase(value)) {
				return category;
			}
		}
		throw new IllegalArgumentException("Invalid category value: " + value);
	}
}
