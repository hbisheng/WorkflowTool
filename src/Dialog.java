import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class Dialog{
	
	
	
	JFrame backgroundFrame;
	JButton button1;
	JTextArea logTextArea;
	JScrollPane jScrollPanel;
	
	JPanel inputPanel;
	JButton inputB;
	JLabel inputL1;
	JLabel inputL2;
	JTextField inputTf1 = new JTextField();
	JPasswordField inputTf2 = new JPasswordField();
	JTextField inputLabel = null;
	private Button inputButton;
    
	public void go(){
		backgroundFrame = new JFrame();
		backgroundFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		backgroundFrame.setTitle("Work flow");
		backgroundFrame.setLayout(new BorderLayout());
		
		logTextArea = new JTextArea("name:name\npassword:password\n");
		logTextArea.setEditable(false);
		jScrollPanel = new JScrollPane(logTextArea);
		
		backgroundFrame.getContentPane().add(jScrollPanel,"Center");
		
		inputPanel = new JPanel();
		inputPanel.setLayout(new GridLayout(1,2));
		
		inputLabel = new JTextField();
		inputButton = new Button("Commit");
		
		inputPanel.add(inputLabel);
		inputPanel.add(inputButton);
		
		backgroundFrame.getContentPane().add(inputPanel, "South");
		
		inputButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				logTextArea.append(inputLabel.getText()+"\n");
			}
		});
		inputPanel.setSize(280, 110);
		backgroundFrame.setSize(280, 250);
		backgroundFrame.setVisible(true);
	}
	
	public static void main(String[] args){		
		new Dialog().go();
	}
}
