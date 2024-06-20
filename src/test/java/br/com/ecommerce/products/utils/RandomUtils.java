package br.com.ecommerce.products.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;

import br.com.ecommerce.products.model.manufacturer.Manufacturer;
import br.com.ecommerce.products.model.product.Category;
import br.com.ecommerce.products.model.product.Product;
import br.com.ecommerce.products.model.product.ProductSpec;
import br.com.ecommerce.products.model.product.Stock;

public class RandomUtils {
    
    private static Random random = new Random();

    static Product createProduct(Manufacturer manufacturer, long id, String name, String description, BigDecimal price, Category category, int stockUnits, String specAttribute, String specValue) {
        ProductSpec spec = new ProductSpec(specAttribute, specValue);
        Product product = Product.builder()
            .id(id)
            .name(name)
            .description(description)
            .price(price)
            .category(category)
            .stock(new Stock(stockUnits))
            .manufacturer(manufacturer)
            .specs(List.of(spec))
            .build();
        spec.setProduct(product);
        return product;
    }

    public static Product getRandomProduct(boolean withId) {
        return createProduct(
            new Manufacturer(getRandomString()),
            randomIdOrNull(withId),
            getRandomString(), 
            getRandomString(), 
            getRandomBigDecimal(), 
            getRandomCategory(), 
            getRandomInt(), 
            getRandomString(), 
            getRandomString()
        );
    }
    public static List<Product> getListOfRandomProducts(long bound, boolean withId) {
        List<Product> products = new ArrayList<>();
        for (long i = 0; i < bound; i++) {
            products.add(getRandomProduct(withId));
        }
        return products;
    }

    public static Manufacturer getRandomManufacturer() {
        return new Manufacturer(getRandomString());
    }
    public static List<Manufacturer> getListOfRandomManufacturers(int bound) {
        List<Manufacturer> manufacturers = new ArrayList<>();
        for (long i = 0; i < bound; i++) {
            manufacturers.add(getRandomManufacturer());
        }
        return manufacturers;
    }

    static Long randomIdOrNull(boolean withId) {
        if (withId) {
            return (long) getRandomInt();
        }
        return null;
    }
    static int getRandomInt() {
        return random.nextInt(100);
    }
    public static BigDecimal getRandomBigDecimal() {
        return BigDecimal.valueOf(getRandomInt());
    }
    public static String getRandomString() {
        return RandomStringUtils.randomAlphabetic(10);
    }
    public static Category getRandomCategory() {
        Category[] constants = Category.values();
        return constants[random.nextInt(constants.length)];
    }
}