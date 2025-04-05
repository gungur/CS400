// --== CS400 Project One File Header ==--
// Name: Sai Gungurthi
// CSL Username: gungurthi
// Email: sgungurthi@wisc.edu
// Lecture #: LEC 001/71951, TR 9:30AM-10:45AM
// Notes to Grader: None

/**
 * Helper class that groups together key-value pairs within a single object, so that you can store these pairs
 * within your hashtable's 1D array.
 */
public class HashPair<KeyType, ValueType> {

    // data fields
    protected KeyType key;
    protected ValueType value;

    // important for sentinel objects: tells you which empty indexes previously held objects
    // this can be useful for search methods
    protected boolean isDeleted = false;

    /**
     * Constructor that creates the key-value pair object.
     *
     * @param key to be stored in the HashPair object
     * @param value to be stored in the HashPair object
     */
    public HashPair(KeyType key, ValueType value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Simple "getter" method for the key
     *
     * @return key of a particular HashPair instance
     */
    public KeyType getKey() {
        return key;
    }

    /**
     * Simple "getter" method for the value
     *
     * @return value of a particular HashPair instance
     */
    public ValueType getValue() {
        return value;
    }

    /**
     * Simple "setter" method for the isDeleted data field, used in the remove() method to create sentinel objects
     *
     * @param isDeleted whether the particular key-value pair is "deleted" or not
     */
    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    /**
     * Simple "setter" method used in the remove() method to set key to "null", removing it from the collection.
     *
     * @param key to be set for a particular index's HashPair object
     */
    public void setKey(KeyType key) {
        this.key = key;
    }
}