package controllers;

import entities.Event;
import entities.Location;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import services.EventService;
import services.LocationService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class UserDashboardController {

    @FXML private ListView<Location> locationLV;
    @FXML private ListView<Event> eventLV;

    private final LocationService locationService = new LocationService();
    private final EventService eventService = new EventService();

    @FXML
    public void initialize() {
        try {
            loadLocations();
            setupLocationSelection();
        } catch (Exception e) {
            showError("Error initializing dashboard: " + e.getMessage());
        }
    }

    @FXML
    private void showCalendarView() {
        try {
            Location location = locationLV.getSelectionModel().getSelectedItem();
            Event event = eventLV.getSelectionModel().getSelectedItem();

            if (location == null || event == null) {
                showAlert("Selection Required",
                        "Please select both a location and an event");
                return;
            }

            openCalendarView(location, event);
        } catch (Exception e) {
            showError("Error opening calendar: " + e.getMessage());
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
        // Show alert to user
    }
    private void handleSceneLoadError(IOException e) {
        System.err.println("Scene loading error: " + e.getMessage());
        e.printStackTrace();
        // Show alert to user
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