package controllers;

import entities.Event;
import entities.Location;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.EventService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class EventCrud implements Initializable {

    @FXML
    private DatePicker dateDP;

    @FXML
    private TextArea descriptionTF;

    @FXML
    private ListView<Event> eventLV;

    @FXML
    private TextField nameTF;

    private Location location;

    public void setLocation(Location location) {
        this.location = location;
        refreshEventListView(); // Refresh the ListView when the location is set
    }

    @FXML
    void addevent(ActionEvent event) {
        if (validateForm()) {
            Event newEvent = new Event(
                    nameTF.getText(),
                    descriptionTF.getText(),
                    location.getId(),
                    dateDP.getValue()
            );
            try {
                EventService eventService = new EventService();
                eventService.create(newEvent);
                refreshEventListView();
                clearFields(); // Clear fields after adding an event
            } catch (SQLException e1) {
                showErrorAlert("Error adding event: " + e1.getMessage());
            }
        }
    }

    @FXML
    void deleteevent(ActionEvent event) {
        Event selectedEvent = eventLV.getSelectionModel().getSelectedItem();
        if (selectedEvent != null) {
            try {
                EventService eventService = new EventService();
                eventService.delete(selectedEvent);
                refreshEventListView();
                clearFields(); // Clear fields after deleting an event
            } catch (SQLException e1) {
                showErrorAlert("Error deleting event: " + e1.getMessage());
            }
        } else {
            showErrorAlert("No event selected for deletion.");
        }
    }

    @FXML
    void afyevent(ActionEvent event) {
        Event selectedEvent = eventLV.getSelectionModel().getSelectedItem();
        if (selectedEvent != null && validateForm()) {
            // Debug: Print the selected event's current values
            System.out.println("Before update:");
            System.out.println("Name: " + selectedEvent.getName());
            System.out.println("Description: " + selectedEvent.getDescription());
            System.out.println("Date: " + selectedEvent.getDate());

            // Update the selected event with the new values from the form
            selectedEvent.setName(nameTF.getText());
            selectedEvent.setDescription(descriptionTF.getText());
            selectedEvent.setDate(dateDP.getValue());

            // Debug: Print the selected event's updated values
            System.out.println("After update:");
            System.out.println("Name: " + selectedEvent.getName());
            System.out.println("Description: " + selectedEvent.getDescription());
            System.out.println("Date: " + selectedEvent.getDate());

            try {
                EventService eventService = new EventService();
                eventService.update(selectedEvent); // Save the updated event to the database
                refreshEventListView(); // Refresh the ListView to reflect the changes
                clearFields(); // Clear the form after modification
            } catch (SQLException e1) {
                showErrorAlert("Error updating event: " + e1.getMessage());
            }
        } else {
            showErrorAlert("No event selected for modification or invalid input.");
        }
    }

    @FXML
    void showlocation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LocationCrud.fxml"));
            Parent root = loader.load();
            LocationCrud controller = loader.getController();
            controller.setLocation(location);
            Stage stage = (Stage) eventLV.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showErrorAlert("Error loading LocationCrud window: " + e.getMessage());
        }
    }

    private void refreshEventListView() {
        try {
            ObservableList<Event> events = FXCollections.observableArrayList(new EventService().readByLocationId(location.getId()));
            eventLV.setItems(events);
        } catch (SQLException e) {
            showErrorAlert("Error loading events: " + e.getMessage());
        }
    }

    private boolean validateForm() {
        String name = nameTF.getText();
        String description = descriptionTF.getText();
        LocalDate date = dateDP.getValue();

        if (name.isEmpty() || description.isEmpty() || date == null) {
            showErrorAlert("All fields must be filled.");
            return false;
        }

        return true;
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setEvents(ObservableList<Event> obs) {
        eventLV.setItems(obs);
    }

    public void setLocationId(int id) {
        location = new Location();
        location.setId(id);
        refreshEventListView();
    }

    private void populateFields(Event event) {
        nameTF.setText(event.getName());
        descriptionTF.setText(event.getDescription());
        dateDP.setValue(event.getDate());


    }

    private void selectEvent(Event selectedEvent) {
        if (selectedEvent != null) {
            populateFields(selectedEvent); // Populate fields with the selected event's data
        } else {
            clearFields(); // Clear fields if no event is selected
        }
    }

    // Method to clear fields
    private void clearFields() {
        nameTF.clear();
        descriptionTF.clear();
        dateDP.setValue(null);


    }
    @FXML
    void modifyevent(ActionEvent event) {
        Event selectedEvent = eventLV.getSelectionModel().getSelectedItem();
        if (selectedEvent != null && validateForm()) {
            // Update the selected event with new values
            selectedEvent.setName(nameTF.getText());
            selectedEvent.setDescription(descriptionTF.getText());
            selectedEvent.setDate(dateDP.getValue());

            try {
                EventService eventService = new EventService();
                eventService.update(selectedEvent); // Save changes to the database
                refreshEventListView(); // Refresh the ListView
                clearFields(); // Clear the form
            } catch (SQLException e) {
                showErrorAlert("Error updating event: " + e.getMessage());
            }
        } else {
            showErrorAlert("No event selected or invalid input.");
        }
    }

    @FXML
    private void switchAccount(ActionEvent event) {
        try {
            // Load the UserDashboard FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserDashboard.fxml"));
            Parent root = loader.load();

            // Get the current stage from any component (e.g., the "Switch to User" button)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("User Dashboard");
            stage.show();
        } catch (IOException e) {
            showErrorAlert("Error loading user dashboard: " + e.getMessage());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initialize method called."); // Debug statement

        if (eventLV == null) {
            System.out.println("eventLV is null. Check FXML binding."); // Debug statement
        } else {
            // Add a listener to the ListView to handle selection changes
            eventLV.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                System.out.println("Event selected: " + (newSelection != null ? newSelection.getName() : "null")); // Debug statement
                selectEvent(newSelection); // Call the dedicated selection function
            });
        }
    }
}


