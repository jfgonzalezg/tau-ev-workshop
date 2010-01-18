package pavUI;


import org.eclipse.swt.layout.FillLayout;


import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.SWT;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.events.KeyListener;

import com.cloudgarden.resource.SWTResourceManager;

import global.Consts;
import pavBallot.*;




/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class InitialGUI extends org.eclipse.swt.widgets.Composite {

	{
		//Register as a resource user - SWTResourceManager will
		//handle the obtaining and disposing of resources
		SWTResourceManager.registerResourceUser(this);
	}
	
	
	private static int ezer=1;
	private static int sw=1;
	private static int swStart=1;
	private static String userID;
	private Button startVoting;
	private Text idInfo;
	private Text header;
	private Button BExit;
	private static Shell shell = null;
	private static String voterID;
	
	/**
	* Auto-generated main method to display this 
	* org.eclipse.swt.widgets.Composite inside a new Shell.
	*/
	public static void runGUI() {
		while (sw>0){
		switch (ezer){
		case 1:	
			swStart = 1;
			showGUI();
			break;
		case 2:
	        GUI2.showGUI2();
	        break;
		case 3:
			GUI3.showGUI3();
			break;
		case 4:
			GUI4.showGUI4();
			break;
		case 5:
			GUI5.showGUI5();
		default:
			System.out.println("exiting");
		}
		}
		}
	         
	
	/**
	* Overriding checkSubclass allows this class to extend org.eclipse.swt.widgets.Composite
	*/	
	protected void checkSubclass() {
	}
	//public class BvoteHandler implements SelectionListener{
	//	public void widgidSelected()
	//}
	/**
	* Auto-generated method to display this 
	* org.eclipse.swt.widgets.Composite inside a new Shell.
	*/
	public static void showGUI() {
		sw = 0;
		Display display = Display.getDefault();
		shell = new Shell(display, SWT.DIALOG_TRIM | SWT.ON_TOP);
		
		InitialGUI inst = new InitialGUI(shell, SWT.ON_TOP);
		Point size = inst.getSize();
		shell.setLayout(new FillLayout());
		shell.setText("Pret-A-Voter");
		shell.layout();
		if(size.x == 0 && size.y == 0) {
			inst.pack();
			shell.pack();
		} else {
			Rectangle shellBounds = shell.computeTrim(0, 0, size.x, size.y);
			shell.setSize(shellBounds.width, shellBounds.height);
		}
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();

		}
		}
	

	public InitialGUI(org.eclipse.swt.widgets.Composite parent, int style) {
		super(parent, style);
		initGUI1();
	}
		

	
	private void numbersOnlyMessage(){
		MessageBox emptyName = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
		emptyName.setMessage("The ID must contain numbers only.");
		emptyName.setText("Bad Key Entered");
		emptyName.open();
		idInfo.setText(""); 
		
	}
	
	/**
	 * The procedure displays a message about empty ID
	 */
	private void shortIDMessage(){
		MessageBox emptyName = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
		emptyName.setMessage("Please enter your ID number");
		emptyName.setText("Blank ID number");
		emptyName.open();
		idInfo.setText(""); 
	}
	/**
	 * This procedure is called by:
	 * 1) KeyListener- in case [ENTER] was pressed by the user.
	 * 2) SelectionListener- in case the user pressed "Start Voting" button.
	 * The procedure will check that that ID is legal and will call the next GUI screen. 
	 */
	private void checkAndExecute(String IDuser){
    	boolean isLeg = true;
    	idInfo.setText(IDuser);
    	if ((IDuser.length() == 0)) {
    		isLeg= false;
    		shortIDMessage();
;
    	}else{
    	// Check that the user entered numbers only 
    	for (int i = 0; i < IDuser.length(); i++) {
            if (!Character.isDigit(IDuser.charAt(i))){
            	numbersOnlyMessage();
            	isLeg = false;
            	idInfo.setText("");
              	break;
            }
    	}
    	}
           	if (isLeg){ 
    		/// If Here Then Legal ID Was Entered.
    		System.out.println("Entered User ID Is:"+userID);
    		shell.close();
    		sw = 1;
    		ezer = 2;
    		voterID = IDuser;
       	}
	}

	private void initGUI1() {
		Listener listener0 = new Listener() {
		      public void handleEvent(Event event) {
		        if (event.widget == startVoting) {
		        	System.out.println("You clicked Strart Voting");
		        	userID = idInfo.getText();
		        	checkAndExecute(userID);
		        }else
		        if (event.widget == BExit){
		        	System.out.println("You clicked Exit");
		        	shell.close();
		        	sw = 0;
		        }
		      }
		    };
		    try{
		    	FormLayout thisLayout = new FormLayout();
				this.setLayout(thisLayout);
				this.setBackground(SWTResourceManager.getColor(223, 255, 255));
		FormData idInfoLData = new FormData();
		idInfoLData.left =  new FormAttachment(0, 1000, 92);
		idInfoLData.right =  new FormAttachment(1000, 1000, -92);
		idInfoLData.top =  new FormAttachment(0, 1000, 76);
		idInfoLData.width = 72;
		idInfoLData.height = 15;
		idInfo = new Text(this, SWT.MULTI | SWT.CENTER | SWT.WRAP | SWT.BORDER);
		idInfo.setLayoutData(idInfoLData);
		
		
		FormData headerLData = new FormData();
		headerLData.width = 212;
		headerLData.height = 26;
		headerLData.right =  new FormAttachment(1000, 1000, -26);
		headerLData.left =  new FormAttachment(0, 1000, 26);
				
		FormData startVotingLData = new FormData();
		startVotingLData.left =  new FormAttachment(0, 1000, 180);
		startVotingLData.right =  new FormAttachment(1000, 1000, -0);
		startVotingLData.top =  new FormAttachment(0, 1000, 167);
		startVotingLData.width = 74;
		startVotingLData.height = 25;
		
		FormData goOutLData = new FormData();
		goOutLData.left =  new FormAttachment(0, 1000, 6);
		goOutLData.top =  new FormAttachment(0, 1000, 167);
		goOutLData.width = 74;
		goOutLData.height = 25;
		 
		
				KeyListener kL1 = new KeyListener() {
					public void keyPressed(org.eclipse.swt.events.KeyEvent arg0) {
					}
					
					public void keyReleased(org.eclipse.swt.events.KeyEvent arg0) {
						// In case [ENTER] was pressed
						if (arg0.character == 13) {
							userID = idInfo.getText();
							int len = userID.length()-2;
							userID = userID.substring(0, len);
							checkAndExecute(userID);
						}
					}
				};
		
				idInfo.addKeyListener((org.eclipse.swt.events.KeyListener) kL1);
			
			{
				header = new Text(this, SWT.MULTI | SWT.CENTER | SWT.READ_ONLY | SWT.WRAP);
				header.setLayoutData(headerLData);
				header.setText("Please enter your ID:");
				header.setOrientation(SWT.HORIZONTAL);
				header.setFont(SWTResourceManager.getFont("Arial", 10, 1, false, false));
				header.setBackground(SWTResourceManager.getColor(223, 255, 255));
			}
			
			{
				startVoting = new Button(this, SWT.PUSH | SWT.FLAT | SWT.RIGHT | SWT.BORDER);
				startVoting.setText("Start Voting");
				startVoting.setAlignment(SWT.CENTER);
				startVoting.setLayoutData(startVotingLData);
				startVoting.setFont(SWTResourceManager.getFont("Arial", 8, 3, false, false));
				startVoting.addListener(SWT.Selection, listener0);
			}
			{
				BExit = new Button(this, SWT.PUSH | SWT.FLAT | SWT.RIGHT | SWT.BORDER);
				BExit.setText("Exit");
				BExit.setAlignment(SWT.CENTER);
				BExit.setLayoutData(goOutLData);
				BExit.setFont(SWTResourceManager.getFont("Arial", 8, 3, false, false));
				BExit.addListener(SWT.Selection, listener0);
			}
			this.layout();
			pack();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
      public static String getvoterID(){
    	  return voterID;
      }
     
      public static void setsw(int i){
    	  sw = i;
      }
      public static void setezer(int i){
    	  ezer = i;
      }
      public static int getswStart(){
    	  return swStart;
      }
      public static void setswStart(int i){
    	  swStart = i;
      }
}


 




