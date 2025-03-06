package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ChatbotInterfaceController {

    @FXML
    private VBox chatbotMessages; // Container for chatbot messages

    @FXML
    private TextField userInputTF; // User input field

    @FXML
    private VBox participationForm; // Participation form (hidden by default)

    @FXML
    private TextField fullNameTF;

    @FXML
    private TextField emailTF;

    @FXML
    private TextField phoneTF;

    @FXML
    private TextField locationTF;

    @FXML
    private void handleUserInput() {
        String userInput = userInputTF.getText().trim();

        if (!userInput.isEmpty()) {
            // Display user's message
            addMessage("You: " + userInput, "user-message");

            // Process user input
            if (userInput.equalsIgnoreCase("participate")) {
                // Show the participation form
                participationForm.setVisible(true);
                addMessage("Chatbot: Please fill out the form to participate.", "chatbot-message");
            } else {
                // Default chatbot response
                addMessage("Chatbot: How can I assist you? Type 'participate' to join the event.", "chatbot-message");
            }

            // Clear the input field
            userInputTF.clear();
        }
    }

    @FXML
    private void submitParticipation() {
        // Handle participation submission
        String fullName = fullNameTF.getText();
        String email = emailTF.getText();
        String phone = phoneTF.getText();
        String location = locationTF.getText();

        // Validate and process the data
        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || location.isEmpty()) {
            addMessage("Chatbot: Please fill out all fields.", "chatbot-message");
        } else {
            addMessage("Chatbot: Thank you for participating, " + fullName + "!", "chatbot-message");
            System.out.println("Participation submitted: " + fullName + ", " + email + ", " + phone + ", " + location);

            // Clear the form and hide it
            fullNameTF.clear();
            emailTF.clear();
            phoneTF.clear();
            locationTF.clear();
            participationForm.setVisible(false);
        }
    }

    // Helper method to add a message to the chatbot interface
    private void addMessage(String message, String styleClass) {
        TextFlow textFlow = new TextFlow(new Text(message));
        textFlow.getStyleClass().add(styleClass);
        chatbotMessages.getChildren().add(textFlow);
    }
}