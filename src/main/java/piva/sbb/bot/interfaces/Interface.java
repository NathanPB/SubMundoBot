package piva.sbb.bot.interfaces;

import me.piva.utils.task.PyvaTask;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import piva.sbb.bot.commands.control.ChatInput;
import piva.sbb.bot.utils.Emoji;
import piva.sbb.bot.utils.Misc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Interface extends ListenerAdapter {
    public static List<Interface> interfaces = new ArrayList<>();

    protected Member member;
    protected TextChannel channel;
    protected LocalDateTime time;
    protected Message message;
    boolean asyncReactEvent;

    PyvaTask task;

    public Interface(Member member, TextChannel channel, LocalDateTime time, boolean asyncReactEvent) {
        this.member = member;
        this.channel = channel;
        this.time = time;
        this.asyncReactEvent = asyncReactEvent;
    }

    protected abstract MessageEmbed build(EmbedBuilder eb);
    protected abstract Emoji[] reactEmojis();
    protected void reactEvent(String unicode) { }

    public void setup() {
        boolean addNew = true;

        for (Interface anInterface : interfaces) {
            if (anInterface == this) {
                addNew = false;
                break;
            }

            if (anInterface.member.getIdLong() == this.member.getIdLong() && anInterface.channel.getIdLong() == this.channel.getIdLong()) {
                anInterface.finish();
                break;
            }
        }

        message = channel.sendMessage(build(Misc.getEmbedBuilder(member.getUser(), time))).complete();
        if (addNew)
            interfaces.add(this);

        task = PyvaTask.builder().delay(60000).runnable(task -> {
            this.finish();
        }).buildAndStart();

        try {
            for (Emoji emoji : reactEmojis()) {
                message.addReaction(emoji.unicode).queue();
                Thread.sleep(80);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        message.delete().queue();
    }

    public void remove() {
        interfaces.remove(this);
    }

    public void finish() {
        remove();
        delete();
    }

    protected boolean reactToAsk(ChatInput.Input input) {
        if (input.timeout || input.cancelled) {
            if (input.timeout) {
                this.remove();
                return true;
            }

            this.setup();
            return true;
        }
        return false;
    }
}