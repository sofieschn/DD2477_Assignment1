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

    public int docID;
    public double score = 0;


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


    //
    // YOUR CODE HERE
    //

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


    // adding offset for task 1.4 phrase queries
    public ArrayList<Integer> offsets = new ArrayList<Integer>();
    

    public void addOffset(int offset) {
        offsets.add(offset);
    }



}

