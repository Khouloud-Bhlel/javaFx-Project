package controllers;

import entities.Event;
import entities.Location;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import java.sql.SQLException;
import java.time.LocalDate;

public class EventCrud {

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
            } catch (SQLException e1) {
                showErrorAlert("Error deleting event: " + e1.getMessage());
            }
        } else {
            showErrorAlert("No event selected for deletion.");
        }
    }

    @FXML
    void modifyevent(ActionEvent event) {
        Event selectedEvent = eventLV.getSelectionModel().getSelectedItem();
        if (selectedEvent != null && validateForm()) {
            selectedEvent.setName(nameTF.getText());
            selectedEvent.setDescription(descriptionTF.getText());
            selectedEvent.setDate(dateDP.getValue());
            try {
                EventService eventService = new EventService();
                eventService.update(selectedEvent);
                refreshEventListView();
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
}