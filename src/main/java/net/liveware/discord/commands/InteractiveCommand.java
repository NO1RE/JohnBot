package net.liveware.discord.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.liveware.discord.chatterbot.ChatterBot;
import net.liveware.discord.chatterbot.ChatterBotFactory;
import net.liveware.discord.chatterbot.ChatterBotSession;
import net.liveware.discord.chatterbot.ChatterBotType;

public class InteractiveCommand extends Command {
	
	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		ChatterBotFactory factory = new ChatterBotFactory();
		try {
	        /*ChatterBot bot1 = factory.create(ChatterBotType.CLEVERBOT);
	        ChatterBotSession bot1session = bot1.createSession();*/
	        ChatterBot bot2 = factory.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477");
	        ChatterBotSession bot2session = bot2.createSession();
	        String[] arrayMessageFront = {"", "Fuck ", "Hell ", "Bloody ", "Cunt, ", "Bitch, ", "Fucking hell! ", "Fucking muppet ", "You donkey ", "You donut ", "You dumb shit "};
	        String[] arrayMessageBack = {"", " Fuck.", " Hell.", " Cunt.", " Bitch.", " Fuck you!", " You fuck!", " Fucking imbecile.", " You gay fruitcake.", " Bloody hell", " Fuck off!"};
        	int rndFront = new Random().nextInt(arrayMessageFront.length);
        	int rndBack = new Random().nextInt(arrayMessageBack.length);
        	User authour = e.getMessage().getAuthor();
        	String s = args[1];
	        while (true) {
	        	//sendMessage(e, bot1session.think(s));
	        	if(authour.getName().equals("NO1RE") || authour.getName().equals("Kuckakuma")) {
	        		sendMessage(e, bot2session.think(s));
	        	} else {
	        		sendMessage(e, arrayMessageFront[rndFront] + bot2session.think(s) + arrayMessageBack[rndBack]);
	        	}
	        	/*s = e.getMessage().getContent(); 
	        	if(s.equalsIgnoreCase("quit") 
	        			|| s.equalsIgnoreCase("bye") 
	        			|| s.equalsIgnoreCase("byee") 
	        			|| s.equalsIgnoreCase("byeee") 
	        			|| s.equalsIgnoreCase("bye john")
	        			|| s.equalsIgnoreCase("byee john")) {*/
	        		break;
	        	/*}*/
	        }
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("John",
				"@John Fitzgerald",
				"john",
				"john,",
				"John,",
				"Hey",
				"hey",
				"john?",
				"John?",
				"ei",
				"Ei",
				"sup",
				"Sup",
				"Hello",
				"hello",
				"morning",
				"Morning",
				"Heya",
				"heya",
				"Oy",
				"oy",
				"So",
				"so");
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public List<String> getUsageInstructions() {
		return null;
	}

}
