package controllers;

import entities.Event;
import entities.Location;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.EventService;
import services.LocationService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class UserDashboardController {

    public VBox participationForm;
    @FXML private ListView<Location> locationLV;
    @FXML private ListView<Event> eventLV;
    @FXML private Button switchAccountButton;

    private final LocationService locationService = new LocationService();
    private final EventService eventService = new EventService();

    @FXML
    public void initialize() {
        try {
            initializeLocations();
            setupLocationSelectionListener();
        } catch (SQLException e) {
            handleDatabaseError(e);
        }
    }

    private void initializeLocations() throws SQLException {
        List<Location> locations = locationService.readAll();
        locationLV.getItems().setAll(locations);
    }

    private void setupLocationSelectionListener() {
        locationLV.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        loadEventsForLocation(newSelection);
                    }
                }
        );
    }

    @FXML
    private void switchAccount() {
        loadScene("/LocationCrud.fxml", "Location Management System");
    }

    @FXML
    private void showCalendarView() {
        loadScene("/calendar-view.fxml", "Event Calendar");
    }

    private void loadEventsForLocation(Location location) {
        try {
            List<Event> events = eventService.readByLocationId(location.getId());
            eventLV.getItems().setAll(events);
        } catch (SQLException e) {
            handleDatabaseError(e);
        }
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
}