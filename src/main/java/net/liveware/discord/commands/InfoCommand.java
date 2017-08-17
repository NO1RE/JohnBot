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
package net.liveware.discord.commands;

import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.liveware.discord.MainInfo;

import java.util.Arrays;
import java.util.List;

public class InfoCommand extends Command

{
    @Override
    public void onCommand(MessageReceivedEvent e, String[] args)
    {

        MessageBuilder builder = new MessageBuilder();
        builder.append("__John Fitzgerald Information__\n")
                .append("    **Version**:       " + MainInfo.VERSION.toString().replace("_", "\\_") + "\n")
                .append("    **ID**:                " + e.getJDA().getSelfUser().getId() + "\n")
                .append("__Development__\n")
                .append("    **Language**:   Java 8\n")
                .append("    **Library**:        JDA - v" + JDAInfo.VERSION + "\n")
        		.append("That's all I know about me apparently and I'm a HEUGG 2HU FAN!");
        sendMessage(e, builder.build());
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList(".info");
    }

    @Override
    public String getDescription()
    {
        return "Provides information about your daily weeb.";
    }

    @Override
    public String getName()
    {
        return "John Fitzgerald Information";
    }

    @Override
    public List<String> getUsageInstructions()
    {
        return Arrays.asList(".info - Prints all information pertaining to the current instance of John Fitzgerald.");
    }
}
