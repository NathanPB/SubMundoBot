package piva.sbb.bot.board;

import net.dv8tion.jda.api.entities.TextChannel;

public class Board {
    public TextChannel channel;
    public String emoji;
    public int emojiCount;

    public Board(TextChannel channel, String emoji, int emojiCount) {
        this.channel = channel;
        this.emoji = emoji;
        this.emojiCount = emojiCount;
    }
}