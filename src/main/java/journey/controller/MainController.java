package journey.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import journey.data.*;
import journey.repository.DatabaseManager;
import journey.repository.NoteDAO;
import journey.repository.StationDAO;
import journey.repository.VehicleDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;

/**
 * FXML controller class for the main window
 * This window is the basis for the application and has components of other controllers within itself
 * @author journey dev team
 */
public class MainController {

    private static final Logger log = LogManager.getLogger();

    private StationDAO stationDAO;
    private NoteDAO noteDAO;
    private VehicleDAO vehicleDAO;

    private Stage stage;

    private static int selectedStation = -1;

    private static final ObservableList<String> filterListOptions =
        FXCollections.observableArrayList (
            "Distance",
            "Cost"
        );

    private static final ObservableList<String> chargerTypeOptions =
        FXCollections.observableArrayList (
            "",
            "AC",
            "DC"
        );

    private static final ObservableList<String> chargerTypeSearchOptions =
            FXCollections.observableArrayList (
                    "",
                    "Mixed",
                    "AC",
                    "DC"
            );

    private static final ObservableList<String> yesNoMaybeSo =
            FXCollections.observableArrayList (
                    "",
                    "Yes",
                    "No"
            );

    private static final ObservableList<String> sortListOptions =
        FXCollections.observableArrayList (
            "Increasing",
            "Decreasing"
        );


    private static QueryResult currentStations;
    private String chargerTypeChoice;
    @FXML private ChoiceBox<String> chargerBox;
    @FXML private TextField registrationTextBox;
    @FXML private TextField makeTextBox;
    @FXML private TextField modelTextBox;
    @FXML private TextField yearTextBox;
    @FXML private ComboBox<String> filterList;
    @FXML private ComboBox<String> sortList;

    @FXML private AnchorPane scrollPane_inner;
    @FXML private TextArea chargingStationTextArea;
    @FXML private TextArea stationDetailTextArea;
    @FXML private ComboBox<String> stationDropDown;
    @FXML private ListView<String> visitedStationsList;
    @FXML private TextField startTextBox;
    @FXML private TextField endTextBox;
    @FXML private AnchorPane searchPane;
    @FXML private HBox searchRow;
    @FXML private BorderPane mapPane;
    @FXML private TabPane mainTabs;
    @FXML private AnchorPane tablePane;
    @FXML private TextField addressSearch;
    @FXML private TextField nameSearch;
    @FXML private TextField operatorSearch;
    @FXML private TextField timeSearch;
    @FXML private ChoiceBox<String> chargerBoxSearch;
    @FXML private ChoiceBox<String> attractionSearch;
    @FXML private TextField distanceSearch;
    @FXML private TextField latSearch;
    @FXML private TextField longSearch;
    @FXML private Text stationDescription;


    /**
     * Loads the open layers map view into the tab pane;
     */
    @FXML void selectMapViewTab() {
        // viewMap();
    }

    // Function called when start journey button pressed
    @FXML private void startJourneyButton(Event event) {
        System.out.println("Start journey button pressed!");
        event.consume();
    }

    // Function called when end journey button pressed
    @FXML private void endJourneyButton(Event event) {
        System.out.println("End journey button pressed!");
        event.consume();
    }

    /**
     * Function run when charger combo box choice is changed
     * Used to set the value that is stored
     * @param event change of Type Choice event
     */
    @FXML private void chargerTypeChoice(Event event) {
        chargerTypeChoice = chargerBox.getValue();
        event.consume();
    }

