package controllers;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import utils.CalendarApp;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.IntStream;

public class CalendarViewController implements Initializable {

    @FXML private GridPane calendarGrid;
    @FXML private ComboBox<String> monthCombo;
    @FXML private ComboBox<Integer> yearCombo;
    @FXML private ListView<String> eventList;
    @FXML private Button prevMonthBtn, nextMonthBtn;

    private YearMonth currentYearMonth;
    private Map<LocalDate, List<Event>> eventsMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupDateControls();
        loadCalendarEvents();
        setupNavigation();
        updateCalendar();
    }

    private void setupDateControls() {
        // Populate month combo
        monthCombo.getItems().addAll(DateTimeFormatter.ofPattern("MMMM").withLocale(Locale.ENGLISH)
                        .format(Month.JANUARY), "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December");
        monthCombo.getSelectionModel().select(YearMonth.now().getMonthValue() - 1);

        // Populate year combo
        int currentYear = Year.now().getValue();
        yearCombo.getItems().addAll(IntStream.range(currentYear - 5, currentYear + 6).boxed().toList());
        yearCombo.getSelectionModel().select(Integer.valueOf(currentYear));

        // Add listeners
        monthCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateCalendar());
        yearCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateCalendar());
    }

    private void setupNavigation() {
        prevMonthBtn.setOnAction(e -> navigateMonth(-1));
        nextMonthBtn.setOnAction(e -> navigateMonth(1));
    }

    private void navigateMonth(int months) {
        currentYearMonth = currentYearMonth.plusMonths(months);
        updateDateControls();
        updateCalendar();
    }

    private void updateDateControls() {
        monthCombo.getSelectionModel().select(currentYearMonth.getMonthValue() - 1);
        yearCombo.getSelectionModel().select(Integer.valueOf(currentYearMonth.getYear()));
    }

    private void updateCalendar() {
        calendarGrid.getChildren().clear();
        createCalendarHeader();
        createCalendarDays();
    }

    private void createCalendarHeader() {
        String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = 0; i < 7; i++) {
            Label dayLabel = new Label(dayNames[i]);
            dayLabel.setPadding(new Insets(5));
            dayLabel.setStyle("-fx-font-weight: bold; -fx-background-color: #f0f0f0;");
            calendarGrid.add(dayLabel, i, 0);
        }
    }

    private void createCalendarDays() {
        currentYearMonth = YearMonth.of(
                yearCombo.getValue(),
                Month.valueOf(monthCombo.getValue().toUpperCase())
        );

        LocalDate calendarDate = currentYearMonth.atDay(1);
        int row = 1;

        while (calendarDate.getMonth() == currentYearMonth.getMonth()) {
            for (int col = 0; col < 7; col++) {
                if (calendarDate.getDayOfWeek().getValue() == col % 7 + 1) {
                    VBox dayCell = createDayCell(calendarDate);
                    calendarGrid.add(dayCell, col, row);
                    calendarDate = calendarDate.plusDays(1);
                    if (calendarDate.getMonth() != currentYearMonth.getMonth()) break;
                }
            }
            row++;
        }
    }

    private VBox createDayCell(LocalDate date) {
        VBox cell = new VBox(3);
        cell.setPadding(new Insets(5));
        cell.setStyle("-fx-border-color: #e0e0e0;");

        // Day number
        Label dayNumber = new Label(String.valueOf(date.getDayOfMonth()));
        dayNumber.setStyle(date.equals(LocalDate.now())
                ? "-fx-font-weight: bold; -fx-text-fill: red;"
                : "-fx-font-weight: normal;");

        // Event indicators
        FlowPane eventIndicators = new FlowPane();
        eventIndicators.setHgap(3);

        List<Event> dayEvents = eventsMap.getOrDefault(date, Collections.emptyList());
        dayEvents.forEach(event -> {
            Circle indicator = new Circle(3, Color.valueOf("#4CAF50"));
            Tooltip.install(indicator, new Tooltip(event.getSummary()));
            eventIndicators.getChildren().add(indicator);
        });

        cell.getChildren().addAll(dayNumber, eventIndicators);
        cell.setOnMouseClicked(e -> showEventsForDate(date));

        return cell;
    }

    private void showEventsForDate(LocalDate date) {
        eventList.getItems().clear();
        eventsMap.getOrDefault(date, Collections.emptyList()).forEach(event -> {
            String eventDetails = String.format("%s\nLocation: %s\n%s",
                    event.getSummary(),
                    event.getLocation() != null ? event.getLocation() : "No location",
                    event.getDescription() != null ? event.getDescription() : "");
            eventList.getItems().add(eventDetails);
        });
    }

    private void loadCalendarEvents() {
        try {
            Calendar service = CalendarApp.getCalendarService();
            LocalDate startDate = currentYearMonth.atDay(1);
            LocalDate endDate = currentYearMonth.atEndOfMonth();

            Events events = service.events().list("primary")
                    .setTimeMin(new DateTime(startDate.toString()))
                    .setTimeMax(new DateTime(endDate.toString()))
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();

            eventsMap.clear();
            for (Event event : events.getItems()) {
                LocalDate eventDate = LocalDate.parse(event.getStart().getDateTime().toStringRfc3339()
                        .substring(0, 10));
                eventsMap.computeIfAbsent(eventDate, k -> new ArrayList<>()).add(event);
            }
        } catch (IOException | GeneralSecurityException e) {
            showAlert("Error loading calendar events: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(type.name());
        alert.setContentText(message);
        alert.showAndWait();
    }
}