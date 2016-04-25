package aigraph;

import graph.Node;
import scotlandyard.ScotlandYardView;

/**
 * This class extends Node<ScotlandYardView>. Each AINode contains two pieces
 * of data: a ScotlandYardView gameState, and an associated score: a double
 * score.
 */
public class AINode extends Node<ScotlandYardView> {

    private Double score;

    public AINode(ScotlandYardView gameState, Double score) {
        super(gameState);
        this.score = score;
    }

    public ScotlandYardView getGameState() {
        return super.getIndex();
    }

    public void setGameState(ScotlandYardView gameState) {
        super.setIndex(gameState);
    }

    public double getScore() {
        return score;
    }

    public void setScore(Double score) {
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
