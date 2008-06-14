package cfgen;

import java.util.*;

public class CantusFirmus {

    public boolean verbose = false;
		
    public Key k;
    public Pitch[] notes;
		
    public CantusFirmus(String s) {
				String[] s2 = s.split(" ");
				notes = new Pitch[s2.length];
				for(int i = 0 ; i < s2.length ; i++)
						notes[i] = new Pitch(s2[i]);
				fixKey(true);
    }
    public CantusFirmus(Pitch[] newNotes) {
				notes = newNotes;
				fixKey(false);
    }
    public CantusFirmus(Pitch[] newNotes, Key newK) {
				notes = newNotes;
				k = newK;
    }
		
    public void setKey(Key newK) {
				k = newK;
    }
		
    void fixKey(boolean verbose2) {
				Key major = new Key(notes[0], Key.Quality.MAJOR);
				Key minor = new Key(notes[0], Key.Quality.MINOR);
				int majorDiff = 0, minorDiff = 0;
				char n; int a;
				Interval in;
				for(int i = 0 ; i < notes.length ; i++) {
						n = notes[i].name;
						a = notes[i].acc;
						if (major.getPitch(n).acc != a) majorDiff++;
						if (minor.getPitch(n).acc != a) {
								in = new Interval(minor.tonic, notes[i]);
								if ((in.name == 6 || in.name == 7) &&
										minor.getPitch(n).acc == a+1 ) {}
								else
										minorDiff++;
						}
				}
				setKey((majorDiff > minorDiff) ? minor : major);
				if (verbose2) {
						System.out.println("Assigning key...\n" +
															 "\tMajor: " + majorDiff + " discrepancies.\n" +
															 "\tMinor: " + minorDiff + " discrepancies.\n" +
															 "\tKey is: " + k);
				}
    }
    
    public String toString() {
				StringBuffer buf = new StringBuffer();
				for(int i = 0 ; i < notes.length ; i++) {
						buf.append(notes[i]);
						buf.append(' ');
				}
				return buf.toString();
    }
		
    public List<Comment> check() {
				//	System.out.println("Checking: " + this);
				List<Comment> result = new LinkedList<Comment>();
				
				if(notes.length == 0) {
						result.add(new Comment("Please enter a cantus firmus."));
						return result;
				}
				
				result.addAll(checkLength());
				result.addAll(checkEnds());
				result.addAll(checkRange());
				result.addAll(checkJumps());
				
				return result;
    }
    
    List<Comment> checkLength() {
				List<Comment> result = new LinkedList<Comment>();
				if (notes.length < 8 || notes.length > 16)
						result.add(new Comment("Must be between 8 and 16 notes.",
																	 new Pitch[] { notes[0],
																								 notes[notes.length-1] }, 
																	 Comment.Severity.WARNING));
				return result;
    }
    
    List<Comment> checkEnds() {
				List<Comment> result = new LinkedList<Comment>();
				//	if (verbose)	System.out.println("Tonic is: " + k.tonic);
				if (k.tonic.name != notes[0].name ||
						k.tonic.acc != notes[0].acc) {
						result.add(new Comment("CF doesn't begin on tonic.", new Pitch[] { notes[0] }));
				} // else if (verbose) System.out.println("\tCF begins on: " + notes[0]);
				if (k.tonic.name != notes[notes.length - 1].name ||
						k.tonic.acc != notes[notes.length - 1].acc) {
						result.add(new Comment("CF doesn't end on tonic.", new Pitch[] { notes[notes.length-1] }));
				} // else if (verbose) System.out.println("\tCF ends on: " + notes[notes.length - 1]);
				return result;
    }
		
