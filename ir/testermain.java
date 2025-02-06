package ir;

public class testermain {

    public static void main(String[] args) {
        PersistentHashedIndex index = new PersistentHashedIndex();
    
        // Insert terms and document metadata
        index.docNames.put(1, "doc1.txt");
        index.docLengths.put(1, 100);
        index.docNames.put(2, "doc2.txt");
        index.docLengths.put(2, 200);
    
        index.insert("apple", 1, 10);
        index.insert("apple", 1, 20);
        index.insert("banana", 2, 5);
    
        // Write the index to disk
        index.writeIndex();
    }
    
    
}
