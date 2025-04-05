// --== CS400 Project One File Header ==--
// Name: Sai Gungurthi
// CSL Username: gungurthi
// Email: sgungurthi@wisc.edu
// Lecture #: LEC 001/71951, TR 9:30AM-10:45AM
// Notes to Grader: None

import java.util.NoSuchElementException;

/**
 * By implementing the MapADT interface, this class represents a collection that maps keys to values, in which
 * duplicate keys are not allowed (each key maps to exactly one value).
 *
 * @param <KeyType> generic type for key objects
 * @param <ValueType> generic type for value objects
 */
public class HashtableMap<KeyType, ValueType> implements MapADT<KeyType, ValueType> {

    // data fields

    // cannot instantiate an array with an associated generic type in Java
    @SuppressWarnings("unchecked") // this annotation suppresses the unchecked cast warning

    // array is only 1D so a helper class, called HashPair, was created to store both the key and value
    protected HashPair<KeyType, ValueType>[] hashtable;
    protected int size; // over-sized arrays need a size variable
    protected int capacity; // this will increase as the collection is filled
    protected double loadFactor; // once the array gets filled >= to this variable, the array is expanded and rehashed

    /**
     * Constructor with a capacity argument.
     *
     * @param capacity number of indexes in the array
     */
    public HashtableMap(int capacity) {
        this.capacity = capacity;
        // need to cast the array initialization
        hashtable = (HashPair<KeyType, ValueType>[]) new HashPair[this.capacity];

    }

    /**
     * Constructor with default capacity = 8.
     */
    public HashtableMap() {
        this.capacity = 8;
        hashtable = (HashPair<KeyType, ValueType>[]) new HashPair[this.capacity];
    }

    /**
     * Adds a new key-value pair/mapping to this collection.
     *
     * @param key to be added to collection
     * @param value to be added to collection
     * @throws IllegalArgumentException when key is null or duplicate of one already stored
     */
    @Override
    public void put(KeyType key, ValueType value) throws IllegalArgumentException {

        if (key == null || containsKey(key)) {
            throw new IllegalArgumentException("Key is null or duplicate is already stored.");
        }

        int hashCode = key.hashCode(); // long string of numbers

        // calculates the remainder (with modulo) which becomes the index for the mapping
        int index = Math.abs(hashCode) % this.capacity;

        HashPair<KeyType, ValueType> pair = new HashPair<>(key, value);

        // avoids collisions if index is already taken
        while (hashtable[index] != null) {
            index++;
            index = index % hashtable.length; // wraps back around array if end is reached
        }
        hashtable[index] = pair;
        size++; // important not to forget

        // essential to cast each variable in the calculation as a double or the loadFactor is a whole number
        loadFactor = ((double)getSize() / (double)hashtable.length);

        // the threshold in the specification is 0.7
        if (loadFactor >= 0.7) {
            growArray();
        }
    }

    /**
     * Private helper method that grows the array and rehashes the mappings once the loadFactor reaches the threshold.
     */
    private void growArray() {
        capacity = hashtable.length * 2;
        HashPair<KeyType, ValueType>[] tempArray = (HashPair<KeyType, ValueType>[]) new HashPair[capacity];

        // indexing through entire hashtable to find mappings to rehash while avoiding NullPointerExceptions
        for (int i = 0; i < hashtable.length; i++) {
            if (hashtable[i] != null) {
                if (hashtable[i].getKey() != null) {
                    int hashCode = hashtable[i].getKey().hashCode();
                    int index = Math.abs(hashCode) % tempArray.length;

                    while (tempArray[index] != null) {
                        index++;
                        index = index % tempArray.length;
                    }
                    tempArray[index] = hashtable[i];
                }
            }
        }

        hashtable = tempArray; // hashtable reference variable now points at the expanded and rehashed array in the heap
    }

    /**
     * Checks whether a key maps to a value within this collection.
     *
     * @param key to check if it maps to a value within collection
     * @return true if key maps to a value within this collection; false otherwise
     */
    @Override
    public boolean containsKey(KeyType key) {

        // indexing through entire hashtable to find key while avoiding NullPointerExceptions
        for (int i = 0; i < hashtable.length; i++) {
            if (hashtable[i] != null) {
                if (hashtable[i].getKey() != null) {
                    if (key.equals(hashtable[i].getKey())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Retrieves the specific value that a key maps to.
     *
     * @param key that is mapped to the specific value we want
     * @return the specific value that a key maps to
     * @throws NoSuchElementException when key is not stored in this collection
     */
    @Override
    public ValueType get(KeyType key) throws NoSuchElementException {

        if (!containsKey(key)) {
            throw new NoSuchElementException("Key is not stored in this collection.");
        }

        // indexing through entire hashtable to get value mapped to key while avoiding NullPointerExceptions
        for (int i = 0; i < hashtable.length; i++) {
            if (hashtable[i] != null) {
                if (hashtable[i].getKey() != null) {
                    if (key.equals(hashtable[i].getKey())) {
                        if (hashtable[i].getValue() != null) {
                            return hashtable[i].getValue();
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * Removes the mapping for a given key from this collection.
     *
     * @param key
     * @return the specific value that the key used to map to
     * @throws NoSuchElementException when key is not stored in this collection
     */
    @Override
    public ValueType remove(KeyType key) throws NoSuchElementException {

        if (!containsKey(key)) {
            throw new NoSuchElementException("Key is not stored in this collection.");
        }

        ValueType value = get(key);

        // same idea
        for (int i = 0; i < hashtable.length; i++) {
            if (hashtable[i] != null) {
                if (hashtable[i].getKey() != null) {
                    if (key.equals(hashtable[i].getKey())) {
                        hashtable[i].setKey(null); // key is removed
                        hashtable[i].setIsDeleted(true); // object is "removed", but still exists as a sentinel value
                        size--;
                    }
                }
            }
        }

        return value;
    }

    /**
     * Removes all key-value pairs from this collection.
     */
    @Override
    public void clear() {

        // can simply create a new array instead of painstakingly indexing through the entire array
        hashtable = (HashPair<KeyType, ValueType>[]) new HashPair[this.capacity];
        size = 0;
    }

    /**
     * Retrieves the number of keys stored within this collection.
     *
     * @return the number of keys stored within this collection
     */
    @Override
    public int getSize() {

        return size;
    }

    /**
     * Retrieves this collection's capacity (size of its underlying array).
     *
     * @return this collection's capacity
     */
    @Override
    public int getCapacity() {

        return capacity;
    }
}
