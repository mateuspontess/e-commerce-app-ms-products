package br.com.ecommerce.products.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ecommerce.products.model.manufacturer.Manufacturer;

public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long>{
}