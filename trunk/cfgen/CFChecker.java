package cfgen;

import java.util.*;

public class CFChecker {

    public static void main(String[] args) {
	CantusFirmus cf = new CantusFirmus(args[0]);
	FirstSpecies fs = new FirstSpecies(args[1]);
	fs.addCantusFirmus(cf);
	cf.verbose = true;
	fs.verbose = true;
	Collection goodCF = cf.check();
	Collection goodFS = fs.check();
	System.out.println((goodCF.size() == 0) ? "Cantus Firmus follows rules." : "Cantus Firmus breaks rules.");
	System.out.println((goodFS.size() == 0) ? "Solution follows rules." : "Solution breaks rules.");
    }

}
