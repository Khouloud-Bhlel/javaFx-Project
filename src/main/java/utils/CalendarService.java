package utils;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.calendar.Calendar;
import com.google.api.client.util.DateTime;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class CalendarService {

    private static final String CALENDAR_ID = "primary";

    /**
     * Fetch events filtered by location and date.
     */
    public static List<Event> getFilteredEvents(String location, LocalDate dateFilter) throws IOException, GeneralSecurityException {
        System.out.println("getFilteredEvents called with: location = " + location + ", dateFilter = " + dateFilter);

        Calendar service = CalendarApp.getCalendarService();
        Calendar.Events.List request = service.events().list(CALENDAR_ID)
                .setOrderBy("startTime")
                .setSingleEvents(true);

        // Apply date filter if provided
        if (dateFilter != null) {
            ZonedDateTime startOfDay = dateFilter.atStartOfDay(ZoneId.of("UTC"));
            ZonedDateTime endOfDay = dateFilter.plusDays(1).atStartOfDay(ZoneId.of("UTC"));

            DateTime startDateTime = new DateTime(startOfDay.toInstant().toEpochMilli());
            DateTime endDateTime = new DateTime(endOfDay.toInstant().toEpochMilli());

            request.setTimeMin(startDateTime);
            request.setTimeMax(endDateTime);
            System.out.println("Filtering events between: " + startDateTime + " and " + endDateTime);
        }

        Events events = request.execute();
        List<Event> filteredEvents = new ArrayList<>();
        System.out.println("Events returned: " + events.getItems().size()); // Log the size of the events list

        if (events.getItems().isEmpty()) {
            System.out.println("No events found for the given criteria.");
        } else {
            for (Event event : events.getItems()) {
                // Log event summary and location for debugging
                System.out.println("Event: " + event.getSummary());
                System.out.println("Event Location: " + event.getLocation());
                System.out.println("Event Start: " + event.getStart().getDateTime());

                // Apply location filter (case-insensitive comparison)
                if (location == null || (event.getLocation() != null && event.getLocation().toLowerCase().contains(location.toLowerCase()))) {
                    filteredEvents.add(event);
                }
            }
        }

        return filteredEvents;
    }
}
