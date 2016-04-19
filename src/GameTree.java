package player;

import graph;

/**
 *
 * @param <ScotlandYardView>
 * @param <Move>
 */
class GameTree<ScotlandYardView, Move> extends DirectedGraph {

    /**
     *
     * @param currentGameState
     */
    public GameTree(ScotlandYardView currentGameState) {
        super();
        this.add(currentGameState);
    }


}