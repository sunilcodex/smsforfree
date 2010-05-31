package it.rainbowbreeze.smsforfree.domain;

/**
 * Define a command the provider can implement
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class SmsServiceCommand
{
	//---------- Ctors

	public SmsServiceCommand(
			int commandId,
			String commandDescription,
			int commandOrder)
	{
		this(commandId, commandDescription, commandOrder, NO_ICON);
	}

	public SmsServiceCommand(
			int commandId,
			String commandDescription,
			int commandOrder,
			int commandIcon)
	{
		mCommandId = commandId;
		mCommandDescription = commandDescription;
		mCommandOrder = commandOrder;
		mCommandIcon = commandIcon;
	}
	
	
	
	//---------- Private fields
	public static final int NO_ICON = -1;


	
	
	
	//---------- Public properties
	protected int mCommandId;
	public int getCommandId()
	{ return mCommandId; }

	protected String mCommandDescription;
	public String getCommandDescription()
	{ return mCommandDescription; }

	protected int mCommandOrder;
	public int getCommandOrder()
	{ return mCommandOrder; }

	protected int mCommandIcon;
	public int getCommandIcon()
	{ return mCommandIcon; }


	
	//---------- Public methods
	public boolean hasIcon()
	{ return NO_ICON != mCommandIcon; }
	
	
	
	//---------- Private methods

}
