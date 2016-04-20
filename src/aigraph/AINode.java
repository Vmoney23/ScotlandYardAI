package aigraph;

import graph.*;
import scotlandyard.*;

/**
 * Created by ahmerb on 20/04/16.
 */
public class AINode<X extends ScotlandYardView> extends Node<X> {

    private Integer score;

    public AINode(X gameState, Integer score) {
        super(gameState);
        this.score = score;
    }

    public X getGameState() {
        return super.getIndex();
    }

    public void setGameState(X gameState) {
        super.setIndex(gameState);
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    /**
     * Returns a string representation of the score of the game state associated
     * with this node.
     *
     * @return a representation of the score of the game state associated with
     *         this node as a String.
     */
    @Override
    public String toString() {
        return score.toString();
    }
}
