import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Todo:
 * Inputarea height bigger
 * 
 * file IO and classification
 * font size and style customization
 * 
 * Save work status, do classification
 * 
 * @author tsinghuabci02
 */
public class WorkflowRecorder{
	
	private JFrame backgroundFrame = null;
	private int BACKGROUND_WIDTH = 600;
	private int BACKGROUND_HEIGHT = 800;
	private float FONT_SIZE = 30.0f;
	
	private JScrollPane jScrollPanel = null;
	private JTextArea logTextArea = null;
	
	private JTextArea inputArea = null;
    
	public void build() throws FontFormatException, IOException{
		
		Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File("Fonts\\YaHei Consolas Hybrid.ttf")).deriveFont(20f);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("Fonts\\YaHei Consolas Hybrid.ttf")));
		
		logTextArea = new JTextArea();
		logTextArea.setEditable(false);
		logTextArea.setFont(logTextArea.getFont().deriveFont(FONT_SIZE));
		logTextArea.setLineWrap(true);
		logTextArea.setFont(customFont);
		jScrollPanel = new JScrollPane(logTextArea);
		
		inputArea = new JTextArea("");
		inputArea.setLineWrap(true);
        inputArea.setFont(inputArea.getFont().deriveFont(30f));
        inputArea.setSize(2, 1);
        inputArea.setBackground(Color.LIGHT_GRAY);
        
        SimpleDateFormat sdf = new SimpleDateFormat("[yyyy-MM-dd kk:mm:ss]");
        
        // Set key mapping
        InputMap input = inputArea.getInputMap();
	    KeyStroke enter = KeyStroke.getKeyStroke("ENTER");
	    KeyStroke shiftEnter = KeyStroke.getKeyStroke("ctrl ENTER");
	    input.put(shiftEnter, "insert-break");  // input.get(enter)) = "insert-break"
	    input.put(enter, "text-submit");
	    ActionMap actions = inputArea.getActionMap();
	    actions.put("text-submit", new AbstractAction() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	logTextArea.append(sdf.format(new Date())+inputArea.getText()+"\n");
				inputArea.setText("");
	        }
	    });
	    
		backgroundFrame = new JFrame();
		backgroundFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		backgroundFrame.setTitle("Work flow");
		backgroundFrame.setLayout(new BorderLayout());
		backgroundFrame.getContentPane().add(jScrollPanel,"Center");
		backgroundFrame.getContentPane().add(inputArea, "South");
		backgroundFrame.setSize(BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
		backgroundFrame.setVisible(true);
		
		// Set window positions
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - backgroundFrame.getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - backgroundFrame.getHeight()) / 2);
	    backgroundFrame.setLocation(x, y);
	    
	    inputArea.requestFocus();
	}
	
	public static void main(String[] args) throws FontFormatException, IOException{		
		new WorkflowRecorder().build();
	}
}
