import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Todo:
 * 
 * work classification, pause and resume
 * font style customization, font display
 * 
 * @author tsinghuabci02
 */
public class WorkflowRecorder{
	
	private JFrame backgroundFrame = null;
	private int BACKGROUND_WIDTH = 350;
	private int BACKGROUND_HEIGHT = 350;
	private float FONT_SIZE = 30.0f;
	
	private JScrollPane jScrollPanel = null;
	private JTextArea logTextArea = null;
	
	private JTextArea inputArea = null;
    
	int logTimes = 0;
	long firstTime = -1;
	long lastTime = -1;
	
	String FILE_NAME = "log.txt";
	
	public void build() throws FontFormatException, IOException{
		
		Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File("Fonts\\YaHei Consolas Hybrid.ttf")).deriveFont(15f);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("Fonts\\YaHei Consolas Hybrid.ttf")));
		
		logTextArea = new JTextArea();
		logTextArea.setEditable(false);
		//logTextArea.setFont(logTextArea.getFont().deriveFont(15f));
		logTextArea.setLineWrap(true);
		logTextArea.setFont(customFont);
		jScrollPanel = new JScrollPane(logTextArea);
		
		inputArea = new JTextArea("", 3, 2);
		inputArea.setLineWrap(true);
        inputArea.setFont(inputArea.getFont().deriveFont(20f));
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
			private static final long serialVersionUID = -486793975637083782L;
			@Override
	        public void actionPerformed(ActionEvent e) {
	        	if(logTimes == 0) {
	        		firstTime = System.currentTimeMillis();
	        	} else {
	        		lastTime = System.currentTimeMillis();
	        	}
	        	if(inputArea.getText().length() == 0)
	        		return;
	        	
	        	logTimes++;
	        	String appendText = sdf.format(new Date())+inputArea.getText()+"\n";
	        	
	        	writeToFile(FILE_NAME, appendText);
	        	logTextArea.append(appendText);
	        	//logTextArea.insert(sdf.format(new Date())+inputArea.getText()+"\n", 0);
	        	//logTextArea.setCaretPosition(0);
	        	System.out.println(backgroundFrame.getSize().toString());
	        	inputArea.setText("");
	        }
	    });
	    
		backgroundFrame = new JFrame();
		//backgroundFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		backgroundFrame.setTitle("Work flow");
		backgroundFrame.setLayout(new BorderLayout());
		backgroundFrame.getContentPane().add(jScrollPanel,"Center");
		backgroundFrame.getContentPane().add(inputArea, "South");
		backgroundFrame.setSize(BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
		backgroundFrame.setVisible(true);
		
		backgroundFrame.addWindowListener(new java.awt.event.WindowAdapter() {
	        public void windowClosing(WindowEvent winEvt) {
	        	if(logTimes < 2) {
	        		System.exit(0);
	        	}
	        	writeToFile(FILE_NAME, "\n");				
	        }
	    });
		
		// Set window positions
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - backgroundFrame.getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - backgroundFrame.getHeight()) / 2);
	    backgroundFrame.setLocation(x, y);
	    
	    inputArea.requestFocus();
	}
	
	protected void writeToFile(String file_name, String text) {
		FileOutputStream os = null;
		OutputStreamWriter or = null;
		BufferedWriter on = null;
		try {
			os = new FileOutputStream(file_name, true);
			or = new OutputStreamWriter(os);
			on = new BufferedWriter(or);
			on.write(text);
			on.flush();
			
			on.close();
			or.close();
			os.close();
		} catch (IOException error) {
			error.printStackTrace();
		}	
	}

	public static void main(String[] args) throws FontFormatException, IOException{		
		new WorkflowRecorder().build();
	}
}
