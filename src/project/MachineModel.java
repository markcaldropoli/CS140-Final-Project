package project;

import java.util.TreeMap;

public class MachineModel {
	public TreeMap IMAP;
	private CPU cpu = new CPU();
	private Memory memory = new Memory();
	private HaltCallback callback;
	
	public MachineModel(HaltCallback callback) {
		this.callback = callback;
		
		//INSTRUCTION MAP entry for "ADD"
		IMAP.put(0x3, (arg, level) -> {
			if(level < 0 || level > 2) {
				throw new IllegalArgumentException(
					"Illegal indirection level in ADD instruction");
			}
			if(level > 0) {
				IMAP.get(0x3).execute(memory.getData(cpu.getMemBase()+arg), level-1);
			} else {
				cpu.setAccum(cpu.getAccum() + arg);
				cpu.incrPC();
			}
		});
		
	}
	
	public MachineModel() {
		this(() -> System.exit(0));
	}
}
