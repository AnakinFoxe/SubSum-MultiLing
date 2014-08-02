/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.csupomona.nlp.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import suk.code.SubjectiveLogic.MDS.SubSumChinese;
import suk.code.SubjectiveLogic.MDS.SubSumGenericMDS;
import suk.code.SubjectiveLogic.MDS.SubSumSpanish;

/**
 *
 * @author Xing
 */
public class MainWindow extends JFrame {
    private ButtonGroup bgLanguage;
    private JRadioButton rbZh, rbEn, rbEs;
    private JButton btnSummarize;
    private JButton btnClear;
    private JButton btnExit;
    private JButton btnChooseFile;
    private JTextArea taWhiteboardIn;
    private JTextArea taWhiteboardOut;
    private JScrollPane spWhiteboardIn;
    private JScrollPane spWhiteboardOut;
    private JLabel lblLeft;
    private JLabel lblRight ;
    private File[] files;
    
    public MainWindow() {
        setVisible( true );
        setDefaultCloseOperation( EXIT_ON_CLOSE );
        bgLanguage = new ButtonGroup();
        rbZh = new JRadioButton( "Chinese" );
        rbEn = new JRadioButton( "English" );
        rbEs = new JRadioButton( "Spanish" );
        bgLanguage.add( rbZh );
        bgLanguage.add( rbEn );
        bgLanguage.add( rbEs );
        add( rbZh );
        add( rbEn );
        add( rbEs );
    		
        btnChooseFile = new JButton( "Choosing File" );
        btnChooseFile.addActionListener(
            new ActionListener()
            {
                @Override
                public void actionPerformed( ActionEvent e ) {

                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setMultiSelectionEnabled( true );
                    fileChooser.showOpenDialog( null );
                    files = fileChooser.getSelectedFiles();
                }
            }
        );
    		
    		
        btnSummarize = new JButton( "Summarize" );
        btnSummarize.addActionListener(
            new ActionListener(){
                @Override
                public void actionPerformed( ActionEvent e ){
                    List<String> summaries = new ArrayList<>();
                    try {
                        if( rbZh.isSelected() ){
                            // read sentences from selected files
                            List<String> sentences = readFiles(files);

                            // get summaries
                            summaries.addAll(getChineseSum(sentences));
                        }else if( rbEn.isSelected() ){
                            // read sentences from selected files
                            List<String> sentences = readFiles(files);

                            // get summaries
                            summaries.addAll(getEnglishSum(sentences));
                        }else if( rbEs.isSelected() ){
                            // read sentences from selected files
                            List<String> sentences = readFiles(files);
                            
                            // get summaries
                            summaries.addAll(getSpanishSum(sentences)); 
                        }else
                            JOptionPane.showMessageDialog( null, 
                                    "Please select one language to proceed" );
                    
                        // append summaries to whiteboard for display
                        taWhiteboardOut.setText("");
                        for (String summary : summaries) 
                            taWhiteboardOut.append(summary);
                    
                    } catch (IOException ex) {
                        Logger.getLogger(MainWindow.class.getName())
                                .log(Level.SEVERE, null, ex);
                    }
                    
                }
            }
        );  
    		
        btnClear = new JButton( "Clear" ); 
        btnClear.addActionListener(
            new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent arg0) 
                {
                    taWhiteboardOut.setText("");
                    taWhiteboardIn.setText("");
                }
            }
        );
        btnExit = new JButton( "Exit" );
    //	add( ExitButton );
        btnExit.addActionListener(
            new ActionListener(){
                @Override
                public void actionPerformed( ActionEvent e ){
                    System.exit( 0 );
                }
            }
        );
    		
    		
        lblLeft = new JLabel( "Or paste article below: " );
        lblRight = new JLabel( "Summary: " );
        taWhiteboardIn = new JTextArea( 10, 31 );
        taWhiteboardIn.setLineWrap( true );
        spWhiteboardIn = new JScrollPane( taWhiteboardIn );
        spWhiteboardIn.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    		
        taWhiteboardOut = new JTextArea( 10, 31 );
        taWhiteboardOut.setLineWrap( true );
        spWhiteboardOut = new JScrollPane( taWhiteboardOut);
        spWhiteboardOut.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    		
        Container c = getContentPane();
        GroupLayout layout = new GroupLayout(c);
        c.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        GroupLayout.ParallelGroup hpg1a = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        hpg1a.addComponent( rbZh );
        hpg1a.addComponent( btnChooseFile );

        GroupLayout.ParallelGroup hpg1b = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        hpg1b.addComponent( rbEn );
        hpg1b.addComponent( btnSummarize );

        GroupLayout.ParallelGroup hpg1c = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        hpg1c.addComponent( rbEs );	
        hpg1c.addComponent( btnClear );

        GroupLayout.SequentialGroup hpg1h = layout.createSequentialGroup();
        hpg1h.addGroup( hpg1a ).addGroup( hpg1b ).addGroup( hpg1c );

        GroupLayout.ParallelGroup hpg1 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        hpg1.addGroup( hpg1h );
        hpg1.addComponent( lblLeft );
        hpg1.addComponent( spWhiteboardIn );


        GroupLayout.ParallelGroup hpg2a = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        hpg2a.addComponent( lblRight  );

        GroupLayout.SequentialGroup hpg2h = layout.createSequentialGroup();
        hpg2h.addGroup( hpg2a ).addComponent( btnExit );

        GroupLayout.ParallelGroup hpg2 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        hpg2.addGroup( hpg2h );
        hpg2.addComponent( spWhiteboardOut );

        layout.setHorizontalGroup(layout.createSequentialGroup().addGroup( hpg1 ).addGroup( hpg2 ));



        GroupLayout.ParallelGroup vpg1 = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
        vpg1.addComponent(rbZh);
        vpg1.addComponent(rbEn);
        vpg1.addComponent(rbEs);

        GroupLayout.ParallelGroup vpg2 = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
        vpg2.addComponent( btnChooseFile );
        vpg2.addComponent( btnSummarize );
        vpg2.addComponent( btnClear );
        vpg2.addComponent( btnExit );

        GroupLayout.ParallelGroup vpg3 = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
        vpg3.addComponent(lblLeft);
        vpg3.addComponent(lblRight );

        GroupLayout.ParallelGroup vpg4 = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
        vpg4.addComponent( spWhiteboardIn );
        vpg4.addComponent( spWhiteboardOut );


        layout.setVerticalGroup(layout.createSequentialGroup().addGroup(vpg1).addGroup(vpg2).addGroup(vpg3).addGroup(vpg4));

        layout.linkSize(SwingConstants.HORIZONTAL,new Component[] { spWhiteboardIn, spWhiteboardOut });
        layout.linkSize(SwingConstants.HORIZONTAL,new Component[] { btnChooseFile, btnSummarize, btnClear, btnExit });

        setPreferredSize( new Dimension(730, 700) );
        pack();
    }
    
    // read sentences from files
    // NOTE: each line contains at most one sentence
    private List<String> readFiles(File[] files) 
            throws FileNotFoundException, IOException {
        List<String> sentences = new ArrayList<>();
        for (File file : files) {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().length() > 0)
                    sentences.add(line.trim());
            }
        }
        
        return sentences;
    }
    
    private List<String> getChineseSum(List<String> sentences) {
        SubSumChinese ssc = new SubSumChinese(sentences, 
                "./data/stopwords/zh_CN.txt", 10);
        
        ssc.assignScoreToSentences();
        
       return ssc.getCandidateSentences();
    }
    
    private List<String> getEnglishSum(List<String> sentences) {
        SubSumGenericMDS ssgm = new SubSumGenericMDS(sentences, 
                "./data/stopwords/en.txt", 10);
        
        ssgm.assignScoreToSentences();
        
       return ssgm.getCandidateSentences();
    }
    
    private List<String> getSpanishSum(List<String> sentences) {
        SubSumSpanish sss = new SubSumSpanish(sentences, 
                "./data/stopwords/en.txt", 10);
        
        sss.assignScoreToSentences();
        
       return sss.getCandidateSentences();
    }
    
    public static void main(String[] args) {
        new MainWindow();
    }
}
