package sd.swiftclient.mp.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import sd.swiftglobal.mp.gui.GraphicalInterface.SwiftContainer;
import sd.swiftglobal.mp.gui.GraphicalInterface.SwiftMaster;
import sd.swiftglobal.mp.gui.GraphicalInterface.SwiftPanel;
import sd.swiftglobal.rk.Meta.Isaiah;
import sd.swiftglobal.rk.Settings;

public class MenuContainer extends JPanel implements Settings, SwiftPanel, SwiftContainer, ActionListener {
	public static final long serialVersionUID = 1l;

	private SwiftMaster master;
	private SwiftContainer parent;
	private SwiftPanel current;
	private JPanel active, output;

	private JLabel background;
	private JButton task, home, upload, newFolder, connect, recent, trash, hide, unhide;
	private JFileChooser fc;

	public MenuContainer(SwiftMaster m, SwiftContainer c) {
		setMaster(m);
		setParentContainer(c);

		setSize(250, 530);
        setLayout(null);
        setLocation(0, 0);

        task = new JButton("Task");
		task.setBounds(30, 30, 185, 40);
		task.setFont(new Font("Arial", Font.PLAIN, 17));
    	task.setForeground(Color.DARK_GRAY);
		task.setVisible(true);
		task.setBorderPainted(false);
		task.addActionListener(this);
		add(task);
        
		home = new JButton("home");
        home.setBounds(30, 90, 185, 40);
        home.setFont(new Font("Arial", Font.PLAIN, 17));
    	home.setForeground(Color.DARK_GRAY);
		home.setVisible(true);
		home.setBorderPainted(false);
		home.addActionListener(this);
		add(home);
		
        newFolder = new JButton("New Folder");
        newFolder.setBounds(30, 150, 185, 40);
        newFolder.setFont(new Font("Arial", Font.PLAIN, 17));
        newFolder.setForeground(Color.DARK_GRAY);
        newFolder.setVisible(true);
        newFolder.setBorderPainted(false);
        newFolder.addActionListener(this);
		add(newFolder);
		
		upload = new JButton("Upload File");
		upload.setBounds(30, 210, 185, 40);
		upload.setVisible(true);
		upload.setBorderPainted(false);
		upload.setFont(new Font("Arial",Font.PLAIN,17));
		upload.setForeground(Color.DARK_GRAY);
		upload.addActionListener(this);
		add(upload);
		
		connect = new JButton("Connect with Others");
		connect.setBounds(30, 270, 185, 40);
		connect.setVisible(true);
		connect.setFont(new Font("Arial",Font.PLAIN,17));
		connect.setForeground(Color.DARK_GRAY);
		connect.setBorderPainted(false);
		connect.addActionListener(this);
		add(connect);
		
		recent = new JButton("recent");
		recent.setBounds(30, 330, 185, 40);
		recent.setVisible(true);
		recent.setFont(new Font("Arial",Font.PLAIN,17));
		recent.setForeground(Color.DARK_GRAY);
		recent.setBorderPainted(false);
		recent.addActionListener(this);
		add(recent);
		
		trash = new JButton("trash");
		trash.setBounds(30, 390, 185, 40);
		trash.setVisible(true);
		trash.setFont(new Font("Arial",Font.PLAIN,17));
		trash.setForeground(Color.DARK_GRAY);
		trash.setBorderPainted(false);
		trash.addActionListener(this);
		add(trash);
		
		hide = new JButton("hide");
		hide.setBounds(185, 470, 60, 25);
		hide.setVisible(true);
		hide.setFont(new Font("Arial",Font.PLAIN,12));
		hide.setForeground(Color.DARK_GRAY);
		hide.setBorderPainted(false);
		hide.addActionListener(this);
		add(hide);
		
		unhide = new JButton("");
		unhide.setBounds(0, 0, 20, 530);
		unhide.setVisible(false);
		unhide.setFont(new Font("Arial",Font.PLAIN,5));
		unhide.setForeground(Color.DARK_GRAY);
		unhide.setBorderPainted(false);
		unhide.addActionListener(this);
		add(unhide);
        
        background = getBackgroundLabel();
		background.setBounds(0, 0, 250, 530);
        add(background);

		setVisible(true);
	}

