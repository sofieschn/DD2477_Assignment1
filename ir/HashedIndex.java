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
 /**
  * Inserts this token in the hashtable.
  */
 public void insert(String token, int docID, int offset) {
     // Check if the token already exists in the index
     PostingsList postings = index.getOrDefault(token, new PostingsList());
 
     // Check if an entry for this docID already exists
     boolean entryExists = false;
     for (PostingsEntry entry : postings.list) {
         if (entry.docID == docID) {
             // Add the new offset to the existing entry
             entry.addOffset(offset);
             entryExists = true;
             break;
         }
     }
 
     // If no entry exists for this docID, create a new one
     if (!entryExists) {
         PostingsEntry newEntry = new PostingsEntry(docID);
         newEntry.addOffset(offset);
         postings.insert(newEntry);
     }
 
     // Add or update the postings list in the index
     index.put(token, postings);
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