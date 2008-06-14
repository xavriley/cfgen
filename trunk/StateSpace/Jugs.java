package StateSpace;

public class Jugs {

    public static void main(String[] args) {
        int goal = Integer.parseInt(args[0]);
        Integer[] c = new Integer[args.length - 1];
        for(int i = 1 ; i < args.length ; i++)
            c[i-1] = new Integer(args[i]);
        solveJugs(c, goal);
    }
    
    public static void solveJugs(Integer[] capacities, int goal) {
        State init = new State();
        init.data = new Integer[capacities.length];
        for(int i = 0 ; i < init.data.length ; i++)
            init.data[i] = new Integer(0);
        
        final int target = goal;
        final Integer[] capacity = capacities;
        
        SuccessCheck sc = new SuccessCheck() {
                public boolean check(State s) {
                    for(int i = 0 ; i < s.data.length ; i++)
                        if (((Integer) s.data[i]).intValue() == target) return true;
                    return false;
                }
            };
        
        MoveGenerator mg = new MoveGenerator() {
                public Operator[] moves(State s) {
                    Operator[] moves = new Operator[2*s.data.length + 
                                                    s.data.length*(s.data.length-1)];
                    int count = 0;
                    for(int i = 0 ; i < s.data.length ; i++) {
                        moves[count++] = new Operator() {
                                public State apply(State state) {
                                    State newState = new State();
                                    newState.data = new Integer[state.data.length];
                                    for(int j = 0 ; j < state.data.length ; j++)
                                        newState.data[j] = state.data[j];
                                    newState.data[((Integer) args[0]).intValue()] = new Integer(0);
                                    return newState;
                                }
                                public String toString() {
                                    return "Empty jug #" + ((Integer) args[0]).intValue();
                                }
                            };
                        moves[count-1].args = new Integer[] {new Integer(i) };
                        moves[count++] = new Operator() {
                                public State apply(State state) {
                                    State newState = new State();
                                    newState.data = new Integer[state.data.length];
                                    for(int j = 0 ; j < state.data.length ; j++)
                                        newState.data[j] = state.data[j];
                                    newState.data[((Integer) args[0]).intValue()] = args[1];
                                    return newState;
                                }
                                public String toString() {
                                    return "Fill jug #" + args[0];
                                }
                            };
                        moves[count-1].args = new Integer[] {new Integer(i), capacity[i]};
                    }
                    for(int i = 0 ; i < s.data.length ; i++) {
                        for(int j = 0 ; j < s.data.length ; j++) {
                            if (i == j) continue;
                            moves[count++] = new Operator() {
                                    public State apply(State state) {
                                        State newState = new State();
                                        newState.data = new Integer[state.data.length];
                                        for(int k = 0 ; k < state.data.length ; k++)
                                            newState.data[k] = state.data[k];
                                        newState.data[((Integer) args[1]).intValue()] = new Integer(Math.min(((Integer) args[2]).intValue(),
                                                                                                             ((Integer) state.data[((Integer) args[0]).intValue()]).intValue() +
                                                                                                             ((Integer) state.data[((Integer) args[1]).intValue()]).intValue()));
                                        newState.data[((Integer) args[0]).intValue()] = new Integer(Math.max(0,
                                                                                                             ((Integer) state.data[((Integer) args[1]).intValue()]).intValue() +
                                                                                                             ((Integer) state.data[((Integer) args[0]).intValue()]).intValue() -
                                                                                                             ((Integer) args[2]).intValue()));
                                        
                                        return newState;
                                    }
                                    public String toString() {
                                        return "Pour jug #" + args[0] + " into jug #" + args[1];
                                    }
                                };
                            moves[count-1].args = new Integer[] {new Integer(i), 
                                                                 new Integer(j), 
                                                                 capacity[j]};
                        }
                    }
                    return moves;
                }
            };
        
        System.out.println(ThreadedSolver.solveAndReturn(init, sc, mg, true));
    }
    
}
