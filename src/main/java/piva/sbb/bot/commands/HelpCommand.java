package piva.sbb.bot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import piva.sbb.bot.commands.control.Command;
import piva.sbb.bot.commands.control.CommandExecutable;
import piva.sbb.bot.commands.control.CommandHandler;
import piva.sbb.bot.commands.control.HelpDescription;
import piva.sbb.bot.utils.Misc;

import java.time.LocalDateTime;

@Command(name = "ajuda", aliases = "help")
@HelpDescription(
        category = HelpDescription.Category.UTILS,
        useModes = {
                "",
                "Exibe todos os comandos",
                "``comando``",
                "Exibe informações de um comando específico"
        },
        description = "Exibe informações de comandos do Bot"
)
public class HelpCommand implements CommandExecutable {
    @Override
    public void run(Member member, TextChannel textChannel, Message message, String[] args, LocalDateTime time) {
        if (args.length == 0) {
            EmbedBuilder eb = Misc.getEmbedBuilder(member.getUser(), time, "Comandos do Bot");

            eb.setDescription("Use ``" + CommandHandler.prefix + "ajuda <nome do comando>`` para ver informações de algum comando");

            for (HelpDescription.Category value : HelpDescription.Category.values()) {
                StringBuilder sb = new StringBuilder();

                CommandHandler.commands
                        .stream()
                        .filter(sbbCommand -> sbbCommand.helpDescription != null && sbbCommand.helpDescription.category().equals(value))
                        .forEach(
                                sbbCommand -> sb.append("``").append(sbbCommand.command.name()).append("``, ")
                        );

                if (sb.length() != 0) {
                    sb.deleteCharAt(sb.length() - 1);
                    sb.deleteCharAt(sb.length() - 1);
                }

                eb.addField(value.getEmoji() + " " + value.getName(), sb.toString(), false);
            }

            textChannel.sendMessage(eb.build()).queue();
            return;
        }

        CommandHandler.SBBCommand command = CommandHandler.searchCommand(args[0]).orElse(null);

        if (command == null) {
            textChannel.sendMessage(":interrobang: Comando ``" + args[0] + "`` não encontrado.").queue();
            return;
        }

        if (!command.hasHelp() && !command.hasAlias()) {
            textChannel.sendMessage("Infelizmente o comando ``" + command.command.name() +"`` não possui nenhuma informação adicional. :confused:").queue();
            return;
        }

        EmbedBuilder eb = Misc.getEmbedBuilder(member.getUser(), time, "Comando " + command.command.name());

        eb.setDescription("Informações sobre este comando:");

        if (command.hasHelp()) {
            eb.addField("Categoria:", command.helpDescription.category().getEmoji() + " " + command.helpDescription.category().getName(), false);
            eb.addField("Descrição:", command.helpDescription.description().isEmpty() ? "Este comando não possui nenhuma descrição" : command.helpDescription.description(), false);
        }
        if (command.hasAlias()) {
            StringBuilder sb = new StringBuilder();
            for (String alias : command.command.aliases()) {
                sb.append("``").append(alias).append("``, ");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            eb.addField("Apelidos/Aliases: ", sb.toString(), false);
        }
        if (command.hasUseMode()) {
            StringBuilder sb = new StringBuilder();
            boolean commandUseMode = true;
            for (String s : command.helpDescription.useModes()) {
                if (commandUseMode) {
                    sb
                            .append("**")
                            .append(CommandHandler.prefix)
                            .append(command.command.name())
                            .append(" ")
                            .append(s)
                            .append("**:\n");
                    commandUseMode = false;
                    continue;
                }

                sb.append(s).append("\n");
                commandUseMode = true;
            }

            if (!commandUseMode)
                sb.append("Nenhum modo de uso informado para este caso.");

            eb.addField("Modo(s) de uso: ", sb.toString(), false);
        }

        textChannel.sendMessage(eb.build()).queue();
    }
}