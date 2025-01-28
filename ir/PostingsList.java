/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   Johan Boye, 2017
 */  

package ir;

import java.util.ArrayList;

public class PostingsList {
    
    /** The postings list */
    public ArrayList<PostingsEntry> list = new ArrayList<PostingsEntry>();

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

    // 
    //  YOUR CODE HERE

    // Inserts a new PostingsEntry to the list
    // avoids adding query term to the same document multiple time
    public void insert(PostingsEntry entry) {
        for (PostingsEntry e : list) {
            if (e.docID == entry.docID) {

                if (e.docID == entry.docID) {
                    return;
                }
            }
        }
        list.add(entry);
    }

               


    

}

