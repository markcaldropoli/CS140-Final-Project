package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Assembler {
	public static void assemble(File input, File output, ArrayList<String> errors) {
		ArrayList<String> code = new ArrayList<>();
		ArrayList<String> data = new ArrayList<>();
		try(Scanner sc = new Scanner(input)) {
			boolean inData = false;
			while(sc.hasNextLine()) {
				String line = sc.nextLine();

				if (line.trim().startsWith("--")) {
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
		for(String line : code) {
			String[] parts = line.trim().split("\\s+");

			int indirLvl = 1;

			if (parts.length == 2) {
				if (parts[1].startsWith("[")) {
					parts[1] = parts[1].substring(1, parts[1].length() - 1);
					indirLvl = 2;
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

		outText.addAll(data);

		try (PrintWriter out = new PrintWriter(output)){
			for(String s : outText) out.println(s);
		} catch (FileNotFoundException e) {
			errors.add("Cannot create output file");
		}
	}

	public static void main(String[] args) {
		ArrayList<String> errors = new ArrayList<>();
		assemble(new File("in.pasm"), new File("original_out.pexe"), errors);
	}
}
