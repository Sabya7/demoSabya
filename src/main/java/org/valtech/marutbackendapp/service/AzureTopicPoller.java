package org.valtech.marutbackendapp.service;

import com.azure.core.util.BinaryData;
import com.azure.messaging.servicebus.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class AzureTopicPoller {

    @Value("${topicNamespaceConnectionString}")
    private String connectionString;

    @Value("${topic.name}")
    private String topicName;

    @Value("${topic.subscriber}")
    private String subsrciber;

    @Autowired
    CheckOutService checkOutService;

    List<BinaryData> messageBatcher = new ArrayList<>();

    Timer timer = new Timer();

    public void pollMessagesFromServiceBusTopic()
    {

        ServiceBusProcessorClient processorClient = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .processor()
                .topicName(topicName)
                .subscriptionName(subsrciber)
                .processMessage(this::processMessage)
                .processError(this::processError)
                .buildProcessorClient();

        processorClient.start();


    }

    private void processMessage(ServiceBusReceivedMessageContext context)
    {
        System.out.println("context.getMessage()" + context.getMessage());
        System.out.println("context.getMessage().getMessageId()" + context.getMessage().getMessageId());
        System.out.println("context.getMessage().getBody()" + context.getMessage().getBody());

        if(messageBatcher.isEmpty())
        {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(4500L);
                        checkOutService.lineItemsToCustomerMapper(messageBatcher);
                    } catch (InterruptedException | JsonProcessingException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    messageBatcher.clear();
                }
            };
        }

        messageBatcher.add(context.getMessage().getBody());
        context.complete();
    }
    private void processError(ServiceBusErrorContext context) {
        System.out.println(
                "\n" +  "  context.getEntityPath()\n" +  context.getEntityPath() +
                        "  context.getErrorSource()\n" + context.getErrorSource()+
                        "  context.getFullyQualifiedNamespace()\n" + context.getFullyQualifiedNamespace()+
                        "  context.getException()" + context.getException() );

    }

}
