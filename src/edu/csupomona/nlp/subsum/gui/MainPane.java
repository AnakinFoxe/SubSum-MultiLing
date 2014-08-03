/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.csupomona.nlp.subsum.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import suk.code.SubjectiveLogic.MDS.SubSumChinese;
import suk.code.SubjectiveLogic.MDS.SubSumGenericMDS;
import suk.code.SubjectiveLogic.MDS.SubSumSpanish;

/**
 *
 * @author Xing
 */
public final class MainPane extends GridPane {
    
    private List<String> inputTexts;
    
    private final TextArea taLeft = new TextArea();
    private final TextArea taRight = new TextArea();
    
    public MainPane() {
        
        
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
        rcRest.setPercentHeight(95);    // only way to fill the height...
        getRowConstraints().setAll(rcTop, rcRest);
        
        // browsing button
        final Button btnBrowse = new Button("Select files...");
        btnBrowse.setOnAction(
                (ActionEvent t) -> {
                    FileChooser fc = new FileChooser();
                    fc.setInitialDirectory(new File(
                            System.getProperty("user.dir")));
                    List<File> files = fc.showOpenMultipleDialog(null);
                    
                    try {
                        // read files
                        inputTexts = readFiles(files);
                        
                        inputTexts.stream().forEach((text) -> {
                            taLeft.appendText(text + "\n");
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
        
        // percentage slider
        final Slider sldPercentage = new Slider();
        sldPercentage.setMin(0);
        sldPercentage.setMax(100);
        sldPercentage.setValue(10); // default
        sldPercentage.setShowTickLabels(true);
        sldPercentage.setShowTickMarks(true);
        sldPercentage.setMajorTickUnit(50);
        sldPercentage.setMinorTickCount(5);
        sldPercentage.setBlockIncrement(10);
        
        // reset button
        final Button btnReset = new Button("Reset");
        btnReset.setOnAction(
                (ActionEvent t) -> {
                    // remove everything on both text areas
                    taLeft.setText("");
                    taRight.setText("");
                    
                    // reset input texts
                    inputTexts.clear();
                }
        );
        
        // summarize button
        final Button btnSummarize = new Button("Summarize");
        btnSummarize.setOnAction(
                (ActionEvent t) -> {
                    List<String> summaries = new ArrayList<>();
                    
                    String language = cbLanguage.getValue().toString();
                    if (language.equals("Chinese")) {
                        summaries.addAll(getChineseSum(inputTexts));
                    } else if (language.equals("Spanish")) {
                        summaries.addAll(getSpanishSum(inputTexts)); 
                    } else if (language.equals("English")){
                        summaries.addAll(getEnglishSum(inputTexts));
                    } else {
                        Stage dialog = new Stage();
                        dialog.initStyle(StageStyle.UTILITY);
                        Scene scene = new Scene(new Group(new Text(25, 25, "Please select one language to proceed.")));
                        dialog.setScene(scene);
                        dialog.show();
                    }
                    
                    // append summaries to right text area for display
                    taRight.setText("");
                    int num = 0;
                    for (String summary : summaries) {
                        ++num;
                        taRight.appendText(num + ". " + summary + "\n");
                    }
                }
        );
        
        // toolbar
        final ToolBar tbTop = new ToolBar(btnBrowse,
                                    cbLanguage,
                                    sldPercentage,
                                    btnReset,
                                    btnSummarize);
        GridPane.setConstraints(tbTop, 0, 0, 2, 1);
        add(tbTop, 0, 0);
        
        int prefWidth = 400;
        int prefHeight = 600;
        // left pane for input or display input
//        taLeft = new TextArea();
        taLeft.prefWidth(prefWidth);
        taLeft.prefHeight(prefHeight);
        taLeft.setWrapText(true);
        
        
        final ScrollPane spLeft = new ScrollPane();
        spLeft.setContent(taLeft);
        spLeft.setFitToHeight(true);
        spLeft.setFitToWidth(true);
        spLeft.setPrefWidth(prefWidth);
        spLeft.setPrefHeight(prefHeight);
        
        add(spLeft, 0, 1);
        
        
        // right pane for display output
//        taRight = new TextArea();
        taRight.prefWidth(prefWidth);
        taRight.prefHeight(prefHeight);
        taRight.setWrapText(true);
        
        
        final ScrollPane spRight = new ScrollPane();
        spRight.setContent(taRight);
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
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().length() > 0)
                    lines.add(line.trim());
            }
        }
        
        return lines;
    }
    
    private List<String> getChineseSum(List<String> texts) {
        SubSumChinese ssc = new SubSumChinese(texts, 10);
        
        ssc.assignScoreToSentences();
        
       return ssc.getCandidateSentences();
    }
    
    private List<String> getEnglishSum(List<String> texts) {
        SubSumGenericMDS ssgm = new SubSumGenericMDS(texts, 10);
        
        ssgm.assignScoreToSentences();
        
       return ssgm.getCandidateSentences();
    }
    
    private List<String> getSpanishSum(List<String> texts) {
        SubSumSpanish sss = new SubSumSpanish(texts, 10);
    
        sss.assignScoreToSentences();
        
       return sss.getCandidateSentences();
    }
}
