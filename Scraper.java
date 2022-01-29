/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstoneproject.jobsearchapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;

public class Scraper implements API{
    
    //Search params
    private String[] searchTerms, location, jobs;
       
    //Constructor
    //Create a new apitest object with supplied search and location params
    public Scraper(String[] searchTerms, String[] location) {
        
        this.searchTerms = searchTerms;
        this.location = location;
        
        this.jobs = getRequest();
        
    } //end constructor
    
    private String[] getRequest() {
        
        try{
            
            //Build the search address
            StringBuffer sb = new StringBuffer("https://www.indeed.com/q-"); //base
            
            //add the search terms
            for(String s : this.searchTerms) {
                
                sb.append(s);
                sb.append("-");
                
            } //end for
            
            //add the location
            for(String s: this.location) {
                
                sb.append(s);
                sb.append("-");
                
            } //end for
            
            sb.append("jobs.html");
            
            
            URL url = new URL(sb.toString());
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            
            //get the HTTP response code
            int rCode = conn.getResponseCode();
            System.out.println("Response Code :: " + rCode);
            
            //On success rCode = 200
            if (rCode == HttpURLConnection.HTTP_OK) { 
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                }
                in.close();
                
                //split out the jobs
                String[] pResp = response.toString().split("jobmap");
                
                //cut off the long tail
                String[] temp = pResp[pResp.length-1].split(";");
                pResp[pResp.length-1] = temp[0];
                
                //indeed returns 15 jobs
                String[] jobs = new String[15];
                
                //remove the useless data
                for(int i = 2; i < (pResp.length); i++) {
                    
                    //System.out.println(pResp[i] + "\n");
                    jobs[i-2] = pResp[i];
                    
                } //end for
                
                
                //return the jobs
                return jobs;

            } else { //On failure
                
                    System.out.println("Request Failed.");
                    
            } //end if/else
            
        } catch (Exception e) {     
            
            //err hsndlin'
            
        } //end try/catch
     
        String[] uhoh = new String[1];
        uhoh[0] = "999";
        return uhoh;
        
    } //end getRequest()
    
    @Override
    public String[] getJobs() {
        
        return this.jobs;
        
    } //end getJobs()
    
} // end class