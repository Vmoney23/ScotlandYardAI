package player;

import scotlandyard.*;

import java.util.*;

/**
 * A class to store the state of a Scotland Yard game at some fixed point during
 * the game. An object of this type can be cloned.
 */
// TODO TEST VALID MOVES
// TODO if playMove returns the cloned object, fields can be made final as the updated fields can be calculated before creating new instance of object.
public final class ScotlandYardState {

    public final ScotlandYardGraph graph;
    private final List<Colour> players;
    // which of these are these needed?
    private Set<Colour> winningPlayers;
    private Map<Colour, Integer> playerLocations;
    private Map<Colour, Map<Ticket, Integer>> playerTickets;
    private boolean gameOver;
    private boolean ready;
    private Colour currentPlayer;
    private int round;
    private List<Boolean> rounds;


    private ScotlandYardState(ScotlandYardGraph graph, List<Colour> players, Map<Colour, Integer> playerLocations, Map<Colour, Map<Ticket, Integer>> playerTickets, Set<Colour> winningPlayers, boolean gameOver, boolean ready, Colour currentPlayer, int round, List<Boolean> rounds) {
        this.graph = graph;
        this.players = players;
        this.winningPlayers = new HashSet<>(winningPlayers);
        this.playerLocations = new HashMap<>(playerLocations);
        this.playerTickets = new HashMap<>(playerTickets);
        this.gameOver = gameOver;
        this.ready = ready;
        this.currentPlayer = currentPlayer;
        this.round = round;
        this.rounds = new ArrayList<>(rounds);
    }


    public ScotlandYardState(ScotlandYardView view, Integer
            location, ScotlandYardGraph graph) {
        this.graph = graph;
        this.players = view.getPlayers();

        this.winningPlayers = view.getWinningPlayers(); // NEW

        this.currentPlayer = view.getCurrentPlayer();

        this.playerLocations = new HashMap<>();
        for (Colour player : players) {
            if (player == currentPlayer)
                playerLocations.put(player, location);
            else
                playerLocations.put(player, view.getPlayerLocation(player));
        }

        this.playerTickets = new HashMap<>();
        for (Colour player : players) {
            HashMap<Ticket, Integer> thisPlayersTicketMap = new HashMap<>();
            for (Ticket ticket : Ticket.values()) {
                thisPlayersTicketMap.put(ticket, view.getPlayerTickets(player, ticket));
            }
            playerTickets.put(player, thisPlayersTicketMap);
        }

        this.gameOver = view.isGameOver();
        this.ready = view.isReady();
        this.round = view.getRound();
        this.rounds = view.getRounds();
    }


    /**
     * Returns a deep copy of this ScotlandYardState.
     *
     * @return a deep copy of this ScotlandYardState.
     */
    public ScotlandYardState copy() {
        return new ScotlandYardState(graph,
                                     players,
                                     playerLocations,
                                     playerTickets,
                                     winningPlayers,
                                     gameOver,
                                     ready,
                                     currentPlayer,
                                     round,
                                     rounds);
    }


    /**
     * Evaluates whether the next player to play is MrX.
     *
     * @return true is next player is MrX.
     */
    public boolean isNextPlayerMrX() {
        int currentIx = players.lastIndexOf(currentPlayer) + 1;
        int playersSize = players.size();
        return players.get(currentIx + 1 % playersSize).equals(Colour.Black);
    }

    /**
     * Returns the list of valid moves for a given player.
     *
     * @param player the player whose moves we want to see.
     * @return the list of valid moves for a given player.
     */
    public List<Move> validMoves(Colour player) {
        int location = playerLocations.get(player);
        List<Move> moves = graph.generateMoves(player, location);
        List<Move> validMoves = new ArrayList<>();
		Map<Ticket, Integer> thisPlayersTicketMap = new HashMap<>();
		thisPlayersTicketMap = playerTickets.get(player);
        if (player != Colour.Black) {
            for (Move move : moves) {
                int dest = ((MoveTicket) move).target;
                boolean occupied = false;
                for (Colour internalPlayer : players.subList(1, players.size())) {
                    if (playerLocations.get(internalPlayer) == dest)
                        occupied = true;
                }
                if (!occupied && thisPlayersTicketMap.get(((MoveTicket) move).ticket) > 0)
                    validMoves.add(move);
            }
            if (validMoves.isEmpty())
                validMoves.add(MovePass.instance(player));
        } else {
            for (Move move : moves) {
                if (move instanceof MoveTicket) {
                    int dest = ((MoveTicket) move).target;
                    boolean occupied = false;
                    for (Colour internalPlayer : players) {
                        if (playerLocations.get(internalPlayer) == dest)
                            occupied = true;
                    }
                    if (!occupied && thisPlayersTicketMap.get(((MoveTicket) move).ticket) > 0)
                        validMoves.add(move);
                } else if ((move instanceof MoveDouble) && thisPlayersTicketMap.get(Ticket.Double) > 0) {
                    int dest1 = ((MoveDouble) move).move1.target;
                    int dest2 = ((MoveDouble) move).move2.target;
                    boolean occupied1 = false;
                    boolean occupied2 = false;
                    for (Colour internalPlayer : players) {
                        if (playerLocations.get(internalPlayer) == dest1)
                            occupied1 = true;
                        if (playerLocations.get(internalPlayer) == dest2)
                            occupied2 = true;
                    }
                    if (((thisPlayersTicketMap.get(((MoveDouble) move).move1.ticket)) != (thisPlayersTicketMap.get(((MoveDouble) move).move2.ticket))) &&(thisPlayersTicketMap.get(((MoveDouble) move).move1.ticket) > 0) && (thisPlayersTicketMap.get(((MoveDouble) move).move2.ticket) > 0) && !occupied1 && !occupied2)
                        validMoves.add(move);
					else if (((thisPlayersTicketMap.get(((MoveDouble) move).move1.ticket)) == (thisPlayersTicketMap.get(((MoveDouble) move).move2.ticket))) &&(thisPlayersTicketMap.get(((MoveDouble) move).move1.ticket) > 1) && !occupied1 && !occupied2)
						validMoves.add(move);
                }
            }
        }
        return validMoves;
    }


