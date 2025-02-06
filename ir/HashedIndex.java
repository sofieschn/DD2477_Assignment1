/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   Johan Boye, 2017
 */  


package ir;

import java.util.HashMap;
import java.util.Iterator;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Map;
import java.io.IOException;


/**
 *   Implements an inverted index as a Hashtable from words to PostingsLists.
 */
public class HashedIndex implements Index {


    /** The index as a hashtable. */
    private HashMap<String,PostingsList> index = new HashMap<String,PostingsList>();

    private static final String INDEX_SAVE_PATH = "/Users/sofieschnitzer/Desktop/KTH_HT24_filer/T2/DD2477_search_engines/Assignment_1/assignment1/indexSave.txt";



    


/**
 * Inserts this token in the hashtable.
 * For each postingsentry, we want to know in what documents it appears, and where in the documents (offset)
 * We keep all this info in a postingslist, and each term has a postingslist associated
 */
    public void insert(String token, int docID, int offset) {
        // Check if the token already exists in the index. if it does the postings will be assigned value of this token, if not a new empty one  will be created
        PostingsList postings = index.getOrDefault(token, new PostingsList()); 

        // iterate over the postingslist and check if the docID already exists
        boolean entryExists = false; 
        for (PostingsEntry entry : postings.list) { 
            if (entry.docID == docID) { // if the entry being inserted occurs in the document allready 
                entry.addOffset(offset); // Add the new offset (new position of the word) to the existing entry
                entryExists = true; // boolean is now true which means it will not loop next statement 
                break;
            }
        }

        // entry has not occured in this document before 
        if (!entryExists) {
            PostingsEntry newEntry = new PostingsEntry(docID); // we add the docID to the new entry
            newEntry.addOffset(offset); // and add the offset where the word occurs
            postings.insert(newEntry); // and add the entry to the postings list
        }

        index.put(token, postings); // and update the index with this new addition

    }




    /**
     *  Returns the postings for a specific term, or null
     *  if the term is not in the index.
     */
    public PostingsList getPostings( String token ) {
        //
        // REPLACE THE STATEMENT BELOW WITH YOUR CODE
        //
        if (index.containsKey(token)) { // if the index contains the token
            return index.get(token); // return the postings
        }
        return null; // else return null
    }



    /// we save the index to a file however not used (yet)
    public void writeIndexToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Map.Entry<String, PostingsList> entry : index.entrySet()) {
                String token = entry.getKey();
                PostingsList postings = entry.getValue();
                
                // Write the token followed by its postings list
                writer.write(token + ": " + postings.toString()); 
                writer.newLine();
            }
            System.out.println("Index successfully written to " + INDEX_SAVE_PATH);
        } catch (IOException e) {
            System.err.println("Error writing index to file: " + e.getMessage());
        }
    }
    
    
 

    /**
     *  No need for cleanup in a HashedIndex.
     */
    public void cleanup() {
        writeIndexToFile("indexSave.txt");
    }
}
