package project;

import java.util.TreeMap;

public class MachineModel {
	public TreeMap<Integer, Instruction> IMAP;
	private CPU cpu = new CPU();
	private Memory memory = new Memory();
	private HaltCallback callback;
	
	public MachineModel(HaltCallback callback) {
		this.callback = callback;
		
		//INSTRUCTION MAP entry for "NOP"
		IMAP.put(0x0, (arg, level) -> {
			cpu.incrPC();
		});
		
		//INSTRUCTION MAP entry for "LOD"
		IMAP.put(0x1, (arg, level) -> {
			if(level < 0 || level > 2) {
				throw new IllegalArgumentException("Illegal indirection level in LOD instruction");
			}
			if(level > 0) {
				IMAP.get(0x1).execute(memory.getData(cpu.getMemBase()+arg), level-1);
			} else {
				cpu.setAccum(arg);
				cpu.incrPC();
			}
		});
		
		//INSTRUCTION MAP entry for "STO"
		IMAP.put(0x2, (arg, level) -> {
			if(level < 1 || level > 2) {
				throw new IllegalArgumentException("Illegal indirection level in STO instruction");
			}
			if(level > 1) {
				IMAP.get(0x2).execute(memory.getData(cpu.getMemBase()+arg), level-1);
			} else {
				memory.setData(arg, cpu.getAccum());
				cpu.incrPC();
			}
		});
		
		//INSTRUCTION MAP entry for "ADD"
		IMAP.put(0x3, (arg, level) -> {
			if(level < 0 || level > 2) {
				throw new IllegalArgumentException("Illegal indirection level in ADD instruction");
			}
			if(level > 0) {
				IMAP.get(0x3).execute(memory.getData(cpu.getMemBase()+arg), level-1);
			} else {
				cpu.setAccum(cpu.getAccum() + arg);
				cpu.incrPC();
			}
		});
		
		//INSTRUCTION MAP entry for "SUB"
		IMAP.put(0x4, (arg, level) -> {
			if(level < 0 || level > 2) {
				throw new IllegalArgumentException("Illegal indirection level in SUB instruction");
			}
			if(level > 0) {
				IMAP.get(0x4).execute(memory.getData(cpu.getMemBase()+arg), level-1);
			} else {
				cpu.setAccum(cpu.getAccum() - arg);
				cpu.incrPC();
			}
		});
		
		//INSTRUCTION MAP entry for "MUL"
		IMAP.put(0x5, (arg, level) -> {
			if(level < 0 || level > 2) {
				throw new IllegalArgumentException("Illegal indirection level in MUL instruction");
			}
			if(level > 0) {
				IMAP.get(0x5).execute(memory.getData(cpu.getMemBase()+arg), level-1);
			} else {
				cpu.setAccum(cpu.getAccum() * arg);
				cpu.incrPC();
			}
		});
		
		//INSTRUCTION MAP entry for "DIV"
		IMAP.put(0x6, (arg, level) -> {
			if(level < 0 || level > 2) {
				throw new IllegalArgumentException("Illegal indirection level in DIV instruction");
			}
			if(level > 0) {
				IMAP.get(0x6).execute(memory.getData(cpu.getMemBase()+arg), level-1);
			} else {
				if(arg == 0) {
					throw new DivideByZeroException();
				}
				cpu.setAccum(cpu.getAccum() / arg);
				cpu.incrPC();
			}
		});
		
		//INSTRUCTION MAP entry for "AND"
		IMAP.put(0x7, (arg, level) -> {
			if(level < 0 || level > 2) {
				throw new IllegalArgumentException("Illegal indirection level in AND instruction");
			}
			if(level > 0) {
				IMAP.get(0x7).execute(memory.getData(cpu.getMemBase()+arg), level-1);
			} else {
				if(cpu.getAccum() != 0 && arg != 0) {
					cpu.setAccum(1);
				} else {
					cpu.setAccum(0);
				}
				cpu.incrPC();
			}
		});
	}
	
	public MachineModel() {
		this(() -> System.exit(0));
	}
}
