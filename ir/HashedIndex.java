/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   Johan Boye, 2017
 */  


package ir;

import java.util.HashMap;
import java.util.Iterator;


/**
 *   Implements an inverted index as a Hashtable from words to PostingsLists.
 */
public class HashedIndex implements Index {


    /** The index as a hashtable. */
    private HashMap<String,PostingsList> index = new HashMap<String,PostingsList>();


    /**
     *  Inserts this token in the hashtable.
     */
    public void insert( String token, int docID, int offset ) {
        //
        // YOUR CODE HERE

        // If the token is not in the index, create a new PostingsList
        // and add it to the index.
        if (!index.containsKey(token)) {
            PostingsList postings = new PostingsList();
            PostingsEntry entry = new PostingsEntry(docID, offset);
            postings.insert(entry);
            index.put(token, postings);
        }
        else {
            PostingsList postings = index.get(token);
            PostingsEntry entry = new PostingsEntry(docID, offset);
            postings.insert(entry);
        }


        
    }


    /**
     *  Returns the postings for a specific term, or null
     *  if the term is not in the index.
     */
    public PostingsList getPostings( String token ) {
        //
        // REPLACE THE STATEMENT BELOW WITH YOUR CODE
        //
        if (index.containsKey(token)) {
            return index.get(token);
        }
        return null;
    }


    /**
     *  No need for cleanup in a HashedIndex.
     */
    public void cleanup() {
    }
}