    List<Comment> checkRange() {
				List<Comment> result = new LinkedList<Comment>();
				Pitch high = notes[0];
				Pitch low = notes[0];
				for(int i = 1 ; i < notes.length ; i++) {
						if (notes[i].compareTo(high) > 0) high = notes[i];
						if (notes[i].compareTo(low) < 0) low = notes[i];
				}
				int range = high.compareTo(low);
				//	if (verbose) 	System.out.println("CF has range: " + new Interval(high, low));
				//	if (verbose) 	System.out.println("\tHigh: " + high.toStringVerbose() + "\n\tLow: " + low.toStringVerbose());
				if (range > 16) {
						result.add(new Comment("Range too large.",
																	 new Pitch[] { low, high },
																	 Comment.Severity.WARNING));
				}
				if (range < 7) {
						result.add(new Comment("Range too small.",
																	 new Pitch[] { low, high },
																	 Comment.Severity.WARNING));
				}
				return result;
    }
    
    List<Comment> checkJumps() {
				List<Comment> result = new LinkedList<Comment>();
				Interval in;
				int totalJumps = 0;
				int consecutiveJumps = 0;
				int ties = 0;
				int thisJumpDirection, lastJumpDirection = 0;
				//	if (verbose) 	System.out.println("Checking jumps...");
				for(int i = 1 ; i < notes.length ; i++) {
						in = new Interval(notes[i], notes[i-1]);
						//	    if (verbose) 	    System.out.print("\t" + in + " ");
						if ((in.name == 4 && in.quality == 1) || (in.name == 5 && in.quality == -1)) {
								result.add(new Comment("Tritone! AAAARGH!",
																			 new Pitch[] { notes[i], notes[i-1] }));
						}
						if (in.name == 2 && in.quality == 1) {
								if (k.isMinor() &&
                    k.getDegree(notes[i].name) == 7) { // jumping to the 7th of the key
										result.add(new Comment("Augmented 2nd! (You should sharp the 6th)",
																					 new Pitch[] { notes[i], notes[i-1] },
																					 Comment.Severity.WARNING));
								} else {
										result.add(new Comment("Augmented 2nd!",
																					 new Pitch[] { notes[i], notes[i-1] }));
								}
						}
						if (in.name > 2) {
								totalJumps++;
								consecutiveJumps++;
								if (consecutiveJumps > 2) {
										result.add(new Comment("Too many consecutive jumps.",
																					 new Pitch[] { notes[i],
																												 notes[i-1],
																												 notes[i-2],
																												 notes[i-3] },
																					 Comment.Severity.WARNING));
								}
						} else {
								consecutiveJumps = 0;
						}
						if (in.getValue() > 9) {
								result.add(new Comment("Jump too large (" + in + ")",
																			 new Pitch[] { notes[i], notes[i-1] }));
						}
						thisJumpDirection = notes[i].compareTo(notes[i-1]);
						//	    if (verbose) System.out.println((thisJumpDirection > 0) ? "up" : ((thisJumpDirection < 0) ? "down" : ""));
						if ((lastJumpDirection < -4 && thisJumpDirection < 0) ||
								(lastJumpDirection > 4 && thisJumpDirection > 0)) {
								result.add(new Comment("You need to change directions after a jump greater than a third.",
																			 new Pitch[] { notes[i],
																										 notes[i-1],
																										 notes[i-2] }));
						}
						if ((lastJumpDirection <= -3 && thisJumpDirection <= -3) ||
								(lastJumpDirection >= 3 && thisJumpDirection >= 3)) {
								result.add(new Comment("Don't jump in the same direction twice in a row.",
																			 new Pitch[] { notes[i],
																										 notes[i-1],
																										 notes[i-2] }));
						}
						
						if (in.name != 1) {
								ties = 0;
								lastJumpDirection = thisJumpDirection;
						} else {
								ties++;
								if (ties > 1) {
										result.add(new Comment("Too many ties in a row.",
																					 new Pitch[] { notes[i], notes[i-1] }));
								}
						}
				}
				
				in = new Interval(notes[notes.length - 1], notes[notes.length - 2]);
				if (in.name != 2 || in.getValue() > 2) {
						result.add(new Comment("Must end by step.",
																	 new Pitch[] { notes[notes.length - 2],
																								 notes[notes.length - 1] }));
				}
				
				return result;
    }
}
