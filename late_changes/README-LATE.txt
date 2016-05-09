LATE CHANGES
=================

Fix Detectives AI
------------------
We were previously attempting to find MrX's location before he revealed himself.
Now, before MrX reveals himself, detective AI player just chooses a random move.
This avoids a null pointer in multiple places we
try to find information about MrX.

This code was added to notify() method in MiniMaxPlayer.java:

`
// if a detective, and mrX never revealed himself, choose a random move  
if (colour != Colour.Black && view.getPlayerLocation(Colour.Black) == 0) {  
    // choose random move  
    System.out.println("detective played default move");  
    aiMove = moves.get(0);  
}  
else {  
    // get ai move  
    aiMove = getAIMove();  
}  
`


Fix MiniMax
--------------

## The bug
In presentation with Tilo, identified problem in MiniMax and alpha-beta pruning.
We were actually using an adapted MiniMax algorithm, by calculating the score
at every node, not just the leaf nodes. As we also iterated through the child
nodes in order of their scores, we would only look at one child node, then
prune.

## Fix
We have since updated the MiniMax method to calculate MiniMax correctly.
It now returns a Pair<Double, Move>, and only calculates scores for leaf nodes.
(We created a generic Pair class with getters, setters and toString in Pair/).
By printing depth at every recurse, we see the whole tree is explored properly.
Here is the updated code:

`
/**
 * Implements the MiniMax algorithm with alpha-beta pruning.
 *
 * @param node the AINode from which to create a tree from. node will be
 *             head of the (sub)tree.
 * @param depth the number of lays to generate for the tree.
 * @param alpha Best already explored score along path to the root for
 *              maximiser. Should be set to initial value
 *              <p>Double.NEGATIVE_INFINITY</p>
 * @param beta Best already explored option along path to the root for
 *             minimiser. Should be set to initial value
 *             <p>Double.POSITIVE_INFINITY</p>
 * @param max If true, the player who's turn it is in
 *            {@code node.getGameState()}
 *            should be a maximising player, else minimising player.
 * @return the best possible score that scoreMoveTicket or
 *         scoreMoveDouble assigns that
 *         {@code node.getGameState().getCurrentPlayer()}
 *         can get, based on a tree of {@code depth} depth.
 */
private Pair<Double, Move> MiniMax(AINode node, int depth, Double alpha, Double beta, boolean max) {
    // store the valid moves for current player at current game state
    List<Move> nodeValidMoves = node.getGameState().validMoves(node.getGameState().getCurrentPlayer());

    System.out.println("depth = " + depth);

    // base cases - 1) depth is zero. 2) node is leaf (no valid moves).
    //              3) game is over in current state
    if (depth == 0 || nodeValidMoves.size() == 0 || node.getGameState().isGameOver()) {
        if (depth == 0) System.out.println("BASE CASE - depth == 0");
        if (node.getGameState().isGameOver()) System.out.println("BASE CASE - game over");
        if (nodeValidMoves.size() == 0) System.out.println("BASE CASE - no valid moves");

        calculateScore(node);
        return new Pair<>(node.getScore(), null);
    }

    // store the current bestPair, initialising it's score also
    double bestScore = max ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
    Pair<Double, Move> bestPair = new Pair<>(bestScore, null);

    // store the pair to be returned from each child
    Pair<Double, Move> currentPair;

    for (Move currentMove : nodeValidMoves) {

        // make a copy of currentGameState for the next move
        ScotlandYardState stateAfterMove = node.getGameState().copy();
        // play move on the copy
        stateAfterMove.playMove(currentMove);

        // create child node for this game state and link to tree.
        // Give unassigned score = -1.0
        AINode child = new AINode(stateAfterMove, -1.0);
        gameTree.add(child);
        Edge<ScotlandYardState, Move> edgeToChild = new Edge<>(node, child, currentMove);
        gameTree.add(edgeToChild);


        // get the MiniMax pair for current node
        boolean isMax = Objects.equals(child.getGameState().getCurrentPlayer(), Colour.Black);
        currentPair = MiniMax(child, depth-1, alpha, beta, isMax);

        // if the current pair is better, assign the bestPair it's score,
        // and make the currentMove the best move.
        if (max) {
            if (currentPair.getLeft() > bestPair.getLeft()) {
                bestPair = currentPair; // update bestPair score
                bestPair.setRight(currentMove);
            }
            // update alpha and prune, if possible
            alpha = Math.max(alpha, bestPair.getLeft());
            if (beta <= alpha) {
                System.out.println("PRUNED FOR MAXIMISER");
                break;
            }
        }
        else {//min
            if (currentPair.getLeft() < bestPair.getLeft()) {
                bestPair = currentPair; // update bestPair score
                bestPair.setRight(currentMove);
            }
            // update beta and prune, if possible
            beta = Math.min(beta, bestPair.getLeft());
            if (beta <= alpha) {
                System.out.println("PRUNED FOR MINIMISER");
                break;
            }
        }
    }



    return bestPair;
}
`

## Possible bug
Upon raising the depth of search, we noticed that the method was still returning
a move in exceptionally fast time. By printing the base case whenever it was
true, we found that much of the tree would not be explored at higher depths,
because validMoves was returning an empty list, so there were no moves.
It is possible that there are no validMoves this deep into the game, but it
is also possible that ScotlandYardState.java has some bug(s) that mean that
validMoves is returning an empty list when it shouldn't.
We have not had time to test to see if there is actually a bug.


Updates to ScotlandYardState class
------------------------------------
validMoves now uses adapted hasTickets function from PlayerData class in cw5.

We also added all the game logic necessary for calculating is a game is over.
This base case was added to the MiniMax method in MiniMaxPlayer.java.


Other Minor Changes
---------------------
Added some small changes such as a few @SuppressWarnings("unchecked")
annotations and updating doc comments.
Also, did other small improvements, like changing some access modifiers (many
were previously set to protected for testing, but are now set to private).


Things we could improve
-------------------------
Improve detective AI - pass AI as spectator to see moves MrX makes, and generate
a tree for MrX to see where MrX would have moved to.
Add queiscence search to avoid horizon effect.
