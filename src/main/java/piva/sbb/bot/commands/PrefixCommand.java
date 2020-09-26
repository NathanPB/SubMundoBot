package piva.sbb.bot.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import piva.sbb.bot.BotConfigs;
import piva.sbb.bot.commands.control.Command;
import piva.sbb.bot.commands.control.CommandExecutable;
import piva.sbb.bot.commands.control.CommandHandler;
import piva.sbb.bot.commands.control.HelpDescription;
import piva.sbb.bot.utils.Replies;

import java.time.LocalDateTime;

@Command(name = "prefixo", async = true)
@HelpDescription(
        category = HelpDescription.Category.ADMIN,
        description = "Altera o prefixo do Bot.",
        useModes = {
                "``novo prefixo``",
                "Altera o prefixo do Bot para ``novo prefixo``"
        }
)
public class PrefixCommand implements CommandExecutable {
    @Override
    public void run(Member member, TextChannel textChannel, Message message, String[] args, LocalDateTime time) {
        if (args.length == 0) {
            Replies.incorrectUse(textChannel);
            return;
        }

        String newPrefix = args[0].toLowerCase();

        BotConfigs.json.put("prefix", newPrefix);
        BotConfigs.save();
        CommandHandler.prefix = newPrefix;

        textChannel.sendMessage(":white_check_mark: Prefixo do Bot definido para ``" + newPrefix + "``").queue();
    }
}