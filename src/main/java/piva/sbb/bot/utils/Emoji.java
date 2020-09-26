package piva.sbb.bot.utils;

public enum Emoji {
    WHITE_CHECK_MARK("✅"),
    OCTAGONAL_SIGN("\uD83D\uDED1"),
    ASTERISK("*️⃣"),
    NO_ENTRY("⛔");

    Emoji(String unicode) {
        this.unicode = unicode;
    }

    public String unicode;
}