package controllers;

import entities.Event;
import entities.Location;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import services.LocationService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class LocationCrud {

    @FXML
    private TextArea adressTF;

    @FXML
    private TextField capacityTF;

    @FXML
    private ListView<Location> locationLV;

    @FXML
    private TextField nameTF;

    private final LocationService locationService = new LocationService();

    @FXML
    void addlocation(ActionEvent event) {
        if (validateForm()) {
            Location location = new Location(
                nameTF.getText(),
                adressTF.getText(),
                Integer.parseInt(capacityTF.getText())
            );
            try {
                locationService.create(location);
                refreshLocationListView();
            } catch (SQLException e) {
                showErrorAlert("Error adding location: " + e.getMessage());
            }
        }
    }

    @FXML
    void deletelocation(ActionEvent event) {
        Location selectedLocation = locationLV.getSelectionModel().getSelectedItem();
        if (selectedLocation != null) {
            try {
                locationService.delete(selectedLocation);
                refreshLocationListView();
            } catch (SQLException e) {
                showErrorAlert("Error deleting location: " + e.getMessage());
            }
        } else {
            showErrorAlert("No location selected for deletion.");
        }
    }

    @FXML
    void modifylocation(ActionEvent event) {
        Location selectedLocation = locationLV.getSelectionModel().getSelectedItem();
        if (selectedLocation != null && validateForm()) {
            selectedLocation.setName(nameTF.getText());
            selectedLocation.setAddress(adressTF.getText());
            selectedLocation.setCapacity(Integer.parseInt(capacityTF.getText()));
            try {
                locationService.update(selectedLocation);
                refreshLocationListView();
            } catch (SQLException e) {
                showErrorAlert("Error updating location: " + e.getMessage());
            }
        } else {
            showErrorAlert("No location selected for modification or invalid input.");
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
    void initialize() {
        assert adressTF != null : "fx:id=\"adressTF\" was not injected: check your FXML file 'LocationCrud.fxml'.";
        assert capacityTF != null : "fx:id=\"capacityTF\" was not injected: check your FXML file 'LocationCrud.fxml'.";
        assert locationLV != null : "fx:id=\"locationLV\" was not injected: check your FXML file 'LocationCrud.fxml'.";
        assert nameTF != null : "fx:id=\"nameTF\" was not injected: check your FXML file 'LocationCrud.fxml'.";
        refreshLocationListView();
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
            showErrorAlert("Error loading locations: " + e.getMessage());
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setEventsForLocation(Location location) {
        try {
            List<Event> events = locationService.readAllForLocation(location);
            locationLV.getSelectionModel().select(location);
            locationLV.scrollTo(location);
            nameTF.setText(location.getName());
            adressTF.setText(location.getAddress());
            capacityTF.setText(String.valueOf(location.getCapacity()));
        } catch (SQLException e) {
            showErrorAlert("Error loading events for location: " + e.getMessage());
        }
    }

    public void setLocation(Location location) {
        setEventsForLocation(location);
    }
}