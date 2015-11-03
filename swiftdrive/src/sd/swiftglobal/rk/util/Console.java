package sd.swiftglobal.rk.util;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class Console extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JTextArea text;
	
	private final int size_x = 500,
					  size_y = 300,
					  border = 2;
	
	
	public Console() {
		setLayout(null);
		setUndecorated(true);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setResizable(false);
		setSize(size_x, size_y);
		
		text = new JTextArea();
		text.setSize(size_x - border, size_y - border);
		text.setLocation(border / 2, border / 2);
		text.setEditable(false);
		text.setForeground(Color.CYAN);
		
		add(text);
		setVisible(true);
	}
	
	public void append(String str) {
		text.setText(text.getText() + "\n" + str);
	}
}
