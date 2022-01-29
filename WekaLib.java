/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstoneproject.jobsearchapp;

//Weka imports   
import weka.core.Instance;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffLoader.ArffReader;
import weka.core.Attribute;
import weka.classifiers.Classifier;
import weka.core.SerializationHelper;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayesMultinomialText;

//other imports
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Random;

public class WekaLib {
    
    //set the data, classifier file locations
    //private static final URL DATAPATH = WekaTest.class.getResource("data.arff");
    //public static final File CLASSPATH = new File(getServletContext().getRealPath("/"), "nb.model");
    //public static final String DATAPATH = "data.arff";
    
    private final static File catalinaBase = new File(System.getProperty("catalina.base")).getAbsoluteFile();
    private static final String modelFile = "nb.model";
    private static final String dataFile = "data.arff";
    public final static File CLASSPATH = new File(catalinaBase, "webapps/" + modelFile);
    public final static File DATAPATH = new File(catalinaBase, "webapp/" + dataFile);
            
    public static Instances createNewDataset() {
        
        //method for creating a new arff file for job data
        //this method is called the first time the program is run
        //and at every model evaluation
        
        //set attributes for title, company, classification
        Attribute title = new Attribute("title", (ArrayList<String>)null);
        Attribute company = new Attribute("company", (ArrayList<String>)null);
        
        ArrayList<String> labels = new ArrayList();
        labels.add("JUNK");
        labels.add("APPLIED");
        labels.add("NEITHER");
        
        Attribute classification = new Attribute("classification", labels);
        
        ArrayList<Attribute> atts = new ArrayList();
        
        atts.add(title);
        atts.add(company);
        atts.add(classification);
        
        //data is capped at 1000 entries
        Instances data = new Instances("data", atts, 1000);
        
        //set the attribute of classification
        data.setClassIndex(data.numAttributes()-1);
        
        return data;
        
    } //end createNewDataset()
    
    public static Instances addData(Instances data, ArrayList<Job> jobs) {
        
        //method for adding new job data to the existing dataset
   
        for(Job j : jobs) {
            //System.out.println(j.getClassification().toString());
            double[] values = new double[data.numAttributes()];
            values[0] = data.attribute(0).addStringValue(j.getTitle());
            values[1] = data.attribute(1).addStringValue(j.getCompany());
            values[2] = data.attribute(2).indexOfValue(j.getClassification().toString());
            
            Instance inst = new DenseInstance(1.0, values);
            data.add(inst);
            
        } //end for
        
        return data;
        
    } // end addData
    
    public static Instances loadData() throws Exception {
        
        //method for loading the dataset from the harddrive
                
        BufferedReader reader =
            new BufferedReader(new FileReader(DATAPATH));
        ArffReader arff = new ArffReader(reader);
        Instances data = arff.getData();
        data.setClassIndex(data.numAttributes() - 1);
        
        return data;
        
    } // end loadData()
    
    public static int saveData(Instances data) throws Exception {
        
        //method for writing the dataset to the harddrive

        BufferedWriter writer = new BufferedWriter(new FileWriter(DATAPATH));
        writer.write(data.toString());
        writer.flush();
        writer.close();
        
        return 0;
        
    } //end saveData
    
    public static NaiveBayesMultinomialText createClassifier(Instances data) throws Exception{
        
        //method to build a new NB classifier and train it with the dataset
        
        ArffLoader loader = new ArffLoader();
        InputStream is = new ByteArrayInputStream(data.toString().getBytes());
        loader.setSource(is);
        Instances insts = data;
        insts.setClassIndex(insts.numAttributes()-1);
        
        NaiveBayesMultinomialText nb = new NaiveBayesMultinomialText();
        nb.buildClassifier(insts); 
        nb.setLowercaseTokens(true);
        //Instance curr;
        for(int i = 0; i < insts.numInstances(); i++)
            nb.updateClassifier(insts.get(i));
            
        return nb;
        
    } // end createClassifier
    
    public static int saveClassifier(Classifier nb) throws Exception {
        
        //method for saving a classifier
        
        SerializationHelper.write(CLASSPATH.toString(), nb);
        
        return 0;
        
    } // end saveClassifier
    
    public static Classifier loadClassifier() throws Exception {
        
        //method for loading a classifier
        
        Classifier nb = (Classifier) SerializationHelper.read(CLASSPATH.toString());
        
        return nb;
        
    } //end loadClassifer
    
    public static double evaluateModel(Instances data) throws Exception {
        
        //method for deciding if there is enough data to rely on the model
        //return value is the percent of correct predictions
        
        Evaluation eval = new Evaluation(data);
        NaiveBayesMultinomialText nb = new NaiveBayesMultinomialText();
        
        int folds = (data.numInstances()/2)+1; //number of folds for xvalidation
        
        eval.crossValidateModel(nb, data, folds, new Random(42));

        return eval.pctCorrect();
        
    } // end evaluateModel
    
    public static ArrayList<Job> classifyJobs(Instances data, ArrayList<Job> jobs, Classifier nb) throws Exception {
        
        //classify jobs by converting them to an unlabeled instance, classifying
        //them, then assign the classification to the job
        //return the reclassified jobs
        
        Instances unlabeled = data;
        double[] values = new double[unlabeled.numAttributes()];
        
        for(Job j : jobs) {
            
            //already classified -> skip
            if(j.getClassification() != Job.Classification.NEITHER)
                continue;
            
            //set the
            values[0] = unlabeled.attribute(0).addStringValue(j.getTitle());
            values[1] = unlabeled.attribute(1).addStringValue(j.getCompany());
            values[2] = unlabeled.attribute(2).addStringValue(j.getClassification().toString());

            Instance inst = new DenseInstance(1.0, values);
            inst.setDataset(data);
            double label = nb.classifyInstance(inst);
            inst.setClassValue(label);
            
            switch(inst.stringValue(2)) {
                
                case "NEITHER": j.setClassification(Job.Classification.NEITHER); break;
                case "JUNK": j.setClassification(Job.Classification.JUNK); break;
                case "APPLIED": j.setClassification(Job.Classification.APPLIED); break;
                
            } //end switch
            
        } // end for
        
        return jobs;
        
    } //end classifiy jobs
    
    public static boolean clearData() {
        
        //method for deleting the data file containing the dataset
        
        File file = DATAPATH;
        
        if(file.exists()) {
            
            return file.delete();
            
        } // end if
        
        return false;
        
    } // end clearData()
    
} //end class
