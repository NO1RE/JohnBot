package net.liveware.discord.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.Arrays;
import java.util.List;

public class GeneralCommand extends Command
{
    @Override
    public void onCommand(MessageReceivedEvent e, String[] args)
    {
    	for (int i = 1; i < args.length; i++)
        {
	    	String arg = args[i].substring(0).toLowerCase();
	    	switch(arg)
	        {
	            case "disapprove":
	            	sendMessage(e, "ಠ_ಠ");
	            	break;
	            case "fap":
	            	sendMessage(e, "John F. Kenedy is watching you fapping");
	            	break;
	            case "lenny":
	            	sendMessage(e, "( ͡° ͜ʖ ͡°)");
	            	break;
	            case "love":
	            	sendMessage(e, "John F. Kenedy is lubing out those stick");
	            	break;
	            case "god":
	            	sendMessage(e, "Nothing lmao");
	            	break;
	        }
        }
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList(".general");
    }

    @Override
    public String getDescription()
    {
        return "General command apparently";
    }

    @Override
    public String getName()
    {
        return "General command apparently";
    }

    @Override
    public List<String> getUsageInstructions()
    {
        return null;
    }
}
