/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   Johan Boye, 2017
 */  

package ir;

import java.util.ArrayList;
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

        
        // 1.4 Phrase queries
      
        if (queryType == QueryType.PHRASE_QUERY && query.queryterm.size() > 1) {
            PostingsList result = null;
    
            for (QueryTerm term : query.queryterm) {
                String token = term.term;
                PostingsList postings = index.getPostings(token);
    
                if (postings == null) {
                    return new PostingsList(); // Return empty result if a term is missing
                }

                if (result == null) {
                    result = postings; // Initialize result with the first term's postings
                } else {
                    result = phraseIntersect(result, postings); // Perform phrase intersection
                }


            }
    
            return result;
            
        // 1.3 Multi-word queries
        } else if (queryType == QueryType.INTERSECTION_QUERY) {
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

    private PostingsList phraseIntersect(PostingsList list1, PostingsList list2) {
        PostingsList result = new PostingsList(); // The final result
        int i = 0, j = 0;
    
        // Iterate over both postings lists
        while (i < list1.size() && j < list2.size()) {
            PostingsEntry entry1 = list1.get(i);
            PostingsEntry entry2 = list2.get(j);
    
            if (entry1.docID == entry2.docID) {
                // Both entries are for the same document
                List<Integer> offsets1 = entry1.offsets;
                List<Integer> offsets2 = entry2.offsets;
    
                System.out.println("Processing docID: " + entry1.docID);
                System.out.println("Offsets1: " + offsets1);
                System.out.println("Offsets2: " + offsets2);
    
                // Matching offsets where terms are adjacent
                List<Integer> matchingOffsets = new ArrayList<>();
                for (int offset1 : offsets1) {
                    for (int offset2 : offsets2) {
                        if (offset2 == offset1 + 1) { // Adjacency condition
                            matchingOffsets.add(offset2);
                            break; // No need to check further for this offset1
                        }
                    }
                }
    
                if (!matchingOffsets.isEmpty()) {
                    // Add matching offsets to the result
                    PostingsEntry newEntry = new PostingsEntry(entry1.docID);
                    for (int offset : matchingOffsets) {
                        newEntry.addOffset(offset); // Use the addOffset method
                    }
                    result.insert(newEntry);
                }
    
                i++;
                j++;
            } else if (entry1.docID < entry2.docID) {
                i++;
            } else {
                j++;
            }
        }
    
        System.out.println("PhraseIntersect Result: " + result);
        return result;
    }
  
}



    

