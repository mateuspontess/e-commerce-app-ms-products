package br.com.ecommerce.products.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.ecommerce.products.model.manufacturer.Manufacturer;
import br.com.ecommerce.products.model.manufacturer.ManufacturerDTO;
import br.com.ecommerce.products.model.manufacturer.ManufacturerResponseDTO;
import br.com.ecommerce.products.repository.ManufacturerRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ManufacturerService {

	@Autowired
	private ManufacturerRepository repository;
	
	
	public Page<ManufacturerResponseDTO> getAll(Pageable pageable){
		return repository
				.findAll(pageable)
				.map(ManufacturerResponseDTO::new);
	}
	
	public ManufacturerResponseDTO getById(Long id){
		Manufacturer mf = repository.findById(id)
				.orElseThrow(EntityNotFoundException::new);
		return new ManufacturerResponseDTO(mf);
	}
	
	public ManufacturerResponseDTO saveManufacturer(ManufacturerDTO dto){
		return new ManufacturerResponseDTO(repository.save(new Manufacturer(dto.getName())));
	}
	
	public void updateManufacturer(Long id, ManufacturerDTO dto){
		Manufacturer mf = repository.getReferenceById(id);
		mf.updateName(dto.getName());
	}
}