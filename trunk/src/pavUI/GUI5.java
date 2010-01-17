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
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.SWT;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;


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
public class GUI5 extends org.eclipse.swt.widgets.Composite {

	{
		//Register as a resource user - SWTResourceManager will
		//handle the obtaining and disposing of resources
		SWTResourceManager.registerResourceUser(this);
	}
	
	
	private Button Rchoise;
	
	private Label LTcand;
	private Label Ltenc;
    private Label Lplease;    
	private static String TheChoisen = "aaaaa";
	
	private Button Bnew;
	private Button Bbac;
	private static Shell shell = null;
	private static String str;
	private List list;
 
	
		/**
	* Auto-generated main method to display this 
	* org.eclipse.swt.widgets.Composite inside a new Shell.
	*/
	public static void main(String[] args) {
			showGUI5();
		}
	
	/**
	* Auto-generated method to display this 
	* org.eclipse.swt.widgets.Composite inside a new Shell.
	*/
	public static void showGUI5() {
		InitialGUI.setsw(0);
		Display display = Display.getDefault();
		shell = new Shell(display, SWT.DIALOG_TRIM | SWT.ON_TOP);
		
		GUI5 inst = new GUI5(shell, SWT.NULL);
		Point size = inst.getSize();
		shell.setLayout(new FillLayout());
		shell.layout();
		if(size.x == 0 && size.y == 0) {
			inst.pack();
			shell.pack();
		} else {
			Rectangle shellBounds = shell.computeTrim(0, 0, size.x, size.y);
			shell.setSize(450,650);
		}
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();

		}
		}
	

	public GUI5(org.eclipse.swt.widgets.Composite parent, int style) {
		super(parent, style);
		initGUI5();
	}

	
	
	private void initGUI5() {
			
	
    Listener listener5 = new Listener() {
      public void handleEvent(Event event) {
        if (event.widget == Bnew) {
        	System.out.println("You clicked New Ballot");
        	shell.close();
        	InitialGUI.setsw(1);
        	InitialGUI.setezer(1);
            }
//          else 
//       	if (event.widget == Bbac) {
//            	System.out.println(" Printing...");
//            	shell.close();
//            	InitialGUI.setsw(1);
//            	InitialGUI.setezer(1);
//            	
//        }
      }
    };
		try {
			FormLayout thisLayout = new FormLayout();
			this.setLayout(thisLayout);
			this.setBackground(SWTResourceManager.getColor(248, 231, 231));
			{
				
				Bnew = new Button(this, SWT.PUSH | SWT.CENTER | SWT.FLAT | SWT.BORDER);
				FormData BnewLData = new FormData();
				BnewLData.left =  new FormAttachment(0, 1000, 260);
				BnewLData.top =  new FormAttachment(0, 1000, 540);
				BnewLData.width = 107;
				BnewLData.height = 50;
				Bnew.setLayoutData(BnewLData);
				Bnew.setText("New Ballot");
				Bnew.setFont(SWTResourceManager.getFont("Arial", 8, 3, false, false));
				Bnew.setBackground(SWTResourceManager.getColor(255, 255, 255));
				Bnew.addListener(SWT.Selection, listener5);
			}
//			{
//				Bbac = new Button(this, SWT.PUSH | SWT.CENTER | SWT.FLAT | SWT.BORDER);
//				FormData BbacLData = new FormData();
//				BbacLData.left =  new FormAttachment(0, 1000, 88);
//				BbacLData.top =  new FormAttachment(0, 1000, 510);
//				BbacLData.width = 150;
//				BbacLData.height = 28;
//				Bbac.setLayoutData(BbacLData);
//				Bbac.setText("Print Ballot and Seed");
//				Bbac.setFont(SWTResourceManager.getFont("Arial", 8, 3, false, false));
//				Bbac.setBackground(SWTResourceManager.getColor(255, 255, 255));
//				Bbac.addListener(SWT.Selection, listener5);
//			}

			{
				LTcand = new Label(this, SWT.NONE);
				FormData LTcandLData = new FormData();
				LTcandLData.left =  new FormAttachment(0, 1000, 12);
				LTcandLData.top =  new FormAttachment(0, 1000, 235);
				LTcandLData.width = 126;
				LTcandLData.height = 17;
				LTcand.setLayoutData(LTcandLData);
				LTcand.setText("Candidate Name");
				LTcand.setFont(SWTResourceManager.getFont("Arial", 9, 3, false, false));
				LTcand.setBackground(SWTResourceManager.getColor(248, 231, 231));
			}
			{
				Ltenc = new Label(this, SWT.NONE);
				FormData LtencLData = new FormData();
				LtencLData.left =  new FormAttachment(0, 1000, 167);
				LtencLData.top =  new FormAttachment(0, 1000, 235);
				LtencLData.width = 102;
				LtencLData.height = 17;
				Ltenc.setLayoutData(LtencLData);
				Ltenc.setText("Encryption");
				Ltenc.setFont(SWTResourceManager.getFont("Arial", 9, 3, false, false));
				Ltenc.setBackground(SWTResourceManager.getColor(248, 231, 231));
			}
						
			{
				Lplease = new Label(this, SWT.NONE);
				FormData LpleaseLData = new FormData();
				LpleaseLData.left =  new FormAttachment(0, 1000, 12);
				LpleaseLData.top =  new FormAttachment(0, 1000, 44);
				LpleaseLData.width = 367;
				LpleaseLData.height = 17;
				Lplease.setLayoutData(LpleaseLData);
				Lplease.setText("The random seed used to encrypt your vote is:");
				Lplease.setFont(SWTResourceManager.getFont("Arial", 10, 3, false, false));
				Lplease.setBackground(SWTResourceManager.getColor(248, 231, 231));
			}
			
			{
				FormData RchoiseLData = new FormData();
				RchoiseLData.left =  new FormAttachment(0, 1000, 10);
				RchoiseLData.top =  new FormAttachment(0, 1000, 285);
				RchoiseLData.width = 120;
				RchoiseLData.height = 20;
				Rchoise = new Button(this, SWT.RADIO | SWT.LEFT);
				Rchoise.setText(GUI2.getTheChoisen());
				Rchoise.setLayoutData(RchoiseLData);
				Rchoise.setBackground(SWTResourceManager.getColor(248, 231, 231));
			}			
			
			FormData list1LData = new FormData();
			list1LData.left =  new FormAttachment(0, 1000, 135);
			list1LData.top =  new FormAttachment(0, 1000, 285);
			list1LData.width = 300;
			list1LData.height = 220;
			list = new List(this, SWT.NONE);
			list.setLayoutData(list1LData);
			
			int mana = (GUI2.str.length())/35;
			for (int j=0;j<mana;j++  ){
				 list.add(GUI2.str.substring(j*35,(j+1)*35));    
				 }
				 list.add(GUI2.str.substring(mana*35));
	    	
             
           {
           FormData listSLData = new FormData();
			listSLData.left =  new FormAttachment(0, 1000, 90);
			listSLData.top =  new FormAttachment(0, 1000, 78);
			listSLData.width = 270;
			listSLData.height = 100;
			List listS = new List(this, SWT.NONE);
			listS.setLayoutData(listSLData);
			
          str = GUI2.getVote().getSeedInBase64();   
		int manaS = (str.length())/30;
	    

	     
	    for (int i=0;i<manaS;i++  ){
	    listS.add(str.substring(i*30,(i+1)*30));    
	    }
	    listS.add(str.substring(manaS*30));
        }
		this.layout();
			pack();
		
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
