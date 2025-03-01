package controllers;

import entities.Event;
import entities.Location;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
<<<<<<< HEAD
import java.util.ResourceBundle;
=======
import java.time.temporal.ChronoUnit;
>>>>>>> 4f403f60510e5890bd1e66acf09a99524d69066d

public class EventCrud implements Initializable {

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
<<<<<<< HEAD
        refreshEventListView(); // Refresh the ListView when the location is set
=======
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
>>>>>>> 4f403f60510e5890bd1e66acf09a99524d69066d
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
<<<<<<< HEAD
                clearFields(); // Clear fields after adding an event
=======
                clearForm(); // Clear the form after adding an event
>>>>>>> 4f403f60510e5890bd1e66acf09a99524d69066d
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
<<<<<<< HEAD
                eventService.delete(selectedEvent);
                refreshEventListView();
                clearFields(); // Clear fields after deleting an event
=======
                int eventId = extractEventIdFromString(selectedEventString);
                Event selectedEvent = eventService.readById(eventId);
                if (selectedEvent != null) {
                    eventService.delete(selectedEvent);
                    refreshEventListView();
                    clearForm(); // Clear the form after deleting an event
                } else {
                    showErrorAlert("Event not found.");
                }
>>>>>>> 4f403f60510e5890bd1e66acf09a99524d69066d
            } catch (SQLException e1) {
                showErrorAlert("Error deleting event: " + e1.getMessage());
            }
        } else {
            showErrorAlert("No event selected for deletion.");
        }
    }

    @FXML
<<<<<<< HEAD
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
=======
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
>>>>>>> 4f403f60510e5890bd1e66acf09a99524d69066d
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

<<<<<<< HEAD
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

=======
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
>>>>>>> 4f403f60510e5890bd1e66acf09a99524d69066d
