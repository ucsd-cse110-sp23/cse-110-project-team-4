# Frontend/UI Code Documentation
#### by Louie Cai

## Basic Concepts

### JavaFX and Design Flow
JavaFX is a UI framework that allows for the creation of UIs using FXML files, which contain the UI layout and 
design. The FXML files are then linked to a controller class that contains the logic for the UI. Each combination of 
a controller and a FXML file is called a **scene**. 

To design/edit the FXML files, you should use [SceneBuilder](https://gluonhq.com/products/scene-builder/). **Make sure 
to link your FXML files to their corresponding controller class by clicking on the controller button on the bottom 
and to link the necessary UI elements to their corresponding variables in the controller class.**

The main application has a **stage** that loads different scenes to allow window switching, popups, etc; similar to 
how you can switch between slides on a PowerPoint presentation. The stage also handles the resizing of the window, 
icons, and other window properties.

Here is an example of how you can load a `Scene` and a `GenericController` with a `FXMLLoader` object:
```java
FXMLLoader fxmlLoader = new FXMLLoader(); // create a new instance of a FXML loader
fxmlLoader.setLocation(getClass().getResource("/Login.fxml")); // set the location of the FXML file
Parent root = fxmlLoader.load(); // load the FXML file
Scene scene = new Scene(root, Color.WHITE); // create a new scene with the FXML file
GenericController controller = fxmlLoader.getController(); // get the controller of the FXML file, specify in the FXML file
```
There are two ways to find the reference to the current running stage. The stage is either passed in as a parameter, 
such as the `start` method in Application class:
```java
public void start(Stage stage) {
    // load the scene...
    stage.setScene(scene);
    stage.show();
}
```
or it can be found by using the `getScene` method on any current UI element (`Node` object), such as a `Button`:
```java
Stage stage = (Stage) button.getScene().getWindow();
```

### API Calls
...