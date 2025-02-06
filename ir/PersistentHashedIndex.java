/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   Johan Boye, KTH, 2018
 */  

package ir;

import java.io.*;
import java.util.*;
import java.nio.charset.*;



/*
 *   Implements an inverted index as a hashtable on disk.
 *   
 *   Both the words (the dictionary) and the data (the postings list) are
 *   stored in RandomAccessFiles that permit fast (almost constant-time)
 *   disk seeks. 
 *
 *   When words are read and indexed, they are first put in an ordinary,
 *   main-memory HashMap. When all words are read, the index is committed
 *   to disk.
 */
public class PersistentHashedIndex implements Index {

    /** The directory where the persistent index files are stored. */
    public static final String INDEXDIR = "./index";

    /** The dictionary file name */
    public static final String DICTIONARY_FNAME = "dictionary";

    /** The data file name */
    public static final String DATA_FNAME = "data";

    /** The terms file name */
    public static final String TERMS_FNAME = "terms";

    /** The doc info file name */
    public static final String DOCINFO_FNAME = "docInfo";

    /** The dictionary hash table on disk can fit this many entries. */
    public static final long TABLESIZE = 611953L;

    /** The dictionary hash table is stored in this file. */
    RandomAccessFile dictionaryFile;

    /** The data (the PostingsLists) are stored in this file. */
    RandomAccessFile dataFile;

    /** Pointer to the first free memory cell in the data file. */
    long free = 0L;

    /** The cache as a main-memory hash map. */
    HashMap<String,PostingsList> index = new HashMap<String,PostingsList>();


    // ===================================================================

    /**
     *   A helper class representing one entry in the dictionary hashtable.
     */ 
    public class Entry {
        //
        //  YOUR CODE HERE
        //

        String term;
        long ptr;
        int size;

        public Entry(String term, long ptr, int size) {
            this.term = term;
            this.ptr = ptr;
            this.size = size;
        }

        public Entry(long ptr, int size) {
            this.ptr = ptr;
            this.size = size;
        }


    }


