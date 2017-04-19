package project;

import java.util.TreeMap;

public class MachineModel {
	public TreeMap IMAP;
	private CPU cpu = new CPU();
	private Memory memory = new Memory();
	private HaltCallback callback;
	
	public MachineModel(HaltCallback callback) {
		this.callback = callback;
	}
	
	public MachineModel() {
		this(() -> System.exit(0));
	}
}
