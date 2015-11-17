package sd.swiftclient.mp.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class SwiftMenu_Apart extends JPanel implements SwiftPanel, ActionListener{

	/**
	 * @author Mohan Pan
	 * 
	 * The user control after login.
	 */
	private static final long serialVersionUID = 1L;
	private JPanel current;
	
	public JPanel getPanel() {
		JPanel output = new JPanel();
		output.setSize(1000, 530);
		output.setLocation(0, 0);
		output.setLayout(null);
		output.add(current);
		output.add(this);
		return output;
	}
	
	private JButton Task_percent, Home, Upload_file, 
	MakeNFolder, ConnectedWOthers, Recent, Trash, Hide;
	
	private final main_GUI parent;
	private JLabel APartimage;
	final JFileChooser fc = new JFileChooser();
	
	public SwiftMenu_Apart(main_GUI parent){
		
		this.parent = parent;
		setSize(250, 530);
        setLayout(null);
        setLocation(0, 0);
        setBackground(Color.RED);
        
        APartimage = APart_bg();
		APartimage.setBounds(0, 0, 250, 530);
        add(APartimage);

        Task_percent = new JButton("Task");
		Task_percent.setBounds(30, 30, 185, 40);
		Task_percent.setFont(new Font("Arial", Font.PLAIN, 17));
    	Task_percent.setForeground(Color.DARK_GRAY);
		Task_percent.setVisible(true);
		Task_percent.setBorderPainted(false);
		Task_percent.addActionListener(this);
		add(Task_percent);
        
		Home = new JButton("Home");
        Home.setBounds(30, 90, 185, 40);
        Home.setFont(new Font("Arial", Font.PLAIN, 17));
    	Home.setForeground(Color.DARK_GRAY);
		Home.setVisible(true);
		Home.setBorderPainted(false);
		Home.addActionListener(this);
		add(Home);
		
        MakeNFolder = new JButton("New Folder");
        MakeNFolder.setBounds(30, 150, 185, 40);
        MakeNFolder.setFont(new Font("Arial", Font.PLAIN, 17));
        MakeNFolder.setForeground(Color.DARK_GRAY);
        MakeNFolder.setVisible(true);
        MakeNFolder.setBorderPainted(false);
        MakeNFolder.addActionListener(this);
		add(MakeNFolder);
		
		Upload_file = new JButton("Upload File");
		Upload_file.setBounds(30, 210, 185, 40);
		Upload_file.setVisible(true);
		//Upload_file.setOpaque(false);  
		//Upload_file.setContentAreaFilled(false);
		Upload_file.setBorderPainted(false);
		Upload_file.setFont(new Font("Arial",Font.PLAIN,17));
		Upload_file.setForeground(Color.DARK_GRAY);
		Upload_file.addActionListener(this);
		add(Upload_file);
		
		ConnectedWOthers = new JButton("Connect with Others");
		ConnectedWOthers.setBounds(30, 270, 185, 40);
		ConnectedWOthers.setVisible(true);
		ConnectedWOthers.setFont(new Font("Arial",Font.PLAIN,17));
		ConnectedWOthers.setForeground(Color.DARK_GRAY);
		ConnectedWOthers.setBorderPainted(false);
		ConnectedWOthers.addActionListener(this);
		add(ConnectedWOthers);
		
		Recent = new JButton("Recent");
		Recent.setBounds(30, 330, 185, 40);
		Recent.setVisible(true);
		Recent.setFont(new Font("Arial",Font.PLAIN,17));
		Recent.setForeground(Color.DARK_GRAY);
		Recent.setBorderPainted(false);
		Recent.addActionListener(this);
		add(Recent);
		
		Trash = new JButton("Trash");
		Trash.setBounds(30, 390, 185, 40);
		Trash.setVisible(true);
		Trash.setFont(new Font("Arial",Font.PLAIN,17));
		Trash.setForeground(Color.DARK_GRAY);
		Trash.setBorderPainted(false);
		Trash.addActionListener(this);
		add(Trash);
		
		Hide = new JButton("Hide");
		Hide.setBounds(185, 470, 60, 25);
		Hide.setVisible(true);
		Hide.setFont(new Font("Arial",Font.PLAIN,12));
		Hide.setForeground(Color.DARK_GRAY);
		Hide.setBorderPainted(false);
		Hide.addActionListener(this);
		add(Hide);
		
		repaint();
		setVisible(true);
	}
	private JLabel APart_bg() {
        try {
            return new JLabel(new ImageIcon(ImageIO.read(new File("6.jpg"))));
        }
        catch(IOException ix) {
            return new JLabel("Failed to load background image.");
        }
    }
	
	public void showPanel(JPanel panel) {
		if(current != null) {
        	current.setVisible(false);
        	remove(current);
		}
		
        current = panel;
        current.setLocation(250, 0);
        current.setBackground(Color.GREEN);
        current.setVisible(true);
        current.repaint();
        setVisible(true);
        
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == Upload_file) {
	        int returnVal = fc.showOpenDialog(SwiftMenu_Apart.this);
		}
	}
}
