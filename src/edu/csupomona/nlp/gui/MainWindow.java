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
import java.util.ArrayList;
import java.util.List;
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
    // GUI 
    private final ButtonGroup bgLanguage;
    private JRadioButton rbZh, rbEn, rbEs;
    private final JButton btnSummarize;
    private final JButton btnClear;
    private final JButton btnBrowse;
    private JTextArea taWhiteboardIn;
    private JTextArea taWhiteboardOut;
    private final JScrollPane spWhiteboardIn;
    private final JScrollPane spWhiteboardOut;
    private final JLabel lblLeft;
    private final JLabel lblRight;
    
    // parameters and data
    private File[] files;
    private List<String> inputText;
    private int percentage;
    
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
    		
        btnBrowse = new JButton( "Browse..." );
        btnBrowse.addActionListener(
            new ActionListener()
            {
                @Override
                public void actionPerformed( ActionEvent e ) {

                    JFileChooser fc = new JFileChooser();
                    fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
                    fc.setMultiSelectionEnabled( true );
                    fc.showOpenDialog( null );
                    files = fc.getSelectedFiles();
                    
                    try {
                        // read lines from selected files
                        inputText = readFiles(files);
                        
                        // reset whiteboard and add new lines
                        taWhiteboardIn.setText("");
                        for (String text : inputText) 
                            taWhiteboardIn.append(text + "\n");
                    } catch (IOException ex) {
                        Logger.getLogger(MainWindow.class.getName())
                                .log(Level.SEVERE, null, ex);
                    }
                }
            }
        );
    		
    		
        btnSummarize = new JButton( "Summarize" );
        btnSummarize.addActionListener(
            new ActionListener(){
                @Override
                public void actionPerformed( ActionEvent e ){
                    List<String> summaries = new ArrayList<>();
                    
                    if( rbZh.isSelected() ){
                        // get summaries
                        summaries.addAll(getChineseSum(inputText));
                    }else if( rbEn.isSelected() ){
                        // get summaries
                        summaries.addAll(getEnglishSum(inputText));
                    }else if( rbEs.isSelected() ){
                        // get summaries
                        summaries.addAll(getSpanishSum(inputText)); 
                    }else
                        JOptionPane.showMessageDialog( null, 
                                "Please select one language to proceed" );

                    // append summaries to whiteboard for display
                    taWhiteboardOut.setText("");
                    int num = 0;
                    for (String summary : summaries) {
                        num++;
                        taWhiteboardOut.append(num + ". " + summary + "\n");
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
                    // remove everything on both whiteboards
                    taWhiteboardOut.setText("");
                    taWhiteboardIn.setText("");
                    
                    // reset sentences
                    inputText.clear();
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
        hpg1a.addComponent( btnBrowse );

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
        hpg2a.addComponent( lblRight );

//        GroupLayout.SequentialGroup hpg2h = layout.createSequentialGroup();
//        hpg2h.addGroup( hpg2a ).addComponent( btnExit );

        GroupLayout.ParallelGroup hpg2 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        hpg2.addGroup( hpg2a );
        hpg2.addComponent( spWhiteboardOut );

        // TODO: here comes exception
        layout.setHorizontalGroup(layout.createSequentialGroup().addGroup( hpg1 ).addGroup( hpg2 ));



        GroupLayout.ParallelGroup vpg1 = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
        vpg1.addComponent(rbZh);
        vpg1.addComponent(rbEn);
        vpg1.addComponent(rbEs);

        GroupLayout.ParallelGroup vpg2 = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
        vpg2.addComponent( btnBrowse );
        vpg2.addComponent( btnSummarize );
        vpg2.addComponent( btnClear );

        GroupLayout.ParallelGroup vpg3 = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
        vpg3.addComponent(lblLeft);
        vpg3.addComponent(lblRight );

        GroupLayout.ParallelGroup vpg4 = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
        vpg4.addComponent( spWhiteboardIn );
        vpg4.addComponent( spWhiteboardOut );


        layout.setVerticalGroup(layout.createSequentialGroup().addGroup(vpg1).addGroup(vpg2).addGroup(vpg3).addGroup(vpg4));

        layout.linkSize(SwingConstants.HORIZONTAL,new Component[] { spWhiteboardIn, spWhiteboardOut });
        layout.linkSize(SwingConstants.HORIZONTAL,new Component[] { btnBrowse, btnSummarize, btnClear });

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
    
    public static void main(String[] args) {
        new MainWindow();
    }
}
