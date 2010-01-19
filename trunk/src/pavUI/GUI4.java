package pavUI;
import com.cloudgarden.resource.SWTResourceManager;

import org.eclipse.swt.layout.FillLayout;

import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.SWT;


import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import pav.PAVShared;

public class GUI4 extends org.eclipse.swt.widgets.Composite {
	{
		//Register as a resource user - SWTResourceManager will
		//handle the obtaining and disposing of resources
		SWTResourceManager.registerResourceUser(this);
	}
	
	private  static int numVote = global.Consts.VOTERS_AMOUNT;
	private Label Ltenc;
  	private Button Bdone;
	private Label Lhear;
	private Label Lyour;
	private static Shell shell = null;
	private List list;
	
		
	/**
	* Auto-generated main method to display this 
	* org.eclipse.swt.widgets.Composite inside a new Shell.
	*/
	public static void main(String[] args) {
		showGUI4();
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
	public static void showGUI4() {
		InitialGUI.setsw(0);
		Display display = Display.getDefault();
		//Shell
		shell = new Shell(display, SWT.DIALOG_TRIM | SWT.ON_TOP);
		shell.setText("Pert-A-Voter");
		GUI4 inst = new GUI4(shell, SWT.ON_TOP);
		Point size = inst.getSize();
		shell.setLayout(new FillLayout());
		shell.layout();
		if(size.x == 0 && size.y == 0) {
			inst.pack();
			shell.pack();
		} else {
		shell.setSize(430,650);
		}
		shell.setLocation(100, 30);

		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		}
	

	public GUI4(org.eclipse.swt.widgets.Composite parent, int style) {
		super(parent, style);
		initGUI4();
	}
    // The number of the voters has reached the max number
	private void maxNumVoters(){
		MessageBox emptyName = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
		emptyName.setMessage("The number of voters has reached the max num");
		emptyName.setText("MaxNumVoters");
		emptyName.open();		
	}

	private void initGUI4() {
		
	    Listener listener3 = new Listener() {
	      public void handleEvent(Event event) {
	    	  //[Done] was pressed
	        if (event.widget == Bdone) {
	        	System.out.println("You clicked done");
	        	if (GUI3.getmone() >= numVote){
	        		maxNumVoters();
	        		shell.close();
	           	    InitialGUI.setsw(0);
	        	}else{
	        		shell.close();	
	        		InitialGUI.setsw(1);
	        		InitialGUI.setezer(1);
	        	}
	        } 

	      }
	    };    
			try {
				FormLayout thisLayout = new FormLayout();
				this.setLayout(thisLayout);
				this.setBackground(SWTResourceManager.getColor(223, 255, 255));

				{
					Bdone = new Button(this, SWT.PUSH | SWT.CENTER | SWT.FLAT | SWT.BORDER);
					FormData BdoneLData = new FormData();
					BdoneLData.left =  new FormAttachment(0, 1000, 300);
					BdoneLData.top =  new FormAttachment(0, 1000, 510);
					BdoneLData.width = 79;
					BdoneLData.height = 52;
					Bdone.setLayoutData(BdoneLData);
					Bdone.setText("Done");
					Bdone.setFont(SWTResourceManager.getFont("Arial", 8, 3, false, false));
					Bdone.setBackground(SWTResourceManager.getColor(255, 255, 255));
					Bdone.addListener(SWT.Selection, listener3);
				}
				{
					Lyour = new Label(this, SWT.NONE);
					FormData LyourLData = new FormData();
					LyourLData.left =  new FormAttachment(0, 1000, 0);
					LyourLData.top =  new FormAttachment(0, 1000, 72);
					LyourLData.width = 383;
					LyourLData.height = 35;
					Lyour.setLayoutData(LyourLData);
					Lyour.setText("Your vote has been registered successfully!");
					Lyour.setFont(SWTResourceManager.getFont("Arial", 11, 3, false, false));
					Lyour.setBackground(SWTResourceManager.getColor(223, 255, 255));
				}
				{
					Lhear = new Label(this, SWT.NONE);
					FormData LhearLData = new FormData();
					LhearLData.left =  new FormAttachment(0, 1000, 42);
					LhearLData.top =  new FormAttachment(0, 1000, 127);
					LhearLData.width = 200;
					LhearLData.height = 22;
					Lhear.setLayoutData(LhearLData);
					Lhear.setText("Here is your receipt:");
					Lhear.setFont(SWTResourceManager.getFont("Arial", 11, 3, false, false));
					Lhear.setBackground(SWTResourceManager.getColor(223, 255, 255));
				}
				{
					Ltenc = new Label(this, SWT.NONE);
					FormData LtencLData = new FormData();
					LtencLData.left =  new FormAttachment(0, 1000, 74);
					LtencLData.top =  new FormAttachment(0, 1000, 222);
					LtencLData.width = 107;
					LtencLData.height = 26;
					Ltenc.setLayoutData(LtencLData);
					Ltenc.setText("Encryption:");
					Ltenc.setFont(SWTResourceManager.getFont("Arial", 10, 3, false, false));
					Ltenc.setBackground(SWTResourceManager.getColor(223, 255, 255));
				}
           //Printing the encryption				
				{
	    			FormData list1LData = new FormData();
	    			list1LData.left =  new FormAttachment(0, 1000, 65);
	    			list1LData.top =  new FormAttachment(0, 1000, 260);
	    			list1LData.width = 290;
	    			list1LData.height = 190;
	    			list = new List(this, SWT.NONE);
	    			list.setLayoutData(list1LData);			
	    			}
          			int mana = (GUI2.getstr().length())/35;
	    			for (int j=0;j<mana;j++  ){
	    				 list.add(GUI2.getstr().substring(j*35,(j+1)*35));    
	    				 }
	    				 list.add(GUI2.getstr().substring(mana*35));
				
	            
	            
			this.layout();
				pack();
		
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
}
	
