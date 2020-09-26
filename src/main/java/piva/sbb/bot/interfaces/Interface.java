package piva.sbb.bot.interfaces;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
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
    private InterfaceID id;
    protected Message message;
    boolean asyncReactEvent;

    public Interface(Member member, TextChannel channel, LocalDateTime time, InterfaceID id, boolean asyncReactEvent) {
        this.member = member;
        this.channel = channel;
        this.time = time;
        this.id = id;
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

            if (anInterface.member.getIdLong() == this.member.getIdLong() && anInterface.id == this.id) {
                anInterface.delete();
                interfaces.remove(anInterface);
                break;
            }
        }

        message = channel.sendMessage(build(Misc.getEmbedBuilder(member.getUser(), time))).complete();
        if (addNew)
            interfaces.add(this);

        try {
            for (Emoji emoji : reactEmojis()) {
                message.addReaction(emoji.unicode).queue();
                Thread.sleep(100);
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

    public enum InterfaceID {
        CONFIG_INTERFACE
    }
}