package piva.sbb.bot.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import piva.sbb.bot.commands.control.ChatInput;
import piva.sbb.bot.commands.control.Command;
import piva.sbb.bot.commands.control.CommandExecutable;

import java.time.LocalDateTime;

@Command(name = "prefixo", async = true)
public class PrefixCommand implements CommandExecutable {
    @Override
    public void run(Member member, TextChannel textChannel, Message message, String[] args, LocalDateTime time) {
        ChatInput.Input input = ChatInput.ask(member, "o novo prefixo para o Bot neste bate-papo", textChannel);

        if (input.cancelled) {
            textChannel.sendMessage("cancelaste").queue();
            return;
        }
        if (input.timeout) {
            textChannel.sendMessage("ó o timeout").queue();
            return;
        }

        textChannel.sendMessage("digitaste: " + input.response.getContentDisplay()).queue();
    }
}