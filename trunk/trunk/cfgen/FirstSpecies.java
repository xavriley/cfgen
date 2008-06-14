package cfgen;

import java.util.*;

public class FirstSpecies extends CantusFirmus {
		
    CantusFirmus cf;
		
    public FirstSpecies(String s) {
				super(s);
    }
    public FirstSpecies(Pitch[] newNotes) {
				super(newNotes);
    }
    public FirstSpecies(Pitch[] newNotes, Key newK) {
				super(newNotes, newK);
    }
		
		
    public void addCantusFirmus(CantusFirmus c) {
				cf = c;
    }
    
    public List<Comment> check() {
				//	if (!cf.check())
				//	    System.out.println("Warning: CF does not obey rules.");
				
				//	System.out.println("Checking: " + this);
				List<Comment> result = checkLength();
				
				if (result.size() != 0) return result;
				
				result.addAll(checkRange());
				result.addAll(checkConsonance());
				result.addAll(checkParallels());
				result.addAll(checkJumps());
				
				return result;
    }
    
    List<Comment> checkJumps() {
				List<Comment> result = super.checkJumps();
				
				int numJumps = 0;
				int numHolds = 0;
				Interval in;
				for(int i = 1 ; i < notes.length ; i++) {
						in = new Interval(notes[i], notes[i-1]);
						if (in.name > 2) numJumps++;
						if (in.name == 1) numHolds++;
				}
				if (numHolds > 1) {
						result.add(new Comment("Too many ties.",
																	 new Pitch[0],
																	 Comment.Severity.WARNING));
				}
				if (3.* ((float) notes.length - 1.0) / 5. < (float) numJumps) {
						result.add(new Comment("Probably too many jumps.",
																	 new Pitch[0],
																	 Comment.Severity.FYI));
				}
				return result;
    }
		
    List<Comment> checkLength() {
				List<Comment> result = new LinkedList<Comment>();
				
				if (notes.length != cf.notes.length) {
						result.add(new Comment("Must have the same number of notes as the CF.",
																	 notes));
				}
				return result;
    }
    
    List<Comment> checkConsonance() {
				List<Comment> result = new LinkedList<Comment>();
				
				//	if (verbose) 	System.out.println("Checking vertical intervals:");
				Interval in = new Interval(notes[0], cf.notes[0]);
				//	if (verbose) 	System.out.println("\t" + in);
				if (in.quality != 0 || (in.name != 1 && in.name != 8 && (in.name != 5 || notes[0].compareTo(cf.notes[0]) < 0))) {
						result.add(new Comment("First interval must be a unison, octave, or perfect 5th with CF in base.",
																	 new Pitch[] { notes[0],
																								 cf.notes[0] }));
				}
				
				int streakLength = 1;
				int streakType = in.name;
				
				for(int i = 1 ; i < notes.length - 1 ; i++) {
						in = new Interval(notes[i], cf.notes[i]);
						//	    if (verbose) 	    System.out.println("\t" + in);
						if (!in.isConsonant() ||
								(in.isPerfect() && in.quality != 0) ||
								((in.name == 3 || in.name == 6) && (in.quality == -2 || in.quality == 1))) {
								result.add(new Comment("Dissonant interval.",
																			 new Pitch[] { notes[i],
																										 cf.notes[i] } ) );
						}
						
						if (in.isPerfect()) {
								int i1 = notes[i].compareTo(notes[i-1]);
								int i2 = cf.notes[i].compareTo(cf.notes[i-1]);
								if (i1*i2 > 0) {
										result.add(new Comment("Hidden " + in.name,
																					 new Pitch[] { notes[i],
																												 notes[i-1],
																												 cf.notes[i],
																												 cf.notes[i-1] } ) );
								}
						}
						
						if (in.name == streakType) {
								streakLength++;
								if (streakLength > 3) {
										result.add(new Comment("Too many " + in.name + "'s in a row.",
																					 new Pitch[] { notes[i],
																												 notes[i-1],
																												 notes[i-2],
																												 notes[i-3],
																												 cf.notes[i],
																												 cf.notes[i-1],
																												 cf.notes[i-2],
																												 cf.notes[i-3] } ) );
								}
						} else {
								streakType = in.name;
								streakLength = 1;
						}
				}
				in = new Interval(notes[notes.length - 1], cf.notes[notes.length - 1]);
				//	if (verbose) 	System.out.println("\t" + in);
				if ((in.name != 1 && in.name != 8) || in.quality !=0) {
						result.add(new Comment("Last interval must be perfect unison or octave.",
																	 new Pitch[] { notes[notes.length - 1],
																								 cf.notes[notes.length - 1] } ) );
				}
				
				return result;
    }
		
    List<Comment> checkParallels() {
				List<Comment> result = new LinkedList<Comment>();
				
				//	if (verbose) 	System.out.println("Checking for parallel perfect intervals...");
				Interval lastInterval = new Interval(notes[0], cf.notes[0]);
				Interval thisInterval;
				for(int i = 1; i < notes.length ; i++) {
						//	    if (verbose) 	    System.out.println("\t" + lastInterval);
						thisInterval = new Interval(notes[i], cf.notes[i]);
						if ((thisInterval.name == 1 || thisInterval.name == 5 || thisInterval.name == 8) &&
								(thisInterval.quality == 0) &&
								(thisInterval.name == lastInterval.name && thisInterval.quality == lastInterval.quality)) {
								result.add(new Comment("Parallel " + thisInterval + "s",
																			 new Pitch[] { notes[i],
																										 notes[i-1],
																										 cf.notes[i],
																										 cf.notes[i-1] } ) );
						}
						lastInterval = thisInterval;
				}
				return result;
    }
		
}
