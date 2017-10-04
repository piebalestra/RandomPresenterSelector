/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;

/**
 *
 * @author Pietro
 */
public class CustomDialog {

    private Alert alert;

    public CustomDialog(String title, CheckBox check, String chekcBoxMessage) {
        alert = new Alert(AlertType.INFORMATION);
        alert.setDialogPane(new DialogPane() {
            @Override
            protected Node createDetailsButton() {
                check.setText(chekcBoxMessage);
                return check;
            }
        });
        alert.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        alert.getDialogPane().setExpandableContent(new Group());
        alert.getDialogPane().setExpanded(true);
        alert.setTitle(title);
        alert.setHeaderText(null);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/view/dialog.css").toExternalForm());
        dialogPane.getStyleClass().add("customDialog");
    }

    public void setContentText(String text) {
        alert.setContentText(text);
    }

    public void showAndWait() {
        alert.showAndWait();
    }

    public void show() {
         alert.show();
    }

}
