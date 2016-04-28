package aigraph;

import graph.Edge;
import graph.Node;
import scotlandyard.Move;
import scotlandyard.ScotlandYard;

/**
 * Created by ahmer on 26/04/16.
 */
public class AIEdge extends Edge<ScotlandYard, Move> {
    public AIEdge(Node<ScotlandYard> source, Node<ScotlandYard> target, Move data) {
        super(source, target, data);
    }
}
