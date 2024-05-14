package br.com.ecommerce.products.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class Configs implements WebMvcConfigurer{

	@Bean
	ModelMapper getMapper() {
		return new ModelMapper();
	}
	
	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new StringToCategoryConverter());
	}
}