package org.valtech.marutbackendapp.controllers.merchant;

import com.commercetools.importapi.client.ApiRoot;
import com.commercetools.importapi.models.importoperations.ImportOperationPagedResponse;
import com.commercetools.importapi.models.importrequests.PriceImportRequestBuilder;
import com.commercetools.importapi.models.importsinks.ImportSink;
import com.commercetools.importapi.models.prices.PriceImport;
import com.commercetools.importapi.models.prices.PriceImportBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import org.valtech.marutbackendapp.model.PriceImportModel;
import org.valtech.marutbackendapp.util.GroupingCollector;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class PriceImporterController {

    @Autowired
    ApiRoot ctoolsImportApiClient;

    @Value("${ct.project}")
    private String project;

    @Autowired
    ImportSink importSink;

    //@RequestParam("csv") MultipartFile csv
    @PostMapping("/importPrices")
    public void importPrices() throws FileNotFoundException {
        //Step-1 : parse the CSV file.
        File csv = ResourceUtils.getFile("classpath:testingCSVtoJAVA1.csv");

        HeaderColumnNameMappingStrategy<PriceImportModel> strategy
                = new HeaderColumnNameMappingStrategy<>();
        strategy.setType(PriceImportModel.class);

        CsvToBean<PriceImportModel> csvToBean =
                new CsvToBeanBuilder<PriceImportModel>(new FileReader(csv))
                        .withMappingStrategy(strategy)
                        .build();
        /*
         * Step-2 : Create a list of PriceImportLists(each list can have maximum of 20 PriceImport Objects)
         * This limit is set by CT Import Api.
         */
        List<List<PriceImport>> priceImportLists =
                csvToBean.stream().parallel().map(priceImportModel ->
                        PriceImportBuilder.of()
                                .key(priceImportModel.getKey())
                                .value(priceImportModel.getValue())
                                .product(priceImportModel.getProduct())
                                .productVariant(priceImportModel.getProductVariant())
                                .build()
                ).collect(new GroupingCollector<>(20));

        //Step-3: hit the import api to update the prices in bulk.
        for (List<PriceImport> priceImportList : priceImportLists) {

            ctoolsImportApiClient.withProjectKeyValue(project)
                    .prices()
                    .importSinkKeyWithImportSinkKeyValue(importSink.getKey())
                    .post(
                            PriceImportRequestBuilder.of()
                                    .resources(priceImportList)
                                    .build()
                    ).execute();


        }

    }

    //This response object will have the details of all import operations performed on this import sink.
    @RequestMapping("/queryPriceImportOperations")
    public ApiHttpResponse<ImportOperationPagedResponse> queryImportOperations() throws ExecutionException, InterruptedException {

        CompletableFuture<ApiHttpResponse<ImportOperationPagedResponse>> imoprtOperationResponse = ctoolsImportApiClient.withProjectKeyValue(project)
                .prices()
                .importSinkKeyWithImportSinkKeyValue(importSink.getKey())
                .importOperations()
                .get().withLimit(10000.0).execute();

        return imoprtOperationResponse.get();
    }

}
