package net.liveware.discord.commands;

import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.Arrays;
import java.util.List;

public class GeneralCommand extends Command
{
    @Override
    public void onCommand(MessageReceivedEvent e, String[] args)
    {
    	String arg = args[1].toLowerCase();
    	List<User> mentions = e.getMessage().getMentionedUsers();
    	String printMention = null;
    	if(mentions.isEmpty()) {
    		printMention = "you";
    	}else {
    		printMention = e.getMessage().getMentionedUsers().get(0).getAsMention();
    	}
	    	switch(arg)
	        {
	            case "disapprove":
	            	sendMessage(e, "ಠ_ಠ");
	            	break;
	            case "fap":
	            	sendMessage(e, "John F. Kenedy is watching "+ printMention +" fapping");
	            	break;
	            case "lenny":
	            	sendMessage(e, "( ͡° ͜ʖ ͡°)");
	            	break;
	            case "love":
	            	sendMessage(e, "John F. Kenedy is lubing "+ printMention +" stick");
	            	break;
	            case "noire":
	            	sendMessage(e, "Noire is making " + printMention +" turns gay");
	            	break;
	            case "salvo":
	            	sendMessage(e, "Salvo will punch "+ printMention +" to mars");
	            	break;
	            case "heretic":
	            	sendMessage(e, printMention + " is now registered as a heretic. Burn to hell!");
	            	break;
	            case "faggot":
	            	sendMessage(e, printMention + " is a massive faggot");
	            	break;
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
