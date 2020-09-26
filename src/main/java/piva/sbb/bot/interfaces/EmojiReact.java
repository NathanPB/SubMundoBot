package piva.sbb.bot.interfaces;

public class EmojiReact {
    public String unicode;
    public String emoteId;

    public EmojiReact(String unicode, String emoteId) {
        this.unicode = unicode;
        this.emoteId = emoteId;
    }
}