package player;

import org.junit.Test;
import scotlandyard.Colour;
import scotlandyard.MoveTicket;
import scotlandyard.ScotlandYard;

import java.util.Collections;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by ahmerb on 24/04/16.
 */
public class MiniMaxPlayerTest {
    // TODO remove this test
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
        ScotlandYard game = TestHelper.getStoppedGame(1, "test_resources/graph.txt");
        MiniMaxPlayer mrx = new TestHelper.MrXMiniMaxTestPlayer(game, "graph" +
                ".txt", Colour.Black);
        TestHelper.addMrxToGame(game, mrx, 1);

        // TODO get a weaker and a stronger move.

        // check mrx has enough moves
        if (mrx.moves.size() < 2) throw new Exception("internal error: not enough mrx moves");
        System.err.println(mrx.moves);


        MoveTicket moveStrong = null;
        MoveTicket moveWeak = null;

        // get score for moves.
        Double moveStrongScore = mrx.scoreMoveTicket(moveStrong);
        Double moveWeakScore = mrx.scoreMoveTicket(moveWeak);

        // assert the better move has a better score
        assertTrue(moveStrongScore > moveWeakScore);
    }

}