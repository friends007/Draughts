package nl.tue.s2id90.group39;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import java.util.Collections;
import java.util.List;
import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import org10x10.dam.game.Move;

/**
 * Implementation of the DraughtsPlayer interface.
 * @author huub
 */
// ToDo: rename this class (and hence this file) to have a distinct name
//       for your player during the tournament
public class AmsterDammer  extends DraughtsPlayer{
    private int bestValue=0;
    int minSearchDepth = 2;
    int maxDepth;
    int depth;
    int with;
    
    /** boolean that indicates that the GUI asked the player to stop thinking. */
    private boolean stopped;

    public AmsterDammer(int maxSearchDepth) {
        super("AmsterDammer.png"); // ToDo: replace with your own icon
        //this.maxSearchDepth = maxSearchDepth;
    }
    
    @Override public Move getMove(DraughtsState s) {
        DraughtsState state = s; //for AIStoppedException
        Move bestMove = null;
        bestValue = 0;
        maxDepth = minSearchDepth;
        depth = 0;
        with = 0;
        DraughtsNode node = new DraughtsNode(s);    // the root of the search tree
        try {
            // compute bestMove and bestValue in a call to alphabeta
            bestValue = alphaBeta(node, MIN_VALUE, MAX_VALUE);
            
            
        } catch (AIStoppedException ex) { 
            System.err.println("time out!!"); 
            s = state; //in case no move was found
        }
        // store the bestMove found uptill now
        // NB this is also done in case of an AIStoppedException in alphaBeta()
        bestMove  = node.getBestMove();
        // print the results for debugging reasons
        System.err.format(
            "%s: depth= %2d, maxdepth= %2d, with= %2d, best move = %5s, value=%d\n", 
            this.getClass().getSimpleName(), depth, maxDepth, with, bestMove, bestValue
        );
        if (bestMove==null) {
            System.err.println("no valid move found!");
            return getRandomValidMove(s);
        } else {
            return bestMove;
        }
    } 

    /** This method's return value is displayed in the AICompetition GUI.
     * 
     * @return the value for the draughts state s as it is computed in a call to getMove(s). 
     */
    @Override public Integer getValue() { 
       return bestValue;
    }

    /** Tries to make alphabeta search stop. Search should be implemented such that it
     * throws an AIStoppedException when boolean stopped is set to true;
    **/
    @Override public void stop() {
       stopped = true; 
    }
    
    /** returns random valid move in state s, or null if no moves exist. */
    Move getRandomValidMove(DraughtsState s) {
        List<Move> moves = s.getMoves();
        Collections.shuffle(moves);
        return moves.isEmpty()? null : moves.get(0);
    }
    
    /** Implementation of alphabeta that automatically chooses the white player
     *  as maximizing player and the black player as minimizing player.
     * @param node contains DraughtsState and has field to which the best move can be assigned.
     * @param alpha
     * @param beta
     * @param depth maximum recursion Depth
     * @return the computed value of this node
     * @throws AIStoppedException
     **/
    int alphaBeta(DraughtsNode node, int alpha, int beta)
            throws AIStoppedException
    {
        if (node.getState().isWhiteToMove()) {
            return alphaBetaMax(node, alpha, beta);
        } else  {
            return alphaBetaMin(node, alpha, beta);
        }
    }
    
    /** Does an alphabeta computation with the given alpha and beta
     * where the player that is to move in node is the minimizing player.
     * 
     * <p>Typical pieces of code used in this method are:
     *     <ul> <li><code>DraughtsState state = node.getState()</code>.</li>
     *          <li><code> state.doMove(move); .... ; state.undoMove(move);</code></li>
     *          <li><code>node.setBestMove(bestMove);</code></li>
     *          <li><code>if(stopped) { stopped=false; throw new AIStoppedException(); }</code></li>
     *     </ul>
     * </p>
     * @param node contains DraughtsState and has field to which the best move can be assigned.
     * @param alpha
     * @param beta
     * @param depth  maximum recursion Depth
     * @return the compute value of this node
     * @throws AIStoppedException thrown whenever the boolean stopped has been set to true.
     */
     int alphaBetaMin(DraughtsNode node, int alpha, int beta)
            throws AIStoppedException {
        if (stopped) { stopped = false; throw new AIStoppedException(); }
        DraughtsState state = node.getState();
        // ToDo: write an alphabeta search to compute bestMove and value
        int imax = state.getMoves().size();
        if(imax == 1){
            node.setBestMove(state.getMoves().get(0));
            return calcValue(state);
        }
        Move bestMove = null;//state.getMoves().get(0);
        while(true){
            int bestValue = 1000;
            for(int i = 0;i<imax;i++){
                depth = 1;
                Move move = state.getMoves().get(i);
                int value = alphaBetaMax(node, alpha, beta, depth, move, bestValue);
                if(value < bestValue){
                    bestValue = value;
                    node.setBestMove(move);
                }
            }
            maxDepth++;
            this.bestValue = bestValue;
            with = 0;
//            return(bestValue);
        }
     }
    
