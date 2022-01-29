package com.capstoneproject.jobsearchapp;

import static com.capstoneproject.jobsearchapp.WekaLib.CLASSPATH;
import static com.capstoneproject.jobsearchapp.WekaLib.DATAPATH;
import static com.capstoneproject.jobsearchapp.WekaLib.addData;
import static com.capstoneproject.jobsearchapp.WekaLib.classifyJobs;
import static com.capstoneproject.jobsearchapp.WekaLib.clearData;
import static com.capstoneproject.jobsearchapp.WekaLib.createClassifier;
import static com.capstoneproject.jobsearchapp.WekaLib.createNewDataset;
import static com.capstoneproject.jobsearchapp.WekaLib.evaluateModel;
import static com.capstoneproject.jobsearchapp.WekaLib.loadClassifier;
import static com.capstoneproject.jobsearchapp.WekaLib.loadData;
import static com.capstoneproject.jobsearchapp.WekaLib.saveClassifier;
import static com.capstoneproject.jobsearchapp.WekaLib.saveData;
import java.io.File;
import java.util.ArrayList;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayesMultinomialText;
import weka.core.Instances;

/**
 *
 * @author Jeffrey
 */
public class SpamFilter implements Filter {
    
    private static Instances data;
    private ArrayList<Job> jobs;
    private NaiveBayesMultinomialText nb;
    
    public SpamFilter(ArrayList<Job> jobs) {
        
        //We are taking the jobs that have been classified from the Keywordfilter
        //removing the classified jobs, loading up the dataset and model
        //classifying the jobs if the model isaccurate enough, saving everything
        
        File file = DATAPATH;
        this.jobs = jobs;
        
        try{
            
            //clearData(); //for testing mostly
            
            //load an existing dataset or make a new one
            //if(!file.exists()) {
            if(SpamFilter.data==null) { 
                   
                SpamFilter.data = this.data = createNewDataset();
            
            } else {
            
                this.data = createNewDataset();
                
            }   
            
            this.data = addData(this.data, this.jobs);
            
            System.out.println(this.data.toString()); //this can get big
            System.out.println("Total instances: " + this.data.numInstances() + "\n\n");
            //if we have some data
            if(this.data.numInstances()>0) {
                
                //evaluate the model
                double pct = evaluateModel(this.data);
                System.out.println("Evaluation results: " + pct);
                
                //if it is any good, load or create a classifier and classify
                if(pct>51.0) {

                    //File model = CLASSPATH;

                    this.nb = createClassifier(SpamFilter.data);

                    System.out.println(this.nb.toString());

                    this.jobs = classifyJobs(this.data, this.jobs, this.nb);

                } //end if pct
            
            } // end if data>0
            
            //print the job classifications to the output
            for(Job j : this.jobs) {
                
                System.out.println(j.getTitle() + " " + j.getClassification().toString());
                
            } //end for
            System.out.println("Here");
            //update data file
            SpamFilter.data = addData(this.data, this.jobs);
            
            //save the dataset and the classifier
            //System.out.println("Saved Data: " + saveData(this.data));
            //System.out.println("Saved Model: " + saveClassifier(this.nb));
            
        } catch (Exception e) {      
            
            e.printStackTrace();
            
        } // end try catch
        
    } //end constructor
    
    @Override
    public ArrayList<Job> getFilteredJobs() {
        
        return this.jobs;
        
    } // end get jobs
                
} // end class
