package piva.sbb.bot.commands.control;

import me.piva.utils.task.PyvaTask;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import piva.sbb.bot.Core;
import piva.sbb.bot.utils.Emoji;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ChatInput extends ListenerAdapter {
    public static List<Input> inputWaiting = new ArrayList<>();

    public static Input ask(long member, long textChannel, long reactMessage) {
        Input input = new Input(member, textChannel, reactMessage);

        inputWaiting.add(input);

        synchronized (input) {
            try {
                input.wait(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (input.response == null && !input.cancelled) {
            input.timeout = true;
            inputWaiting.remove(input);
        }

        return input;
    }

    public static Input ask(Member member, TextChannel channel, String message) {
        Message reactMessage = channel.sendMessage(":pen_ballpoint: " + message + "\n\nCancele esta operação reagindo com " + Emoji.NO_ENTRY.id + " nesta mensagem").complete();
        reactMessage.addReaction(Emoji.NO_ENTRY.id).queue();

        Input input = ask(member.getIdLong(), channel.getIdLong(), reactMessage.getIdLong());

        if (input.timeout) {
            PyvaTask.builder().runnable(task -> channel.sendMessage(":timer: " + member.getAsMention() + " Tempo esgotado, o comando será cancelado.").complete().delete().queueAfter(5, TimeUnit.MINUTES)).buildAndStart();
        }
        if (input.cancelled) {
            PyvaTask.builder().runnable(task -> channel.sendMessage(":negative_squared_cross_mark: Operação cancelada.").complete().delete().queueAfter(1, TimeUnit.MINUTES)).buildAndStart();
        }

        reactMessage.delete().queue();

        return input;
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (event.getGuild().getIdLong() != Core.getGuild().getIdLong())
            return;

        for (Input input : inputWaiting) {
            if (input.member == event.getMember().getIdLong() && input.textChannel == event.getChannel().getIdLong()) {
                input.response = event.getMessage();
                inputWaiting.remove(input);
                synchronized (input) {
                    input.notify();
                }
                return;
            }
        }
    }

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        if (event.getGuild().getIdLong() != Core.getGuild().getIdLong())
            return;

        for (Input input : inputWaiting) {
            if (input.reactMessage == event.getMessageIdLong() &&
                    event.getMember().getIdLong() == input.member &&
                    event.getChannel().getIdLong() == input.textChannel &&
                    event.getReactionEmote().getEmoji().equals(Emoji.NO_ENTRY.id)) {
                input.cancelled = true;
                inputWaiting.remove(input);
                synchronized (input) {
                    input.notify();
                }
                return;
            }
        }
    }

    public static class Input {
        public long member;
        public long textChannel;
        public long reactMessage;
        public boolean timeout;
        public boolean cancelled;
        public Message response;

        public Input(long member, long textChannel, long reactMessage) {
            this.member = member;
            this.textChannel = textChannel;
            this.reactMessage = reactMessage;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Input input = (Input) o;
            return textChannel == input.textChannel &&
                    member == input.member;
        }
    }
}