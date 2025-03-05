package controllers;

import com.google.api.services.calendar.model.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import utils.CalendarService;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class CalendarViewController implements Initializable {

    @FXML private ListView<String> eventsListView;

    private String selectedLocation;
    private LocalDate selectedDate;

    /**
     * Method to receive data from UserDashboard and initialize filters.
     */
    public void initializeWithData(String location, String date) {
        System.out.println("initializeWithData called with: location = " + location + ", date = " + date);

        this.selectedLocation = location;
        this.selectedDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE) : null;

        loadFilteredEvents();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initial load without filters
        loadEvents();
    }

    /**
     * Load events without any filters.
     */
    private void loadEvents() {
        try {
            loadEventsInternal(null, null);
        } catch (Exception e) {
            showError("Failed to fetch events: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Load events with the selected filters.
     */
    private void loadFilteredEvents() {
        try {
            loadEventsInternal(selectedLocation, selectedDate);
        } catch (Exception e) {
            showError("Failed to fetch filtered events: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Internal method to fetch and display events.
     */
    private void loadEventsInternal(String locationFilter, LocalDate dateFilter)
            throws IOException, GeneralSecurityException {

        eventsListView.getItems().clear();

        if (dateFilter != null) {
            System.out.println("Fetching events for date: " + dateFilter);
        } else {
            System.out.println("Fetching events without date filter");
        }

        List<Event> events = CalendarService.getFilteredEvents(locationFilter, dateFilter);

        if (events == null || events.isEmpty()) {
            eventsListView.getItems().add("No events found for selected criteria");
        } else {
            for (Event event : events) {
                String eventDate = (event.getStart().getDateTime() != null)
                        ? event.getStart().getDateTime().toStringRfc3339()
                        : event.getStart().getDate().toString();

                String details = String.format("%s\nLocation: %s\nDate: %s",
                        event.getSummary(),
                        event.getLocation() != null ? event.getLocation() : "Unknown Location",
                        eventDate
                );
                eventsListView.getItems().add(details);
            }
        }
    }

    /**
     * Show error messages in an alert dialog.
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Calendar Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
