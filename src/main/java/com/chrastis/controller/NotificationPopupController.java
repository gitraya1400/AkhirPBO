package com.chrastis.controller;

import com.chrastis.model.Notifikasi;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.geometry.Insets; // <-- TAMBAHKAN INI

public class NotificationPopupController {

    @FXML private VBox popupContainer;
    @FXML private ListView<Notifikasi> notificationListView;
    @FXML private Label emptyMessageLabel;

    public void initializeData(List<Notifikasi> notifications) {
        if (notifications == null || notifications.isEmpty()) {
            notificationListView.setVisible(false);
            emptyMessageLabel.setVisible(true);
            emptyMessageLabel.setManaged(true);
            return;
        }

        ObservableList<Notifikasi> notificationList = FXCollections.observableArrayList(notifications);
        notificationListView.setItems(notificationList);

        notificationListView.setCellFactory(param -> new ListCell<>() {
            private final VBox container = new VBox();
            private final Text messageText = new Text();
            private final Text dateText = new Text();

            {
                container.setSpacing(5);
                messageText.getStyleClass().add("notification-item-message");
                messageText.setWrappingWidth(300); // Agar teks panjang turun ke bawah
                dateText.getStyleClass().add("notification-item-date");
                container.getChildren().addAll(messageText, dateText);
                setGraphic(container);
                setPadding(new Insets(10));
            }

            @Override
            protected void updateItem(Notifikasi item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    getStyleClass().remove("notification-item-unread");
                } else {
                    messageText.setText(item.getPesan());
                    dateText.setText(item.getTanggal().format(DateTimeFormatter.ofPattern("dd MMM uuuu, HH:mm")));

                    // Tambahkan style jika belum dibaca
                    if ("Belum Dibaca".equals(item.getStatus())) {
                        if (!getStyleClass().contains("notification-item-unread")) {
                            getStyleClass().add("notification-item-unread");
                        }
                    } else {
                        getStyleClass().remove("notification-item-unread");
                    }
                    setGraphic(container);
                }
            }
        });
    }
}