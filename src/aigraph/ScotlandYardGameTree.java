package aigraph;

import graph.Edge;
import graph.UndirectedGraph;
import scotlandyard.Move;
import scotlandyard.ScotlandYard;

import java.util.List;

/**
 *
 */
public class ScotlandYardGameTree extends UndirectedGraph<ScotlandYard, Move> {

    private AINode head;

    /**
     * Creates a game tree with just the parent node.
     *
     * @param currentGameState ScotlandYardView implementing object to be root
     *                         node of ScotlandYardGameTree.
     */
    public ScotlandYardGameTree(ScotlandYard currentGameState) {
        super();
        this.head = new AINode(currentGameState, 0.0);
        this.add(head);
    }

    // TODO getFirstLevelEdges
    public List<Edge<ScotlandYard, Move>> getListFirstLevelEdges() {
        return null;
    }

    public List<Integer> getFinalScoresList() {
        return null;
    }

    public AINode getHead() {
        return head;
    }

    // TODO generateTree
    public void generateTree(int depth, boolean max) {
        MiniMax(head, depth, max);
    }

    // TODO Implement the minimax algorithm
    private AINode MiniMax(AINode node, int depth, boolean max) {
        return null;
    }
}