package aigraph;

import graph.*;
import scotlandyard.*;

import java.util.*;

/**
 *
 */
public class ScotlandYardGameTree extends UndirectedGraph<ScotlandYardView, Move> {

    private AINode<ScotlandYardView> head;

    /**
     * Creates a game tree with just the parent node.
     *
     * @param currentGameState ScotlandYardView implementing object to be root
     *                         node of ScotlandYardGameTree.
     */
    public ScotlandYardGameTree(ScotlandYardView currentGameState) {
        super();
        this.head = new AINode<>(currentGameState, 0);
        this.add(head);
    }

    public List<AINode<ScotlandYardView>> getFinalStatesList() {
        return new ArrayList<AINode<ScotlandYardView>>(Collections.singletonList(getHead()));
    }

    public List<Integer> getFinalScoresList() {
        return null;
    }

    public AINode<ScotlandYardView> getHead() {
        return head;
    }
}