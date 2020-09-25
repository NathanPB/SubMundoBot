package piva.sbb.bot.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import piva.sbb.bot.commands.control.Command;
import piva.sbb.bot.commands.control.CommandExecutable;
import piva.sbb.bot.commands.control.HelpDescription;

import java.time.LocalDateTime;

@Command(name = "leite", aliases = "milk")
@HelpDescription(
        category = HelpDescription.Category.FUN,
        description = "Te trás leitinho ;)"
)
public class MilkCommand implements CommandExecutable {
    @Override
    public void run(Member member, TextChannel textChannel, Message message, LocalDateTime time) {
        textChannel.sendMessage(member.getAsMention() + " **aqui está seu leite!** :milk:").queue();
    }
}