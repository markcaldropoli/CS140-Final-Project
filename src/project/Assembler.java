package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Assembler {
	public static void assemble(File input, File output, ArrayList<String> errors) {
		ArrayList<String> code = new ArrayList<>();
		ArrayList<String> data = new ArrayList<>();
		try(Scanner sc = new Scanner(input)) {
			boolean divider = false;
			while(sc.hasNextLine()) {
				String line = sc.nextLine();
				if(!line.trim().startsWith("--") && !divider) {
					code.add(line.trim());
				} else if(divider) {
					data.add(line.trim());
				} else {
					divider = true;
				}
			}
			sc.close();
		} catch(FileNotFoundException e) {
			errors.add("Input file does not exist");
			return;
		}
		ArrayList<String> outText = new ArrayList<>();
		for(String line : code) {
			String[] parts = line.trim().split("\\s+");
		}
	}
}
