/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   Johan Boye, 2017
 */  

package ir;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.io.Serializable;
import java.util.List;

public class PostingsEntry implements Comparable<PostingsEntry>, Serializable {

    // each entry has a docID which is the document where it occurs 
    public int docID;
    public double score = 0;



    // contructor
    public PostingsEntry(int docID, double score) {
        this.docID = docID;
        this.score = score;
        this.offsets = new ArrayList<>(); // 1.4
    }

    

    // constructor no score
    public PostingsEntry(int docID) {
        this.docID = docID;
        this.score = 0;
    }


    
    /**
     *  PostingsEntries are compared by their score (only relevant
     *  in ranked retrieval).
     *
     *  The comparison is defined so that entries will be put in 
     *  descending order.
     */

    
    public int compareTo( PostingsEntry other ) {
        return Double.compare( other.score, score );
     }
 

    // adding offset for task 1.4 phrase queries
    // this is an array that keeps track of WHERE in the document an entry occurs 
    public ArrayList<Integer> offsets = new ArrayList<Integer>();
    


    // we can add an offset when an entry occurs in a document more than once. (or only once for that matter)
    public void addOffset(int offset) {
        offsets.add(offset);
    }



}

