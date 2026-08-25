package com.aqvp.platform.admin.service.provider;

import com.aqvp.platform.admin.domain.Notification;
import com.aqvp.platform.admin.domain.NotificationChannel;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "aqvp.notifications.mock", havingValue = "false")
@RequiredArgsConstructor
public class SmsNotificationProvider implements NotificationProvider {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${aqvp.notifications.twilio.account-sid}")
    private String accountSid;

    @Value("${aqvp.notifications.twilio.auth-token}")
    private String authToken;

    @Value("${aqvp.notifications.twilio.from-number}")
    private String fromNumber;

    public boolean supports(NotificationChannel channel) {
        return channel == NotificationChannel.SMS;
    }

    public boolean send(Notification notification) {
        final String body = form("To", notification.getRecipient())
            + "&" + form("From", fromNumber)
            + "&" + form("Body", notification.getMessage());
        final String credentials = Base64.getEncoder().encodeToString(
            (accountSid + ":" + authToken).getBytes(StandardCharsets.UTF_8));
        final HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.twilio.com/2010-04-01/Accounts/" + accountSid + "/Messages.json"))
            .header("Authorization", "Basic " + credentials)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();
        try {
            final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Twilio returned HTTP " + response.statusCode());
            }
            notification.setProviderReference("twilio");
            notification.setProviderResponse(response.body());
            return true;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Twilio request interrupted", ex);
        } catch (Exception ex) {
            throw new IllegalStateException("Twilio request failed", ex);
        }
    }

    private String form(String name, String value) {
        return URLEncoder.encode(name, StandardCharsets.UTF_8) + "="
            + URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
