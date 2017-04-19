package project;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class InstructionMap {
	public static Set<String> sourceCodes = new TreeSet<>();
	public static Map<String, Integer> opcode = new TreeMap<>();
	public static Map<Integer, String> mnemonics = new TreeMap<>();
	public static Set<String> noArgument = new TreeSet<>();
	public static Set<String> indirectOK= new TreeSet<>();

	static {

		sourceCodes.add("NOP");
		//add the other source code names listed above (24 including NOP)
				
		indirectOK.add("LOD");
		//add the other source code names that allow indirect forms (11 including LOD)
		
		noArgument.add("HALT");
		//add the other source code names that do not take arguments
		//(3 including HALT)

		opcode.put("NOP", 0x0);
		//add all the other instructions given in Project Part 1 and the mapping to their
		//opcode, which is the number of the instruction. Note ADDI maps to 0x3, the same
		//as ADD--similarly for the other instructions ending in I
		//JUMPA maps to 0xB and JMPZ maps to 0xC (the same as JUMP and JMPZ)

		mnemonics.put(0x0, "NOP");
		//add one entry for 0x1 through 0xC and 0xF. Where there are multiple possibilities
		//use the basic mnemonic, e.g. mnemonics.put(0x3, "ADD"), 
		//mnemonics.put(0xB, "JUMP")
		//Here and in opcode, it is OK if you prefer to use 0,1,...,12 and 15 in place of 
		//0x0, 0x1, ..., 0xC, and 0xF  
	}
}