package piva.sbb.bot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;

public class BotConfigs {
    public static JSONObject json;

    public static void load() throws IOException {
        File file = new File("config.json");
        boolean newJSON = false;

        if (!file.exists()) {
            file.createNewFile();
            newJSON = true;
        }

        if (newJSON)
            json = new JSONObject();
        else {
            BufferedReader reader = new BufferedReader(new FileReader("config.json"));

            StringBuilder stringBuilder = new StringBuilder();
            while (reader.ready()) {
                stringBuilder.append(reader.readLine()).append("\n");
            }
            reader.close();

            json = new JSONObject(stringBuilder.toString());
        }

        loadDefaults();
        save();
    }

    public static void save() {
        try {
            FileWriter writer = new FileWriter("config.json");

            writer.write(json.toString(4));
            writer.close();
        } catch (IOException e) {
            Core.getLogger().error("An error occured while saving the JSON file");
            e.printStackTrace();
        }
    }

    private static void loadDefaults() {
        setDefault(json, "guild", 0L);
        setDefault(json, "prefix", "*");
        JSONObject permissions = new JSONObject();
        setDefault(permissions, "mod", 0L);
        setDefault(permissions, "admin", 0L);
        setDefault(json, "permissions", permissions);
        setDefault(json, "prohibited channels", new JSONArray(new ArrayList<>()));

        JSONArray boards = new JSONArray();
        JSONObject board = new JSONObject();
        board.put("channel", 0);
        board.put("emoji", "⭐");
        board.put("count", 3);
        boards.put(board);
        setDefault(json, "boards", boards);
    }

    private static void setDefault(JSONObject jsonObject, String key, Object value) {
        if (!jsonObject.has(key))
            jsonObject.put(key, value);
    }
}
