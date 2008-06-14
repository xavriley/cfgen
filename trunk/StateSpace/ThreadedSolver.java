package StateSpace;

import java.util.*;

public class ThreadedSolver implements Runnable {
    
    // Populated by solveLater, the run loop will pick these up and start solving.
    State initialState;
    SuccessCheck successChecker;
    MoveGenerator moveGenerator;
    boolean searchMode; // true = bfs, false = dfs;
    Callback<String> solutionHandler;

    // Wait for solveLater() to be called (could have been called already).
    public void run() {
        while(true) {
            // wait for solveLater to be called
            synchronized(this) {
                if (initialState != null) break;
            }
        }
        solveWithCallback(initialState, successChecker, moveGenerator, searchMode, solutionHandler);
    }
    
    // When run() is called, solve this.
    public void solveLater(State init, SuccessCheck sc, MoveGenerator mg, boolean bfs, Callback<String> handler) {
        synchronized(this) {
            initialState = init;
            successChecker = sc;
            moveGenerator = mg;
            searchMode = bfs;
            solutionHandler = handler;
        }
    }
    
    public static Collection<String> solveAndReturn(State init, SuccessCheck sc, MoveGenerator mg, boolean bfs) {
        final LinkedList<String> solutions = new LinkedList<String>();
        Callback<String> callback = new Callback<String>() {
            public void run(String s) { solutions.add(s); }
        };
        solveWithCallback(init, sc, mg, bfs, callback);
        return solutions;
    }
    
    static void solveWithCallback(State init, SuccessCheck sc, MoveGenerator mg, boolean bfs, Callback<String> handler) {
        LinkedList<State> q = new LinkedList<State>();
        q.addLast(init);
        HashSet<State> checked = new HashSet<State>();
        checked.add(init);
        int solutions = 0;
        
        while(!q.isEmpty()) {
            State s = (State) q.removeFirst();
            //	    System.out.println("Examining: " + s);
            if(sc.check(s)) {
                handler.run(s.toString());
                solutions++;
            }
            Operator[] ops = mg.moves(s);
            for(int i = 0 ; i < ops.length ; i++) {
                State newState = ops[i].apply(s);
                if (!checked.contains(newState)) {
                    if (bfs) {
                        q.addLast(newState); // breadth first		    
                    } else {
                        q.addFirst(newState); // depth first
                    }
                    checked.add(newState);
                    newState.parent = s;
                    newState.path = ops[i];
                }
            }
        }
        
        System.out.println("Solutions found: " + solutions + "\nStates examined: " + checked.size());
        return;
    }
    
}
