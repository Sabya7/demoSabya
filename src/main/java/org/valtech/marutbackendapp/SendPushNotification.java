package org.valtech.marutbackendapp;

import com.commercetools.api.models.product.ProductImpl;
import com.windowsazure.messaging.*;

import java.util.HashMap;
import java.util.Map;

public class SendPushNotification {

    static String connectionString = "Endpoint=sb://marut-push-ns.servicebus.windows.net/;SharedAccessKeyName=DefaultFullSharedAccessSignature;SharedAccessKey=kLN0ZVRKn5IpZjkpOJ5t71alwWPn6eTLAzU4+UB2YSI=";

    public static void main(String[] args) throws NotificationHubsException {
        NotificationHub hub = new NotificationHub(connectionString,"MarutAndroidPush");

        String messageTemplate = "{\"data\":{\"message\":\"$(message)\"}}";
        String temp = "{\n" +
                "\t\"notification\":{\n" +
                "\t\t\"title\":\"Notification Hub Test Notification\",\n" +
                "\t\t\"body\":\"MANGAL PANDEY.\"\n" +
                "\t},\n" +
                "\t\"data\":{\n" +
                "\t\t\"property1\":\"It's not Our JOB\",\n" +
                "\t\t\"property2\":42\n" +
                "\t}\n" +
                "}";

//        FcmTemplateRegistration fcmTemplateRegistration = new
//                FcmTemplateRegistration("e6aBR5SkRby-XWjCFRYLKj",messageTemplate);
        Installation installation = new Installation("KYJO",NotificationPlatform.Gcm,"e6aBR5SkRby-XWjCFRYLKj");
        installation.addTemplate("template",new InstallationTemplate(temp));
        hub.createOrUpdateInstallation(installation);
//        Map<String, String> prop =  new HashMap<String, String>();
//        prop.put("message","Hello from Hanuman");
        Notification notification = Notification.createFcmNotification(temp);

        System.out.println(hub.sendNotification(notification));

    }
}