    // ==================================================================

    
    /**
     *  Constructor. Opens the dictionary file and the data file.
     *  If these files don't exist, they will be created. 
     */
    public PersistentHashedIndex() {
        try {
            dictionaryFile = new RandomAccessFile( INDEXDIR + "/" + DICTIONARY_FNAME, "rw" );
            dataFile = new RandomAccessFile( INDEXDIR + "/" + DATA_FNAME, "rw" );
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        try {
            readDocInfo();
        } catch ( FileNotFoundException e ) {
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    /**
     *  Writes data to the data file at a specified place.
     *
     *  @return The number of bytes written.
     */ 
    int writeData( String dataString, long ptr ) {
        try {
            dataFile.seek( ptr ); 
            byte[] data = dataString.getBytes();
            dataFile.write( data );
            return data.length;
        } catch ( IOException e ) {
            e.printStackTrace();
            return -1;
        }
    }


    /**
     *  Reads data from the data file
     */ 
    String readData( long ptr, int size ) {
        try {
            dataFile.seek( ptr );
            byte[] data = new byte[size];
            dataFile.readFully( data );
            return new String(data);
        } catch ( IOException e ) {
            e.printStackTrace();
            return null;
        }
    }


    // ==================================================================
    //
    //  Reading and writing to the dictionary file.

    /*
     *  Writes an entry to the dictionary hash table file. 
     *
     *  @param entry The key of this entry is assumed to have a fixed length
     *  @param ptr   The place in the dictionary file to store the entry
     */
    void writeEntry(Entry entry, long ptr) {
        try {
            dictionaryFile.seek(ptr); // Move to the correct location in the dictionary file
    
            // Write the term, padded to 20 characters
            String termPadded = String.format("%-20s", entry.term);
            dictionaryFile.write(termPadded.getBytes(Charset.forName("UTF-8")));
    
            // Write the pointer and size
            dictionaryFile.writeLong(entry.ptr);
            dictionaryFile.writeInt(entry.size);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    /**
     *  Reads an entry from the dictionary file.
     *
     *  @param ptr The place in the dictionary file where to start reading.
     */
    Entry readEntry(long ptr) {
        try {
            // Check if the pointer is within the file bounds
            if (ptr >= dictionaryFile.length()) {
                return null; // Out of bounds
            }
    
            dictionaryFile.seek(ptr); // Move to the correct location in the dictionary file
    
            // Read the term (20 characters)
            byte[] termBytes = new byte[20];
            dictionaryFile.read(termBytes);
            String term = new String(termBytes, Charset.forName("UTF-8")).trim();
    
            // Read the pointer and size
            long dataPtr = dictionaryFile.readLong();
            int size = dictionaryFile.readInt();
    
            // Check for uninitialized entry
            if (term.isEmpty() && dataPtr == 0 && size == 0) {
                return null;
            }
    
            return new Entry(term, dataPtr, size);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    


    // ==================================================================

    /**
     *  Writes the document names and document lengths to file.
     *
     * @throws IOException  { exception_description }
     */
    private void writeDocInfo() throws IOException {
        FileOutputStream fout = new FileOutputStream( INDEXDIR + "/docInfo" );
        for ( Map.Entry<Integer,String> entry : docNames.entrySet() ) {
            Integer key = entry.getKey();
            String docInfoEntry = key + ";" + entry.getValue() + ";" + docLengths.get(key) + "\n";
            fout.write( docInfoEntry.getBytes() );
        }
        fout.close();
    }


    /**
     *  Reads the document names and document lengths from file, and
     *  put them in the appropriate data structures.
     *
     * @throws     IOException  { exception_description }
     */
    private void readDocInfo() throws IOException {
        File file = new File( INDEXDIR + "/docInfo" );
        FileReader freader = new FileReader(file);
        try ( BufferedReader br = new BufferedReader(freader) ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                docNames.put( new Integer(data[0]), data[1] );
                docLengths.put( new Integer(data[0]), new Integer(data[2]) );
            }
        }
        freader.close();
    }


    /**
     *  Write the index to files.
     * takes all the terms in the in-memory index and writes them to the data file and dictionary file.
     */
    public void writeIndex() {
        int collisions = 0;
        ArrayList<Long> hashes = new ArrayList<Long>();
        try {
            // Write the 'docNames' and 'docLengths' hash maps to a file
            writeDocInfo();

            // Write the dictionary and the postings list

            //
            //  YOUR CODE HERE
            //

            // Go through all terms in the index
            long ptr = free;
            for(String term: index.keySet()){
                String encodedPostings = encode(index.get(term));
                int size = writeData(encodedPostings, ptr);
                Entry entry = new Entry(term, ptr, size);
                long hash = computeHash(term);
                if(hashes.contains(hash)){collisions++;}
                else{hashes.add(hash);}
                writeEntry(entry, hash);
                ptr += size;
            }

            //dataFile.close();
            //dictionaryFile.close();

        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        System.err.println( collisions + " collisions." );
    }
    

    // ==================================================================


    /**
     *  Returns the postings for a specific term, or null
     *  if the term is not in the index.
     */
    public PostingsList getPostings( String token ) {
        //
        //  REPLACE THE STATEMENT BELOW WITH YOUR CODE
        //

        try {
            // compute hash for this term
            long hash = computeHash(token);
            long dictionaryPtr = hash * 32; // Fixed size for dictionary entries

            // locate term in dict 
            while (true) {
                Entry entry = readEntry(dictionaryPtr);
                if (entry == null) {
                    return null;
                }
                if (entry.term.equals(token)) {

                    // read the postings list from the data file
                    String encodedPostings = readData(entry.ptr, entry.size);

                    // decode the postings list

                    if (encodedPostings == null || encodedPostings.isEmpty()) {
                        return null;
                    }

                    System.err.println(encodedPostings);
                    return decode(encodedPostings);
                }
                // collisions 
                dictionaryPtr = (dictionaryPtr + 32) % (TABLESIZE * 32); // Linear probing
            }

        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;

    }

    /// ____________ Helper function to get postings and decode the format and encode
    /// 
    /// 
    private PostingsList decode(String data) {
        PostingsList postings = new PostingsList();
    
        // Handle empty or null data
        if (data == null || data.trim().isEmpty()) {
            System.out.println("No data to decode. Returning empty postings list.");
            return postings;
        }
    
        System.out.println("Decoding data: '" + data + "'");
    
        String[] entries = data.split(";");
        for (String entry : entries) {
            if (entry.trim().isEmpty()) {
                System.out.println("Skipping empty entry in data.");
                continue; // Skip empty entries
            }
    
            String[] parts = entry.split(":");
            if (parts.length < 1) {
                System.out.println("Malformed entry: " + entry);
                continue; // Skip malformed entries
            }
    
            try {
                // Parse docID
                int docID = Integer.parseInt(parts[0].trim());
                PostingsEntry postingsEntry = new PostingsEntry(docID);
    
                // Parse offsets
                for (int i = 1; i < parts.length; i++) {
                    if (!parts[i].trim().isEmpty()) {
                        postingsEntry.addOffset(Integer.parseInt(parts[i].trim()));
                    }
                }
    
                // Add the entry to the postings list
                postings.insert(postingsEntry);
            } catch (NumberFormatException e) {
                System.err.println("Failed to parse entry: " + entry);
                e.printStackTrace();
            }
        }
    
        return postings;
    }
    
    
    
    private String encode(PostingsList postingsList) {
        StringBuilder sb = new StringBuilder();
    
        for (PostingsEntry entry : postingsList.getList()) {
            // Append the document ID
            sb.append(entry.docID);
    
            // Append offsets, separated by colons
            for (int offset : entry.offsets) {
                sb.append(":").append(offset);
            }
    
            // Separate entries with a semicolon
            sb.append(";");
        }
    
        return sb.toString();
    }
    
    
    
///-----------------

    /**
     *  Inserts this token in the main-memory hashtable.
     */
    public void insert( String token, int docID, int offset ) {
        //
        //  YOUR CODE HERE
        //

        PostingsList postings = index.getOrDefault(token, new PostingsList());

        boolean entryExists = false;
        for (PostingsEntry entry : postings.list) {
            if (entry.docID == docID) {

                // if an entry exists already we add the offset to the list of offsets
                entry.addOffset(offset);
                entryExists = true;
                break;
            }
        }

        // if we dont find the dcoument id in the list we create a new entry
        if (!entryExists) {
            PostingsEntry newEntry = new PostingsEntry(docID);
            newEntry.addOffset(offset);
            postings.insert(newEntry);
        }

        // we update the postings list in the index
        index.put(token, postings);
    }


    /// ==================================================================
    /// hash function
    /// ==================================================================
    /// 
    /// 
    
    private long computeHash(String term) {
        long hash = 7; // Start with a small prime number
        for (int i = 0; i < term.length(); i++) {
            hash = (31 * hash + term.charAt(i)) % TABLESIZE; // Polynomial rolling hash
        }
        return hash;
    }
    


    /**
     *  Write index to file after indexing is done.
     */
    public void cleanup() {
        System.err.println( index.keySet().size() + " unique words" );
        System.err.print( "Writing index to disk..." );
        writeIndex();
        System.err.println( "done!" );
    }
}
