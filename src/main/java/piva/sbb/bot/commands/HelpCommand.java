package piva.sbb.bot.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import piva.sbb.bot.commands.control.Command;
import piva.sbb.bot.commands.control.CommandExecutable;

@Command(name = "ajuda")
public class HelpCommand implements CommandExecutable {
    @Override
    public void run(Member member, TextChannel textChannel, Message message) {
        textChannel.sendMessage("ajuda rodo").queue();
    }
}