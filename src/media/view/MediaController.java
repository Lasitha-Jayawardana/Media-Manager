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
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.lang.Object;
import org.apache.commons.io.FileUtils;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.Label;
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
    // private ObservableList<MediaPlayer> playerList;
    public Media media;
    public File batFile;
    private PrintWriter writer;
    public File mediaFile;
    private ObservableList<MediaItem> mediaList;
    private ObservableList<MediaItem> selectedList;
    private ObservableList<MediaItem> deletedList;
    private ObservableList<MediaItem> unsupportedList;
    private String destinationPath = null;
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
    @FXML
    private Label pathView;

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
        //execute while exiting program
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                runBat(); // 
            }
        }));
        //seek set with slider
/*mediaSlider.valueProperty().addListener((Observable) -> {
            if (mediaSlider.isValueChanging()) {
                if (mediaPlayer != null) {
                    double durasi = mediaPlayer.getMedia().getDuration().toMillis();
                    durasi = durasi * (mediaSlider.getValue() / 100);
                    mediaPlayer.seek(Duration.millis(durasi));
                }
            }
        });*/
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
            deletedList.add(0, item);
            mediaList.remove(item);
            if (mediaPlayer != null) {
                mediaPlayer.stop();

                mediaPlayer.dispose();
            }
            mediaPlayer = null;
            media = null;
            mediaFile = null;
            // Media media = new Media(new File(mediaList.get(0).getpath()).toURI().toString());
            mediaFile = new File(mediaList.get(0).getpath());
            media = new Media(mediaFile.toURI().toString());

            play();
        }

    }

    public void addToSelectedList() {
        //MediaItem item = mediaListView.getSelectionModel().getSelectedItem();
        MediaItem item = mediaList.get(0);

        if (item != null) {
            selectedList.add(0, item);
            mediaList.remove(item);
            if (mediaPlayer != null) {
                mediaPlayer.stop();

                mediaPlayer.dispose();
            }
            mediaPlayer = null;
            media = null;
            mediaFile = null;
            // Media media = new Media(new File(mediaList.get(0).getpath()).toURI().toString());
            mediaFile = new File(mediaList.get(0).getpath());
            media = new Media(mediaFile.toURI().toString());

            play();
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
            destinationPath = Path;
            pathView.setText(Path);
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

            mediaFile = new File(mediaList.get(0).getpath());
            media = new Media(mediaFile.toURI().toString());

            play();
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

    private void play() {
        if (mediaPlayer != null) {

            mediaPlayer.dispose();
        }

        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
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

    }

    public void analys() {
        System.gc();

        if (mediaPlayer != null) {
            mediaPlayer.stop();

            mediaPlayer.dispose();
        }
        mediaPlayer = null;
        media = null;
        mediaView = null;
        File f = new File(destinationPath + "\\Deleted");
        if (!f.exists()) {
            f.mkdir();
        }
        f = new File(destinationPath + "\\Selected");
        if (!f.exists()) {
            f.mkdir();
        }
        batFile = new File("run.bat");

        try {
            batFile.createNewFile();
            writer = new PrintWriter(batFile, "UTF-8");

            writer.println("echo Script to delete file");
            //writer.println("del \"" + "D:\\My Project\\Java\\MediaManager\\New folder\\New Text Document.txt" + "\" /s /f /q\n");
            writer.println("timeout 10");

            analysDeletedList();
            analysSelectedList();
            //writer.println("timeout 10");
            writer.println("del \"%~f0\" & exit");

            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(MediaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void analysDeletedList() {
        String desPath = destinationPath + "\\Deleted";
        ObservableList<MediaItem> tempList;
        tempList = FXCollections.observableArrayList();
        tempList.addAll(deletedList);

        tempList.forEach(item -> {
            System.out.println(item.getname());

            if (moveFile(item.getpath(), desPath)) {
                deletedList.remove(item);
            }

        });

    }

    private void analysSelectedList() {
        String desPath = destinationPath + "\\Selected";
        ObservableList<MediaItem> tempList;
        tempList = FXCollections.observableArrayList();
        tempList.addAll(selectedList);

        tempList.forEach(item -> {
            System.out.println(item.getname());

            if (moveFile(item.getpath(), desPath)) {
                selectedList.remove(item);
            }

        });
    }

    public void analysUnsupportedList() {
        File f = new File(destinationPath + "\\Unsupported");
        if (!f.exists()) {
            f.mkdir();
        }

        String desPath = destinationPath + "\\Unsupported";
        ObservableList<MediaItem> tempList;
        tempList = FXCollections.observableArrayList();
        tempList.addAll(unsupportedList);

        tempList.forEach(item -> {
            System.out.println(item.getname());

            if (moveFile(item.getpath(), desPath)) {
                unsupportedList.remove(item);
            }

        });
    }

    private boolean moveFile(String path, String desPath) {
        desPath = desPath + "\\";

        File f = new File(path);
        if (path.equals(desPath + f.getName())) {
            return true;
        }
        File fileTo = new File(desPath);
        try {

            FileUtils.copyFileToDirectory(f, fileTo, true);
            FileUtils.forceDelete(f);
            return true;

        } catch (IOException ex) {
            writer.println("del \"" + f.getPath() + "\" /s /f /q\n");

            return false;

        }

    }

    public void clearDeletedList() {
        deletedList.clear();
    }

    public void clearSelectedList() {
        selectedList.clear();
    }

    public void clearUnsupportedList() {
        unsupportedList.clear();
        runBat();
    }

    public void clearMediaList() {
        mediaList.clear();
    }

    private void runBat() {
        try {
            Runtime.getRuntime().exec("cmd /c start /min " + batFile.getPath());
        } catch (IOException ex) {
            Logger.getLogger(MediaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}