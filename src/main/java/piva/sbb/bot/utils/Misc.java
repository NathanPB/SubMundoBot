package piva.sbb.bot.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

import java.time.LocalDateTime;

public class Misc {
    public static EmbedBuilder getEmbedBuilder(User user, LocalDateTime time, String title) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setFooter(user.getName() + "#" + user.getDiscriminator(), user.getEffectiveAvatarUrl());
        eb.setTimestamp(time);
        eb.setTitle(title);

        return eb;
    }

    public static EmbedBuilder getEmbedBuilder(User user, LocalDateTime time) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setFooter(user.getName() + "#" + user.getDiscriminator(), user.getEffectiveAvatarUrl());
        eb.setTimestamp(time);

        return eb;
    }
}