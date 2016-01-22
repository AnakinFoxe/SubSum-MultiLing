/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.cpp.iipl.subsum;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import suk.code.SubjectiveLogic.MDS.SubSumChinese;


/**
 *
 * @author Xing
 */
public class PrepareChineseEva extends PrepareEnglishEva {
    
    @Override
    public void prepSubSum(String srcPath, String dstPath) 
            throws IOException {
        File[] folders = new File(srcPath).listFiles();
        
        for (File folder : folders){
            // for each document set
            if (folder.isDirectory()) {
                System.out.println("Working on " + folder.getName());
                long elapsed = System.currentTimeMillis();
                
                // generate summaries
                SubSumChinese ssc = 
                        new SubSumChinese(folder.getCanonicalPath(), 30);
                ssc.assignScoreToSentences();
                List<String> results = ssc.getCandidateSentences();
                
                if (getCharCount(results) < 100)
                    System.out.println("Result may not meet limit: " 
                            + folder.getCanonicalPath());
                
                // create folder if not exist
                String newDstPath = dstPath + folder.getName() + "/";
                if (!new File(newDstPath).exists()) 
                    if (new File(newDstPath).mkdirs())
                        System.out.println("Created: " + newDstPath);
                    else
                        System.out.println("Failed creating: " + newDstPath);
                
                // write summaries to the file
                newDstPath += "SubSum.txt";
                FileWriter fw = new FileWriter(newDstPath, false);  // replace
                try (BufferedWriter bw = new BufferedWriter(fw)) {
                    for (String result : results)
                        bw.write(result);
                }
                
                elapsed = System.currentTimeMillis() - elapsed;
                System.out.println("  DONE: " + newDstPath 
                        + " (" + elapsed + "ms)");
            }
        }
    }
    
    
    public static void main(String[] args) throws IOException {
        PrepareChineseEva prep = new PrepareChineseEva();
        
        prep.prepFolders("./data/translated/chinese/task5/t5/docs/");
        
        prep.prepEvalFiles("./data/translated/chinese/eval/peers/5/", 
                "./data/evaluation/chinese/");
        
        prep.prepSource("./data/translated/chinese/task5/t5/docs/", 
                "./data/evaluation/chinese/source/");
        
        prep.prepSubSum("./data/evaluation/chinese/source/", 
                "./data/evaluation/chinese/SubSum/");
    }
}
