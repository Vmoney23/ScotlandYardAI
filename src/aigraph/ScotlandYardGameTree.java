package aigraph;

import graph.*;
import scotlandyard.*;

import java.util.List;

/**
 *
 * @param <X>
 * @param <Y>
 */
public class ScotlandYardGameTree<X extends ScotlandYardView, Y extends Move> extends UndirectedGraph {

    /**
     * Creates a game tree with just the parent node.
     *
     * @param currentGameState ScotlandYardView implementing object to be root
     *                         node of ScotlandYardGameTree.
     */
    public ScotlandYardGameTree(ScotlandYardView currentGameState) {
        super();
        this.add(new AINode<>(currentGameState, 0));
    }

    public List<X> getFinalStatesList() {
        return null;
    }

    public List<Integer> getFinalScoresList() {
        return null;
    }
}