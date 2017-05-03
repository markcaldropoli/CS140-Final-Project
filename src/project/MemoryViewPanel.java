package project;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

public class MemoryViewPanel implements Observer {
    private MachineModel model;
    private JScrollPane scroller;
    private JTextField[] dataHex;
    private JTextField[] dataDecimal;
    private int lower = -1;
    private int upper = -1;
    private int previousColor = -1;

    public MemoryViewPanel(GUIMediator gui, MachineModel md1, int lwr, int upr) {
        model = md1;
        lower = lwr;
        upper = upr;
        gui.addObserver(this);
    }

    public JComponent createMemoryDisplay() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        Border border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                "Data Memory View [" + lower + "-" + upper + "]",
                TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION);
        panel.setBorder(border);
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BorderLayout());
        JPanel numPanel = new JPanel();
        JPanel decimalPanel = new JPanel();
        JPanel hexPanel = new JPanel();
        numPanel.setLayout(new GridLayout(0, 1));
        decimalPanel.setLayout(new GridLayout(0, 1));
        hexPanel.setLayout(new GridLayout(0, 1));
        innerPanel.add(numPanel, BorderLayout.LINE_START);
        innerPanel.add(decimalPanel, BorderLayout.CENTER);
        innerPanel.add(hexPanel, BorderLayout.LINE_END);
        dataHex = new JTextField[upper - lower];
        dataDecimal = new JTextField[upper - lower];
        for (int i = lower; i < upper; i++) {
            numPanel.add(new JLabel(i + ": ", JLabel.RIGHT));
            dataDecimal[i - lower] = new JTextField(10);
            dataHex[i - lower] = new JTextField(10);
            decimalPanel.add(dataDecimal[i - lower]);
            hexPanel.add(dataHex[i - lower]);
        }
        scroller = new JScrollPane(innerPanel);
        panel.add(scroller);
        return panel;
    }

    @Override
    public void update(Observable arg0, Object arg1) {
        for (int i = lower; i < upper; i++) {
            int val = model.getData(i);
            dataDecimal[i - lower].setText("" + val);
            String s = Integer.toHexString(val);
            if (val < 0)
                s = "-" + Integer.toHexString(-val);
            dataHex[i - lower].setText(s.toUpperCase());
        }
        if (arg1 != null && arg1.equals("Clear")) {
            if (lower <= previousColor && previousColor < upper) {
                dataDecimal[previousColor - lower].setBackground(Color.WHITE);
                dataHex[previousColor - lower].setBackground(Color.WHITE);
                previousColor = -1;
            }
        } else {
            if (previousColor >= lower && previousColor < upper) {
                dataDecimal[previousColor - lower].setBackground(Color.WHITE);
                dataHex[previousColor - lower].setBackground(Color.WHITE);
            }
            previousColor = model.getChangedIndex();
            if (previousColor >= lower && previousColor < upper) {
                dataDecimal[previousColor - lower].setBackground(Color.YELLOW);
                dataHex[previousColor - lower].setBackground(Color.YELLOW);
            }
        }
        if (scroller != null && model != null) {
            JScrollBar bar = scroller.getVerticalScrollBar();
            if (model.getChangedIndex() >= lower &&
                    model.getChangedIndex() < upper &&
                    // the following just checks createMemoryDisplay has run
                    dataDecimal != null) {
                Rectangle bounds = dataDecimal[model.getChangedIndex() - lower].getBounds();
                bar.setValue(Math.max(0, bounds.y - 15 * bounds.height));
            }
        }
    }

    public static void main(String[] args) {
        GUIMediator view = new GUIMediator();
        MachineModel model = new MachineModel();
        MemoryViewPanel panel = new MemoryViewPanel(view, model, 0, 500);
        JFrame frame = new JFrame("TEST");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 700);
        frame.setLocationRelativeTo(null);
        frame.add(panel.createMemoryDisplay());
        frame.setVisible(true);
        System.out.println(Loader.load(model, new File("out.pexe"), 0, 0));
        panel.update(view, null);
    }
}
