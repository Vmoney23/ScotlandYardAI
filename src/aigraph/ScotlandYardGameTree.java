package aigraph;

import graph.UndirectedGraph;
import scotlandyard.Move;
import scotlandyard.ScotlandYard;

import java.util.ArrayList;
import java.util.Collections;
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

    public List<AINode> getFinalStatesList() {
        return new ArrayList<AINode>(Collections.singletonList(getHead()));
    }

    public List<Integer> getFinalScoresList() {
        return null;
    }

    public AINode getHead() {
        return head;
    }
}