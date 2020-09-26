package piva.sbb.bot.utils;

import net.dv8tion.jda.api.entities.TextChannel;

public class Replies {
    public static void incorrectUse(TextChannel channel) {
        channel.sendMessage(":exclamation: Modo incorreto de uso!\nUse: ``sm!ajuda`` para mais detalhes do comando.").queue();
    }
}