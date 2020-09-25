package piva.sbb.bot.commands.control;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import piva.sbb.bot.Core;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CommandHandler extends ListenerAdapter {
    public static String prefix;
    public static List<SBBCommand> commands = new ArrayList<>();

    public static void register(CommandExecutable command) {
        Command commandAnnotation = command.getClass().getAnnotation(Command.class);

        if (commandAnnotation != null) {
            HelpDescription helpAnnotation = command.getClass().getAnnotation(HelpDescription.class);

            if (helpAnnotation != null)
                commands.add(new SBBCommand(commandAnnotation, command, helpAnnotation));
            else
                commands.add(new SBBCommand(commandAnnotation, command));

            return;
        }

        Core.getLogger().error(command.getClass().getSimpleName() + " doesn't have the command annotation on it");
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (event.getMessage().getContentDisplay().startsWith(prefix)) {
            String commandName = event.getMessage().getContentDisplay().split(" ")[0].replace(prefix, "");

            commands
                    .stream()
                    .filter(sbbCommand -> {
                        if (sbbCommand.command.name().equalsIgnoreCase(commandName))
                            return true;
                        else {
                            for (String alias : sbbCommand.command.aliases())
                                if (alias.equalsIgnoreCase(commandName))
                                    return true;
                        }

                        return false;
                    })
                    .findFirst()
                    .ifPresent(command -> command.executable.run(event.getMember(), event.getChannel(), event.getMessage()));
        }
    }

    private static class SBBCommand {
        Command command;
        CommandExecutable executable;
        HelpDescription helpDescription;

        public SBBCommand(Command command, CommandExecutable executable) {
            this.command = command;
            this.executable = executable;
        }

        public SBBCommand(Command command, CommandExecutable executable, HelpDescription helpDescription) {
            this.command = command;
            this.executable = executable;
            this.helpDescription = helpDescription;
        }
    }
}