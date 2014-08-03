/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.csupomona.nlp.subsum.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialogs;
import suk.code.SubjectiveLogic.MDS.SubSumChinese;
import suk.code.SubjectiveLogic.MDS.SubSumGenericMDS;
import suk.code.SubjectiveLogic.MDS.SubSumSpanish;

/**
 *
 * @author Xing
 */
public final class MainPane extends GridPane {
    
    private int percentage_ = 10;
    
    private final TextArea taLeft_ = new TextArea();
    private final TextArea taRight_ = new TextArea();
    
    public MainPane(Stage stage) {
        
        
        // column and row definition
        // 2 columns
        ColumnConstraints ccLeft = new ColumnConstraints();
        ccLeft.setPercentWidth(50);
        ColumnConstraints ccRight = new ColumnConstraints();
        ccRight.setPercentWidth(50);
        getColumnConstraints().setAll(ccLeft, ccRight);
        // 2 rows
        RowConstraints rcTop = new RowConstraints();
        rcTop.setMinHeight(40);     // this min value is probably to small
        RowConstraints rcRest = new RowConstraints();
        rcRest.setVgrow(Priority.ALWAYS);   // finally found a way to expand
        getRowConstraints().setAll(rcTop, rcRest);
        
        // browsing button
        final Button btnBrowse = new Button("Select files...");
        btnBrowse.setPrefWidth(110);
        btnBrowse.setOnAction(
                (ActionEvent t) -> {
                    FileChooser fc = new FileChooser();
                    fc.setInitialDirectory(new File(
                            System.getProperty("user.dir")));
                    List<File> files = fc.showOpenMultipleDialog(null);
                    
                    try {
                        // read files
                        List<String> inputTexts = readFiles(files);
                        
                        taLeft_.setText("");
                        inputTexts.stream().forEach((text) -> {
                            taLeft_.appendText(text + "\n");
                        });
                    } catch (IOException ex) {
                        Logger.getLogger(MainPane.class.getName())
                                .log(Level.SEVERE, null, ex);
                    }
        });
        
        // language list
        ObservableList<String> supportLanguages = 
                FXCollections.observableArrayList(
                    "Chinese",
                    "English",
                    "Spanish"
                );
        final ComboBox cbLanguage = new ComboBox(supportLanguages);
        cbLanguage.setPrefWidth(110);
        
        // percentage slider & percentage textfield
        final Slider sldPercentage = new Slider(10, 90, 30); // min, max, default
        sldPercentage.setShowTickMarks(true);
        sldPercentage.setMajorTickUnit(50);
        sldPercentage.setMinorTickCount(4);
        sldPercentage.setBlockIncrement(10);
        sldPercentage.setSnapToTicks(true);
        sldPercentage.setPrefWidth(300);
        final Label lblPercentageValue = new Label();
        lblPercentageValue.setText(String.format("%3.0f", sldPercentage.getValue()));
        lblPercentageValue.setPrefWidth(50);
        sldPercentage.valueProperty().addListener(
                (ObservableValue<? extends Number> observable, 
                        Number oldValue, Number newValue) -> {
            lblPercentageValue.setText(String.format("%3.0f", newValue));
            percentage_ = newValue.intValue();
        });
        
        
        // reset button
        final Button btnReset = new Button("Reset");
        btnReset.setPrefWidth(60);
        btnReset.setOnAction(
                (ActionEvent t) -> {
                    // remove everything on both text areas
                    taLeft_.setText("");
                    taRight_.setText("");
                }
        );
        
        // summarize button
        final Button btnSummarize = new Button("Summarize");
        btnSummarize.setPrefWidth(150);
        btnSummarize.setOnAction(
                (ActionEvent t) -> {
                    List<String> summaries = new ArrayList<>();
                    
                    // read from left text area
                    List<String> inputTexts = new ArrayList<>();
                    inputTexts.add(taLeft_.getText());
                    
                    if (taLeft_.getText().trim().length() > 1) {
                        if (cbLanguage.getValue() != null) {
                            // get summaries from SubSum-Core program
                            String language = cbLanguage.getValue().toString();
                            if (language.equals("Chinese"))
                                summaries.addAll(getChineseSum(inputTexts, percentage_));
                            else if (language.equals("Spanish"))
                                summaries.addAll(getSpanishSum(inputTexts, percentage_)); 
                            else if (language.equals("English"))
                                summaries.addAll(getEnglishSum(inputTexts, percentage_));
                        } else {
                            // using ControlsFX to create error dialog
                            Dialogs.create()
                                    .owner(stage)
                                    .title("Oops!")
                                    .masthead("Please select a language to proceed.")
                                    .showError();
                        }

                        // append summaries to right text area for display
                        taRight_.setText("");
                        int num = 0;
                        for (String summary : summaries) {
                            ++num;
                            taRight_.appendText(num + ". " + summary.trim() + "\n");
                        }
                    }
                }
        );
        
        // toolbar
        final ToolBar tbTop = new ToolBar(btnBrowse,
                                    cbLanguage,
                                    sldPercentage,
                                    lblPercentageValue,
                                    btnReset,
                                    btnSummarize);
        GridPane.setConstraints(tbTop, 0, 0, 2, 1);
        add(tbTop, 0, 0);
        
        int prefWidth = 400;
        int prefHeight = 600;
        // left pane for input or display input
        taLeft_.prefWidth(prefWidth);
        taLeft_.prefHeight(prefHeight);
        taLeft_.setWrapText(true);
        
        
        final ScrollPane spLeft = new ScrollPane();
        spLeft.setContent(taLeft_);
        spLeft.setFitToHeight(true);
        spLeft.setFitToWidth(true);
        spLeft.setPrefWidth(prefWidth);
        spLeft.setPrefHeight(prefHeight);
        
        add(spLeft, 0, 1);
        
        
        // right pane for display output
        taRight_.prefWidth(prefWidth);
        taRight_.prefHeight(prefHeight);
        taRight_.setWrapText(true);
        taRight_.setEditable(false);    // read-only
        
        
        final ScrollPane spRight = new ScrollPane();
        spRight.setContent(taRight_);
        spRight.setFitToHeight(true);
        spRight.setFitToWidth(true);
        spRight.setPrefWidth(prefWidth);
        spRight.setPrefHeight(prefHeight);
        
        add(spRight, 1, 1);
        
        
        
    }
    
    private List<String> readFiles(List<File> files) 
            throws FileNotFoundException, IOException {
        List<String> lines = new ArrayList<>();
        for (File file : files) {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), "UTF8"));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().length() > 0)
                    lines.add(line.trim());
            }
        }
        
        return lines;
    }
    
    private List<String> getChineseSum(List<String> texts, int percentage) {
        SubSumChinese ssc = new SubSumChinese(texts, percentage);
        
        ssc.assignScoreToSentences();
        
       return ssc.getCandidateSentences();
    }
    
    private List<String> getEnglishSum(List<String> texts, int percentage) {
        SubSumGenericMDS ssgm = new SubSumGenericMDS(texts, percentage);
        
        ssgm.assignScoreToSentences();
        
       return ssgm.getCandidateSentences();
    }
    
    private List<String> getSpanishSum(List<String> texts, int percentage) {
        SubSumSpanish sss = new SubSumSpanish(texts, percentage);
    
        sss.assignScoreToSentences();
        
       return sss.getCandidateSentences();
    }
}
