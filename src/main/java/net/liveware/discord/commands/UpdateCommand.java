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

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.liveware.discord.Main;
import net.liveware.discord.MainInfo;
import net.liveware.discord.Permissions;
import net.liveware.discord.SettingsManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class UpdateCommand extends Command
{
    @Override
    public void onCommand(MessageReceivedEvent e, String[] args)
    {
        if (!Permissions.getPermissions().isOp(e.getAuthor().getId()))
        {
            sendMessage(e, Permissions.OP_REQUIRED_MESSAGE);
            return;
        }

        if(SettingsManager.getInstance().getSettings().getUseBetaBuilds() && MainInfo.hasNewBetaVersion())
        {
            sendMessage(e, new MessageBuilder()
                .append("Updating to the latest **beta** version.\n")
                .append(MainInfo.VERSION.toString())
                .append(" -> ")
                .append(MainInfo.getLatestBetaVersion().toString())
                .build());
            System.exit(Main.UPDATE_LATEST_EXITCODE);
        }
        else if (MainInfo.hasNewRecommendedVersion())
        {
            sendMessage(e, new MessageBuilder()
                    .append("Updating to the latest **recommended** version.\n")
                    .append(MainInfo.VERSION.toString())
                    .append(" -> ")
                    .append(MainInfo.getLatestRecommendedVersion().toString())
                    .build());
            System.exit(Main.UPDATE_RECOMMENDED_EXITCODE);
        }
        else
        {
            sendMessage(e, new MessageBuilder()
                .append("Yui is currently up-to-date compared to the latest ")
                .append(SettingsManager.getInstance().getSettings().getUseBetaBuilds() ? "beta" : "recommended")
                .append("build.\n")
                .append("Current version: ", MessageBuilder.Formatting.BOLD)
                .append(MainInfo.VERSION.toString())
                .build());
        }
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList(".update");
    }

    @Override
    public String getDescription()
    {
        return "Used to update the Bot to the latest available version.";
    }

    @Override
    public String getName()
    {
        return "Bot Update Command";
    }

    @Override
    public List<String> getUsageInstructions()
    {
        return Collections.singletonList(
                ".update\n"
                + "If you are running the bot without the bootloader, this command will be disabled.\n"
                + "The bootloader is required to update the bot.");
    }
}
