package edu.anadolu.gui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.*;
import java.net.URISyntaxException;

public class GUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("gui.fxml"));
        System.out.println(getClass().getClassLoader().getResource("gui.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        Parent root =  loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @FXML
    private TextField serverIP;

    @FXML
    private TextField serverPort;

    @FXML
    private Button hostButton;

    @FXML
    private TextFlow hostPanel;

    @FXML
    private TextField joinIP;

    @FXML
    private TextField joinPort;

    @FXML
    private TextFlow userPanel;

    @FXML
    void hostServer(ActionEvent event) {
    }

    @FXML
    void joinServer(ActionEvent event) {
    }
}
