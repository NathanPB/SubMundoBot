package piva.sbb.bot.commands.control;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import piva.sbb.bot.utils.Emojis;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ChatInput extends ListenerAdapter {
    public static List<Input> inputWaiting = new ArrayList<>();

    public static Input ask(long member, long textChannel, long reactMessage) {
        Input input = new Input(member, textChannel, reactMessage);

        inputWaiting.add(input);

        synchronized (input) {
            try {
                input.wait(10000);
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

    public static Input ask(Member member, String message, TextChannel channel) {
        Message reactMessageId = channel.sendMessage(":pen_ballpoint: Digite " + message + "\n\nCancele esta operação reagindo com " + Emojis.NO_ENTRY.unicode + " nesta mensagem").complete();
        reactMessageId.addReaction(Emojis.NO_ENTRY.unicode).queue();

        Input input = ask(member.getIdLong(), channel.getIdLong(), reactMessageId.getIdLong());

        if (input.timeout)
            channel.sendMessage(":timer: " + member.getAsMention() + " Tempo esgotado, o comando será cancelado.").queue();
        if (input.cancelled)
            channel.sendMessage(":negative_squared_cross_mark: Operação cancelada.").queue();

        return input;
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        for (Input input : inputWaiting) {
            if (input.textChannel == event.getChannel().getIdLong())
                if (input.member == event.getMember().getIdLong()) {
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
        for (Input input : inputWaiting) {
            if (input.reactMessage == event.getMessageIdLong() && event.getMember().getIdLong() == input.member && event.getReactionEmote().getEmoji().equals(Emojis.NO_ENTRY.unicode)) {
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