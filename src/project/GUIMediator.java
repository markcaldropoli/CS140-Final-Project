package project;

import java.util.Observable;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class GUIMediator extends Observable {
	private MachineModel model;
	private FileMgr filesMgr;
	private StepControl stepControl;
	private JFrame frame;
	
	public MachineModel getModel() {
		return model;
	}
	
	public void setModel(MachineModel model) {
		this.model = model;
	}
	
	public States getCurrentState() {
		return model.getCurrentState();
	}
	
	public void setCurrentState(States s) {
		if(s == States.PROGRAM_HALTED) {
			stepControl.setAutoStepOn(false);
		}
		model.setCurrentState(s);
		model.getCurrentState().enter();
		setChanged();
		notifyObservers();
	}
	
	public void assembleFile() {
		filesMgr.assembleFile();
	}

	public void loadFile() {
		filesMgr.loadFile(model.getCurrentJob());
	}
	
	public void setPeriod(int value) {
		stepControl.setPeriod(value);
	}
	
	public void changeToJob(int i) {
		model.changeToJob(i);
		if(model.getCurrentState() != null) {
			model.getCurrentState().enter();
			setChanged();
			notifyObservers();
		}
	}
	
	public JFrame getFrame() {
		return frame;
	}
	
	void clearJob() {
		model.clearJob();
		model.setCurrentState(States.NOTHING_LOADED);
		model.getCurrentState().enter();
		setChanged();
		notifyObservers("Clear");
	}
	
	void toggleAutoStep() {
		stepControl.toggleAutoStep();
		if(stepControl.isAutoStepOn()) {
			model.setCurrentState(States.AUTO_STEPPING);
		} else {
			model.setCurrentState(States.PROGRAM_LOADED_NOT_AUTOSTEPPING);
		}
		model.getCurrentState().enter();
		setChanged();
		notifyObservers();
	}
	
	void reload() {
		stepControl.setAutoStepOn(false);
		clearJob();
		filesMgr.finalLoad_ReloadStep(model.getCurrentJob());
	}
	
	void makeReady(String s) {
		stepControl.setAutoStepOn(false);
		model.setCurrentState(States.PROGRAM_LOADED_NOT_AUTOSTEPPING);
		model.getCurrentState().enter();
		setChanged();
		notifyObservers(s);
	}
	
	public void step() {
		if(model.getCurrentState() != States.PROGRAM_HALTED && 
				model.getCurrentState() != States.NOTHING_LOADED) {
			try {
				model.step();
			} catch(CodeAccessException e) {
				JOptionPane.showMessageDialog(
						frame,
						"Illegal access to code from " + model.getpCounter() + "\n"
						 + "Exception message: " + e.getMessage(),
						 "Run time error", JOptionPane.OK_OPTION);
			} catch(ArrayIndexOutOfBoundsException e) {
				JOptionPane.showMessageDialog(frame,
						"Illegal access to data from " + model.getpCounter() + "\n"
						+ "Exception message: " + e.getMessage(),
						"Run time error", JOptionPane.OK_OPTION);
			} catch(NullPointerException e) {
				JOptionPane.showMessageDialog(frame,
						"NullPointerException  from" + model.getpCounter() + "\n"
						+ "Exception message: " + e.getMessage(),
						"Run time error", JOptionPane.OK_OPTION);
			} catch(IllegalArgumentException e) {
				JOptionPane.showMessageDialog(frame,
						"Program error from " + model.getpCounter() + "\n"
						+ "Exception message: " + e.getMessage(),
						"Run time error", JOptionPane.OK_OPTION);
			} catch(DivideByZeroException e) {
				JOptionPane.showMessageDialog(frame,
						"Divide by serp from " + model.getpCounter() + "\n"
						+ "Exception message: " + e.getMessage(),
						"Rune time error", JOptionPane.OK_OPTION);
			}
			setChanged();
			notifyObservers();
		}
	}
	
	public void execute() {
		while(model.getCurrentState() != States.PROGRAM_HALTED && 
				model.getCurrentState() != States.NOTHING_LOADED) {
			try {
				model.step();
			} catch(CodeAccessException e) {
				JOptionPane.showMessageDialog(
						frame,
						"Illegal access to code from " + model.getpCounter() + "\n"
						 + "Exception message: " + e.getMessage(),
						 "Run time error", JOptionPane.OK_OPTION);
			} catch(ArrayIndexOutOfBoundsException e) {
				JOptionPane.showMessageDialog(frame,
						"Illegal access to data from " + model.getpCounter() + "\n"
						+ "Exception message: " + e.getMessage(),
						"Run time error", JOptionPane.OK_OPTION);
			} catch(NullPointerException e) {
				JOptionPane.showMessageDialog(frame,
						"NullPointerException  from" + model.getpCounter() + "\n"
						+ "Exception message: " + e.getMessage(),
						"Run time error", JOptionPane.OK_OPTION);
			} catch(IllegalArgumentException e) {
				JOptionPane.showMessageDialog(frame,
						"Program error from " + model.getpCounter() + "\n"
						+ "Exception message: " + e.getMessage(),
						"Run time error", JOptionPane.OK_OPTION);
			} catch(DivideByZeroException e) {
				JOptionPane.showMessageDialog(frame,
						"Divide by serp from " + model.getpCounter() + "\n"
						+ "Exception message: " + e.getMessage(),
						"Rune time error", JOptionPane.OK_OPTION);
			}
		}
		setChanged();
		notifyObservers();
	}
	
	public void exit() {
		int decision = JOptionPane.showConfirmDialog(
				frame, "Do you really wish to exit?", 
				"Confirmation", JOptionPane.YES_NO_OPTION);
		if(decision == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
	}
}
