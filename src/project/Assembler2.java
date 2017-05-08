package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Assembler2 {
	public static void assemble(File input, File output, ArrayList<String> errors) {
		ArrayList<String> code = new ArrayList<>();
		ArrayList<String> data = new ArrayList<>();
		try(Scanner sc = new Scanner(input)) {
			ArrayList<String> inText = new ArrayList<>();
			while(sc.hasNextLine()) {
				inText.add(sc.nextLine());
			}
			boolean dataSeparator = false;
			for(int i=0; i<inText.size(); i++) {
				String line = inText.get(i);
				if(line.trim().length() == 0 && i + 1 < inText.size() && inText.get(i+1).trim().length() > 0) {
					errors.add("Error: line "+(i+1)+" is a blank line");
				}
				if(!line.trim().isEmpty() && (line.charAt(0) == ' ' || line.charAt(0) == '\t')) {
					errors.add("Error: line "+(i+1)+" starts with white space");
				}
				if(line.trim().toUpperCase().startsWith("--")) {
					if(dataSeparator) {
						errors.add("Error: line "+(i+1)+" has a duplicate data separator");
					}
					if(line.trim().replace("-", "").length() != 0) {
						errors.add("Error: line "+(i+1)+" has a badly formatted data separator");
					}
					dataSeparator = true;
				}
			}
			boolean inData = false;
			for(int i=0; i<inText.size(); i++) {
				String line = inText.get(i).trim();

				if (line.startsWith("--")) {
					inData = true;
					continue;
				}

				if (inData) {
					data.add(line);
				} else {
					code.add(line);
				}
			}
		} catch(FileNotFoundException e) {
			errors.add("Input file does not exist");
			return;
		}

		ArrayList<String> outText = new ArrayList<>();
		System.out.println("Code size: " + code.size());
		for(int i=0; i<code.size(); i++) {
			String line = code.get(i);

			if (line.trim().isEmpty()) {
				continue;
			}

			System.out.println(line);

			String[] parts = line.trim().split("\\s+");

			if(!InstructionMap.opcode.containsKey(parts[0]) &&
					!InstructionMap.opcode.containsKey(parts[0].toUpperCase())) {
				errors.add("Error: line "+(i+1)+" has an illegal mnemonic: " + parts[0]);
				continue;
			}
			
			if(InstructionMap.sourceCodes.contains(parts[0].toUpperCase()) &&
					!InstructionMap.sourceCodes.contains(parts[0])) {
				errors.add("Error: line "+(i+1)+" does not have the instruction mnemonic in uppercase");
				continue;
			} else if(InstructionMap.noArgument.contains(parts[0])) {
				if(parts.length != 1) {
					errors.add("Error: line "+(i+1)+" has an illegal argument");
					continue;
				}
			}
			
			if(!InstructionMap.noArgument.contains(parts[0])) {
				if(parts.length == 1) {
					errors.add("Error: line "+(i+1)+" is missing an argument");
					continue;
				}
				if(parts.length >= 3) {
					errors.add("Error: line "+(i+1)+" has more than one argument");
					continue;
				}
			}
			
			int indirLvl = 1;
			
			if(parts.length == 2) {
				if(parts[1].startsWith("[")) {
					if(!InstructionMap.indirectOK.contains(parts[0])) {
						errors.add("Error: line "+(i+1)+" does not have indirect instruction");
						continue;
					} else {
						if(parts[1].lastIndexOf("]") != parts[1].length()-1) {
							errors.add("Error: line "+(i+1)+" does not have a closing bracket");
							continue;
						} else {
							parts[1] = parts[1].substring(1, parts[1].length() - 1);
							indirLvl = 2;
						}
					}
				}

				try {
					Integer.parseInt(parts[1],16);
				} catch(NumberFormatException e) {
					errors.add("Error: line "+(i+1)+" does not have a numeric argument");
					continue;
				}
			}

			if (parts[0].endsWith("I")) {
				indirLvl = 0;
			} else if (parts[0].endsWith("A")) {
				indirLvl = 3;
			}

			String opcode = Integer.toHexString(InstructionMap.opcode.get(parts[0])).toUpperCase();

			if (parts.length == 1) {
				outText.add(opcode + " 0 0");
			} else {
				outText.add(opcode + " " + indirLvl + " " + parts[1]);
			}
		}

		outText.add("-1");
		
		for(int i=1; i<=data.size(); i++) {
			String line = data.get(i-1);
			String[] parts = line.trim().split("\\s+");

			if (line.trim().isEmpty()) {
				continue;
			}

			if(parts.length == 2) {
				try {
					Integer.parseInt(parts[1],16);
					Integer.parseInt(parts[0],16);
				} catch (NumberFormatException e) {
					errors.add("Error: line "+(code.size()+1+i)+" data address is not a hex number");
				}
			} else {
				errors.add("Error: line "+(code.size()+1+i)+" does not have a numeric argument");
			}
		}
		
		outText.addAll(data);
		
		if(errors.size() == 0) {
			try (PrintWriter out = new PrintWriter(output)){
				for(String s : outText) out.println(s);
			} catch (FileNotFoundException e) {
				errors.add("Cannot create output file");
			}
		}
	}

	public static void main(String[] args) {
		ArrayList<String> errors = new ArrayList<>();
		assemble(new File("in.pasm"), new File("out.pexe"), errors);
		for (String error : errors) {
			System.out.println(error);
		}
	}
}
