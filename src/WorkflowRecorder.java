import javax.swing.*;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TODO:
 * 1. (Done, using Alt+Shift+W) A boss key, call it and hide it easily. 
 * 2. Extended features like timers(record how much time at work) and alarms(<rest> and wake me up in 5 minutes)
 * 3. Shortcut key like eclipse, convenient for <interrupt>, <flow> and all kinds of labels. 
 * (Write a compiler for it to recognize labels?)
 * 4. Font style customization, font display
 * 5. Interrupt mechanism, nested interrupt. With marks, it would be better for me to classify events.
 * 6. Work classification as different files, and thus pause and resume. One important feature is to tell me what I'm doing.
 * 7. (Done, using Ctrl+Z) Undo function. 
 * 8. A beautiful UI. Modern and fashion.
 * 9. Some text to show in the status bar when needed. Interactions like a game. Gold.(Accumulate when your work time increase)
 * 10. Format output, and thus easier for future analysis.
 * 11. Redo function? You need to maintain an input path.
 * BUG:
 * 1. (Solved?)Sometimes the frame was not brought to the front.
 * @author tsinghuabci02
 */
public class WorkflowRecorder{
	
	private JFrame backgroundFrame = null;
	private int BACKGROUND_WIDTH = 450;
	private int BACKGROUND_HEIGHT = 500;
	private float FONT_SIZE = 30.0f;
	
	private JScrollPane jScrollPanel = null;
	//private JScrollBar jScrollPanelVerticalScrollBar = null;
	
	private JTextArea logTextArea = null;
	
	private JTextArea inputArea = null;
    
	int logTimes = 0;
	long firstTime = -1;
	long lastTime = -1;
	
	String FILE_NAME = "log.txt";
	
	private SimpleDateFormat dateStringFormat = new SimpleDateFormat("yyyy.MM.dd");;
	boolean getFocus = true;
	
