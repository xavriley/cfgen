package StateSpace;

public class State {
    
    public State parent = null;
    public Operator path = null;
    public Object[] data;
    
    public int hashCode() {
        return toString().hashCode();
    }
    
    public boolean equals(Object other) {
        Object[] otherdata = ((State) other).data;
        if (data.length != otherdata.length) return false;
        for(int i = 0 ; i < data.length ; i++) {
            if (data[i] != null &&
                otherdata[i] != null &&
                !data[i].equals(otherdata[i])) return false;
            if ((data[i] == null) != (otherdata[i] == null)) return false;
        }
        return true;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for(int i = 0 ; i < data.length ; i++) {
            sb.append(data[i]);
            sb.append(' ');
        }
        return sb.toString();
    }
 
    public static int depth(State s) {
        return (s == null) ? -1 : 1 + depth(s.parent);
    }
    
    public static String printPath(State s) {
        return printPath(s, 0);
    }    
    public static String printPath(State s, int depth) {
        if (s.parent == null) return s.toString();
        return printPath(s.parent, depth + 1) + '\n' + s.path + '\n' + s;
    }
       
}
