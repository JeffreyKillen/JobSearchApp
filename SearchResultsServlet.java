/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstoneproject.jobsearchapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author quaid
 */
@WebServlet(name = "JobResultsServlet", urlPatterns = {"/searchJobs"})
public class SearchResultsServlet extends HttpServlet {   

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        //Take the input passed from the form in index
        String keyTerms = request.getParameter("keyTerms");
        String filterTerms = request.getParameter("filters");
        String Locations = request.getParameter("locations");
        
        //Put the terms, filters, and locations in arrays
        String[] termsArray = keyTerms.split(" ");
        String[] filterTermsArray = filterTerms.split(" ");
        String[] LocationsArray = Locations.split(" ");
                
        API myAPI = new Scraper(termsArray, LocationsArray);
        
        
        ArrayList<Job> jobs = new ArrayList();
        for(String job : myAPI.getJobs()) {
            if(job!=null)
                jobs.add(stringToJob(job));
            
        }
        
        int numFound = jobs.size();
        
        jobs = new KeywordFilter(filterTermsArray, jobs).getFilteredJobs();
        //jobs = new SpamFilter(jobs).getFilteredJobs();
        Filter spam = new SpamFilter(jobs);
        jobs = spam.getFilteredJobs();
        
        StringBuffer sb = new StringBuffer(buildTable(jobs));
        
        
        //Set the information to attributes in the results page
        request.setAttribute("jobResults", sb.toString());
        request.setAttribute("numFound", numFound);
        
        //Where to post the results to
        String resultPage = "result.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(resultPage);
        dispatcher.forward(request, response);

    }
    
    
    
    private Job stringToJob(String jobString) {
        
        //take the API return string and create a job
        
        //split the String values by the single quotes
        String[] splitJobArray = jobString.split("\'");
        
        //Set the values to their titles in the String
        String title = "";
        String company = "";
        String city = ""; 
        String link = "https://www.indeed.com";
        String link2 = "";
        String link3 = "";
        
        //Search for the title values in the array. The following position
        //will be the desired display value, except for the link
        for(int i=0; i < splitJobArray.length; i++) {           
            if(splitJobArray[i].equals(",title:")){
                if(i < splitJobArray.length-1)
                    title = splitJobArray[i+1];
            }
            if(splitJobArray[i].equals(",cmp:")){
                if(i < splitJobArray.length-1)
                    company = splitJobArray[i+1];
            }
            if(splitJobArray[i].equals(",loc:")){
                if(i < splitJobArray.length-1)
                    city = splitJobArray[i+1];
            }
            if(splitJobArray[i].equals(",country:")){
                if(i < splitJobArray.length-1)
                    city = city + ", " + splitJobArray[i+1];
            }
            if(splitJobArray[i].contains("jk:")){
                if(i < splitJobArray.length-1)
                    link3 = splitJobArray[i+1];
            } 
            if(splitJobArray[i].contains(",cmplnk:")){
                if(i < splitJobArray.length-1)
                    link2 = splitJobArray[i+1];
            } 
        }
        //make the full link to the indeed job page
        link = link + link2 + "?vjk=" + link3;
        
        //create a job using the display values
        Job j = new Job(title,company,city,link); 
        //Dont provide a definitive classification
        j.setClassification(Job.Classification.NEITHER); 
        
        return j;
        
    } // end generateJobs()
    
    
    
    
    public String buildTable(ArrayList<Job> jobs) {
        
        StringBuffer table = new StringBuffer();
        
        for(Job j : jobs) {
            
            table.append(j.getCompany() + "**" 
                    + j.getTitle() + "**"
                    + j.getCity() + "**"
                    + j.getLink() + "***"); //add it to the table
            
        } //end for
        
        table.append("</table>");
        
        return table.toString();
        
    } // end buildTable    

} //end class
