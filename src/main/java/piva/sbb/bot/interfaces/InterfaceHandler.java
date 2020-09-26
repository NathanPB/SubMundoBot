package piva.sbb.bot.interfaces;

import me.piva.utils.task.PyvaTask;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import piva.sbb.bot.Core;

import javax.annotation.Nonnull;

public class InterfaceHandler extends ListenerAdapter {
    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        if (event.getGuild().getIdLong() != Core.getGuild().getIdLong())
            return;

        for (Interface anInterface : Interface.interfaces) {
            if (anInterface.message.getIdLong() == event.getMessageIdLong() &&
                    anInterface.member.getIdLong() == event.getUserIdLong() &&
                    anInterface.channel.getIdLong() == event.getChannel().getIdLong()) {
                anInterface.task.cancel();

                if (!anInterface.asyncReactEvent) {
                    anInterface.reactEvent(
                            new EmojiReact(
                                    event.getReactionEmote().isEmoji() ? event.getReactionEmote().getEmoji() : "",
                                    event.getReactionEmote().isEmote() ? event.getReactionEmote().getId() : ""
                            )
                    );
                    return;
                }

                PyvaTask.builder().runnable(task -> {
                    anInterface.reactEvent(
                            new EmojiReact(
                                    event.getReactionEmote().isEmoji() ? event.getReactionEmote().getEmoji() : "",
                                    event.getReactionEmote().isEmote() ? event.getReactionEmote().getId() : ""
                            )
                    );
                }).buildAndStart();
            }
        }
    }
}