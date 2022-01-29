/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstoneproject.jobsearchapp;

public class Job<E extends Comparable<E>> implements Comparable<Job<E>> {
    
    //vars
    private String title, company, city, link;
    private Classification classification;
    
    public enum Classification{JUNK,NEITHER,APPLIED};
    
    //Constructors
    //default - do not use
    public Job() {
        
        System.out.println("I'm sorry, I cannot do that.");
        System.exit(-1); //to do: error reporting
        
    } // end public Job() {
    
    //use this constructor
    public Job(String title, String company, String city, String link) {
        
        this.title = title;
        this.company = company;
        this.city = city;
        this.link = link;
        this.classification = Classification.NEITHER;        
        
    } // end constructor
    
    //getters
    public String getTitle() {
        
        return this.title;
        
    } //end getTitle()
    
    public String getCompany() {
        
        return this.company;
        
    } //end getCompany()
    
    public String getCity() {
        
        return this.city;
        
    } //end getCity()
    
    public String getLink() {
        
        return this.link;
        
    } //end getLink()
    
    public int setClassification(Classification c) {
        
        try {
            
            this.classification = c;
            
        } catch (Exception e) {
            
            return -1;
            
        } // end try/catch
        
        return 1;
        
    } // end of setClassification(C
    
    public Classification getClassification() {
        
        return this.classification;
        
    } //end getClassification()
    
    @Override
    public int compareTo(Job j) {        
                
        Classification a = this.getClassification();
        Classification b = j.getClassification();
        int aI, bI;
        
        aI = getValueFromClass(a);
        bI = getValueFromClass(b);        
        
        if(aI>bI) {return 1;}
        if(bI>aI) {return -1;}
        return 0;
        
    } // end compareTo()
    
    public void compareTo(String[] keywords) {
        
        //check the title and company for keyword matches
        //mark them as JUNK if a match is found
        
        String title = new String(this.getTitle().toLowerCase());
        String company = new String(this.getCompany().toLowerCase());
        //String city = new String(this.getCity().toUpperCase());
        
        for(String kw : keywords) {
            
            if(title.contains(kw.toLowerCase()) || company.contains(kw.toLowerCase())) {
                
                this.setClassification(Classification.JUNK);
                return;
                
            } // end if
            
        } // end for
        
    } //end compartTO(string)
    
    //utility
    private int getValueFromClass(Classification c) {
        
        switch(c) {
            
            case NEITHER: return 0;
            case JUNK: return -1;
            case APPLIED: return 1;
            
        } // end switch
        
        return 999; // todo: error checking
        
    } // end getValueFromClass(
    
} // end of class
