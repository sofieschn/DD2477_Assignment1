/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   Johan Boye, 2017
 */  

package ir;

import java.util.ArrayList;
import java.util.HashMap;


// there is one postingslist for each term. THe list keeps track of all the documents where the term occurs 
public class PostingsList {
    
    /** The postings list */
    public ArrayList<PostingsEntry> list = new ArrayList<PostingsEntry>();
    // 2.2 
    private HashMap<Integer, PostingsEntry> entryMap = new HashMap<Integer, PostingsEntry>();

    public ArrayList<PostingsEntry> getList() {
        return list;
    }

    /** Number of postings in this list. */
    public int size() {
    return list.size();
    }

    /** Returns the ith posting. */
    public PostingsEntry get( int i ) {
        return list.get( i );
    }


    // returns the Hashmap of the entries
    public HashMap<Integer, PostingsEntry> getEntryMap() {
        return entryMap;
    }


    // Inserts a new PostingsEntry to the list
    // avoids adding query term to the same document multiple times.
    public void insert(PostingsEntry entry) {
        for (PostingsEntry e : list) { // iterate over each entry in the postingslist 
            if (e.docID == entry.docID) {// if the current entry matches the docID of the new entry, we dont need to add the document again
                    return;
            }
        }
        list.add(entry); // if the entry is not in the list, add it
    }

               
    public PostingsEntry checkForEntry(int docID) {
        if(entryMap.containsKey(docID)){
            return entryMap.get(docID);
        } else {
            return null;
        }
    }



    

}

