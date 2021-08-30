package org.valtech.marutbackendapp.controllers.merchant;


import com.commercetools.importapi.client.ApiRoot;
import com.commercetools.importapi.models.importoperations.ImportOperationPagedResponse;
import com.commercetools.importapi.models.importrequests.ProductImportRequestBuilder;
import com.commercetools.importapi.models.importsinks.ImportSink;
import com.commercetools.importapi.models.products.ProductImport;
import com.commercetools.importapi.models.products.ProductImportBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.valtech.marutbackendapp.model.ProductImportModel;
import org.valtech.marutbackendapp.util.GroupingCollector;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@RestController
public class ProductImporterController {

    @Autowired
    ApiRoot ctoolsImportApiClient;

    @Value("${ct.project}")
    private String project;

    @Autowired
    ImportSink importSink;

    //@RequestParam("csv") MultipartFile csv
    @PostMapping("/importProducts")
    public void importProducts() throws FileNotFoundException, ExecutionException, InterruptedException {

        //Step-1 : parse the CSV file.
        File csv = ResourceUtils.getFile("classpath:testingCSVtoJAVA3.csv");

        HeaderColumnNameMappingStrategy<ProductImportModel> strategy
                = new HeaderColumnNameMappingStrategy<>();
        strategy.setType(ProductImportModel.class);


        CsvToBean<ProductImportModel> csvToBean =
                new CsvToBeanBuilder<ProductImportModel>(new FileReader(csv))
                        .withMappingStrategy(strategy)
                        .build();
        /*
         * Step-2 : Create a list of ProductImportLists(each list can have maximum of 20 ProductImport Objects)
         * This limit is set by CT Import Api.
         */
        List<List<ProductImport>> productImportLists =
                csvToBean.stream().parallel().map(productImportModel ->
                        ProductImportBuilder.of()
                                .key(productImportModel.getKey())
                                .name(productImportModel.getName())
                                .productType(productImportModel.getProductType())
                                .slug(productImportModel.getSlug())
                                .categories(productImportModel.getCategories())
                                .build()
                ).collect(new GroupingCollector<>(20));

        //Step-3: hit the import api to create products in bulk.
        for (List<ProductImport> productImportList : productImportLists) {

            ctoolsImportApiClient.withProjectKeyValue(project)
                    .products()
                    .importSinkKeyWithImportSinkKeyValue(importSink.getKey())
                    .post(
                            ProductImportRequestBuilder.of()
                                    .resources(productImportList)
                                    .build()
                    ).execute();


        }

    }

    //This response object will have the details of all import operations performed on this import sink.
    @GetMapping("/queryProductImportOperations")
    public ApiHttpResponse<ImportOperationPagedResponse> queryImportOperations() throws ExecutionException, InterruptedException {

        CompletableFuture<ApiHttpResponse<ImportOperationPagedResponse>> imoprtOperationResponse = ctoolsImportApiClient.withProjectKeyValue(project)
                .products()
                .importSinkKeyWithImportSinkKeyValue(importSink.getKey())
                .importOperations()
                .get().withLimit(10000.0).execute();

        return imoprtOperationResponse.get();
    }


}
