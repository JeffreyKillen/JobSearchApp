/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstoneproject.jobsearchapp;

import java.util.ArrayList;

public class KeywordFilter implements Filter {
    
    private ArrayList<Job> jobs;
    
    public KeywordFilter(String[] filterTerms, ArrayList<Job> jobs) {
        
        this.jobs = jobs;
        
        if(filterTerms.length>0)
            filter(filterTerms, jobs);        
        
    } // end constructor
    
    private void filter(String[] filterTerms, ArrayList<Job> jobs) {
        
        //O(m*n) comparison
        for(Job j : jobs) {
            
            j.compareTo(filterTerms);
            
        } // end for job
        
        this.jobs = jobs;
        
    } // end filter()
    
    @Override
    public ArrayList<Job> getFilteredJobs() {
        
        return this.jobs;
        
    } // end getFilteredJobs()
    
} // end class
