package project;

import java.util.Map;
import java.util.TreeMap;

public class MachineModel {
	public Map<Integer, Instruction> IMAP = new TreeMap<>();
	private CPU cpu = new CPU();
	private Memory memory = new Memory();
	private HaltCallback callback;
	
	public MachineModel(HaltCallback callback) {
		this.callback = callback;
		
		//INSTRUCTION MAP entry for "NOP"
		IMAP.put(0x0, (arg, level) -> cpu.incrPC());
		
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
				memory.setData(cpu.getMemBase()+arg, cpu.getAccum());
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
					throw new DivideByZeroException("Cannot divide by zero in DIV instruction");
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
		
		//INSTRUCTION MAP entry for "NOT"
		IMAP.put(0x8, (arg, level) -> {
			if(level != 0) {
				throw new IllegalArgumentException("Illegal indirection level in NOT instruction");
			}
			if(cpu.getAccum() != 0) {
				cpu.setAccum(0);
			} else {
				cpu.setAccum(1);
			}
			cpu.incrPC();
		});
		
		//INSTRUCTION MAP entry for "CMPL"
		IMAP.put(0x9, (arg, level) -> {
			if(level < 1 || level > 2) {
				throw new IllegalArgumentException("Illegal indirection level in CMPL instruction");
			}
			if(level > 1) {
				IMAP.get(0x9).execute(memory.getData(cpu.getMemBase()+arg), level-1);
			} else {
				if(memory.getData(cpu.getMemBase()+arg) < 0) {
					cpu.setAccum(1);
				} else {
					cpu.setAccum(0);
				}
				cpu.incrPC();
			}
		});
		
		//INSTRUCTION MAP entry for "CMPZ"
		IMAP.put(0xA, (arg, level) -> {
			if(level < 1 || level > 2) {
				throw new IllegalArgumentException("Illegal indirection level in CMPZ instruction");
			}
			if(level > 1) {
				IMAP.get(0xA).execute(memory.getData(cpu.getMemBase()+arg), level-1);
			} else {
				if(memory.getData(cpu.getMemBase()+arg) == 0) {
					cpu.setAccum(1);
				} else {
					cpu.setAccum(0);
				}
				cpu.incrPC();
			}
		});
		
		//INSTRUCTION MAP entry for "JUMP"
		IMAP.put(0xB, (arg, level) -> {
			if(level < 0 || level > 3) {
				throw new IllegalArgumentException("Illegal indirection level in JUMP instruction");
			}
			if(level > 0 && level < 3) {
				IMAP.get(0xB).execute(memory.getData(cpu.getMemBase()+arg), level-1);
			} else if(level == 3) {
				//TODO arg + starting value of pCounter
			} else {
				cpu.setPCounter(cpu.getPCounter()+arg);
			}
		});
		
		//INSTRUCTION MAP entry for "JMPZ"
		IMAP.put(0xC, (arg, level) -> {
			if(level < 0 || level > 3) {
				throw new IllegalArgumentException("Illegal indirection level in JMPZ instruction");
			}
			if(cpu.getAccum() == 0) {
				if(level > 0 && level < 3) {
					IMAP.get(0xB).execute(memory.getData(cpu.getMemBase()+arg), level-1);
				} else if(level == 3) {
					//TODO arg + starting value of pCounter
				} else {
					cpu.setPCounter(cpu.getPCounter()+arg);
				}
			} else {
				cpu.incrPC();
			}
			
		});
		
		//INSTRUCTION MAP entry for "HALT"
		IMAP.put(0xF, (arg, level) -> {
			callback.halt();
		});
	}
	
	public MachineModel() {
		this(() -> System.exit(0));
	}

	public int getAccum() {
		return cpu.getAccum();
	}

	public int getpCounter() {
		return cpu.getPCounter();
	}

	public int getMemBase() {
		return cpu.getMemBase();
	}

	public void setAccum(int ac) {
		cpu.setAccum(ac);
	}

	public void setpCounter(int pc) {
		cpu.setPCounter(pc);
	}

	public void setMemBase(int mb) {
		cpu.setMemBase(mb);
	}

	public int[] getData() {
		return memory.getData();
	}

	public int getData(int index) {
		return memory.getData(index);
	}

	public void setData(int index, int value) {
		memory.setData(index, value);
	}
	
	public Instruction get(int instrNum) {
		return IMAP.get(instrNum);
	}
}
