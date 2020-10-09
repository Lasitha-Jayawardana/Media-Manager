/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package media.view;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import static javafx.application.ConditionalFeature.FXML;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import media.model.MediaItem;

/**
 * FXML Controller class
 *
 * @author Lasitha
 */
public class MediaController implements Initializable {

    public String filePath;
    public MediaPlayer mediaPlayer;
    private ObservableList<MediaItem> mediaList;
    private ObservableList<MediaItem> selectedList;
    private ObservableList<MediaItem> deletedList;

    @FXML
    private MediaView mediaView;
    @FXML
    private ListView<MediaItem> mediaListView;

    @FXML
    private ListView<MediaItem> selectedListView;
    @FXML
    private ListView<MediaItem> deletedListView;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        mediaList = FXCollections.observableArrayList();
        genItems();
        mediaListView.setItems(mediaList);

        mediaListView.setCellFactory(param -> new ListCell<MediaItem>() {
            @Override
            protected void updateItem(MediaItem p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null || p.getname() == null) {
                    setText("");
                } else {
                    setText(p.getname());
                    //Change listener implemented.
                    /*  mediaListView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Person> observable, Person oldValue, Person newValue) -> {
        if(mediaListView.isFocused()){
            textArea.setText(newValue.toString());
        }
    }); */
                }

            }
        });

        filePath = "F:\\Tv Serious\\Scorpion.S01E01.HDTV.x264-LOL.mp4";
        Media media = new Media(new File(filePath).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        mediaPlayer.play();
    }

    private void genItems() {
        for (int i = 0; i < 10; i++) {

            mediaList.add(new MediaItem("Item_" + Integer.toString(i), "dwddawd"));
        }
    }
}
