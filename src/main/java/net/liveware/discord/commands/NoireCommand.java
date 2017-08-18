package net.liveware.discord.commands;

import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class NoireCommand extends Command
{
    @Override
    public void onCommand(MessageReceivedEvent e, String[] args)
    {
    	String arg = args[1].toLowerCase();
    	User authour = e.getMessage().getAuthor();
    	String print = null;
    	String[] arrayNoire = {"I'm sorry Noire for all the swearing I've thrown at you, it's all a joke please forgive me!",
    			"Oh Noire forgive me please! I am nothing without you!",
    			"Almigthy Noire please forgive me!"};
    	String[] arraySalvo = {"Nah you good Salvo.",
    			"Piss off and ride a kangaroo!",
    			"Just go mate, won't do you any harm."};
    	String[] arrayKoa = {"I respect you for your love towards lesbian Koa.",
    			"You deserve all my respect.",
    			"I've sworn by my sword to you."};
    	String[] arrayNotNoire = {"What who are you to respect? Cunt",
    			"Not gonna respect you bastard.",
    			"F-U-C-K Y-O-U"};
    	int rndNoire = new Random().nextInt(arrayNoire.length);
    	int rndSalvo = new Random().nextInt(arraySalvo.length);
    	int rndKoa = new Random().nextInt(arrayKoa.length);
    	int rndNotNoire = new Random().nextInt(arrayNotNoire.length);
    	if(authour.getName().equals("NO1RE")) {
    		print = arrayNoire[rndNoire];
    	} else if(authour.getName().equals("SalvoClan")) {
    		print = arraySalvo[rndSalvo];
    	} else if(authour.getName().equals("Kuckakuma")) {
    		print = arrayKoa[rndKoa];
    	} else {
    		print = arrayNotNoire[rndNotNoire];
    	}
	    	switch(arg)
	        {
	            case "creator":
	            	sendMessage(e, print);
	            	break;
	            case "noire":
	            	sendMessage(e, print);
	            	break;
	            case "me":
	            	sendMessage(e, print);
	            	break;
	        }
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList("respect", "Respect");
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
