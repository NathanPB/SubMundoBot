package piva.sbb.bot.interfaces;

import me.piva.utils.task.PyvaTask;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class InterfaceHandler extends ListenerAdapter {
    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        for (Interface anInterface : Interface.interfaces) {
            if (anInterface.message.getIdLong() == event.getMessageIdLong() &&
                    anInterface.member.getIdLong() == event.getUserIdLong() &&
                    anInterface.channel.getIdLong() == event.getChannel().getIdLong()) {
                if (!anInterface.asyncReactEvent) {
                    anInterface.reactEvent(event.getReaction().getReactionEmote().getEmoji());
                    return;
                }

                PyvaTask.builder().runnable(task -> {
                    anInterface.reactEvent(event.getReaction().getReactionEmote().getEmoji());
                }).buildAndStart();
            }
        }
    }
}