    /**
     * Run when the user presses the register vehicle button
     * Initialises a new vehicle and assigns it to the current user based on the input
     * fields for make, model, year, registration and charger type
     * @param event register vehicle button pressed
     */
    @FXML private void registerVehicle(Event event) {
        //get information about the vehicles and reset to null values
        String registration = getRegistrationTextBox();
        int year = getYearTextBox();
        String make = getMakeTextBox();
        String model = getModelTextBox();
        chargerTypeChoice(event);
        registrationTextBox.setText("");
        yearTextBox.setText("");
        makeTextBox.setText("");
        modelTextBox.setText("");
        chargerBox.setValue("");
        Vehicle newVehicle = new Vehicle(year, make, model, chargerTypeChoice, registration);

        DatabaseManager databaseManager = DatabaseManager.getInstance();
        // Send vehicle to database
        try {
            vehicleDAO.setVehicle(newVehicle);
        } catch (Exception e) {
            log.error(e);
        }
        event.consume();
    }
    /**
     * Loads the OpenLayers map view into the tab pane component of main view
     */
    private void viewMap() {
        try {
            FXMLLoader mapViewLoader = new FXMLLoader(getClass().getResource("/fxml/map.fxml"));
            Parent mapViewParent = mapViewLoader.load();

            MapController mapViewController = mapViewLoader.getController();
            mapViewController.init(stage, this);
            mapPane.setCenter(mapViewParent);
            mapPane.prefWidthProperty().bind(mainTabs.widthProperty());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the table view into the tab pane component of main view
     */
    private void viewTable() {
        try {
            FXMLLoader tableViewLoader = new FXMLLoader(getClass().getResource("/fxml/table.fxml"));
            Parent tableViewParent = tableViewLoader.load();

            TableController tableViewController = tableViewLoader.getController();
            tableViewController.init(stage, this);
            tablePane.getChildren().add(tableViewParent);
            AnchorPane.setTopAnchor(tableViewParent, 0d);
            AnchorPane.setBottomAnchor(tableViewParent, 0d);
            AnchorPane.setLeftAnchor(tableViewParent, 0d);
            AnchorPane.setRightAnchor(tableViewParent, 0d);
            tablePane.prefWidthProperty().bind(mainTabs.widthProperty());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getRegistrationTextBox() {
        return registrationTextBox.getText();
    }
    private String getMakeTextBox() {
        return makeTextBox.getText();
    }
    private String getModelTextBox() {
        return modelTextBox.getText();
    }
    private int getYearTextBox() {
        String year = yearTextBox.getText();
        return Integer.parseInt(year);
    }
    private String getChargerNoteText() {
        return stationDetailTextArea.getText();
    }
    private void setChargerNoteText(String s) {
        stationDetailTextArea.setText(s);
    }
    private void setStationDescription(String s) {
        stationDescription.setText(s);
    }

    /**
     * Sets Note text for a given charger based on the current station selected
     */
    public void setNoteText() {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        Station currStation = stationDAO.queryStation(selectedStation);
        if (currStation != null) {
            Note note = noteDAO.getNoteFromStation(currStation); // Retrieve note from database
            setChargerNoteText(note.getNote());
        }
    }
    /**
     * Sets Long Display text for a given charger based on the current station selected
     */
    public void setStationText() {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        Station currStation = stationDAO.queryStation(selectedStation);
        if (currStation != null) {
            setStationDescription(currStation.getLongDescription());
        }
    }

    /**
     * Submits notes and adds them the database for the current user
     * @param event submit notes button clicked
     */
    @FXML private void submitNotes(Event event) {
        Station currStation = stationDAO.queryStation(selectedStation);
        String stationNote = getChargerNoteText();

        if (currStation != null) {
            Note newNote = new Note(currStation, stationNote);
            // Set the note on the database
            noteDAO.setNote(newNote);
        }
        setNoteText();
        event.consume();
    }

    @FXML private void selectStation(Event event) {
        if(stationDropDown.getValue() != null) {
            ObservableList<String> visitedStations = visitedStationsList.getItems();
            visitedStations.add(stationDropDown.getValue());
            visitedStationsList.setItems(visitedStations);
        }
        event.consume();
    }

    @FXML private void addJourney(Event event) {
        if(!startTextBox.getText().equals("") && !endTextBox.getText().equals("")){
            System.out.println("Start: " + startTextBox.getText() + ", End: " + endTextBox.getText());
            for(String station : visitedStationsList.getItems()) {
                System.out.println(station);
            }
        }
    }

    /**
     * Searches for relevant stations based on users search inputs
     * @param event search button pressed
     */
    @FXML private void search(Event event) {
        QueryStation searchStation = new QueryStation();
        searchStation.setAddress(addressSearch.getText());
        searchStation.setName(nameSearch.getText());
        searchStation.setOperator(operatorSearch.getText());
        searchStation.setCurrentType(chargerBoxSearch.getValue());
        String attractionSearchRes = attractionSearch.getValue();
        if (attractionSearchRes != null) {
            boolean hasAttraction = (attractionSearchRes.equals("Yes"));
            searchStation.setHasTouristAttraction(hasAttraction);
        }
        if (timeSearch.getText().matches("\\d+")) {
            searchStation.setMaxTime(Integer.parseInt(timeSearch.getText()));
        }
        if (latSearch.getText().matches("[+-]?(\\d+|\\d+\\.\\d+|\\.\\d+|\\d+\\.)")
                & longSearch.getText().matches("[+-]?(\\d+|\\d+\\.\\d+|\\.\\d+|\\d+\\.)")
                & distanceSearch.getText().matches("[+-]?(\\d+|\\d+\\.\\d+|\\.\\d+|\\d+\\.)")) {
            searchStation.setLatitude(Double.parseDouble(latSearch.getText()));
            searchStation.setLongitude(Double.parseDouble(longSearch.getText()));
            searchStation.setRange(Double.parseDouble(distanceSearch.getText()));
        }
        currentStations = stationDAO.query(searchStation);
        viewMap();
        viewTable();
    }

    public static int getSelectedStation() {
        return selectedStation;
    }

    public static void setSelectedStation(int selectedStation) {
        MainController.selectedStation = selectedStation;

    }

    public static QueryResult getStations() {
        return currentStations;
    }

    /**
     * Initialize the window
     * @param stage Top level container for this window
     */
    public void init(Stage stage) {
        stationDAO = new StationDAO();
        noteDAO = new NoteDAO();
        vehicleDAO = new VehicleDAO();
        currentStations = stationDAO.getAll();
        // Fill the combo boxes
        this.stage = stage;
        chargerBox.setItems(chargerTypeOptions);

        chargerBoxSearch.setItems(chargerTypeSearchOptions);
        chargerBoxSearch.setValue("");

        attractionSearch.setItems(yesNoMaybeSo);

        QueryResult stations = stationDAO.getAll();
        ObservableList<String> stationList = FXCollections.observableArrayList();
        for (Station station : stations.getStations()) {
            String newString = Arrays.toString(Arrays.copyOfRange(station.getAddress().split(","), 0, 2));
            newString = newString.substring(1, newString.length() - 1);
            stationList.add(newString);
        }

        stationDropDown.setItems(stationList);
        viewMap();
        viewTable();
    }

    /**
     * Brings up the profile popup window when the 'my profile' button is pressed
     * @param event Profile button clicked event
     */
    @FXML private void myProfileButton(Event event) {
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/profile.fxml"));
            root = loader.load();

            ProfileController controller = loader.getController();

            Stage profileStage = new Stage(StageStyle.UNDECORATED);
            controller.init(profileStage);

            profileStage.setTitle("Profile");
            Scene scene = new Scene(root);
            profileStage.setScene(scene);
            profileStage.show();
            profileStage.setMinHeight(400);
            profileStage.setMinWidth(500);
        } catch (IOException e) {
            e.printStackTrace();
        }
        event.consume();
    }

}
