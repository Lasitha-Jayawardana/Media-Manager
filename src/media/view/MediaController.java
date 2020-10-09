/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package media.view;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Stream;
import static javafx.application.ConditionalFeature.FXML;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import media.model.MediaItem;

/**
 * FXML Controller class
 *
 * @author Lasitha
 */
public class MediaController implements Initializable {

    public MediaPlayer mediaPlayer;
    private ObservableList<MediaItem> mediaList;
    private ObservableList<MediaItem> selectedList;
    private ObservableList<MediaItem> deletedList;
    private ObservableList<MediaItem> unsupportedList;
    @FXML
    private Slider mediaSlider;
    @FXML
    private MediaView mediaView;
    @FXML
    private ListView<MediaItem> mediaListView;
    @FXML
    private ListView<MediaItem> unsupportedListView;
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
        // keypress for listview
        mediaListView.setOnKeyPressed(e -> {

            switch (e.getCode()) {
                case RIGHT:
                    seekForward();
                    break;
                case DECIMAL:
                    addToDeletedList();
                    break;
                case NUMPAD0:
                    addToSelectedList();
                    break;
                case LEFT:
                    seekBackward();
                    break;
            }

        });

        mediaList = FXCollections.observableArrayList();
        deletedList = FXCollections.observableArrayList();
        selectedList = FXCollections.observableArrayList();
        unsupportedList = FXCollections.observableArrayList();

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
        deletedListView.setItems(deletedList);
        deletedListView.setCellFactory(param -> new ListCell<MediaItem>() {
            @Override
            protected void updateItem(MediaItem p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null || p.getname() == null) {
                    setText("");
                } else {
                    setText(p.getname());
                }

            }
        });
        selectedListView.setItems(selectedList);
        selectedListView.setCellFactory(param -> new ListCell<MediaItem>() {
            @Override
            protected void updateItem(MediaItem p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null || p.getname() == null) {
                    setText("");
                } else {
                    setText(p.getname());
                }

            }
        });
        unsupportedListView.setItems(unsupportedList);
        unsupportedListView.setCellFactory(param -> new ListCell<MediaItem>() {
            @Override
            protected void updateItem(MediaItem p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null || p.getname() == null) {
                    setText("");
                } else {
                    setText(p.getname());
                }

            }
        });

    }

    public void addToDeletedList() {
        //MediaItem item = mediaListView.getSelectionModel().getSelectedItem();
        MediaItem item = mediaList.get(0);
        if (item != null) {
            deletedList.add(0,item);
            mediaList.remove(item);

            mediaView.getMediaPlayer().dispose();
            Media media = new Media(new File(mediaList.get(0).getpath()).toURI().toString());
            play(media);
        }

    }

    public void addToSelectedList() {
        //MediaItem item = mediaListView.getSelectionModel().getSelectedItem();
        MediaItem item = mediaList.get(0);

        if (item != null) {
            selectedList.add(0,item);
            mediaList.remove(item);

            mediaView.getMediaPlayer().dispose();
            Media media = new Media(new File(mediaList.get(0).getpath()).toURI().toString());
            play(media);
        }
    }

    public void moveToDeletedList() {
        MediaItem item = selectedListView.getSelectionModel().getSelectedItem();
        if (item != null) {
            deletedList.add(item);
            selectedList.remove(item);
        }
    }

    public void moveToSelectedList() {
        MediaItem item = deletedListView.getSelectionModel().getSelectedItem();
        if (item != null) {
            selectedList.add(item);
            deletedList.remove(item);
        }
    }

    public void seekBackward() {
        Double i = mediaPlayer.currentTimeProperty().getValue().toSeconds();
        mediaPlayer.seek(Duration.seconds(i - 10.0));
    }

    public void seekForward() {
        Double i = mediaPlayer.currentTimeProperty().getValue().toSeconds();
        mediaPlayer.seek(Duration.seconds(i + 10.0));
    }

    public void browse() throws IOException {
        String Path = null;
        DirectoryChooser directoryChooser = new DirectoryChooser();

        try {

            File folder = directoryChooser.showDialog(null);
            Path = folder.getAbsolutePath();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        if (Path != null) {

            try (Stream<Path> filePathStream = Files.walk(Paths.get(Path))) {
                filePathStream.forEach(filePath -> {
                    if (Files.isRegularFile(filePath)) {
                        if (checkMediaFileExtension(filePath.getFileName().toString())) {
                            //System.out.println(filePath);
                            if (checkSupportedFileExtension(filePath.getFileName().toString())) {

                                addToMediaList(filePath.getFileName().toString(), filePath.toString());

                            } else {
                                addToUnsupportedList(filePath.getFileName().toString(), filePath.toString());

                            }
                        }

                    }
                });

            }
            Media media = new Media(new File(mediaList.get(0).getpath()).toURI().toString());
            play(media);
        }
    }

    private static boolean checkMediaFileExtension(String fileName) {
        String ext = getFileExtension(fileName);
        if (ext == null) {
            return false;
        }
        try {
            if (MediaFileFormat.valueOf(ext.toUpperCase()) != null) {
                return true;
            }
        } catch (IllegalArgumentException e) {
            //Not known enum value
            return false;
        }
        return false;
    }

    private static boolean checkSupportedFileExtension(String fileName) {
        String ext = getFileExtension(fileName);
        if (ext == null) {
            return false;
        }
        try {
            if (SupportedFileFormat.valueOf(ext.toUpperCase()) != null) {
                return true;
            }
        } catch (IllegalArgumentException e) {
            //Not known enum value
            return false;
        }
        return false;
    }

    public static String getFileExtension(File f) {
        return getFileExtension(f.getName());
    }

    public static String getFileExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            return fileName.substring(i + 1);
        } else {
            return null;
        }
    }

    public enum MediaFileFormat {
        _3GP("3gp"),
        AVI("avi"),
        MP4("mp4"),
        M4A("m4a"),
        AAC("aac"),
        FXM("fxm"),
        MP3("mp3"),
        MID("mid"),
        FLV("flv"),
        AIF("aif"),
        AIFF("aiff"),
        M3U8("m3u8"),
        M4V("m4v"),
        MKV("mkv"),
        WAV("wav");

        private String filesuffix;

        MediaFileFormat(String filesuffix) {
            this.filesuffix = filesuffix;
        }

        public String getFilesuffix() {
            return filesuffix;
        }
    }

    public enum SupportedFileFormat {
        _3GP("3gp"),
        MP4("mp4"),
        M4A("m4a"),
        AAC("aac"),
        FXM("fxm"),
        MP3("mp3"),
        MID("mid"),
        FLV("flv"),
        AIF("aif"),
        AIFF("aiff"),
        M3U8("m3u8"),
        M4V("m4v"),
        WAV("wav");

        private String filesuffix;

        SupportedFileFormat(String filesuffix) {
            this.filesuffix = filesuffix;
        }

        public String getFilesuffix() {
            return filesuffix;
        }
    }

    private void addToMediaList(String name, String path) {

        mediaList.add(new MediaItem(name, path));

    }

    private void addToUnsupportedList(String name, String path) {

        unsupportedList.add(new MediaItem(name, path));

    }

    private void play(Media media) {
        mediaPlayer = new MediaPlayer(media);

        mediaView.setMediaPlayer(mediaPlayer);
        mediaPlayer.setOnReady(() -> {
            //System.out.println("Duration: "+ mediaPlayer.getTotalDuration().toSeconds());
            mediaSlider.setMin(0.0);
            mediaSlider.setValue(0.0);
            mediaSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());

            mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
                @Override
                public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                    mediaSlider.setValue(newValue.toSeconds());
                }

            });

        });

        mediaPlayer.play();
    }
}
