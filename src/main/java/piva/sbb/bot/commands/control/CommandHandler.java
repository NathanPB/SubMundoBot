package piva.sbb.bot.commands.control;

import me.piva.utils.task.PyvaTask;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import piva.sbb.bot.Core;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class CommandHandler extends ListenerAdapter {
    public static String prefix;
    public static List<SBBCommand> commands = new ArrayList<>();
    public static List<Long> prohibitedChannels = new ArrayList<>();

    public static long modRole;
    public static long adminRole;

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
        if (event.getGuild().getIdLong() != Core.getGuild().getIdLong())
            return;

        if (event.getMessage().getContentDisplay().toLowerCase().startsWith(prefix)) {
            for (Long prohibitedChannel : prohibitedChannels) {
                if (event.getChannel().getIdLong() == prohibitedChannel)
                    return;
            }

            String[] split = event.getMessage().getContentDisplay().toLowerCase().replaceFirst(Pattern.quote(prefix), "").trim().split(" ");
            String[] args = new String[split.length - 1];

            System.arraycopy(split, 1, args, 0, args.length);

            searchCommand(split[0])
                    .ifPresent(command -> {
                        if (!Permission.hasPermission(event.getMember(), command.command.permission())) {
                            event.getChannel().sendMessage(":no_entry_sign: Você não tem permissões para este comando.").queue();
                            return;
                        }

                        if (!command.command.async())
                            command.executable.run(event.getMember(), event.getChannel(), event.getMessage(), args, LocalDateTime.now());
                        else
                            PyvaTask.builder().runnable(task -> {
                                command.executable.run(event.getMember(), event.getChannel(), event.getMessage(), args, LocalDateTime.now());
                            }).buildAndStart();
                    });
        }
    }

    public static Optional<SBBCommand> searchCommand(String commandName) {
        return  commands.stream().filter(sbbCommand -> {
            if (sbbCommand.command.name().equalsIgnoreCase(commandName))
                return true;
            else {
                if (!sbbCommand.hasAlias())
                    return false;

                for (String alias : sbbCommand.command.aliases())
                    if (alias.equalsIgnoreCase(commandName))
                        return true;
            }

            return false;
        }).findFirst();
    }

    public static class SBBCommand {
        public Command command;
        public CommandExecutable executable;
        public HelpDescription helpDescription;

        public SBBCommand(Command command, CommandExecutable executable) {
            this.command = command;
            this.executable = executable;
        }

        public SBBCommand(Command command, CommandExecutable executable, HelpDescription helpDescription) {
            this.command = command;
            this.executable = executable;
            this.helpDescription = helpDescription;
        }

        public boolean hasHelp() {
            return helpDescription != null;
        }

        public boolean hasAlias() {
            return command.aliases().length > 0 && !command.aliases()[0].isEmpty();
        }

        public boolean hasUseMode() {
            if (helpDescription.useModes().length == 1)
                return !helpDescription.useModes()[0].isEmpty();

            return helpDescription.useModes().length > 0;
        }
    }
}