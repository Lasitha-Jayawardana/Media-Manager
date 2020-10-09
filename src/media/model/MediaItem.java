/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package media.model;

import java.time.LocalDate;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Lasitha
 */
public class MediaItem {
    private String name;
    private String path;
    
   
    public MediaItem(String name, String path) {
        this.name = name;
        this.path = path;

         }
     public String getname() {
        return name;
    }

    public String getpath() {
        return path;
    }
}
