package br.com.ecommerce.products.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.ecommerce.products.model.manufacturer.ManufacturerDTO;
import br.com.ecommerce.products.model.manufacturer.ManufacturerResponseDTO;
import br.com.ecommerce.products.service.ManufacturerService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/manufacturers")
public class ManufacturerController {

	@Autowired
	private ManufacturerService service;
	
	
	@PostMapping
	@Transactional
	public ResponseEntity<ManufacturerResponseDTO> create(@RequestBody @Valid ManufacturerDTO dto, UriComponentsBuilder uriBuilder){
		ManufacturerResponseDTO responseBody = service.saveManufacturer(dto);
		var uri = uriBuilder.path("/manufacturers/{id}").buildAndExpand(responseBody.getId()).toUri();
		return ResponseEntity.created(uri).body(responseBody);
	}

	@GetMapping
	public ResponseEntity<Page<ManufacturerResponseDTO>> readAll(@PageableDefault(size = 10) Pageable pageable){
		Page<ManufacturerResponseDTO> dto = service.getAll(pageable);
		return ResponseEntity.ok(dto);
	}
	@GetMapping("/{id}")
	public ResponseEntity<ManufacturerResponseDTO> readById(@PathVariable Long id){
		return ResponseEntity.ok(service.getById(id));
	}
	
	@PutMapping("/{id}")
	@Transactional
	public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ManufacturerDTO dto){
		service.updateManufacturer(id, dto);
		return ResponseEntity.ok().build();
	}
}