	public void build() throws FontFormatException, IOException{
		
		Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File("Fonts\\YaHei Consolas Hybrid.ttf")).deriveFont(18f);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("Fonts\\YaHei Consolas Hybrid.ttf")));
		
		logTextArea = new JTextArea();
		logTextArea.setEditable(false);
		//logTextArea.setFont(logTextArea.getFont().deriveFont(15f));
		logTextArea.setLineWrap(true);
		logTextArea.setFont(customFont);
		logTextArea.setText(readFile(getFileName()));
		jScrollPanel = new JScrollPane(logTextArea);
		
		//jScrollPanelVerticalScrollBar = jScrollPanel.getVerticalScrollBar();
		
		inputArea = new JTextArea("", 3, 2);
		inputArea.setLineWrap(true);
        inputArea.setFont(inputArea.getFont().deriveFont(20f));
        inputArea.setSize(2, 1);
        inputArea.setBackground(Color.LIGHT_GRAY);
        
        
        
        SimpleDateFormat sdf = new SimpleDateFormat("[yyyy-MM-dd kk:mm:ss]");
        // Set key mapping
        InputMap input = inputArea.getInputMap();
	    KeyStroke enter = KeyStroke.getKeyStroke("ENTER");
	    input.put(enter, "text-submit");
	    
	    KeyStroke shiftEnter = KeyStroke.getKeyStroke("ctrl ENTER");
	    input.put(shiftEnter, "insert-break");  // input.get(enter)) = "insert-break"
	    
	    KeyStroke crtlZ = KeyStroke.getKeyStroke("ctrl Z");
	    input.put(crtlZ, "deleteLastLine");
	    
	    KeyStroke crtlY = KeyStroke.getKeyStroke("ctrl Y");
	    input.put(crtlY, "reverseLastLine");
	    
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
	        	String appendText = sdf.format(new Date())+"\n"+inputArea.getText()+"\n";      	
	        	writeToFile(getFileName(), appendText);
	        	logTextArea.append(appendText);
	        	logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
	        	// Scroll to bottom
	        	//jScrollPanelVerticalScrollBar.setValue( jScrollPanelVerticalScrollBar.getMaximum() );
	        	//logTextArea.insert(sdf.format(new Date())+inputArea.getText()+"\n", 0);
	        	//logTextArea.setCaretPosition(0);
	        	//System.out.println(backgroundFrame.getSize().toString());
	        	
	        	inputArea.setText("");
	        }
	    });
	    
	    actions.put("deleteLastLine", new AbstractAction() {
			@Override
	        public void actionPerformed(ActionEvent e) {
	        	deleteLastLineInFile(getFileName());
	        	logTextArea.setText(readFile(getFileName()));
	        }
	    });
	    
	    actions.put("reverseLastLine", new AbstractAction() {
			@Override
	        public void actionPerformed(ActionEvent e) {
	        	
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
	        	writeToFile(getFileName(), "\n");				
	        }
	    });
		
	    /**
	     *  These lines must be put after the construction of backgroundFrame
	     */
	    logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
	    inputArea.requestFocus();
	    
	    /**
	     * Unmovable and unresizable
	     */
	    backgroundFrame.setResizable(false);
	    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - backgroundFrame.getWidth()))/2;
	    int y = (int) ((dimension.getHeight() - backgroundFrame.getHeight()) - 45)/2;
	    backgroundFrame.setLocation(x, y);
	    backgroundFrame.addComponentListener(new ComponentAdapter() {
	         public void componentMoved(ComponentEvent e) {
	        	 backgroundFrame.setLocation(x,y);
	         }
	      });
	    
	    
	    // Shortcut
	    JIntellitype.getInstance().registerHotKey(1, JIntellitype.MOD_ALT+JIntellitype.MOD_SHIFT, 'W');  
	    
	    JIntellitype.getInstance().addHotKeyListener(new HotkeyListener() {
	        @Override
	        public void onHotKey(int markCode) {
	        	if(getFocus) {
	        		backgroundFrame.setVisible(false);
	        	} else {
	        		backgroundFrame.setVisible(true);
	        		backgroundFrame.toFront();
	        		inputArea.requestFocus();
	        	}
	        	getFocus = !getFocus;
	        }
	      }); 
	}
	
	
	protected String getFileName() {
		// Be ready for date change
		return dateStringFormat.format(new Date())+".txt";
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

	public static void deleteLastLineInFile(String fileName) {
		StringBuilder fileWithoutLastLine = new StringBuilder();
		
		FileInputStream is;
		InputStreamReader ir;
		BufferedReader in;
		
		FileOutputStream os = null;
		OutputStreamWriter or = null;
		BufferedWriter on = null;
		
		try {
			// Read
			is = new FileInputStream(fileName);
			ir = new InputStreamReader(is);
			in = new BufferedReader(ir);
			
			String s1 = null;
			String s2 = null;
			while( (s2 = in.readLine()) != null ) {	
				if(s1 != null && s2 != null) {
					fileWithoutLastLine.append(s1+"\n");
				}
				s1 = s2;
			}
			in.close();
		
			//Write
			try {
				os = new FileOutputStream(fileName, false);
				or = new OutputStreamWriter(os);
				on = new BufferedWriter(or);
				on.write(fileWithoutLastLine.toString());
				on.flush();
				
				on.close();
				or.close();
				os.close();
			} catch (IOException error) {
				error.printStackTrace();
			}
		}
		catch(Exception e)
		{
			System.out.println("Exception!");
		}
		
	}
	
	public static String readFile(String fileName) //, String charSet)
	{
		StringBuilder wholefile = new StringBuilder();
		String s;
		FileInputStream is;
		InputStreamReader ir;
		BufferedReader in;
		try {
			is = new FileInputStream(fileName);
			ir = new InputStreamReader(is);//, charSet);
			in = new BufferedReader(ir);
			while((s=in.readLine())!= null) {	
				wholefile.append(s+"\n");
			}
			in.close();
		}
		catch(Exception e)
		{
			System.out.println("Exception!");
		}
		return wholefile.toString();
	}
	
	public static void main(String[] args) throws FontFormatException, IOException{		
		new WorkflowRecorder().build();
	}
}
