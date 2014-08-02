/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.csupomona.nlp;

import edu.csupomona.nlp.util.StanfordTools;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import suk.code.SubjectiveLogic.MDS.SubSumGenericMDS;


/**
 *
 * @author Xing
 */
public class PrepareEnglishEva {
    
    // regular expression for generating new folders and files
    private String REG_PATH_SRC;
    private String REG_PATH_DST;
    
    private boolean isRegPathUpdated = false;   // a protection flag
    
    private final StanfordTools stan;   // Stanford NLP tools
    
    private final Pattern ptnFilename;
    
    private final List<String> folderNames;
    
    public PrepareEnglishEva() {
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit");
        stan = new StanfordTools(props);
        
        ptnFilename = 
                Pattern.compile("D([0-9]+)\\.M\\.100\\.[A-Z]\\.([A-Z0-9]+)");
        
        folderNames = new ArrayList<>();
    }
    
    /**
     * Update regular expression based on source and target path
     * @param sourcePath        Path of the files to be translated
     * @param targetPath        Path of translated files
     */
    private void updatePathReg(String sourcePath, String targetPath) {
        String procSource = sourcePath.replaceAll("^\\.", "");
        String procTarget = targetPath.replaceAll("^\\.", "");
        
        // only for windows platform
        if (System.getProperty("os.name").contains("Windows")) {
            procSource = procSource.replaceAll("/", "\\\\\\\\");
            procTarget = procTarget.replaceAll("/", "\\\\\\\\");
        }
        
        REG_PATH_SRC = procSource;
        REG_PATH_DST = procTarget;
        
        isRegPathUpdated = true;
    }
    
    /**
     * Generate corresponding new file path using regular expression
     * @param filePath      Original path
     * @return              Corresponding new path
     */
    private String getNewFilePath(String filePath) {
        if (isRegPathUpdated)
            return filePath.replaceAll(REG_PATH_SRC, REG_PATH_DST);
        else
            return null;
    }
    
    
    /**
     * Create corresponding directory hierarchy according to source.
     * @param sourcePath            Source path 
     * @throws IOException 
     * @throws NullPointerException
     */
    private void createCorresFolders(String sourcePath) 
            throws IOException, NullPointerException {
        File[] files = new File(sourcePath).listFiles();
        
        for (File file : files) {
            if (file.isDirectory()) {
                String path = file.getCanonicalPath();
                
                // get corresponding folder path
                String newPath = getNewFilePath(path);
                
                // if the folder does not exist, create it
                if (!new File(newPath).exists()) {
                    if (new File(newPath).mkdir())
                        System.out.println("Created: " + newPath);
                    else
                        System.out.println("Failed creating: " + newPath);
                }
                
                // go recurrsive
                createCorresFolders(path);
            }
        }
    }
    
    /**
     * Read through the path and record all the files into a list.
     * Note that directories will be excluded.
     * @param basePath          The path to be read
     * @return                  List of canonical file paths
     * @throws IOException 
     */
    private List<String> loadAllFilePath(String basePath) throws IOException {
        List<String> fileList = new ArrayList<>();
        
        File[] files = new File(basePath).listFiles();
        
        for (File file : files) {
            if (file.isDirectory())
                fileList.addAll(loadAllFilePath(file.getCanonicalPath()));
            else
                fileList.add(file.getCanonicalPath());
        }
        
        return fileList;
    }
    
    /**
     * Parse DUC2004 document
     * @param br        BufferedReader to the file
     * @return          List of sentences from the text section of the file
     * @throws IOException
     */
    protected List<String> parseDoc(BufferedReader br) throws IOException {
        List<String> text = new ArrayList<>();
        String line;
        String paragraph = "";
        
        boolean isText = false;
        while ((line = br.readLine()) != null) {
            // setting start and end point
            if (line.contains("<TEXT>"))
                isText = true;
            else if (line.contains("</TEXT>"))
                isText = false;
            
            // removing <***> and </***>
            line = line.replaceAll("<[A-Z]+>", "");
            line = line.replaceAll("</[A-Z]+>", "");
            
            if ((isText == true) && (line.length() > 0)) {
                // when meets a new paragraph, converts the collected paragraph
                // into sentences and reset the paragraph
                if (line.matches("^[\\s]+.*")) {
                    paragraph = paragraph.replaceAll("[ ]+", " ");
                    text.addAll(stan.sentence(paragraph));
                    text.add("\n");
                    paragraph = ""; // reset
                }
                
                // replace those strange `` and '' with "
                line = line.replaceAll("[`]{2,5}", "\"");
                line = line.replaceAll("[']{2,5}", "\"");
                
                // add this new line into paragraph
                paragraph += " " + line;
            }
        }
        
        // last paragraph
        if (paragraph.length() > 1) {
            paragraph = paragraph.replaceAll("[ ]+", " ");
            text.addAll(stan.sentence(paragraph));
            text.add("\n");
        }

        return text;
    }
    
