package player;

import scotlandyard.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by ahmerb on 28/04/16.
 */
public final class ScotlandYardState implements Cloneable {
    public final ScotlandYardView view;
    //private final Map<Colour, PlayerData> playersMap;
    private final ScotlandYardGraph graph;
    private final List<Colour> players;

    public ScotlandYardState(ScotlandYardView view, ScotlandYardGraph graph) {
        this.view = view;
        this.graph = graph;
        this.players = view.getPlayers();

        // TODO initialise playersMap

    }

    // TODO: Possible FIX: don't use playerData's? To get location, use view.getPlayerLocation(Colour player)?
    /**
     * Returns the list of valid moves for a given player.
     *
     * @param player the player whose moves we want to see.
     * @return the list of valid moves for a given player.
     */
    public List<Move> validMoves(Colour player) {
        int location = playersMap.get(player).getLocation();
        List<Move> moves = graph.generateMoves(player, location);
        List<Move> validMoves = new ArrayList<>();
        if (player != Colour.Black) {
            for (Move move : moves) {
                int dest = ((MoveTicket) move).target;
                boolean occupied = false;
                for (PlayerData internalPlayer : players.subList(1, players.size())) {
                    if (internalPlayer.getLocation() == dest)
                        occupied = true;
                }
                if (!occupied && playersMap.get(player).hasTickets(move))
                    validMoves.add(move);
            }
            if (validMoves.isEmpty())
                validMoves.add(MovePass.instance(player));
        } else {
            for (Move move : moves) {
                if (move instanceof MoveTicket) {
                    int dest = ((MoveTicket) move).target;
                    boolean occupied = false;
                    for (PlayerData internalPlayer : players) {
                        if (internalPlayer.getLocation() == dest)
                            occupied = true;
                    }
                    if (!occupied && playersMap.get(player).hasTickets(move))
                        validMoves.add(move);
                } else if ((move instanceof MoveDouble) && playersMap.get(player).hasTickets(move)) {
                    int dest1 = ((MoveDouble) move).move1.target;
                    int dest2 = ((MoveDouble) move).move2.target;
                    boolean occupied1 = false;
                    boolean occupied2 = false;
                    for (PlayerData internalPlayer : players) {
                        if (internalPlayer.getLocation() == dest1)
                            occupied1 = true;
                        if (internalPlayer.getLocation() == dest2 && dest2 != location)
                            occupied2 = true;
                    }
                    if (playersMap.get(player).hasTickets(move) && !occupied1 && !occupied2)
                        validMoves.add(move);
                }
            }
        }
        return validMoves;
    }

    // TODO: Implement playMove
    public void playMove(Move move) {

    }


    // TODO maybe use a copy constructor instead of using clone?
    @Override
    public Object clone() throws CloneNotSupportedException{
        // run super.clone()
        ScotlandYardState copy = (ScotlandYardState) super.clone();

        // TODO copy everything else

        // all done
        return copy;
    }

}
