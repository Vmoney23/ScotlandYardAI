package aigraph;

import graph.DirectedGraph;
import graph.Edge;
import scotlandyard.Move;
import scotlandyard.ScotlandYard;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ScotlandYardGameTree extends DirectedGraph<ScotlandYard, Move> {

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
        head.setScore(MiniMax(head, depth, max));
    }

    // TODO Implement the minimax algorithm
    private Double MiniMax(AINode node, int depth, boolean max) {
        // base case
        if (depth == 0)
            return node.getScore();

        // store the score for the best move
        Double bestScore;

        // this player is maximising MrX score
        if (max) {
            bestScore = Double.NEGATIVE_INFINITY;

            for (AINode child : this.getChildren(node)) {
                Double v = MiniMax(child, depth - 1, false);
                bestScore = bestScore > v ? bestScore : v;
            }
        }

        // or player is minimising MrX score
        else {
            bestScore = Double.POSITIVE_INFINITY;

            for (AINode child : this.getChildren(node)) {
                Double v = MiniMax(child, depth - 1, true);
                bestScore = bestScore < v ? bestScore : v;
            }
        }

        // return the best score
        return bestScore;
    }

    /**
     * Returns children of node in a tree as a List.
     * TODO downcast to AINode is unchecked.
     *
     * @param node the node to get the children of.
     * @return a list with the children of node.
     */
    private List<AINode> getChildren(AINode node) {
        List<AINode> children = new ArrayList<>();
        for (Edge<ScotlandYard, Move> e : this.getEdgesFrom(node)) {
            children.add((AINode) e.getTarget());
        }
        return children;
    }
}