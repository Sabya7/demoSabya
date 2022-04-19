package org.valtech.marutbackendapp.service;

import com.commercetools.api.models.cart.Cart;
import com.windowsazure.messaging.*;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PushNotificationService {

    @Value("${azureHubConnString}")
    static String connectionString;

    public NotificationOutcome sendPushNotification(String fcmToken, ApiHttpResponse<Cart> cartResponse) throws NotificationHubsException {

        NotificationHub hub = new NotificationHub(connectionString,"MarutAndroidPush");

        Cart cart = cartResponse.getBody();

        String template = "{\n" +
                "\t\"notification\":{\n" +
                "\t\t\"title\":\"Cart Created\",\n" +
                "\t\t\"body\":\"" + cart.getCustomerEmail() + "\",\n" +
                "\t},\n" +
                "\t\"data\":{\n" +
                "\t\t\"cart\":\"" + cart+ "\"\n" +
                "\t}\n" +
                "}";


        Installation installation = new Installation("MARUT", NotificationPlatform.Gcm,fcmToken);
        installation.addTemplate("template",new InstallationTemplate(template));
        hub.createOrUpdateInstallation(installation);

        Notification notification = Notification.createFcmNotification(template);

        return hub.sendNotification(notification);

    }

    wallet resource{
        version : long
    }

    one of the thread will get access,
    {
        update action : inc wallet amount by x rupees
            version 9L
    }

    latest version ==9

    updation

    version =10;



}
