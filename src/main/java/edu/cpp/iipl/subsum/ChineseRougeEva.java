/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.cpp.iipl.subsum;

import edu.csupomona.nlp.tool.rouge.ChineseROUGE;
import edu.csupomona.nlp.tool.rouge.Result;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author Xing
 */
public class ChineseRougeEva {
    
    public static void main(String[] args) throws IOException {
        ChineseROUGE rouge = new ChineseROUGE();
        rouge.setAlpha(0.5);
        rouge.setRmStopword(true);
        rouge.setUseStemmer(true);
        
        
        String peerPath = "./data/evaluation/chinese/peer.M.100/";
        String baselinePath = "./data/evaluation/chinese/baseline.M.100/";
        String modelPath = "./data/evaluation/chinese/model.M.100/";
        String subSumPath = "./data/evaluation/chinese/SubSum/";

        HashMap<String, String> docSetRet = new HashMap<>();
        
        FileWriter fwSum = new FileWriter("./data/evaluation/chinese/rouge_1_ss_sum.csv", false);
        BufferedWriter bwSum = new BufferedWriter(fwSum);
        
        File[] peers = new File(peerPath).listFiles();
        
        // for every peer folder
        for (File peer : peers) {
            File[] docSetFolders = new File(peerPath + peer.getName() + "/")
                    .listFiles();
            
            // record sum of results
            double[] sumRet = new double[3];
            
            // for every document set
            for (File docSetFolder : docSetFolders) {
                String docSetFolderName = docSetFolder.getName();
                File[] docs = new File(peerPath + peer.getName() + "/" 
                        + docSetFolderName + "/")
                        .listFiles();
                
                // for every document (should have only one)
                for (File doc : docs) {
                    Result score = rouge.computeNGramScore(1, 100, 0, 
                            doc.getCanonicalPath(), 
                            modelPath + docSetFolderName + "/");
                    
                    System.out.println("Peer" + peer.getName() + ", "
                                    + docSetFolderName + ", "
                                    + score.getGramScore() + ", "
                                    + score.getGramScoreP() + ", "
                                    + score.getGramScoreF());
                    
                    String ret;
                    if (docSetRet.containsKey(docSetFolderName)) 
                        ret = docSetRet.get(docSetFolderName);
                    else
                        ret = "";
                    ret += "Peer" + peer.getName() + ", "
                                    + docSetFolderName + ", "
                                    + score.getGramScore() + ", "
                                    + score.getGramScoreP() + ", "
                                    + score.getGramScoreF() + ",";
                    docSetRet.put(docSetFolderName, ret);
                    
                    sumRet[0] += score.getGramScore();
                    sumRet[1] += score.getGramScoreP();
                    sumRet[2] += score.getGramScoreF();
                }
            }
            
            bwSum.write("Peer" + peer.getName() + ", " 
                    + sumRet[0]/docSetFolders.length + ", " 
                    + sumRet[1]/docSetFolders.length + ", " 
                    + sumRet[2]/docSetFolders.length + ", \n");
            
        }
        
        File[] baselines = new File(baselinePath).listFiles();
        
        // for every baseline folder
        for (File baseline : baselines) {
            File[] docSetFolders = new File(baselinePath + baseline.getName() + "/")
                    .listFiles();
            
            // record sum of results
            double[] sumRet = new double[3];
            
            // for every document set
            for (File docSetFolder : docSetFolders) {
                String docSetFolderName = docSetFolder.getName();
                File[] docs = new File(baselinePath + baseline.getName() + "/" 
                        + docSetFolderName + "/")
                        .listFiles();
                
                // for every document (should have only one)
                for (File doc : docs) {
                    Result score = rouge.computeNGramScore(1, 100, 0, 
                            doc.getCanonicalPath(), 
                            modelPath + docSetFolderName + "/");
                    
                    System.out.println("Baseline" + baseline.getName() + ", "
                                    + docSetFolderName + ", "
                                    + score.getGramScore() + ", "
                                    + score.getGramScoreP() + ", "
                                    + score.getGramScoreF());
                    
                    String ret;
                    if (docSetRet.containsKey(docSetFolderName)) 
                        ret = docSetRet.get(docSetFolderName);
                    else
                        ret = "";
                    ret += "Baseline" + baseline.getName() + ", "
                                    + docSetFolderName + ", "
                                    + score.getGramScore() + ", "
                                    + score.getGramScoreP() + ", "
                                    + score.getGramScoreF() + ",";
                    docSetRet.put(docSetFolderName, ret);
                    
                    sumRet[0] += score.getGramScore();
                    sumRet[1] += score.getGramScoreP();
                    sumRet[2] += score.getGramScoreF();
                }
            }
            
            bwSum.write("Baseline" + baseline.getName() + ", " 
                    + sumRet[0]/docSetFolders.length + ", " 
                    + sumRet[1]/docSetFolders.length + ", " 
                    + sumRet[2]/docSetFolders.length + ", \n");
        }
        
        File[] docSetFolders = new File(subSumPath).listFiles();
        
        // record sum of results
        double[] sumRet = new double[3];
            
        // for every document set
        for (File docSetFolder : docSetFolders) {
            String docSetFolderName = docSetFolder.getName();
            File[] docs = new File(subSumPath + docSetFolderName + "/")
                    .listFiles();

            // for every document (should have only one)
            for (File doc : docs) {
                Result score = rouge.computeNGramScore(1, 100, 0, 
                        doc.getCanonicalPath(), 
                        modelPath + docSetFolderName + "/");

                System.out.println("SubSum, "
                                + docSetFolderName + ", "
                                + score.getGramScore() + ", "
                                + score.getGramScoreP() + ", "
                                + score.getGramScoreF());
                
               String ret;
                if (docSetRet.containsKey(docSetFolderName)) 
                    ret = docSetRet.get(docSetFolderName);
                else
                    ret = "";
                ret += "SubSum, "
                                + docSetFolderName + ", "
                                + score.getGramScore() + ", "
                                + score.getGramScoreP() + ", "
                                + score.getGramScoreF() + ",";
                docSetRet.put(docSetFolderName, ret);
                
                sumRet[0] += score.getGramScore();
                sumRet[1] += score.getGramScoreP();
                sumRet[2] += score.getGramScoreF();
            }
        }
        
        bwSum.write("SubSum, " 
                    + sumRet[0]/docSetFolders.length + ", " 
                    + sumRet[1]/docSetFolders.length + ", " 
                    + sumRet[2]/docSetFolders.length + ", \n");
        bwSum.close();
        
        FileWriter fw = new FileWriter("./data/evaluation/chinese/rouge_1_ss.csv", false);
        try (BufferedWriter bw = new BufferedWriter(fw)) {
            for (String name : docSetRet.keySet())
                bw.write(docSetRet.get(name) + "\n");
        }
    }
}
