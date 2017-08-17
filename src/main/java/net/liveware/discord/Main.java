/**
 *     Copyright 2015-2016 Austin Keener
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.liveware.discord;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.liveware.discord.bridge.IrcConnectInfo;
import net.liveware.discord.bridge.IrcConnection;
import net.liveware.discord.bridge.endpoint.EndPointInfo;
import net.liveware.discord.bridge.endpoint.EndPointManager;
import net.liveware.discord.commands.*;
import net.liveware.discord.music.PlayerControl;
import net.liveware.discord.util.Database;
import net.liveware.discord.util.GoogleSearch;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class Main
{
    //Non error, no action exit codes.
    public static final int NORMAL_SHUTDOWN = 10;
    public static final int RESTART_EXITCODE = 11;
    public static final int NEWLY_CREATED_CONFIG = 12;

    //Non error, action required exit codes.
    public static final int UPDATE_LATEST_EXITCODE = 20;
    public static final int UPDATE_RECOMMENDED_EXITCODE = 21;

    //error exit codes.
    public static final int UNABLE_TO_CONNECT_TO_DISCORD = 30;
    public static final int BAD_USERNAME_PASS_COMBO = 31;
    public static final int NO_USERNAME_PASS_COMBO = 32;

    private static JDA api;
    private static List<IrcConnection> ircConnections;

    public static void main(String[] args) throws InterruptedException, UnsupportedEncodingException
    {
       /* if (System.getProperty("file.encoding").equals("UTF-8"))
        {
       */     setupBot();
       /* }
        else
        {
            relaunchInUTF8();
        }*/
    }

    public static File getThisJarFile() throws UnsupportedEncodingException
    {
      //Gets the path of the currently running Jar file
        String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String decodedPath = URLDecoder.decode(path, "UTF-8");

        //This is code especially written for running and testing this program in an IDE that doesn't compile to .jar when running.
        if (!decodedPath.endsWith(".jar"))
        {
            return new File("Yui.jar");
        }
        return new File(decodedPath);   //We use File so that when we send the path to the ProcessBuilder, we will be using the proper System path formatting.
    }

    public static JDA getAPI()
    {
        return api;
    }

    public static IrcConnection getIrcConnection(String identifier)
    {
        for (IrcConnection irc : ircConnections)
        {
            if (irc.getIdentifier().equals(identifier))
                return irc;
        }
        return null;
    }

    private static void setupBot()
    {
        try
        {
            Settings settings = SettingsManager.getInstance().getSettings();

            JDABuilder jdaBuilder = new JDABuilder(AccountType.BOT).setToken(settings.getBotToken());
            Database.getInstance();
            Permissions.setupPermissions();
            ircConnections = new ArrayList<IrcConnection>();

            HelpCommand help = new HelpCommand();
            jdaBuilder.addEventListener(help.registerCommand(help));
            if (settings.getGoogleApiKey() != null && !settings.getGoogleApiKey().isEmpty())
            {
                GoogleSearch.setup(settings.getGoogleApiKey());
                jdaBuilder.addEventListener(help.registerCommand(new SearchCommand()));
                jdaBuilder.addEventListener(help.registerCommand(new NyaaCommand()));
                jdaBuilder.addEventListener(help.registerCommand(new MyAnimeListCommand()));
                jdaBuilder.addEventListener(help.registerCommand(new AnimeNewsNetworkCommand()));
            }
            else
            {
                System.out.println("No Google API Key provided, all search commands disabled");
            }
            jdaBuilder.addEventListener(help.registerCommand(new ReloadCommand()));
            jdaBuilder.addEventListener(help.registerCommand(new UpdateCommand()));
            jdaBuilder.addEventListener(help.registerCommand(new PermissionsCommand()));
            jdaBuilder.addEventListener(help.registerCommand(new EvalCommand()));
            jdaBuilder.addEventListener(help.registerCommand(new RollCommand()));
            jdaBuilder.addEventListener(help.registerCommand(new InfoCommand()));
            jdaBuilder.addEventListener(help.registerCommand(new UptimeCommand()));
            jdaBuilder.addEventListener(help.registerCommand(new GeneralCommand()));

            //Audio stuff
            jdaBuilder.addEventListener(new PlayerControl());

            for (IrcConnectInfo info  : settings.getIrcConnectInfos())
            {
                if (info.getHost() == null || info.getHost().isEmpty())
                {
                    System.out.println("Skipping IRC connection '" + info.getIdentifier() + "' because no Host was provided.");
                    continue;
                }
                if (info.getNick() == null || info.getNick().isEmpty())
                {
                    System.out.println("Skipping IRC connection '" + info.getIdentifier() + "' because no Nick was provided.");
                    continue;
                }
                IrcConnection irc = new IrcConnection(info);
                ircConnections.add(irc);
                jdaBuilder.addEventListener(irc);
            }

            if (settings.getProxyHost() != null && !settings.getProxyHost().isEmpty())
            {
                //Sets JDA's proxy settings
                jdaBuilder.setProxy(new HttpHost(settings.getProxyHost(), Integer.valueOf(settings.getProxyPort())));

                //Sets the JVM level proxy settings.
                System.setProperty("http.proxyHost", settings.getProxyHost());
                System.setProperty("http.proxyPort", settings.getProxyPort());
                System.setProperty("https.proxyHost", settings.getProxyHost());
                System.setProperty("https.proxyPort", settings.getProxyPort());

            }

            //Login to Discord now that we are all setup.
            api = jdaBuilder.buildBlocking();
            Permissions.getPermissions().setBotAsOp(api.getSelfUser());

            api.addEventListener(help.registerCommand(new TodoCommand(api)));
            api.addEventListener(help.registerCommand(new TouhouCommand(api)));
            api.addEventListener(help.registerCommand(new KanzeTodoCommand(api)));

            //Creates and Stores all Discord endpoints in our Manager.
            for (Guild guild : api.getGuilds())
            {
                for (TextChannel channel : guild.getTextChannels())
                {
                    EndPointManager.getInstance().createEndPoint(EndPointInfo.createFromDiscordChannel(channel));
                }
            }
        }
        catch (IllegalArgumentException e)
        {
            System.out.println("No login details provided! Please provide a botToken in the config.");
            System.exit(NO_USERNAME_PASS_COMBO);
        }
        catch (LoginException e)
        {
            System.out.println("The botToken provided in the Config.json was incorrect.");
            System.out.println("Did you modify the Config.json after it was created?");
            System.exit(BAD_USERNAME_PASS_COMBO);
        }
        catch (InterruptedException e)
        {
            System.out.println("Our login thread was interrupted!");
            System.exit(UNABLE_TO_CONNECT_TO_DISCORD);
        }
        catch (RateLimitedException e)
        {
            System.out.println("Encountered ratelimit while attempting to login!");
            System.exit(UNABLE_TO_CONNECT_TO_DISCORD);
        }
    }

    private static void relaunchInUTF8() throws InterruptedException, UnsupportedEncodingException
    {
        System.out.println("BotLauncher: We are not running in UTF-8 mode! This is a problem!");
        System.out.println("BotLauncher: Relaunching in UTF-8 mode using -Dfile.encoding=UTF-8");

        String[] command = new String[] {"java", "-Dfile.encoding=UTF-8", "-jar", Main.getThisJarFile().getAbsolutePath()};

        //Relaunches the bot using UTF-8 mode.
        ProcessBuilder processBuilder =  new ProcessBuilder(command);
        processBuilder.inheritIO(); //Tells the new process to use the same command line as this one.
        try
        {
            Process process = processBuilder.start();
            process.waitFor();  //We wait here until the actual bot stops. We do this so that we can keep using the same command line.
            System.exit(process.exitValue());
        }
        catch (IOException e)
        {
            if (e.getMessage().contains("\"java\""))
            {
                System.out.println("BotLauncher: There was an error relaunching the bot. We couldn't find Java to launch with.");
                System.out.println("BotLauncher: Attempted to relaunch using the command:\n   " + StringUtils.join(command, " ", 0, command.length));
                System.out.println("BotLauncher: Make sure that you have Java properly set in your Operating System's PATH variable.");
                System.out.println("BotLauncher: Stopping here.");
            }
            else
            {
                e.printStackTrace();
            }
        }
    }
}
