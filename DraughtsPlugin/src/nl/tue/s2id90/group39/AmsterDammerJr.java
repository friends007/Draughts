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
public class AmsterDammerJr  extends DraughtsPlayer{
    private int bestValue=0;
    int minSearchDepth = 4;
    int maxDepth;
    int depth;
    int with;
    
    /** boolean that indicates that the GUI asked the player to stop thinking. */
    private boolean stopped;

    public AmsterDammerJr(int maxSearchDepth) {
        super("amsterDammerJr.png"); // ToDo: replace with your own icon
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
            System.err.println("time out!!"); //displays the text time out on the debug
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
//        if (bestMove==null) {
//            System.err.println("no valid move found!");
//            return getRandomValidMove(s);
//        } else {
            return bestMove;
//        }
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
     * @param depth maximum recursion Depth //I changed this to a global variable -Casper
     * @return the computed value of this node
     * @throws AIStoppedException
     **/
    int alphaBeta(DraughtsNode node, int alpha, int beta)
            throws AIStoppedException
    {
        if (node.getState().isWhiteToMove()) {
            return alphaBetaMax(node, alpha + 1, beta - 1);
        } else  {
            return alphaBetaMin(node, alpha + 1, beta - 1);
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
     int alphaBetaMin(DraughtsNode node, int alpha, int beta) //black
            throws AIStoppedException {
        if (stopped) { stopped = false; throw new AIStoppedException(); }
        DraughtsState state = node.getState();
        // ToDo: write an alphabeta search to compute bestMove and value
        //the amount of possible moves
        int imax = state.getMoves().size();
        //if there is only one move do that move
        if(imax == 1){
            node.setBestMove(state.getMoves().get(0));
            return calcValue(state);
        }
        //variable for choosing that will contain the best move found in the current iteration
        Move bestMove = null;//state.getMoves().get(0);
        while(true){
            //a value that is definitly  way too high could properably be changed with either alpha or beta
            int bestValue = beta + 1;
            //a for loop looping through all the possible moves
            for(int i = 0;i<imax;i++){
                try{
                    depth = 1;
                    Move move = state.getMoves().get(i);
                    //calculating the value of this move
                    state.doMove(move);
                    int value = alphaBetaMax(node, alpha, beta, depth, bestValue);
                    state.undoMove(move);
                    if(value < bestValue){
                        //if the current move is better than previous one found set the current move as the best move found
                        bestValue = value;
                        bestMove = move;
                    }
                    //a win is found no need to continue the algorithem nothing can become better
                    if(bestValue == alpha){ //won
                        node.setBestMove(bestMove);
                        return bestValue;
                    }
                }catch(IndexOutOfBoundsException ex){
                System.err.format(
                    "klote imax = %2d, i = %2d,\n", 
                    imax, i
                );
            }
            }
            //set the best move found as the new current best move and update the maximum search depth and bestValue found
            node.setBestMove(bestMove);
            this.bestValue = bestValue;            
            maxDepth += 2;//always takes the enemies reaction into acount to prevent stupid moves
            with = 0;

//            return(bestValue);
        }
     }
    
     //see the alphaBetaMin above
    int alphaBetaMax(DraughtsNode node, int alpha, int beta) //white
            throws AIStoppedException {
        if (stopped) { stopped = false; throw new AIStoppedException(); }
        DraughtsState state = node.getState();
        // ToDo: write an alphabeta search to compute bestMove and valueint imax = state.getMoves().size();
        int imax = state.getMoves().size();
        Move bestMove = null;//state.getMoves().get(0);
        while(true){
            int bestValue = alpha - 1;
            for(int i = 0;i<imax;i++){
                try{
                    depth = 1;
                    Move move = state.getMoves().get(i);
                    state.doMove(move);
                    int value = alphaBetaMin(node, alpha, beta, depth, bestValue);
                    state.undoMove(move);
                    if(value > bestValue){
                        bestValue = value;
                        bestMove = move;
                    }
                    if(bestValue == beta){ //won
                        node.setBestMove(bestMove);
                        return bestValue;
                    }
                }catch(IndexOutOfBoundsException ex){
                System.err.format(
                    "klotezooi imax = %2d, i = %2d,\n", 
                    imax, i
                );
            }
            }
            node.setBestMove(bestMove);
            maxDepth += 2;//always takes the enemies reaction into acount to prevent stupid moves
            this.bestValue = bestValue;
            with = 0;
//            return(bestValue);
        }
    }
    
    
    int alphaBetaMin(DraughtsNode node, int alpha, int beta, int curDepth, int prevValue) //white
            throws AIStoppedException {
        if (stopped) { stopped = false; throw new AIStoppedException(); }//if the time is over go to AIStoppedExeption
        
        //get the current state
        //DraughtsState s = node.getState();
        //s.doMove(prevMove); //do the move given to the previous function
        //DraughtsState state = s; //state becomes the state where the move happened
        //s.undoMove(prevMove); //s becomes the state given by the previous function
        DraughtsState state = node.getState();
        //calculate the value of the pieces
        int value = calcValue(state);
        //update the current depth
        curDepth++;
        //amount of moves that are possible
        int imax = state.getMoves().size();
        //if there are no more possible moves or if the maximum depth has been overwritten return the found value to the previous function
        if(imax == 0){//lost
            with++;
            return beta;
        }else if(curDepth>maxDepth){
            with++;
            return value;
        }
        //update the current depth for debug purposes
        depth = curDepth;
        
        //see the first alphaBetaMin
        int bestValue = beta + 1;
        for(int i = 0;i<imax;i++){
            try{
                Move move = state.getMoves().get(i);
                state.doMove(move);
                value = alphaBetaMax(node, alpha, beta, curDepth, bestValue);
                state.undoMove(move);

                if(value < bestValue){
                    bestValue = value;
                }
                //if the bestValue is lower that the best value than the previous funcition this move will never be made so no need to calculate further into the future
                if(bestValue < prevValue){
                    with++;
                    return bestValue;
                }
            }catch(IndexOutOfBoundsException ex){
                System.err.format(
                    "tering imax = %2d, i = %2d,\n", 
                    imax, i
                );
            }
        }
        return bestValue;
     }
    
    //see alphaBetaMin right above this one
    int alphaBetaMax(DraughtsNode node, int alpha, int beta, int curDepth, int prevValue) //black
            throws AIStoppedException {
        if (stopped) { stopped = false; throw new AIStoppedException(); }
        
        //DraughtsState s = node.getState();
        //s.doMove(prevMove);
        //DraughtsState state = s;
        //s.undoMove(prevMove);
        DraughtsState state = node.getState();
        int value = calcValue(state);
        int imax = state.getMoves().size();
        curDepth++;
        
        if(imax == 0){//lost
            with++;
            return alpha;
        }else if(curDepth>maxDepth){
            with++;
            return value;
        }
        depth = curDepth;
        
        int bestValue = alpha - 1;
        for(int i = 0;i<imax;i++){
            try{
                Move move = state.getMoves().get(i);
                
                state.doMove(move);
                value = alphaBetaMin(node, alpha, beta, curDepth, bestValue);
                state.undoMove(move);

                if(value > bestValue){
                    bestValue = value;
                }
                if(bestValue > prevValue){
                    with++;
                    return bestValue;
                }
            } catch(IndexOutOfBoundsException ex){
                System.err.format(
                    "kutzooi imax = %2d, i = %2d,\n", 
                    imax, i
                );
            }
        }
        return bestValue;
    }
    
    //calculating the value of the bord
    int calcValue(DraughtsState state){
        int value = 0; //value of the bord
        int king = 300; //value king piece
        int norm = 100; //value normal piece
        int row2 = 20;
        int row3 = 70;
        
        boolean noWhite = true; 
        boolean noBlack = true;
        //cycle through all the spaces of the bord
        for(int i = 1;i != 51;i++){
            int piece = state.getPiece(i);
            switch (piece) {
                //if there is a piece add there value to the current value of the bord
                case DraughtsState.BLACKKING:
                    value -= king;
                    noBlack = false;//there is a black piece
                    break;
                case DraughtsState.BLACKPIECE:
                    value -= norm;
                    noBlack = false;
                    if(i>21){
                        int j = ((i-1)/5) % 2; //even or uneven row
                        if(state.getPiece(i-5+j) == DraughtsState.BLACKPIECE){
                            value -= row2;
                            if(state.getPiece(i-11) == DraughtsState.BLACKPIECE){
                                value -= row3;
                            }
                        }
                    }
                    break;
                case DraughtsState.WHITEKING:
                    value += king;
                    noWhite = false;//there is a white piece
                    break;
                case DraughtsState.WHITEPIECE:
                    value += norm;
                    noWhite = false;
                    if(i<40){
                        int j = ((i-1)/5) % 2; //even or uneven row
                        if(state.getPiece(i+6-j) == DraughtsState.BLACKPIECE){
                            value += row2;
                            if(state.getPiece(i+11) == DraughtsState.BLACKPIECE){
                                value += row3;
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        
        //return the value of the board
        return value;
    }
    
    /** A method that evaluates the given state. */
    // ToDo: write an appropriate evaluation function
    int evaluate(DraughtsState state) { return 0; }
}