    /**
     * Parse the file into a list of sentences
     * @param filePath      Path to the target file
     * @return              List of sentences from the file
     * @throws FileNotFoundException
     * @throws IOException
     */
    protected List<String> parseFile(String filePath) 
            throws FileNotFoundException, IOException  {
        List<String> content = new ArrayList<>();
        
        FileReader fr = new FileReader(filePath);
        BufferedReader br = new BufferedReader(fr);
        
        // read first line to tell the type of file
        String line;
        while ((line = br.readLine()) != null) {
            if (line.trim().length() > 0) {
                if (line.contains("<DOC>")) {
                    // DUC documents
                    content.addAll(parseDoc(br));
                } else if (line.contains("DUC 2004")) {
                    // duc2004.task5.topicsets
                    // do nothing about it currently
                    
                } else {
                    // model or peer summaries
                    do {
                        // replace those strange `` and '' with "
                        line = line.replaceAll("[`]{2,5}", "\"");
                        line = line.replaceAll("[']{2,5}", "\"");
                        
                        content.add(line.trim());
                    } while((line = br.readLine()) != null);
                }
                break;
            }
        }
        
        return content;
    }
    
    /**
     * Write text to file.
     * Note: Will overwrite the old file if names are the same.
     * @param text          List of sentences
     * @param filePath      Path to the file
     * @throws IOException 
     */
    private void writeText(List<String> text, String filePath) 
            throws IOException {
        FileWriter fw = new FileWriter(filePath, false);    // overwrite
        try (BufferedWriter bw = new BufferedWriter(fw)) {
            for (String line : text) 
                bw.write(line + "\n");
        }
    }
    
    /**
     * Compute the character count of the list of sentences.
     * Note: Whitespace is included.
     * @param text      List of sentences
     * @return 
     */
    protected Integer getCharCount(List<String> text) {
        Integer charCount = 0;
        for (String line : text) 
            charCount += line.length();
        
        return charCount;
    }
    
    public void prepFolders(String srcPath) 
            throws IOException {
         File[] folders = new File(srcPath).listFiles();
        
        
        for (File folder : folders) {
            folderNames.add(folder.getName());
            System.out.println("Document sets: " + folder.getName());
        }
    }
    
    public void prepEvalFiles(String srcPath, String dstPath) throws IOException {
        File[] files = new File(srcPath).listFiles();
        
        for (File file : files) {
            String filename = file.getName();
            Matcher matcher = ptnFilename.matcher(filename);
            if (matcher.matches()) {
                String docNum = matcher.group(1);
                String peerNum = matcher.group(2);
                String newDstPath = "";
                
                // update base path according to the file type
                if (peerNum.matches("[A-Z]")) 
                    // model files
                    newDstPath = dstPath + "model.M.100/";
                else {
                    if (Integer.valueOf(peerNum) <= 5) 
                        // baseline files
                        newDstPath = dstPath + "baseline.M.100/" + peerNum + "/";
                    else if ((Integer.valueOf(peerNum) >= 6)
                            && (Integer.valueOf(peerNum) <= 151))
                        // peer files
                        newDstPath = dstPath + "peer.M.100/" + peerNum + "/";
                    else {
                        System.out.println("Unknown type of file: " + filename);
                        continue;
                    }
                }
            
                // search for matching folder
                for (String folderName : folderNames) 
                    if (folderName.contains(docNum)) {
                        newDstPath += folderName + "/";
                        break;
                    }
                
                if (newDstPath.length() > 0) {
                    // create folder if does not exist
                    if (!new File(newDstPath).exists()) {
                        if (new File(newDstPath).mkdirs())
                        System.out.println("Created: " + newDstPath);
                    else
                        System.out.println("Failed creating: " + newDstPath);
                    }
                    
                    // copy file
                    Path finalPath = Files.copy(file.toPath(), 
                            new File(newDstPath + filename).toPath(), 
                            REPLACE_EXISTING);
                    System.out.println("Copied: " + finalPath.toString());
                } else 
                    System.out.println("Can't find folder!?");
                
            }
        }
    }

    
    public void prepSource(String srcPath, String dstPath) throws IOException {
        updatePathReg(srcPath, dstPath);
        
        createCorresFolders(srcPath);
        
        List<String> filePaths = loadAllFilePath(srcPath);
        
        for (String filePath : filePaths) {
            List<String> content = parseFile(filePath);
            
            String newFilePath = getNewFilePath(filePath);
            
            writeText(content, newFilePath);
            
            System.out.println("Extracted source to: " + newFilePath);
        }
    }
    
    public void prepSubSum(String srcPath, String dstPath) 
            throws IOException {
        File[] folders = new File(srcPath).listFiles();
        
        for (File folder : folders){
            // for each document set
            if (folder.isDirectory()) {
                System.out.print("Working on " + folder.getName());
                long elapsed = System.currentTimeMillis();
                // generate summaries
                SubSumGenericMDS ssg = 
                        new SubSumGenericMDS(folder.getCanonicalPath(), 30);
                ssg.assignScoreToSentences();
                List<String> results = ssg.getCandidateSentences();
                
                if (getCharCount(results) < 650)
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
        PrepareEnglishEva prep = new PrepareEnglishEva();
        
        prep.prepFolders("./data/duc/task5/t5/docs/");
        
//        prep.prepEvalFiles("./data/duc/eval/peers/5/", 
//                "./data/evaluation/english/");
//        
//        prep.prepSource("./data/duc/task5/t5/docs/", 
//                "./data/evaluation/english/source/");
        
        prep.prepSubSum("./data/evaluation/english/source/", 
                "./data/evaluation/english/SubSum/");
    }
    
}
