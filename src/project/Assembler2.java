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
				if(line.trim().length() == 0 && inText.get(i+1).trim().length() > 0) {
					errors.add("Error: line "+i+" is a blank line");
				}
				if(line.charAt(0) == ' ' || line.charAt(0) == '\t') {
					errors.add("Error: line "+i+" starts with white space");
				}
				if(!dataSeparator) {
					if(line.trim().toUpperCase().startsWith("--")) {
						if(line.trim().replace("-", "").length() != 0) {
							errors.add("Error: line "+i+" has a badly formatted data separator");
						}
						dataSeparator = true;
					}
				} else {
					errors.add("Error: line "+i+" has a duplicate data separator");
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
		for(int i=0; i<code.size(); i++) {
			String line = code.get(i);
			String[] parts = line.trim().split("\\s+");
			
			if(InstructionMap.sourceCodes.contains(parts[0].toUpperCase()) &&
					!InstructionMap.sourceCodes.contains(parts[0])) {
				errors.add("Error: line "+i+" does not have the instruction mnemonic in upper case");
			} else if(InstructionMap.noArgument.contains(parts[0])) {
				if(parts.length != 1) {
					errors.add("Error: line "+i+" has an illegal argument");
				}
			}
			
			if(!InstructionMap.noArgument.contains(parts[0])) {
				if(parts.length == 1) {
					errors.add("Error: line "+i+" is missing an argument");
				}
				if(parts.length > 3) {
					errors.add("Error: line "+i+" has more than one argument");
				}
			}
			
			int indirLvl = 1;
			
			if(parts.length == 2) {
				if(parts[1].startsWith("[")) {
					if(!InstructionMap.indirectOK.contains(parts[0])) {
						errors.add("Error: line "+i+" does not have indirect instruction");
					} else {
						if(parts[1].lastIndexOf("]") != parts[1].length()-1) {
							errors.add("Error: line "+i+" does not have a closing bracket");
							parts[1] = parts[1].substring(1, parts[1].length() - 1);
							indirLvl = 2;
						}
					}
				}
			}
			
			int arg = 0;
			try {
				arg = Integer.parseInt(parts[1],16);
			} catch(NumberFormatException e) {
				errors.add("Error: line "+i+" does not have a numeric argument");
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
		
		for(int i=0; i<data.size(); i++) {
			String line = data.get(i);
			String[] parts = line.trim().split("\\s+");
			int arg = 0; 
			int arg2 = 0;
			try {
				arg = Integer.parseInt(parts[1],16);
				arg2 = Integer.parseInt(parts[0],16);
			} catch (NumberFormatException e) {
				errors.add("Error: line "+code.size()+1+i+" does not have a numeric argument");
			}
		}
		
		outText.addAll(data);
		
		if(errors.size() > 0) {
			try (PrintWriter out = new PrintWriter(output)){
				for(String s : outText) out.println(s);
			} catch (FileNotFoundException e) {
				errors.add("Cannot create output file");
			}
		}
	}
}
