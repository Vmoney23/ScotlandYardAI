package player;

import org.junit.Test;
import scotlandyard.*;

import java.util.Collections;
import java.util.LinkedHashMap;

import static org.junit.Assert.*;

/**
 * Created by ahmerb on 24/04/16.
 */
public class MiniMaxPlayerTest {
    /**
     * Tests the lambda function used to return key associated with highest
     * value from a map.
     * @throws Exception if test fails
     */
    @Test
    public void getKeyWithHighestValueTest() throws Exception {
        // Fill map with moves
        LinkedHashMap<Colour, Integer> map = new LinkedHashMap<>();
        map.put(Colour.Black, 55);
        map.put(Colour.Blue, -3);
        map.put(Colour.Green, -13);
        map.put(Colour.Red, 109);

        // Get key associated with highest value
        Colour bestColour = Collections.max(map.entrySet(), (entry1, entry2) -> (entry1.getValue() > entry2.getValue()) ? 1 : -1).getKey();

        // Assert we got the highest key
        assertEquals(bestColour, Colour.Red);
    }

    @Test
    public void scoreCalculatesMrXMoveToAwayFromDetectivesAsBetter() throws Exception {
        // instantiate a game with MrX as MiniMax player
        ScotlandYard game = TestHelper.getStoppedGame(1, "graph.txt");
        MiniMaxPlayer mrx = new TestHelper.MrXMiniMaxTestPlayer(game, "graph.txt");
        TestHelper.addMrxToGame(game, mrx, 1);

        // TODO create a weaker and a stronger move.
        MoveTicket moveStrong = null;
        MoveTicket moveWeak = null;

        // get score for moves.
        Double moveStrongScore = mrx.scoreMoveTicket(moveStrong);
        Double moveWeakScore = mrx.scoreMoveTicket(moveWeak);

        assertTrue(moveStrongScore > moveWeakScore);
    }

}