package piva.sbb.bot.utils;

public enum Emoji {
    ADMIN("759113291292606465", true),
    MOD("759526449370628117", true),
    SCALES("⚖️"),
    HEAVY_MINUS_SIGN("➖"),
    HEAVY_PLUS_SIGN("➕"),
    ARROW_LEFT("⬅️"),
    WHITE_CHECK_MARK("✅"),
    OCTAGONAL_SIGN("\uD83D\uDED1"),
    ASTERISK("*️⃣"),
    NO_ENTRY("⛔"),
    STAR("⭐");

    Emoji(String id) {
        this.id = id;
        this.customEmote = false;
    }

    Emoji(String id, boolean customEmote) {
        this.id = id;
        this.customEmote = customEmote;
    }

    public String id;
    public boolean customEmote;
}
