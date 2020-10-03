package piva.sbb.bot.board;

import org.json.JSONObject;
import piva.sbb.bot.BotConfigs;
import piva.sbb.bot.Core;

import java.util.ArrayList;
import java.util.List;

public class Boards {

    public static List<Board> boards = new ArrayList<>();

    private static Board parse(JSONObject json) {
        return new Board(
            Core.getJDA().getTextChannelById(json.getLong("channel")),
            json.getString("emoji"),
            json.getInt("count")
        );
    }

    public static void load() {
        BotConfigs.json.getJSONArray("boards").forEach(o -> {
            if (o instanceof JSONObject) {
                boards.add(parse((JSONObject) o));
            }
        });
    }
}