	private JLabel getBackgroundLabel() {
        try {
            return new JLabel(new ImageIcon(ImageIO.read(new File("6.jpg"))));
        }
        catch(IOException ix) {
            return new JLabel("Failed to load background image.");
        }
	}

	public void setMaster(SwiftMaster m) {
		master = m;
	}
	
	public SwiftMaster getMaster() {
		return master;
	}

	public void setParentContainer(SwiftContainer c) {
		parent = c;
	}

	public SwiftContainer getParentContainer() {
		return parent;
	}

	public void setPanel(SwiftPanel panel) {
		if(active != null) {
			active.setVisible(false);
			remove(active);
		}

		current = panel;
		active = current.getPanel();
		active.setLocation(250, 0);
		active.setVisible(true);
		active.repaint();
		active.setVisible(true);
	}

	public SwiftPanel getCurrentPanel() {
		return current;
	}

	public JPanel getPanel() {
		output = new JPanel();
		output.setSize(1000, 530);
		output.setLocation(0, 0);
		output.setLayout(null);
		if(active != null) output.add(active);
		output.add(this);
		return output;
	}

	@Isaiah
	private void isAct(ActionEvent act) {

	}

	/*public void linkView(FilePanel view){
		fileView = view;
	}*/

	public void Hiding_class(){
		/*JLabel APart_bg = new JLabel();
		APart_bg.setVisible(false); */
		setSize(20, 530);
		if(active != null) {
			active.setSize(980, 530);
			active.setLocation(20, 0);
		}
		//unHide.
	}
	
	public static void task_panel()
    {
        EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run() {
                JFrame taskFrame = new JFrame("Test");
                taskFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                taskFrame.setBounds(0, 0, 650, 400);
                JPanel percentPanel = new JPanel();
                percentPanel.setLayout(null);
                percentPanel.setBounds(0, 0, 650, 400);
                percentPanel.setOpaque(true);
                //frame.pack();
                taskFrame.setLocationByPlatform(true);
                taskFrame.setVisible(true);
                taskFrame.setResizable(false);
                
				JScrollPane scroller = new JScrollPane(percentPanel);
                scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        		taskFrame.add(scroller);
            }
        });
    }

	private void MNFframe() {
		// TODO Auto-generated method stub
		EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                JFrame mnfFrame = new JFrame();
                mnfFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                mnfFrame.setBounds(200, 20, 300, 300);
                JPanel mnew_Panel = new JPanel();
                mnew_Panel.setLayout(null);
                //mnew_Panel.setBounds(1300, 1300, 130, 130);
                mnew_Panel.setOpaque(true);
                mnfFrame.getContentPane().add(BorderLayout.CENTER, mnew_Panel);
                mnfFrame.setLocationByPlatform(true);
                mnfFrame.setVisible(true);
                mnfFrame.setResizable(false);
            }
        });
	}

	//TODO: Note to Mohan; Use BPart of Menu to display new windows ^_
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == task){
			task_panel();
		}
		if(e.getSource() == home){
			//always go back to the original one
		}
		if(e.getSource() == upload) {
			fc = new JFileChooser(LC_PATH);
	        int returnVal = fc.showOpenDialog(this);
	        
	        if(returnVal == JFileChooser.APPROVE_OPTION){
	        	File source = fc.getSelectedFile();
	        	File dest = new File("userInput/" + source.getName());
	        	try {
					Files.copy(source.toPath(), dest.toPath(), 
							StandardCopyOption.COPY_ATTRIBUTES, 
							StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        }
		}
		if(e.getSource() == newFolder){
			MNFframe();
		}
		if(e.getSource() == connect){
			//making sections.
		}
		if(e.getSource() == recent){
			//open "recent" data in part B
		}
		if(e.getSource() == trash){
			//open "Trash" data in part B
		}
		if(e.getSource() == hide){
			Hiding_class();
		}
	}
}
