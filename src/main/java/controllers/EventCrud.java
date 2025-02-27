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
import java.time.temporal.ChronoUnit;

public class EventCrud {

    @FXML
    private DatePicker dateDP;

    @FXML
    private TextArea descriptionTF;

    @FXML
    private ListView<String> eventLV; // ListView to display event details as strings

    @FXML
    private TextField nameTF;

    private Location location;

    public void setLocation(Location location) {
        this.location = location;
        refreshEventListView(); // Refresh the list when the location is set
    }

    @FXML
    void initialize() {
        // Add a listener to the ListView to load event data when an item is selected
        eventLV.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadEventData(newValue);
            }
        });
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
                clearForm(); // Clear the form after adding an event
            } catch (SQLException e1) {
                showErrorAlert("Error adding event: " + e1.getMessage());
            }
        }
    }

    @FXML
    void deleteevent(ActionEvent event) {
        String selectedEventString = eventLV.getSelectionModel().getSelectedItem();
        if (selectedEventString != null) {
            try {
                EventService eventService = new EventService();
                int eventId = extractEventIdFromString(selectedEventString);
                Event selectedEvent = eventService.readById(eventId);
                if (selectedEvent != null) {
                    eventService.delete(selectedEvent);
                    refreshEventListView();
                    clearForm(); // Clear the form after deleting an event
                } else {
                    showErrorAlert("Event not found.");
                }
            } catch (SQLException e1) {
                showErrorAlert("Error deleting event: " + e1.getMessage());
            }
        } else {
            showErrorAlert("No event selected for deletion.");
        }
    }

    @FXML
    void modifyevent(ActionEvent event) {
        String selectedEventString = eventLV.getSelectionModel().getSelectedItem();
        if (selectedEventString != null && validateForm()) {
            try {
                EventService eventService = new EventService();
                int eventId = extractEventIdFromString(selectedEventString);
                Event selectedEvent = eventService.readById(eventId);
                if (selectedEvent != null) {
                    selectedEvent.setName(nameTF.getText());
                    selectedEvent.setDescription(descriptionTF.getText());
                    selectedEvent.setDate(dateDP.getValue());
                    eventService.update(selectedEvent);
                    refreshEventListView();
                    clearForm(); // Clear the form after modifying an event
                } else {
                    showErrorAlert("Event not found.");
                }
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
            ObservableList<String> eventStrings = FXCollections.observableArrayList();

            // Convert Event objects to Strings with event name, description, and remaining time
            for (Event event : events) {
                String remaining = remainingTime(event.getDate());
                eventStrings.add(event.getId() + ": " + event.getName() + " - " + event.getDescription() + " - " + remaining);
            }

            eventLV.setItems(eventStrings);
        } catch (SQLException e) {
            showErrorAlert("Error loading events: " + e.getMessage());
        }
    }

    private void loadEventData(String eventString) {
        try {
            int eventId = extractEventIdFromString(eventString);
            EventService eventService = new EventService();
            Event selectedEvent = eventService.readById(eventId);

            if (selectedEvent != null) {
                // Load the selected event's data into the input fields
                nameTF.setText(selectedEvent.getName());
                descriptionTF.setText(selectedEvent.getDescription());
                dateDP.setValue(selectedEvent.getDate());
            }
        } catch (SQLException e) {
            showErrorAlert("Error loading event data: " + e.getMessage());
        }
    }

    private void clearForm() {
        nameTF.clear();
        descriptionTF.clear();
        dateDP.setValue(null);
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

    private String remainingTime(LocalDate eventDate) {
        LocalDate today = LocalDate.now();
        long daysLeft = ChronoUnit.DAYS.between(today, eventDate);

        if (daysLeft > 0) {
            return daysLeft + " days left";
        } else if (daysLeft == 0) {
            return "Today";
        } else {
            return "Event passed";
        }
    }

    private int extractEventIdFromString(String eventString) {
        // Assuming the event string is formatted as "id: name - description - remaining time"
        return Integer.parseInt(eventString.split(":")[0].trim());
    }

    public void setLocationId(int id) {
        location = new Location();
        location.setId(id);
        refreshEventListView();
    }

    public void setEvents(ObservableList<Event> events) {
        ObservableList<String> eventStrings = FXCollections.observableArrayList();

        // Convert Event objects to Strings with event name, description, and remaining time
        for (Event event : events) {
            String remaining = remainingTime(event.getDate());
            eventStrings.add(event.getId() + ": " + event.getName() + " - " + event.getDescription() + " - " + remaining);
        }

        eventLV.setItems(eventStrings);
    }
}