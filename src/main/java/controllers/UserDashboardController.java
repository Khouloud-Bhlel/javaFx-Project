package controllers;

import entities.Event;
import entities.Location;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import services.EventService;
import services.LocationService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class UserDashboardController {

    @FXML private ListView<Location> locationLV;
    @FXML private ListView<Event> eventLV;

    private final LocationService locationService = new LocationService();
    private final EventService eventService = new EventService();
    private Timeline countdownTimeline; // Timeline to refresh the ListView

    @FXML
    public void initialize() {
        try {
            loadLocations();
            setupLocationSelection();

            // Set a custom cell factory for the event ListView
            eventLV.setCellFactory(param -> new ListCell<Event>() {
                @Override
                protected void updateItem(Event event, boolean empty) {
                    super.updateItem(event, empty);

                    if (empty || event == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        // Display the event name, description, date, and countdown timer
                        String eventDetails = String.format(
                                "Name: %s\nDescription: %s\nDate: %s\nTime Left: %s",
                                event.getName(),
                                event.getDescription(),
                                event.getDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                                event.getRemainingTime()
                        );
                        setText(eventDetails);
                    }
                }
            });

            // Create a Timeline to update the countdown every second
            countdownTimeline = new Timeline(
                    new KeyFrame(Duration.seconds(1), event -> refreshEventList())
            );
            countdownTimeline.setCycleCount(Timeline.INDEFINITE);
            countdownTimeline.play();
        } catch (Exception e) {
            showError("Error initializing dashboard: " + e.getMessage());
        }
    }

    private void refreshEventList() {
        // Refresh the event ListView to update the countdown timers
        eventLV.refresh();
    }

    @FXML
    private void showCalendarView(javafx.event.ActionEvent event) {
        try {
            // Load the Google Calendar URL
            String googleCalendarUrl = "https://calendar.google.com/calendar/u/0/r";

            // Create a new stage (window) to display the Google Calendar
            Stage calendarStage = new Stage();
            calendarStage.setTitle("Google Calendar");

            // Create a WebView to display the Google Calendar website
            WebView webView = new WebView();
            WebEngine webEngine = webView.getEngine();
            webEngine.load(googleCalendarUrl);

            // Add the WebView to the scene
            Scene scene = new Scene(webView, 1024, 768); // Set the preferred size
            calendarStage.setScene(scene);

            // Show the new window
            calendarStage.show();
        } catch (Exception e) {
            showError("Error loading Google Calendar: " + e.getMessage());
        }
    }

    @FXML
    private void switchAccount() {
        loadScene("/LocationCrud.fxml", "Location Management System");
    }

    private void loadScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();

            // Close current window if needed
            // ((Stage) switchAccountButton.getScene().getWindow()).close();

        } catch (IOException e) {
            handleSceneLoadError(e);
        }
    }

    private void handleDatabaseError(SQLException e) {
        System.err.println("Database error: " + e.getMessage());
        e.printStackTrace();
        showError("Database error: " + e.getMessage());
    }

    private void handleSceneLoadError(IOException e) {
        System.err.println("Scene loading error: " + e.getMessage());
        e.printStackTrace();
        showError("Scene loading error: " + e.getMessage());
    }

    private void openCalendarView(Location location, Event event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/calendar-view.fxml"));
        Parent root = loader.load();

        CalendarViewController controller = loader.getController();
        controller.initializeWithData(
                location.getName(),
                event.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE)
        );

        Stage stage = new Stage();
        stage.setTitle("Calendar View - " + location.getName());
        stage.setScene(new Scene(root, 1000, 700));
        stage.show();
    }

    private void loadLocations() throws Exception {
        locationLV.getItems().setAll(locationService.readAll());
    }

    private void setupLocationSelection() {
        locationLV.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) loadEventsForLocation(newVal);
                }
        );
    }

    private void loadEventsForLocation(Location location) {
        try {
            eventLV.getItems().setAll(eventService.readByLocationId(location.getId()));
        } catch (Exception e) {
            showError("Error loading events: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}