package graph;

/**
 * A class which represents a node in a graph.
 */

public class Node<X> implements Comparable<Node<X>> {

    private X index;
    private Double weight;
	//private int degree;

    /**
     * Constructs a new node with the specified index.
     *
     * @param index the index of the node in the graph.
     */
    public Node(X index) {
        this.index = index;
        this.weight = 0.0;
		//this.degree = 0;
    }
    
    public Node(X index, Double weight) {
        this.index = index;
        this.weight = weight;
		//this.degree = 0;
    }

    /**
     * Sets the index of the node in the graph.
     *
     * @param index the new index of the node in the graph.
     */
    public void setIndex(X index) {
        this.index = index;
    }

    /**
     * Returns the index of this node.
     *
     * @return the index of this node.
     */
    public X getIndex() {
        return index;
    }

    /**
     * Returns a representation of this node as a string.
     *
     * @return a representation of this node as a string.
     */
    public String toString() {
        return index.toString();
    }
    
    /**
     * Returns the weight of this node.
     *
     * @return the weight of this node.
     */
    public Double getWeight() {
        return weight;
    }  

    /**
     * Sets the weight of the node in the graph.
     *
     * @param weight
     */
    public void setWeight(Double weight) {
        this.weight = weight;
    }

	//public int getDegree() {
    //    return ;
    //}

    //void incrDegree() {
        //degree++;
    //}
    
    /**
     * Implements Comparable interface
     *
     * @return result of comparing two nodes by weight
     */
    public int compareTo(Node<X> weightedNode) {
    	return Double.compare(this.weight, weightedNode.getWeight());  
    }

}
