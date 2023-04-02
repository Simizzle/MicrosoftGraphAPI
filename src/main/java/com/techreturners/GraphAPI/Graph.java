package com.techreturners.GraphAPI;

import com.azure.identity.*;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.CalendarCollectionPage;
import com.microsoft.graph.requests.EventCollectionPage;
import com.microsoft.graph.requests.GraphServiceClient;
import okhttp3.Request;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

@Service
public class Graph {
    private static Properties _properties;
    private static DeviceCodeCredential _deviceCodeCredential;
    private static GraphServiceClient<Request> _userClient;

    public static void initializeGraphForUserAuth(Properties properties, Consumer<DeviceCodeInfo> challenge) throws Exception {
        if (properties == null) {
            throw new Exception("Properties cannot be null");
        }

        _properties = properties;

        final String clientId = properties.getProperty("app.clientId");
        final String authTenantId = properties.getProperty("app.authTenant");
        final String clientSecret = properties.getProperty("app.clientSecret");
        final List<String> graphUserScopes = Arrays
                .asList(properties.getProperty("app.graphUserScopes").split(","));

        _deviceCodeCredential = new DeviceCodeCredentialBuilder()
                .clientId(clientId)
                .tenantId(authTenantId)
                .challengeConsumer(challenge)
                .build();

        final TokenCredentialAuthProvider authProvider =
                new TokenCredentialAuthProvider(graphUserScopes, _deviceCodeCredential);

        _userClient = GraphServiceClient.builder()
                .authenticationProvider(authProvider)
                .buildClient();
    }

    private static ClientSecretCredential _clientSecretCredential;
    private static GraphServiceClient<Request> _appClient;

    public static void ensureGraphForAppOnlyAuth() throws Exception {
        if (_properties == null) {
            throw new Exception("Properties cannot be null");
        }
        if (_clientSecretCredential == null) {
            final String clientId = _properties.getProperty("app.clientId");
            final String tenantId = _properties.getProperty("app.tenantId");
            final String clientSecret = _properties.getProperty("app.clientSecret");

            _clientSecretCredential = new ClientSecretCredentialBuilder()
                    .clientId(clientId)
                    .tenantId(tenantId)
                    .clientSecret(clientSecret)
                    .build();
        }
        if (_appClient == null) {
            final TokenCredentialAuthProvider authProvider =
                    new TokenCredentialAuthProvider(
                            List.of("http://graph.microsoft.com/.default"), _clientSecretCredential);

            _appClient = GraphServiceClient.builder()
                    .authenticationProvider(authProvider)
                    .buildClient();
        }
    }

    public static CalendarCollectionPage getListOfCalendars() throws GeneralSecurityException {
        if (_userClient == null) {
            throw new GeneralSecurityException();
        }
        return _userClient.me().calendars()
                .buildRequest().top(100)
                .get();
    }

    public static EventCollectionPage getCalendarEvents(String calendarId) throws GeneralSecurityException {

        LinkedList<Option> requestOptions = new LinkedList<>();
        requestOptions.add(new QueryOption("name", "2023-01 Java"));
        if (_userClient == null) {
            throw new GeneralSecurityException();
        }
        return _userClient.me().calendars().byId(calendarId).events()
                .buildRequest(requestOptions)
                .select("id,subject,start,end,bodyPreview,location")
                .top(100)
                .get();
    }
}
