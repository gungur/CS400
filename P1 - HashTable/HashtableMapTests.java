// --== CS400 Project One File Header ==--
// Name: Sai Gungurthi
// CSL Username: gungurthi
// Email: sgungurthi@wisc.edu
// Lecture #: LEC 001/71951, TR 9:30AM-10:45AM
// Notes to Grader: None

import java.util.NoSuchElementException;

/**
 * Tester class for HashtableMap.
 */
public class HashtableMapTests {

    /**
     * Tests if growArray() works as intended.
     *
     * @return true if all cases pass; false otherwise
     */
    public static boolean test1() {
        // case 1: adding the same amount as the capacity
        try {
            HashtableMap<Integer, String> test = new HashtableMap<>(4);
            test.put(1, "a");
            test.put(2, "b");
            test.put(3, "c");
            test.put(4, "d");
            // capacity doubles as it should
            if (test.getCapacity() != 8) {
                return false;
            }
        } catch (Exception e) {
            return false; // no exception should occur
        }

        // case 2: adding Strings as the keys rather than Integers
        try {
            HashtableMap<String, Integer> test = new HashtableMap<>(4);
            test.put("a", 1);
            test.put("b", 2);
            test.put("c", 3);
            test.put("d", 4);
            // Integers are not the only Objects that have hash codes
            if (test.getCapacity() != 8) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        // case 3: making the load factor exactly 0.7
        try {
            HashtableMap<Integer, String> test = new HashtableMap<>(10);
            test.put(1, "a");
            test.put(2, "b");
            test.put(3, "c");
            test.put(4, "d");
            test.put(5, "e");
            test.put(6, "f");
            test.put(7, "g");
            if (test.getCapacity() != 20) {
                return false;
            }
        } catch (Exception e) {
            return false; // no exception should occur
        }

        return true;
    }

    /**
     * Tests the put() method.
     *
     * @return true if all cases pass; false otherwise
     */
    public static boolean test2() {
        // case 1: tests hash collision avoidance
        try {
            HashtableMap<Integer, String> test = new HashtableMap<>(10);
            // the index of both 11 and 21 is 1
            test.put(11, "a");
            test.put(21, "b");
            if (!test.hashtable[1].getKey().equals(11) || !test.hashtable[2].getKey().equals(21)) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        // case 2: testing if method actually puts key-value pair into array/collection
        try {
            HashtableMap<Integer, String> test = new HashtableMap<>();
            test.put(5, "e");
            if (!test.get(5).equals("e")) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    /**
     * Tests the remove() method.
     *
     * @return true if all cases pass; false otherwise
     */
    public static boolean test3() {
        // removes a key and makes sure every variable has the correct updated values, including the sentinel data field
        try {
            HashtableMap<Integer, String> test = new HashtableMap<>(6);
            test.put(1, "a");
            test.put(2, "b");
            test.put(3, "c");
            String returnValue = test.remove(2);
            if (!test.hashtable[2].getValue().equals(returnValue)) {
                return false;
            }
            if (test.hashtable[2].getKey() != null || test.containsKey(2)) {
                return false;
            }
            if (test.hashtable[2].isDeleted == false || test.getSize() != 2) {
                return false;
            }
        } catch (Exception e){
            return false;
        }

        return true;
    }

    /**
     * Tests the clear(), getSize(), and getCapacity() methods.
     *
     * @return true if all cases pass; false otherwise
     */
    public static boolean test4() {
        // case 1: methods used on an occupied hashtable
        try {
            HashtableMap<Integer, String> test = new HashtableMap<>(10);
            test.put(1, "a");
            test.put(2, "b");
            test.put(3, "c");
            test.put(4, "d");
            test.put(5, "e");
            test.put(6, "f");
            if (test.getCapacity() != 10 || test.getSize() != 6) {
                return false;
            }

        // case 2: methods used on a "cleared" hashtable
            test.clear();
            if (test.getCapacity() != 10 || test.getSize() != 0) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    /**
     * Testing whether the correct exceptions are thrown when an invalid key is used.
     *
     * @return true if all cases pass; false otherwise
     */
    public static boolean test5() {
        // case 1: confirming that the proper exception occurs when a null key is used
        try {
            HashtableMap<Integer, String> test = new HashtableMap<>();
            test.put(null, "a");
            return false; // program should not get this far
        } catch (IllegalArgumentException e) {
            // correct
        } catch (Exception e) {
            return false;
        }

        // case 2: confirming that the proper exception occurs when a duplicate key is used
        try {
            HashtableMap<Integer, String> test = new HashtableMap<>();
            test.put(1, "a");
            test.put(1, "a");
            return false;
        } catch (IllegalArgumentException e) {
            // correct
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    /**
     * Main method to run tests.
     *
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Test 1 passed: " + test1());
        System.out.println("Test 2 passed: " + test2());
        System.out.println("Test 3 passed: " + test3());
        System.out.println("Test 4 passed: " + test4());
        System.out.println("Test 5 passed: " + test5());
    }
}
