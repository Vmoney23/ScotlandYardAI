package aigraph;

import graph.DirectedGraph;
import graph.Edge;
import scotlandyard.Move;
import player.ScotlandYardState;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ScotlandYardGameTree extends DirectedGraph<ScotlandYardState, Move> {

    private AINode head;

    /**
     * Creates a game tree with just the parent node.
     *
     * @param currentGameState ScotlandYardView implementing object to be root
     *                         node of ScotlandYardGameTree.
     */
    public ScotlandYardGameTree(ScotlandYardState currentGameState) {
        super();
        this.head = new AINode(currentGameState, 0.0);
        this.add(head);
    }

    // TODO getFirstLevelEdges
    public List<Edge<ScotlandYardState, Move>> getListFirstLevelEdges() {
        return null;
    }

    public List<Double> getFinalScoresList() {
        return null;
    }

    public AINode getHead() {
        return head;
    }

    /**
     * Returns children of node in a tree as a List.
     *
     * @param node the node to get the children of.
     * @return a list with the children of node.
     */
    public List<AINode> getChildren(AINode node) {
        List<AINode> children = new ArrayList<>();
        for (Edge<ScotlandYardState, Move> e : this.getEdgesFrom(node)) {
            children.add((AINode) e.getTarget());
        }
        return children;
    }

}