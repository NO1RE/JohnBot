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

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.liveware.discord.util.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class TouhouCommand extends Command
{
    //Database Methods
    public static final String ADD_TOUHOU_CHARACTER = "addTouhouCharacter";
    public static final String GET_TOUHOU_CHARACTER = "getTouhouCharacter";
    public static final String SET_TOUHOU_CHARACTER = "setTouhouCharacter";
    public static final String REMOVE_TOUHOU_CHARACTER = "removeTouhouCharacter";

    private JDA api;
    private HashMap<String, TouhouCharacter> touhouCharlist = new HashMap<>();

    public TouhouCommand(JDA api)
    {
        this.api = api;
        try
        {
            ResultSet sqltouhouCharLists = Database.getInstance().getStatement(GET_TOUHOU_CHARACTER).executeQuery();
            while (sqltouhouCharLists.next())
            {
                String label = sqltouhouCharLists.getString(2);
                TouhouCharacter touhouCharList = new TouhouCharacter(
                        sqltouhouCharLists.getInt(1),     //Id
                        label,
                        sqltouhouCharLists.getString(3),  //OwnerId
                        sqltouhouCharLists.getString(4)  //Locked
                );
                touhouCharlist.put(label, touhouCharList);
                
                PreparedStatement getEntries = Database.getInstance().getStatement(GET_TOUHOU_CHARACTER);
                getEntries.setInt(1, touhouCharList.id);
                ResultSet sqlTodoEntries = getEntries.executeQuery();
                while (sqlTodoEntries.next())
                {
                    TouhouCharacterEntry todoEntry = new TouhouCharacterEntry(
                            sqlTodoEntries.getInt(1),       //Id
                            sqlTodoEntries.getString(2),    //Content
                            sqlTodoEntries.getString(3),    //Checked
                            sqlTodoEntries.getString(4)    //Checked
                    );
                    touhouCharList.entries.add(todoEntry);
                }
                getEntries.clearParameters();
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onCommand(MessageReceivedEvent e, String[] args)
    {
        try
        {
            checkArgs(args, 1, "No Action argument was provided. Please use `.help "+ getAliases().get(0) + "` for more information.");

            switch (args[1].toLowerCase())
            {
                case "show":
                    handleShow(e, args);
                    break;
                case "add":
                    handleCreate(e, args);
                    break;
                case "update":
                    handleCreate(e, args);
                    break;
                /*case "delete":
                    handleAdd(e, args);
                    break;*/
                default:
                    sendMessage(e, "Unknown Action argument: `" + args[1] + "` was provided. " +
                            "Please use `.help "+ getAliases().get(0) + "` for more information.");
            }
        }
        catch (SQLException e1)
        {
            sendMessage(e, "An SQL error occured while processing command.\nError Message: " + e1.getMessage());
            e1.printStackTrace();
        }
        catch (IllegalArgumentException e2)
        {
            sendMessage(e, e2.getMessage());
        }
    }

    @Override
    public List<String> getAliases()
    {
        return Arrays.asList(".touhou");
    }

    @Override
    public String getDescription()
    {
        return "Used to create todo lists that can be checked off as things are completed.";
    }

    @Override
    public String getName()
    {
        return "Todo Command";
    }

    @Override
    public List<String> getUsageInstructions()
    {
        return Arrays.asList(
                String.format(
                "%1$s [Action] <Action Arguments>\n" +
                "__Actions:__\n" +
                "\n" +
                "__**show [ListName]** - Shows all todo entries in the [ListName] TodoList.__\n" +
                "       Example: `%1$s show shopping-list` would display all entries in the `shopping-list` list.\n" +
                "\n" +
                "__**lists <Mentions...>** - Displays the todo lists owned by the provided user(s).__\n" +
                "       Example 1: `%1$s lists`  Displays lists owned by the User that executed the command.\n" +
                "       Example 2: `%1$s lists @DV8FromTheWorld`  Displays lists owned by DV8FromTheWorld.\n" +
                "\n" +
                "__**create [ListName]** - Creates a new todo list with name [ListName]__\n" +
                "       Example: `%1$s list project5`  would create a todo list with the name `project5`\n" +
                "\n" +
                "__**add [ListName] [Content...]** - Adds a todo entry to the [ListName] todo list.__\n" +
                "       Example: `%1$s add project5 Fix bug where Users can delete System32`\n" +
                "\n" +
                "__**mark/unmark [TodoList] [EntryIndex]** - Marks a todo entry as **complete** or *incomplete**.__\n" +
                "       Example 1: `%1$s mark project5 2` Marks the second entry in the project5 list as compelted.\n" +
                "       Example 2: `%1$s unmark project5 3` Marks the third entry in the project5 list as incomplete.\n" +
                "       Example 3: `%1$s mark project5 *` Marks **all** todo entries in the project5 list as completed.\n" +
                "     **Note:** You can also use `check` and `uncheck`.\n" +
                "\n" +
                "__**lock/unlock [ListName]** - Used to lock a todo list such that only Auth'd users can modify it.__\n" +
                "       Example 1: `%1$s lock project5` Locks the project5 list such that only Auth'd users can use `add`,`mark` and `clear`\n" +
                "       Example 2: `%1$s unlock project5` Unlocks the project5 list so that all users can modify it.\n",
                getAliases().get(0)),

                //Second Usage Message
                String.format(
                "__**users [SubAction] [ListName] <SubAction Args>** Used add, remove and list the Auth'd users for a todo list.__\n" +
                "     __SubActions__:\n" +
                "\n" +
                "       __**add [ListName] [@mentions...]** Adds the mentions users to the Auth'd users for ListName list.__\n" +
                "           Example: `%1$s users add project5 @Joe @DudeMan` Adds Joe and DudeMan Auth'd users for the project5 list.\n" +
                "       __**remove [ListName] [@mentions...]** Removes the mentioned users from the Auth'd users for ListName list.__\n" +
                "           Example: `%1$s users remove project5 @MrCatMan` Removes MrCatMan from the Auth'd users for the project5 list.\n" +
                "       __**list [ListName]** Lists the Owner and Auth'd users for the ListName list.__\n" +
                "           Example: `%1$s users list project5` Lists the owner and all Auth'd users for the project5 list.\n" +
                "\n" +
                "__**clear [ListName]** - Clears all **completed** todo entries from a list.__\n" +
                "       Example: `%1$s clear project5` Clears all **completed** todo entries in the project5 list\n" +
                "\n" +
                "__**remove [ListName]** - Completely deletes the ListName list. Only the list owner can do this.__\n" +
                "       Example: `%1$s remove project5` Completely deletes the project5 todo list.\n",
                getAliases().get(0)));
    }

    //alias show [ListName]
    private void handleShow(MessageReceivedEvent e, String[] args)
    {
        checkArgs(args, 2, "No todo ListName was specified. Usage: `" + getAliases().get(0) + " show [ListName]`");

        String label = args[2].toLowerCase();
        TouhouCharacter touhouCharacter = touhouCharlist.get(label);
        if (touhouCharacter == null)
        {
            sendMessage(e, "Sorry, command `" + label + "` is invalid.");
            return;
        }

        // Discord messages can only be 2000 characters.
        List<Message> touhouCharMessages = new ArrayList<Message>();
        MessageBuilder builder = new MessageBuilder();
        builder.append("__Name: `" + label + "`__\n");
        for (int i = 0; i < touhouCharacter.entries.size(); i++)
        {
            TouhouCharacterEntry characterEntry = touhouCharacter.entries.get(i);
            String nameString = characterEntry.name;
            String characterSummaryString = characterEntry.summary;
            String imageString = characterEntry.image;
            if (builder.length() + nameString.length() + characterSummaryString.length() + imageString.length() > 2000)
            {
                touhouCharMessages.add(builder.build());
                builder = new MessageBuilder();
            }
            builder.append(nameString);
            builder.append(characterSummaryString);
            builder.append(imageString);
        }

        touhouCharMessages.forEach(message -> sendMessage(e, message));
        sendMessage(e, builder.build());
    }

    //alias create [ListName]
    private void handleCreate(MessageReceivedEvent e, String[] args) throws SQLException
    {
        checkArgs(args, 2, "No ListName for the new todo list was provided. Usage: `" + getAliases().get(0) + " create [ListName]`");

        String label = args[2].toLowerCase();
        String labelSummary = args[2].toLowerCase();
        String labelImage = args[2].toLowerCase();
        TouhouCharacter touhouCharacters = touhouCharlist.get(label);

        if (touhouCharacters != null)
        {
            sendMessage(e, "A todo list already exists with the name `" + label + "`.");
            return;
        }

        PreparedStatement touhouCracterList = Database.getInstance().getStatement(ADD_TOUHOU_CHARACTER);
        touhouCracterList.setString(1,  label);               
        touhouCracterList.setString(2, labelSummary);
        touhouCracterList.setString(3, labelImage);
        if (touhouCracterList.executeUpdate() == 0)
            throw new SQLException(ADD_TOUHOU_CHARACTER + " reported no modified rows!");

        touhouCharacters = new TouhouCharacter(Database.getAutoIncrement(touhouCracterList, 1), label, labelSummary, labelImage);
        touhouCharlist.put(label, touhouCharacters);
        touhouCracterList.clearParameters();

        sendMessage(e, "Created `" + label + "` todo list. Use `" + getAliases().get(0) + " add " + label + " [content...]` " +
                "to add entries to this todo list.");
    }

    public void handleRemove(MessageReceivedEvent e, String[] args) throws SQLException
    {
        checkArgs(args, 2, "No todo ListName was specified. Usage: `" + getAliases().get(0) + " remove [ListName]`");

        String label = args[2].toLowerCase();
        TouhouCharacter todoList = touhouCharlist.get(label);
        if (todoList == null)
        {
            sendMessage(e, "Sorry, `" + label + "` isn't a known todo list.");
            return;
        }

        PreparedStatement removeTodoList = Database.getInstance().getStatement(REMOVE_TOUHOU_CHARACTER);
        removeTodoList.setInt(1, todoList.id);
        if (removeTodoList.executeUpdate() == 0)
            throw new SQLException(REMOVE_TOUHOU_CHARACTER + " reported no updated rows!");
        removeTodoList.clearParameters();

        touhouCharlist.remove(label);
        sendMessage(e, "Deleted the `" + label + "` todo list.");
    }

    private void checkArgs(String[] args, int index, String failMessage)
    {
        if (args.length < (index + 1))
            throw new IllegalArgumentException(failMessage);
    }

    private static class TouhouCharacter
    {
        int id;
        String name;
        String summary;
        String image;
        List<TouhouCharacterEntry> entries;

        TouhouCharacter(int id, String name, String summary, String image)
        {
            this.id = id;
            this.name = name;
            this.summary = summary;
            this.image = image;
            this.entries = new ArrayList<>();
        }

        @Override
        public int hashCode()
        {
            return toString().hashCode();
        }

        @Override
        public String toString()
        {
        	 return "CharacterEntry: Id: " + id 
             		+ "\n name: " + name 
             		+ "\n Summary: " + summary 
             		+ "\n Image: " + image;
        }
    }

    private static class TouhouCharacterEntry
    {
        int id;
        String name;
        String summary;
        String image;
        
        TouhouCharacterEntry(int id, String name, String summary, String image)
        {
            this.id = id;
            this.name = name;
            this.summary = summary;
            this.image = image;
        }

        @Override
        public int hashCode()
        {
            return toString().hashCode();
        }

        @Override
        public String toString()
        {
            return "CharacterEntry: Id: " + id 
            		+ "\n name: " + name 
            		+ "\n Summary: " + summary 
            		+ "\n Image: " + image;
        }
    }
}
