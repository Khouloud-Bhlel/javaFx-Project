package controllers;

import entities.Location;
import entities.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.LocationService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public class LocationCrud {

    @FXML
    private TextArea adressTF;

    @FXML
    private TextField capacityTF;

    @FXML
    private ListView<Location> locationLV;

    @FXML
    private TextField nameTF;

    @FXML
    private Button switchAccountButton;

    @FXML
    private Button AdminAccountButton;
    @FXML
    private Button mdifyButton;

    private Location location; // Holds the current location

    private final LocationService locationService = new LocationService();

    @FXML
    public void initialize() {
        try {
            // Load all locations
            List<Location> locations = locationService.readAll();
            locationLV.getItems().setAll(locations);

            // Add listener to location list
            locationLV.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    populateFields(newSelection);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to populate fields with location data
    private void populateFields(Location location) {
        nameTF.setText(location.getName());
        adressTF.setText(location.getAddress());
        capacityTF.setText(String.valueOf(location.getCapacity()));
    }

    // Method to set the location
    public void setLocation(Location location) {
        this.location = location;
        if (location != null) {
            populateFields(location);
        }
    }

    @FXML
    private void switchAccount() {
        try {
            // Load the UserDashboard interface
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserDashboard.fxml"));
            Parent userDashboardPane = loader.load();

            // Get the current stage
            Stage stage = (Stage) switchAccountButton.getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(userDashboardPane);
            stage.setScene(scene);
            stage.setTitle("User Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void adminAccount() {
        try {
            // Load the UserDashboard interface
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Admin-dashboard.fxml"));
            Parent adminDashboardPane = loader.load();

            // Get the current stage
            Stage stage = (Stage) AdminAccountButton.getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(adminDashboardPane);
            stage.setScene(scene);
            stage.setTitle("admin Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void showevents(ActionEvent event) {
        Location selectedLocation = locationLV.getSelectionModel().getSelectedItem();
        if (selectedLocation != null) {
            try {
                ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/EventCrud.fxml"));
                AnchorPane root = loader.load();

                EventCrud controller = loader.getController();
                controller.setLocationId(selectedLocation.getId());
                ObservableList<Event> obs = FXCollections.observableArrayList(this.locationService.readAllForLocation(selectedLocation));
                controller.setEvents(obs);

                Stage stage = new Stage();
                stage.setTitle("Events");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception e) {
                showErrorAlert("Error loading events interface: " + e.getMessage());
            }
        } else {
            showErrorAlert("No location selected for showing events.");
        }
    }

    @FXML
    private void addlocation() {
        if (validateForm()) {
            Location newLocation = new Location(
                    nameTF.getText(),
                    adressTF.getText(),
                    Integer.parseInt(capacityTF.getText())
            );
            try {
                locationService.create(newLocation);
                refreshLocationListView();
                clearFields();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void deletelocation() {
        Location selectedLocation = locationLV.getSelectionModel().getSelectedItem();
        if (selectedLocation != null) {
            try {
                locationService.delete(selectedLocation);
                refreshLocationListView();
                clearFields();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void modifylocation() {
        Location selectedLocation = locationLV.getSelectionModel().getSelectedItem();
        if (selectedLocation != null && validateForm()) {
            selectedLocation.setName(nameTF.getText());
            selectedLocation.setAddress(adressTF.getText());
            selectedLocation.setCapacity(Integer.parseInt(capacityTF.getText()));
            try {
                locationService.update(selectedLocation);
                refreshLocationListView();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean validateForm() {
        String name = nameTF.getText();
        String address = adressTF.getText();
        String capacityStr = capacityTF.getText();

        if (name.isEmpty() || address.isEmpty() || capacityStr.isEmpty()) {
            showErrorAlert("All fields must be filled.");
            return false;
        }

        try {
            Integer.parseInt(capacityStr);
        } catch (NumberFormatException e) {
            showErrorAlert("Capacity must be a valid number.");
            return false;
        }

        return true;
    }

    private void refreshLocationListView() {
        try {
            List<Location> locations = locationService.readAll();
            locationLV.getItems().setAll(locations);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        nameTF.clear();
        adressTF.clear();
        capacityTF.clear();
    }

    private void showErrorAlert(String message) {
        // Implement an error alert (e.g., using JavaFX Alert)
        System.err.println(message);
    }
}