    /**
     * Plays a move sent from a player.
     *
     * @param move the move chosen by the player.
     */
    public void playMove(Move move) {
        play(move);
        nextPlayer();
    }


    // playMove helpers
    // TODO notify spectators?

    /**
     * Passes priority onto the next player whose turn it is to play.
     */
    protected void nextPlayer() {
        int nextPlayerIndex = (players.indexOf(currentPlayer) + 1) % players.size();
        currentPlayer = players.get(nextPlayerIndex);
    }

    /**
     * Allows the game to play a given move.
     *
     * @param move the move that is to be played.
     */
    protected void play(Move move) {
        if (move instanceof MoveTicket)
            play((MoveTicket) move);
        else if (move instanceof MoveDouble)
            play((MoveDouble) move);
        else if (move instanceof MovePass)
            play((MovePass) move);
    }

    /**
     * Plays a MoveTicket.
     *
     * @param move the MoveTicket to play.
     */
    private void play(MoveTicket move) {
        giveTicketsToMrX(move);
        updatePlayerTickets(move);
        updatePlayerLocation(move);
        updateRoundIfBlack();
        //notifySpectators();
    }

    /**
     * Plays a MoveDouble.
     *
     * @param move the MoveDouble to play.
     */
    private void play(MoveDouble move) {
        updatePlayerTickets(move);
        play(move.move1);
        play(move.move2);
    }

    /**
     * Plays a MovePass.
     *
     * @param move the MovePass to play.
     */
    private void play(MovePass move) {
        //notifySpectators(move);
    }

    /**
     * Increments current round if it is mrX's turn
     */
    private void updateRoundIfBlack() {
        if (currentPlayer.equals(Colour.Black))
            round++;
    }

    /**
     * Gives the ticket(s) a detective uses when playing a move to mrX
     *
     * @param move the move to determine what tickets to give to mrX
     */
    private void giveTicketsToMrX(Move move) {
        // if current player detective, update corresponding tickets in
        // playersMap used in for tickets used in move for mrX
        if (move.colour != Colour.Black) {
            if (move instanceof MoveTicket)
                playerTickets.get(Colour.Black).computeIfPresent(((MoveTicket) move).ticket, (k, v) -> v + 1);
        }
    }

    /**
     * Removes the corresponding tickets for a player when a move is played.
     *
     * @param move the move that specifies the tickets to remove
     */
    private void updatePlayerTickets(Move move) {
        // decrement player's tickets in playerTickets map
        if (move instanceof MoveTicket) {
            playerTickets.get(move.colour).computeIfPresent(((MoveTicket) move).ticket, (k, v) -> v - 1);
        }
        else if (move instanceof MoveDouble) {
            playerTickets.get(move.colour).computeIfPresent(Ticket.Double, (k, v) -> v - 1);
            // tickets for specific moves in double move are removed when
            // play(MoveTicket move) is called in play(MoveDouble move)
        }
    }

    /**
     * Updates a players location when a move is played.
     *
     * @param move the move that specifies the location to move to
     */
    private void updatePlayerLocation(MoveTicket move) {
        playerLocations.put(move.colour, move.target);
    }


    // GETTERS
    //

    public Map<Colour, Integer> getPlayerLocations() {
        return playerLocations;
    }

    public Map<Colour, Map<Ticket, Integer>> getPlayerTickets() {
        return playerTickets;
    }

    public Set<Colour> getWinningPlayers() {
        return winningPlayers;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isReady() {
        return ready;
    }

    public Colour getCurrentPlayer() {
        return currentPlayer;
    }

    public int getRound() {
        return round;
    }

    public List<Boolean> getRounds() {
        return rounds;
    }

    public ScotlandYardGraph getGraph() {
        return graph;
    }

    public List<Colour> getPlayers() {
        return players;
    }

}
