package utils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
public class CalendarApp {
    private static final Logger LOGGER = Logger.getLogger(CalendarApp.class.getName());
    private static final String APPLICATION_NAME = "QuickMove Calendar";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_EVENTS);
    private static final String CREDENTIALS_FILE_PATH = "/client_secret.json";
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Load Google credentials from the client_secret.json file.
     */
    public static Credential getCredentials() throws IOException, GeneralSecurityException {
        InputStream in = CalendarApp.class.getResourceAsStream(CREDENTIALS_FILE_PATH);

        if (in == null) {
            throw new FileNotFoundException("Error: client_secret.json file not found in resources folder.");
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                clientSecrets,
                SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver.Builder().setPort(8888).build()).authorize("user");
    }

    /**
     * Returns an authenticated Calendar service instance.
     */
    public static Calendar getCalendarService() throws IOException, GeneralSecurityException {
        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                getCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    public static String createEvent(String title, String description, Date startDate, Date endDate) {
        try {
            Calendar service = getCalendarService();

            // Convert Java Date to Google Event DateTime format
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
            dateTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            Event event = new Event()
                    .setSummary(title)
                    .setDescription(description);

            EventDateTime start = new EventDateTime().setDateTime(new com.google.api.client.util.DateTime(dateTimeFormat.format(startDate)));
            event.setStart(start);

            EventDateTime end = new EventDateTime().setDateTime(new com.google.api.client.util.DateTime(dateTimeFormat.format(endDate)));
            event.setEnd(end);

            String calendarId = "primary"; // Default calendar
            event = service.events().insert(calendarId, event).execute();

            System.out.println("Event created: " + event.getHtmlLink());
            return event.getHtmlLink();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating event: " + e.getMessage(), e);
            return null;
        }
    }
    public static void main(String[] args) {
        try {
            Calendar service = getCalendarService();
            System.out.println("Google Calendar API initialized successfully.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize Google Calendar API: " + e.getMessage(), e);
        }
    }
}
