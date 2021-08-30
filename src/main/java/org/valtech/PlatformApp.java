package org.valtech;


import org.valtech.marutbackendapp.controllers.merchant.PriceImporterController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.valtech.marutbackendapp.controllers.merchant.ProductImporterController;
import org.valtech.marutbackendapp.controllers.merchant.ProductVariantImporterController;

import java.io.FileNotFoundException;
import java.util.concurrent.*;

@SpringBootApplication(scanBasePackages = "org.valtech")
public class PlatformApp {

	public static void main(String[] args) throws FileNotFoundException, ExecutionException, InterruptedException {

		ConfigurableApplicationContext context = SpringApplication.run(PlatformApp.class, args);

		/*
		uncomment any one of the below 3 lines to test the corresponding utility
		Also please make sure whether csv file is formtatted accordingly or not.
		 */

//		context.getBean(ProductImporterController.class).importProducts();
//		Thread.sleep(5000);
//		context.getBean(ProductVariantImporterController.class).importProductVariants();
//		Thread.sleep(5000);
//		context.getBean(PriceImporterController.class).importPrices();
//		Thread.sleep(5000);
	}
}
