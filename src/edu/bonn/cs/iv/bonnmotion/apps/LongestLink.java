package edu.bonn.cs.iv.bonnmotion.apps;

import edu.bonn.cs.iv.bonnmotion.*;
import edu.bonn.cs.iv.graph.*;
import edu.bonn.cs.iv.util.*;

import java.io.*;
import java.util.Vector;


/**
* Longest Link Metric 
*/
public class LongestLink extends App {
    private static ModuleInfo info;
    
    static {
        info = new ModuleInfo("LongestLink");
        info.description = "Longest Link Metric";
        
        info.major = 1;
        info.minor = 0;
        info.revision = ModuleInfo.getSVNRevisionStringValue("$LastChangedRevision: 269 $");
        
        info.contacts.add(ModuleInfo.BM_MAILINGLIST);
        info.authors.add("Raphael Ernst");
		info.affiliation = ModuleInfo.UNIVERSITY_OF_BONN;
    }
    
    public static ModuleInfo getInfo() {
        return info;
    }
    
	protected Scenario s;
	protected double intervalLength = 1.0;

	protected String name;
	protected String basename;

	public LongestLink(String[] args) throws FileNotFoundException, IOException {
		go(args);
	}

	public void go(String[] _args) throws FileNotFoundException, IOException {
		parse(_args);
		if(name == null) {
			printHelp();
			System.exit(0);
		}

		basename = name + "." + getInfo().name + "_";

		System.out.println("#Reading scenario data");
		s = Scenario.getScenario(name);
		System.out.println("#Read: " + s.getModelName());

		longestLink();
	}

	protected boolean parseArg(char key, String val) {
		switch (key) {
			case 'i':
				intervalLength = Double.parseDouble(val);
				return true;
			case 'f':
				name = val;
				return true;
			case 'v':
				System.out.println("Version: " + info.toShortString());
				return true;
			default:
				return super.parseArg(key, val);
		}
	}

	public static void printHelp() {
        System.out.println(getInfo().toDetailString());
		App.printHelp();
		System.out.println("Longest Link Metric:");
		System.out.println("\t-i <(double) interval length> (Default: 1.0)");
		System.out.println("\t-v print version information");
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		new LongestLink(args);
	}


	/* Analysis Code */

	protected boolean longestLink() {
		MobileNode[] node = s.getNode();
                if(node.length == 1) {
                        System.err.println("Only one node in the scenario! No links available");
                        return false;
                }

		double duration = s.getDuration();
		double time = 0;

		Vector<Position> inTheGraph = new Vector<Position>(node.length,1);
		Vector<Position> notInTheGraph = new Vector<Position>(node.length,1);	

		PrintWriter o = null;
		try{
			o = new PrintWriter(new FileOutputStream(basename));
		} catch (IOException e) {
			System.err.println("Error when opening file: " + basename);
		}


		double longestEdge = Double.MIN_VALUE;
		while(time < duration) {
			longestEdge = Double.MIN_VALUE;
			
			inTheGraph.add(node[0].positionAt(time));
			
			for(int i=0;i<node.length;i++) {
				notInTheGraph.add(node[i].positionAt(time));
			}

			double edgeLength;
			Position next;
			double edge;	
			while(!notInTheGraph.isEmpty()) { //Add edges until graph is fully connected
				next = null;
				edgeLength = Double.MAX_VALUE;
				for(int i=0;i<inTheGraph.size();i++) {
					for(int j=0;j<notInTheGraph.size();j++) {
						edge = inTheGraph.elementAt(i).distance(notInTheGraph.elementAt(j));
						if(edge < edgeLength) {
							edgeLength = edge;
							next = notInTheGraph.elementAt(j);
						}
					}	
				}

				inTheGraph.add(next);
				notInTheGraph.remove(next);

				if(edgeLength > longestEdge) { longestEdge = edgeLength; }
			}
			o.println(time + " " + longestEdge);
			
			inTheGraph.clear();
			notInTheGraph.clear();
			time += intervalLength;
		}
		o.close();
                return true;
	}
}

