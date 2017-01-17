package org.wilson.telegram.templates;

public class EventMenu {

	public static final String MENUTEXT  = "<< Return to Menu";
	public static final String MENUDATA  = "Return";
	public static final String MENUTITLE = "<strong>Events Menu</strong>";
	public static final String MENUSEND  = "SendMessage";
	public static final String MENUDESCRIPTION = "<i>You can manage all the events you've hosted here</i>" + System.getProperty("line.separator");
	
	public static final String MENUINTRO = MENUTITLE 
			+ System.getProperty("line.separator") 
			+ System.getProperty("line.separator") 
			+ MENUDESCRIPTION;
	
}
