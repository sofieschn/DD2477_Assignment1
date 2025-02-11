/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   Johan Boye, 2017
 */  

package ir;

import java.util.ArrayList;
import java.util.Collections;
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

        /// 2.1 Ranked retrieval 
        
        if (queryType == QueryType.RANKED_QUERY && query.queryterm.size() > 1) {
            int N = index.docLengths.size(); // the number of documents in the index
            ArrayList<String> queryTerms = new ArrayList<String>();

            for (int i = 0; i < query.queryterm.size(); i++) {
                queryTerms.add(query.queryterm.get(i).term); // add the terms to the list of terms
                }
            
                PostingsList result = new PostingsList();

                for (int counter = 0; counter < queryTerms.size(); counter++) {
                    PostingsList postings = index.getPostings(queryTerms.get(counter)); // look in the index for the postings for this term
                    PostingsList rankedPostings = rankPostingLists(postings, N); // perform the ranked retrieval

                    for (PostingsEntry entry : rankedPostings.getList()) {
                        PostingsEntry existingEntry = result.checkForEntry(entry.docID);

                        if (existingEntry == null) {
                            result.insert(new PostingsEntry(entry.docID));
                        } else {
                            existingEntry.score += entry.score;
                        }
                                        
                    }
                }

                Collections.sort(result.getList());
                return result;
        }
    


        // ranked retrieval for 1 word queries
        if (queryType == QueryType.RANKED_QUERY && query.queryterm.size() == 1) {
            
            String token = query.queryterm.get(0).term; // there is only 1 term in the query
            PostingsList postings = index.getPostings(token); // we look in the index for the postings for this term

            int N = index.docLengths.size(); // the number of documents in the index
            postings = rankPostingLists(postings, N); // perform the ranked retrieval

            return postings;

        }

        // 1.4 Phrase queries
      
        // if we have selected the querytype phrase, and the query has more than one term
        if (queryType == QueryType.PHRASE_QUERY && query.queryterm.size() > 1) {
            PostingsList result = null; 
    
            // iterate through each term in the query
            for (QueryTerm term : query.queryterm) {
                String token = term.term; // we get the token from the term(s) in the query
                PostingsList postings = index.getPostings(token); // and we look in the index for the postings for this term
    
                if (postings == null) { // check that the postingslist is not null
                    return new PostingsList(); // Return new postingslist if it is null
                }

                if (result == null) { // this will happen for the first term in the query
                    result = postings; // set the result of occurencies to this terms postinglist 
                } else {
                    result = phraseIntersect(result, postings); // Perform phrase intersection for the following words in the query
                }


            }
    
            return result; // return the results 
            
        // 1.3 Multi-word queries
        } else if (queryType == QueryType.INTERSECTION_QUERY) {
            PostingsList result = null;

        // Iterate through each term in the query
        for (QueryTerm term : query.queryterm) {
            String token = term.term;
            PostingsList postings = index.getPostings(token); // Get postings for the term

            // if first term does not occur at all, no need to check
            if (postings == null) {
                // If a term has no postings, the result is empty
                return new PostingsList();
            }

            // If the result is null, this is the first term
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

    //  2.1 
    // ranked retrieval function 

    // we compute the tf-idf score for each term in the postingslist
    // we are sorting the hits of a search query by relevance
    // relevance is calculted based on how many times a term occurs in a document
    // and how many documents contain the term

    private PostingsList rankPostingLists(PostingsList list, int N) {
        int tf = 0; // the frequency that the term occurs in the doc
        int df_t = 0; // the number of docs that contain the term
        int docLength = 0; // wordcount of doc
        double idf = 0.0;
        double tf_idf = 0.0;

        for (int i = 0; i< list.size(); i++) {
            tf = list.get(i).offsets.size(); // get the frequency of the term in the doc
            df_t = list.size(); // get the number of docs that contain the term
            docLength = index.docLengths.get(list.get(i).docID); // get the length of the document from the index
            idf = java.lang.Math.log10(N/df_t); // compute the idf
            tf_idf = (tf * idf)/docLength; // compute the tf-idf
            list.get(i).score = tf_idf; // set the score to this doc
        }

        // based on the scores of each doc, sort the postingslist
        Collections.sort(list.getList());
        return list;

    }


    private PostingsList intersect(PostingsList list1, PostingsList list2) {
        PostingsList result = new PostingsList();
        int i = 0, j = 0;
    
        // Perform intersection using two pointers
        while (i < list1.size() && j < list2.size()) {
            PostingsEntry entry1 = list1.get(i);
            PostingsEntry entry2 = list2.get(j);
    
            // if the entries occur in the same docs 
            if (entry1.docID == entry2.docID) {
                // Add the matching document to the result
                result.insert(new PostingsEntry(entry1.docID));
                // move both pointer forward 
                i++;
                j++;
                // else we increase pointer 1 or 2 depending on which one is smaller
            } else if (entry1.docID < entry2.docID) {
                i++;
            } else {
                j++;
            }
        }
    
        return result;

    }


    // for queryterms with more than one word we will do phrase intersection
    private PostingsList phraseIntersect(PostingsList list1, PostingsList list2) {
        PostingsList result = new PostingsList(); // initialize the result postingslist
        int i = 0, j = 0; // initialize the pointers
    
        // Iterate over both postings lists
        // while i is less than the size of the first postings list and j is less than the size of the second postings list (withing the bounds of each list aka)
        while (i < list1.size() && j < list2.size()) {
            PostingsEntry entry1 = list1.get(i); // get the entry from the first postings list
            PostingsEntry entry2 = list2.get(j); // get the entry from the second postings list
    
            // Check if the documents are the same
            // else : else statement 1 
            if (entry1.docID == entry2.docID) {
                // Both entries are for the same document
                List<Integer> offsets1 = entry1.offsets; // get the offsets of the first entry
                List<Integer> offsets2 = entry2.offsets; // get the offsets of the second entry
    
                ////DEBUGGER STATEMENTS no function 
                System.out.println("Processing docID: " + entry1.docID);
                System.out.println("Offsets1: " + offsets1);
                System.out.println("Offsets2: " + offsets2);
    
                // Matching offsets where terms are adjacent

                // create a list for where the offsets "match" aka where they follow each other
                List<Integer> matchingOffsets = new ArrayList<>();

                // for each occurence of the word in the first list
                for (int offset1 : offsets1) {
                    // check if the word in the second list occurs right after it
                    for (int offset2 : offsets2) {
                        if (offset2 == offset1 + 1) { // Adjacency condition : after one another 
                            matchingOffsets.add(offset2); // add this match to the list 
                            break; // No need to check further for this match if it occurs once 
                        }
                    }
                }
    
                // if the list is not empty, means we got matches where the phrases intersect
                if (!matchingOffsets.isEmpty()) {
                    // Add matching offsets to the result
                    PostingsEntry newEntry = new PostingsEntry(entry1.docID);
                    for (int offset : matchingOffsets) {
                        newEntry.addOffset(offset); // Use the addOffset method
                    }
                    result.insert(newEntry); // we add match to the result
                }
    
                // then we move the pointers forward
                i++;
                j++;

                // if the document for entry1 is less than entry2, we move pointer 1 forward
                // repreats until we find a match or we reach the end of one of the postings lists
            } else if (entry1.docID < entry2.docID) {
                i++;
                // ant the reverse happens if entry2 is less than entry1
            } else {
                j++;
            }
        }
    
        System.out.println("PhraseIntersect Result: " + result);
        return result; // return the result back to the caller
    }
  
}



    

