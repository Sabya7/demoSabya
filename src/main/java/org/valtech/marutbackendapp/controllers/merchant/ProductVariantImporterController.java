package org.valtech.marutbackendapp.controllers.merchant;

import com.commercetools.importapi.client.ApiRoot;
import com.commercetools.importapi.models.importoperations.ImportOperationPagedResponse;
import com.commercetools.importapi.models.importrequests.ProductVariantImportRequestBuilder;
import com.commercetools.importapi.models.importsinks.ImportSink;
import com.commercetools.importapi.models.productvariants.ProductVariantImport;
import com.commercetools.importapi.models.productvariants.ProductVariantImportBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.valtech.marutbackendapp.model.ProductVariantImportModel;
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
public class ProductVariantImporterController {

    @Autowired
    ApiRoot ctoolsImportApiClient;

    @Value("${ct.project}")
    private String project;

    @Autowired
    ImportSink importSink;

    //@RequestParam("csv") MultipartFile csv
    @PostMapping("/importProductVariants")
    public void importProductVariants() throws FileNotFoundException, ExecutionException, InterruptedException {

        //Step-1 : parse the CSV file.
        File csv = ResourceUtils.getFile("classpath:testingCSVtoJAVA2.csv");

        HeaderColumnNameMappingStrategy<ProductVariantImportModel> strategy
                = new HeaderColumnNameMappingStrategy<>();
        strategy.setType(ProductVariantImportModel.class);


        CsvToBean<ProductVariantImportModel> csvToBean =
                new CsvToBeanBuilder<ProductVariantImportModel>(new FileReader(csv))
                        .withMappingStrategy(strategy)
                        .build();

        List<List<ProductVariantImport>> productVariantImportLists =
                csvToBean.stream().parallel().map(productVariantImportModel ->
                        ProductVariantImportBuilder.of()
                                .key(productVariantImportModel.getKey())
                                .sku(productVariantImportModel.getSku())
                                .isMasterVariant(productVariantImportModel.getIsMasterVariant())
                                .product(productVariantImportModel.getProduct())
                                .attributes(productVariantImportModel.getAttributes())
                                .build()
                ).collect(new GroupingCollector<>(20));


        for (List<ProductVariantImport> productVariantImportList : productVariantImportLists) {


            ctoolsImportApiClient.withProjectKeyValue(project)
                    .productVariants()
                    .importSinkKeyWithImportSinkKeyValue(importSink.getKey())
                    .post(
                            ProductVariantImportRequestBuilder.of()
                                    .resources(productVariantImportList)
                                    .build()
                    ).execute();


        }

    }

    //This response object will have the details of all import operations performed on this import sink.
    @GetMapping("/queryProductVariantImportOperations")
    public ApiHttpResponse<ImportOperationPagedResponse> queryImportOperations() throws ExecutionException, InterruptedException {

        CompletableFuture<ApiHttpResponse<ImportOperationPagedResponse>> imoprtOperationResponse = ctoolsImportApiClient.withProjectKeyValue(project)
                .productVariants()
                .importSinkKeyWithImportSinkKeyValue(importSink.getKey())
                .importOperations()
                .get().withLimit(10000.0).execute();

        return imoprtOperationResponse.get();
    }


}

