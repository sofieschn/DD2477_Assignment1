/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   Johan Boye, 2017
 */  

package ir;

import java.util.List;
import java.util.ListIterator;

import ir.Query.QueryTerm;

/**
 *  Searches an index for results of a query.
 */
public class Searcher {

    /** The index to be searched by this Searcher. */
    Index index;

    /** The k-gram index to be searched by this Searcher */
    KGramIndex kgIndex;
    
    /** Constructor */
    public Searcher( Index index, KGramIndex kgIndex ) {
        this.index = index;
        this.kgIndex = kgIndex;
    }

    /**
     *  Searches the index for postings matching the query.
     *  @return A postings list representing the result of the query.
     */
    public PostingsList search( Query query, QueryType queryType, RankingType rankingType, NormalizationType normType ) { 
        //
        //  REPLACE THE STATEMENT BELOW WITH YOUR CODE
        
        // 1.4 Phrase queries
      

        // 1.3 Multi-word queries
        if (queryType == QueryType.INTERSECTION_QUERY) {
            PostingsList result = null;

        // Iterate through each term in the query
        for (QueryTerm term : query.queryterm) {
            String token = term.term;
            PostingsList postings = index.getPostings(token); // Get postings for the term

            if (postings == null) {
                // If a term has no postings, the result is empty
                return new PostingsList();
            }

            if (result == null) {
                // First term: initialize the result
                result = postings;
            } else {
                // Intersect the current result with the new postings
                result = intersect(result, postings);
            }
        }

        return result;
    } else /* Assignment 1.2 */ {        
            String token = query.queryterm.get(0).term;
            PostingsList postings = index.getPostings(token);

            return postings;

        }


    }
    private PostingsList intersect(PostingsList list1, PostingsList list2) {
        PostingsList result = new PostingsList();
        int i = 0, j = 0;
    
        // Perform intersection using two pointers
        while (i < list1.size() && j < list2.size()) {
            PostingsEntry entry1 = list1.get(i);
            PostingsEntry entry2 = list2.get(j);
    
            if (entry1.docID == entry2.docID) {
                // Add the matching document to the result
                result.insert(new PostingsEntry(entry1.docID));
                i++;
                j++;
            } else if (entry1.docID < entry2.docID) {
                i++;
            } else {
                j++;
            }
        }
    
        return result;

}
    }

