/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.csupomona.nlp.subsum.gui;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Xing
 */
public final class SubSumApp extends Application {
    
    public SubSumApp() {
        
    }
    
    @Override
    public void init() {
        
    }
    
    @Override
    public void start(Stage stage) {
        setUserAgentStylesheet(STYLESHEET_MODENA);
        
        stage.setTitle("SubSum with multilingual support");
        
        Parent root = new MainPane(stage);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(true);
        
        
        stage.show();
    }
    
    @Override
    public void stop() {
        
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