    int alphaBetaMax(DraughtsNode node, int alpha, int beta)
            throws AIStoppedException {
        if (stopped) { stopped = false; throw new AIStoppedException(); }
        DraughtsState state = node.getState();
        // ToDo: write an alphabeta search to compute bestMove and valueint imax = state.getMoves().size();
        int imax = state.getMoves().size();
        Move bestMove = null;//state.getMoves().get(0);
        while(true){
            int bestValue = -1000;
            for(int i = 0;i<imax;i++){
                depth = 1;
                Move move = state.getMoves().get(i);
                int value = alphaBetaMin(node, alpha, beta, depth, move, bestValue);
                if(value > bestValue){
                    bestValue = value;
                    node.setBestMove(move);
                }
            }
            maxDepth++;
            this.bestValue = bestValue;
            with = 0;
//            return(bestValue);
        }
    }
    int alphaBetaMin(DraughtsNode node, int alpha, int beta, int curDepth, Move prevMove, int prevValue)
            throws AIStoppedException {
        if (stopped) { stopped = false; throw new AIStoppedException(); }
        
        DraughtsState s = node.getState();
        s.doMove(prevMove);
        DraughtsState state = s;
        s.undoMove(prevMove);
        int value = calcValue(state);
        curDepth++;
        int imax = state.getMoves().size();
        if(imax == 0 || depth>maxDepth){
            //state.undoMove(prevMove);
            with++;
            return value;
        }
        depth = curDepth;
        
        int bestValue = 1000;
        for(int i = 0;i<imax;i++){
            Move move = state.getMoves().get(i);
            value = alphaBetaMax(node, alpha, beta, curDepth, move, bestValue);
            
            if(value < bestValue){
                bestValue = value;
            }
            if(bestValue < prevValue){
                with++;
                return bestValue;
            }
        }
        //state.undoMove(prevMove);
        return bestValue;
     }
    
    int alphaBetaMax(DraughtsNode node, int alpha, int beta, int curDepth, Move prevMove, int prevValue)
            throws AIStoppedException {
        if (stopped) { stopped = false; throw new AIStoppedException(); }
        
        DraughtsState s = node.getState();
        s.doMove(prevMove);
        DraughtsState state = s;
        s.undoMove(prevMove);
        int value = calcValue(state);
        int imax = state.getMoves().size();
        curDepth++;
        if(imax == 0 || depth>maxDepth){
            //state.undoMove(prevMove);
            with++;
            return value;
        }
        depth = curDepth;
        
        int bestValue = -1000;
        for(int i = 0;i<imax;i++){
            Move move = state.getMoves().get(i);
            value = alphaBetaMin(node, alpha, beta, curDepth, move, bestValue);
            
            if(value > bestValue){
                bestValue = value;
            }
            if(bestValue > prevValue){
                with++;
                return bestValue;
            }
        }
        //state.undoMove(prevMove);
        return bestValue;
    }
    
    int calcValue(DraughtsState state){
        int value = 0;
        int king = 3; //value king
        int norm = 1; //value normal piece
        boolean noWhite = true;
        boolean noBlack = true;
        for(int i = 1;i != 51;i++){//cycle through the possibilities
            int piece = state.getPiece(i);
            switch (piece) {
                case DraughtsState.BLACKKING:
                    value -= king;
                    noBlack = false;
                    break;
                case DraughtsState.BLACKPIECE:
                    value -= norm;
                    noBlack = false;
                    break;
                case DraughtsState.WHITEKING:
                    value += king;
                    noWhite = false;
                    break;
                case DraughtsState.WHITEPIECE:
                    value += norm;
                    noWhite = false;
                    break;
                default:
                    break;
            }
        }
        if(noWhite)
            return -10000;
        else if(noBlack)
            return 10000;
        else
            return value;
    }
    
    /** A method that evaluates the given state. */
    // ToDo: write an appropriate evaluation function
    int evaluate(DraughtsState state) { return 0; }
}
