package controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.MouseEvent;
import model.CustomDialog;
import model.Database;
import model.Person;
import model.ReadCSV;

/**
 * @author Pietro
 */
public class Controller implements Initializable {

    @FXML
    private TreeTableView<Person> treeTableView;
    @FXML
    private TextField textFirstName;
    @FXML
    private TextField textLastName;
    @FXML
    private TextField textFileCSV;

    Database<Person> db;

    // Initializes the controller class.
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        String fileName = "database.dat";
        int lenghtField = 19;
        int numFields = 3;

        db = Database.getInstance(fileName, lenghtField, numFields);

        TreeItem<Person> itemRoot = new TreeItem<>();
        // Add all elements present in the database.
        itemRoot.getChildren().addAll(db.getTreeList(Person.class));

        inizializeTreeTable(treeTableView);

        // Set root item for Tree.
        treeTableView.setRoot(itemRoot);
        // Make root invisible in the table.
        treeTableView.setShowRoot(false);
    }

    // Initializes treeTableView columns and cells.
    private void inizializeTreeTable(TreeTableView<Person> treeTableView) {
        TreeTableColumn<Person, String> firstNameCol = new TreeTableColumn<>("First Name");
        TreeTableColumn<Person, String> secondNameCol = new TreeTableColumn<>("Second Name");
        TreeTableColumn<Person, Boolean> dateCol = new TreeTableColumn<>("Presentation Date");
        TreeTableColumn<Person, Boolean> assentCol = new TreeTableColumn<>("Absent");

        firstNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("firstName"));
        secondNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("lastName"));
        dateCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("date"));
        assentCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("assent"));

        // Assent column value change listener.
        assentCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<Person, Boolean> param) -> {
            TreeItem<Person> treeItem = param.getValue();
            Person person = treeItem.getValue();
            SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(person.isAssent());
            booleanProp.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                person.setAssent(newValue);
            });
            return booleanProp;
        });

        // Assent column apparence.
        assentCol.setCellFactory((TreeTableColumn<Person, Boolean> p) -> {
            CheckBoxTreeTableCell<Person, Boolean> cell = new CheckBoxTreeTableCell<>();
            cell.setAlignment(Pos.CENTER);
            cell.setFocusTraversable(false);
            cell.setStyle("-fx-focus-color: gray;-fx-faint-focus-color: transparent");
            return cell;
        });

        // Add columns to TreeTable.
        treeTableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
        treeTableView.getColumns().addAll(firstNameCol, secondNameCol, dateCol, assentCol); //rootCol,

        // Alternate row selection when click.
        treeTableView.setRowFactory((TreeTableView<Person> param) -> {
            final TreeTableRow<Person> row = new TreeTableRow<>();
            row.addEventFilter(MouseEvent.MOUSE_PRESSED, (MouseEvent event) -> {
                final int index = row.getIndex();
                if (index >= 0 && index < treeTableView.getRoot().getChildren().size() && treeTableView.getSelectionModel().isSelected(index) || index >= treeTableView.getRoot().getChildren().size()) {
                    treeTableView.getSelectionModel().clearSelection();
                    event.consume();
                }
            });

            return row;
        });
    }

    @FXML
    private void buttonAddOnAction(ActionEvent event) {
        String first = textFirstName.getText();
        String last = textLastName.getText();
        if (first != null && !"".equals(first) && last != null && !"".equals(last)) {
            Person person = new Person(first, last, "");
            treeTableView.getRoot().getChildren().add(new TreeItem<>(person));
            textFirstName.setText("");
            textLastName.setText("");
            db.createRow(person);
        }
    }

    @FXML
    private void buttonCleanOnAction(ActionEvent event) {
        if (treeTableView.getRoot().getChildren().size() > 0 && !treeTableView.getSelectionModel().isEmpty()) {
            int selectedIndex = treeTableView.getSelectionModel().getSelectedIndex();
            if (selectedIndex == treeTableView.getRoot().getChildren().size()) {
                return;
            }
            Person get = treeTableView.getRoot().getChildren().get(selectedIndex).getValue();
            get.setDate("");
            db.updateRow(get, get);
            treeTableView.refresh();
        }
    }

    @FXML
    private void buttonRemoveOnAction(ActionEvent event) {
        if (treeTableView.getRoot().getChildren().size() > 0 && !treeTableView.getSelectionModel().isEmpty()) {
            int selectedIndex = treeTableView.getSelectionModel().getSelectedIndex();
            TreeItem<Person> get = treeTableView.getRoot().getChildren().get(selectedIndex);
            treeTableView.getRoot().getChildren().remove(get);
            treeTableView.refresh();
            db.deleteRow(get.getValue());
        }
    }

    @FXML
    private void buttonInportFromCSVOnAction(ActionEvent event) {
        String fileNameCSV = textFileCSV.getText();
        if (fileNameCSV != null && !fileNameCSV.isEmpty()) {
            List<Person> list = new ReadCSV(fileNameCSV, Person.class).getList();
            list.forEach(p -> {
                treeTableView.getRoot().getChildren().add(new TreeItem<>(p));
                db.createRow(p);
            });
            treeTableView.refresh();
            textFileCSV.setText("");
        }
    }

    @FXML
    private void buttonSelectOnAction(ActionEvent event) {
        // filer available people with stream
        List<TreeItem<Person>> peopleAvailable = treeTableView.getRoot().getChildren().stream()//
                .filter(a -> ("".equals(a.getValue().getDate()) && !a.getValue().isAssent())).collect(Collectors.toList());

        CheckBox check = new CheckBox();
        check.setFocusTraversable(false);
        CustomDialog dialog = new CustomDialog("The winner is...", check, "Close the application");

        if (!peopleAvailable.isEmpty()) {
            int rndIndex = new Random().nextInt(peopleAvailable.size());
            Person selected = peopleAvailable.get(rndIndex).getValue();
            selected.setDate(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy").format(LocalDateTime.now()));
            treeTableView.refresh();
            dialog.setContentText(selected.getFirstName() + " " + selected.getLastName());
            db.updateRow(selected, selected);
        } else {
            dialog.setContentText("Nobody is available!\n");
        }

        check.selectedProperty().addListener((val) -> {
            Platform.exit();
        });

        dialog.show();
    }

    public void closeDB() {
        db.close();
    }
}
