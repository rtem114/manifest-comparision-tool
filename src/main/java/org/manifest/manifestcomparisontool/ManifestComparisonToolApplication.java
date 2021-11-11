package org.manifest.manifestcomparisontool;

import org.apache.camel.CamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ManifestComparisonToolApplication {

	@Autowired
	CamelContext camelContext;

	@Autowired

	public static void main(String[] args) {
		SpringApplication.run(ManifestComparisonToolApplication.class, args);
	}
}
