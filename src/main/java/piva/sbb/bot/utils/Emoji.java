package piva.sbb.bot.utils;

public enum Emoji {
    ASTERISK("*️⃣"),
    NO_ENTRY("⛔");

    Emoji(String unicode) {
        this.unicode = unicode;
    }

    public String unicode;
}