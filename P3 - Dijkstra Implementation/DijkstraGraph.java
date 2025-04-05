// --== CS400 File Header Information ==--
// Name: Sai Gungurthi
// Email: sgungurthi@wisc.edu
// Group and Team: AK Blue
// Group TA: Florian Heimerl
// Lecturer: Gary Dahl
// Notes to Grader: Referenced Florian's pseudocode from lecture

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * This class extends the BaseGraph data structure with additional methods for
 * computing the total cost and list of node data along the shortest path
 * connecting a provided starting to ending nodes. This class makes use of
 * Dijkstra's shortest path algorithm.
 */
public class DijkstraGraph<NodeType, EdgeType extends Number>
    extends BaseGraph<NodeType,EdgeType>
    implements GraphADT<NodeType, EdgeType> {

    /**
     * While searching for the shortest path between two nodes, a SearchNode
     * contains data about one specific path between the start node and another
     * node in the graph. The final node in this path is stored in its node
     * field. The total cost of this path is stored in its cost field. And the
     * predecessor SearchNode within this path is referenced by the predecessor
     * field (this field is null within the SearchNode containing the starting
     * node in its node field).
     *
     * SearchNodes are Comparable and are sorted by cost so that the lowest cost
     * SearchNode has the highest priority within a java.util.PriorityQueue.
     */
    protected class SearchNode implements Comparable<SearchNode> {
        public Node node;
        public double cost;
        public SearchNode predecessor;
        public SearchNode(Node node, double cost, SearchNode predecessor) {
            this.node = node;
            this.cost = cost;
            this.predecessor = predecessor;
        }
        public int compareTo(SearchNode other) {
            if( cost > other.cost ) return +1;
            if( cost < other.cost ) return -1;
            return 0;
        }
    }

    /**
     * This helper method creates a network of SearchNodes while computing the
     * shortest path between the provided start and end locations. The
     * SearchNode that is returned by this method represents the end of the
     * shortest path that is found: its cost is the cost of that shortest path,
     * and the nodes linked together through predecessor references represent
     * all of the nodes along that shortest path (ordered from end to start).
     *
     * @param start the data item in the starting node for the path
     * @param end the data item in the destination node for the path
     * @return SearchNode for the final end node within the shortest path
     * @throws NoSuchElementException when no path from start to end is found
     *         or when either start or end data do not correspond to a graph node
     */
    protected SearchNode computeShortestPath(NodeType start, NodeType end) throws NoSuchElementException {
        // TODO: implement in step 6
        if (!(containsNode(start) && containsNode(end))) {
            throw new NoSuchElementException("Either the start or end data do not correspond to a graph node.");
        }

        // keeps track of visited nodes
        Hashtable<Node, SearchNode> ht = new Hashtable<>();
        // keeps track of unexplored paths by cost
        PriorityQueue<SearchNode> pq = new PriorityQueue<>();

        insertNode(start);
        // cost is 0 and predecessor is null because it is the starting node
        SearchNode startNode = new SearchNode(nodes.get(start), 0, null);
        pq.add(startNode);

        while (!pq.isEmpty()) {
            SearchNode currentSNode = pq.remove();
            // essential if statement; returns SearchNode with the shortest path
            if (currentSNode.node.equals(nodes.get(end))) {
                return currentSNode;
            }
            // aka if the current node is unvisited
            if (!ht.containsKey(currentSNode.node)) {
                ht.put(currentSNode.node, currentSNode); // setting current node as visited
                if (currentSNode.node.edgesLeaving != null) {
                    // iterating through every leaving edge of the current node and creating new SearchNodes
                    // to add to the PriorityQueue
                    for (int i = 0; i < currentSNode.node.edgesLeaving.size(); i++) {
                        if (currentSNode.node.edgesLeaving.get(i) != null) {
                            Edge currentEdge = currentSNode.node.edgesLeaving.get(i);
                            if (currentEdge.successor != null) {
                                // important to calculate the total cost to get to this next node from the start node
                                double totalCost = currentSNode.cost + currentEdge.data.doubleValue();
                                SearchNode newSNode = new SearchNode(currentEdge.successor, totalCost, currentSNode);
                                pq.add(newSNode);
                            }
                        }
                    }
                }
            }
        }
        // if the while loop does not return anything
        // this exception is implicitly thrown in the next two methods as well
        throw new NoSuchElementException("No path from start to end is found.");
    }

    /**
     * Returns the list of data values from nodes along the shortest path
     * from the node with the provided start value through the node with the
     * provided end value. This list of data values starts with the start
     * value, ends with the end value, and contains intermediary values in the
     * order they are encountered while traversing this shortest path. This
     * method uses Dijkstra's shortest path algorithm to find this solution.
     *
     * @param start the data item in the starting node for the path
     * @param end the data item in the destination node for the path
     * @return list of data item from node along this shortest path
     */
    public List<NodeType> shortestPathData(NodeType start, NodeType end) {
        // TODO: implement in step 7
        SearchNode shortestPath = computeShortestPath(start, end);
        List<NodeType> shortestPathData = new ArrayList<>();
        SearchNode temp = shortestPath;
        // keeps going backwards in the path until the start node's data is added to the list
        while (temp != null) {
            shortestPathData.add(temp.node.data);
            temp = temp.predecessor;
        }
        // need to reverse the list so that the list starts with the start value and ends with the end value
        // as specified in the method header description
        Collections.reverse(shortestPathData);
        return shortestPathData;
    }

    /**
     * Returns the cost of the path (sum over edge weights) of the shortest
     * path from the node containing the start data to the node containing the
     * end data. This method uses Dijkstra's shortest path algorithm to find
     * this solution.
     *
     * @param start the data item in the starting node for the path
     * @param end the data item in the destination node for the path
     * @return the cost of the shortest path between these nodes
     */
    public double shortestPathCost(NodeType start, NodeType end) {
        // TODO: implement in step 7
        // simple implementation because the cost field of the SearchNode class accounts for the cost of the entire path
        SearchNode shortestPath = computeShortestPath(start, end);
        return shortestPath.cost;
    }

    /**
     * Creates the graph example from Gary's lecture by inserting nodes and the edges between them. Makes the following
     * test methods much more concise.
     *
     * @return lectureExample graph example from Gary's lecture
     */
    private DijkstraGraph<Character, Integer> testHelper() {
        DijkstraGraph<Character, Integer> lectureExample = new DijkstraGraph<>();

        lectureExample.insertNode('D');
        lectureExample.insertNode('A');
        lectureExample.insertEdge('D', 'A', 7);

        lectureExample.insertNode('G');
        lectureExample.insertEdge('D', 'G', 2);

        lectureExample.insertNode('L');
        lectureExample.insertEdge('G', 'L', 7);

        lectureExample.insertNode('H');
        lectureExample.insertEdge('A', 'H', 8);

        lectureExample.insertNode('B');
        lectureExample.insertEdge('A', 'B', 1);

        lectureExample.insertNode('M');
        lectureExample.insertEdge('A', 'M', 5);

        lectureExample.insertNode('I');
        lectureExample.insertEdge('H', 'I', 2);
        lectureExample.insertEdge('I', 'H', 2);
        lectureExample.insertEdge('I', 'D', 1);
        lectureExample.insertEdge('I', 'L', 5);

        lectureExample.insertEdge('H', 'B', 6);
        lectureExample.insertEdge('B', 'M', 3);

        lectureExample.insertNode('E');
        lectureExample.insertEdge('M', 'E', 3);

        lectureExample.insertNode('F');
        lectureExample.insertEdge('M', 'F', 4);
        lectureExample.insertEdge('F', 'G', 9);

        return lectureExample;
    }

    // TODO: implement 3+ tests in step 8.
    /**
     * Makes use of an example that we traced through in lecture, and confirms that the results of my implementation
     * match what I previously computed by hand.
     */
    @Test
    public void test1() {
        DijkstraGraph<Character, Integer> lectureExample = testHelper();
        lectureExample.computeShortestPath('D', 'I');
        List<Character> shortestPathData = lectureExample.shortestPathData('D', 'I');
        double cost = lectureExample.shortestPathCost('D', 'I');
        // decided to compare expected and actual paths by making their data strings
        String string = shortestPathData.toString();
        String expected = "[D, A, H, I]";
        assertEquals(expected, string);
        assertEquals(17, cost);
    }

    /**
     * Using the same graph as the test above, checks the cost and sequence of data along the shortest path between
     * a different start and end node.
     */
    @Test
    public void test2() {
        DijkstraGraph<Character, Integer> lectureExample = testHelper();
        // different start and end node
        lectureExample.computeShortestPath('I', 'E');
        List<Character> shortestPathData = lectureExample.shortestPathData('I', 'E');
        double cost = lectureExample.shortestPathCost('I', 'E');
        String string = shortestPathData.toString();
        String expected = "[I, H, B, M, E]";
        assertEquals(expected, string);
        assertEquals(14, cost);
    }

    /**
     * Checks the behavior of my implementation when the node that I am searching for a path between exists in the
     * graph, but there is no sequence of directed edges that connects them from the start to the end.
     */
    @Test
    public void test3() {
        DijkstraGraph<Character, Integer> lectureExample = testHelper();
        // expecting a NoSuchElementException to be thrown based on the start and end nodes I selected
        // for assertThrows of JUnit5, the second argument must be an "Executable" according to the API
        assertThrows(NoSuchElementException.class, ()->{lectureExample.computeShortestPath('L', 'M');});
    }
}
