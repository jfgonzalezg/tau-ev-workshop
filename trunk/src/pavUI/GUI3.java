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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.SWT;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import pav.PAVShared;



public class GUI3 extends org.eclipse.swt.widgets.Composite {

	{
		//Register as a resource user - SWTResourceManager will
		//handle the obtaining and disposing of resources
		SWTResourceManager.registerResourceUser(this);
	}
	
	private Button Rchoise;
	private Label LTcand;
	private Label Ltenc;
    private Label Lplease;
    private Button Bconfirm;
    private Button Bback;
	private static Shell shell = null;
	private List list;
	private static int mone = 0;
			
	
	public static void showGUI3() {
		InitialGUI.setsw(0);
		Display display = Display.getDefault();
		//Shell
		shell = new Shell(display, SWT.DIALOG_TRIM | SWT.ON_TOP);
		shell.setText("Pret-A-Voter");
		GUI3 inst = new GUI3(shell, SWT.ON_TOP);
		Point size = inst.getSize();
		shell.setLayout(new FillLayout());
		shell.layout();

		if(size.x == 0 && size.y == 0) {
			inst.pack();
			shell.pack();
		}else{
		shell.setSize(430,650);
		shell.setLocation(100, 30);
		} 
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();

		}
		}
	
	public GUI3(org.eclipse.swt.widgets.Composite parent, int style) {
		super(parent, style);
		initGUI3();
	}

	private void initGUI3() {
	
    Listener listener2 = new Listener() {
      public void handleEvent(Event event) {
    	  //Case [Confirm] was pressed
        if (event.widget == Bconfirm) {
       	//Registering the vote
	        PAVShared.addCastVote(GUI2.getVote(), InitialGUI.getvoterID());
	        mone = mone + 1;//counting the voters
        	shell.close();
           	InitialGUI.setsw(1);
        	InitialGUI.setezer(4);
        	
        	
        } else 
        	//Case [Back to voting screen] was pressed
       	if (event.widget == Bback) {
           	shell.close();
           	InitialGUI.setsw(1);
           	InitialGUI.setezer(2);
         }
      }
    };
		try {
			FormLayout thisLayout = new FormLayout();
			this.setLayout(thisLayout);
			this.setFont(SWTResourceManager.getFont("Tahoma", 16, 3, false, false));
			this.setBackground(SWTResourceManager.getColor(223, 255, 255));
			{
				Bconfirm = new Button(this, SWT.PUSH | SWT.CENTER | SWT.FLAT | SWT.BORDER);
				FormData BconfirmLData = new FormData();
				BconfirmLData.left =  new FormAttachment(0, 1000, 310);
				BconfirmLData.top =  new FormAttachment(0, 1000, 540);
				BconfirmLData.width = 88;
				BconfirmLData.height = 47;
				Bconfirm.setLayoutData(BconfirmLData);
				Bconfirm.setText("Confirm");
				Bconfirm.setFont(SWTResourceManager.getFont("Arial", 8, 3, false, false));
				Bconfirm.setBackground(SWTResourceManager.getColor(255, 255, 255));
				Bconfirm.addListener(SWT.Selection, listener2);
			}
			{
				LTcand = new Label(this, SWT.NONE);
				FormData LTcandLData = new FormData();
				LTcandLData.left =  new FormAttachment(0, 1000, 12);
				LTcandLData.top =  new FormAttachment(0, 1000, 157);
				LTcandLData.width = 159;
				LTcandLData.height = 25;
				LTcand.setLayoutData(LTcandLData);
				LTcand.setText("Candidate Name");
				LTcand.setFont(SWTResourceManager.getFont("Arial", 11, 3, false, false));
				LTcand.setBackground(SWTResourceManager.getColor(223, 255, 255));
			}
			{
				Ltenc = new Label(this, SWT.NONE);
				FormData LtencLData = new FormData();
				LtencLData.left =  new FormAttachment(0, 1000, 209);
				LtencLData.top =  new FormAttachment(0, 1000, 157);
				LtencLData.width = 110;
				LtencLData.height = 25;
				Ltenc.setLayoutData(LtencLData);
				Ltenc.setText("Encryption");
				Ltenc.setFont(SWTResourceManager.getFont("Arial", 11, 3, false, false));
				Ltenc.setBackground(SWTResourceManager.getColor(223, 255, 255));
			}
			{
			    Bback = new Button(this, SWT.PUSH | SWT.CENTER | SWT.FLAT | SWT.BORDER);
				FormData BbackLData = new FormData();
				BbackLData.left =  new FormAttachment(0, 1000, 85);
				BbackLData.top =  new FormAttachment(0, 1000, 540);
				BbackLData.width = 160;
				BbackLData.height = 27;
				Bback.setLayoutData(BbackLData);
				Bback.setText("Back to Voting Screen");
				Bback.setFont(SWTResourceManager.getFont("Arial", 8, 3, false, false));
				Bback.setBackground(SWTResourceManager.getColor(255, 255, 255));
				Bback.addListener(SWT.Selection, listener2);
			}
			{
				Lplease = new Label(this, SWT.NONE);
				FormData LpleaseLData = new FormData();
				LpleaseLData.left =  new FormAttachment(0, 1000, 12);
				LpleaseLData.top =  new FormAttachment(0, 1000, 65);
				LpleaseLData.width = 359;
				LpleaseLData.height = 32;
				Lplease.setLayoutData(LpleaseLData);
				Lplease.setText("Please confirm your selection:");
				Lplease.setFont(SWTResourceManager.getFont("Tahoma", 14, 1, false, false));
				Lplease.setBackground(SWTResourceManager.getColor(223, 255, 255));
			}
            {
				FormData RchoiseLData = new FormData();
				RchoiseLData.left =  new FormAttachment(0, 1000, 10);
				RchoiseLData.top =  new FormAttachment(0, 1000, 216);
				RchoiseLData.width = 102;
				RchoiseLData.height = 21;
				Rchoise = new Button(this, SWT.RADIO | SWT.LEFT);
				Rchoise.setText(GUI2.getTheChosen());
				Rchoise.setLayoutData(RchoiseLData);
				Rchoise.setBackground(SWTResourceManager.getColor(223, 255, 255));
           	}
//Printing the encryption
           	{
    			FormData list1LData = new FormData();
    			list1LData.left =  new FormAttachment(0, 1000, 113);
    			list1LData.top =  new FormAttachment(0, 1000, 216);
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
	public static int getmone(){
		return mone;
	}
}
