package piva.sbb.bot.commands.control;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.time.LocalDateTime;

public interface CommandExecutable {
    void run(Member member, TextChannel textChannel, Message message, LocalDateTime time);
}