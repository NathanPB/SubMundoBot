package piva.sbb.bot;

import me.piva.utils.properties.PropertiesLoader;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import piva.sbb.bot.board.Boards;
import piva.sbb.bot.commands.ConfigCommand;
import piva.sbb.bot.commands.HelpCommand;
import piva.sbb.bot.commands.MilkCommand;
import piva.sbb.bot.commands.control.ChatInput;
import piva.sbb.bot.commands.control.CommandHandler;
import piva.sbb.bot.interfaces.InterfaceHandler;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class Core {
    private static JDA jda;
    private static Logger logger;
    private static BotProperties properties;
    private static Guild guild;

    public static void start() {
        logger = LoggerFactory.getLogger("Bot");

        logger.info("Loading properties...");

        properties = new PropertiesLoader<BotProperties>().load(BotProperties.class);

        logger.info("Properties loaded");

        logger.info("Building bot...");

        if (properties.botToken.isEmpty()) {
            logger.error("Bot Token in properties can't be empty!");
            return;
        }

        try {
            jda = JDABuilder.createDefault(properties.botToken)
                    .setAutoReconnect(true)
                    .addEventListeners(new CommandHandler())
                    .addEventListeners(new ChatInput())
                    .addEventListeners(new InterfaceHandler())
                    .build();
        } catch (LoginException e) {
            logger.error("An login error occurred while building the JDA");
            e.printStackTrace();
            return;
        }

        logger.info("Connecting to Database...");

        if (!properties.database.isEmpty() && !properties.dbHost.isEmpty() && !properties.dbUser.isEmpty()) {
            Database.start(properties.dbHost, 3306, properties.database, properties.dbUser, properties.dbPass);
        } else {
            logger.error("Something for the database isn't in properties!");
            return;
        }

        logger.info("Connected to database");
        logger.info("Loading configs...");

        try {
            BotConfigs.load();
        } catch (IOException e) {
            logger.info("An error occured while loading configs!");
            e.printStackTrace();
            return;
        }

        logger.info("Configs loaded");

        CommandHandler.prefix = BotConfigs.json.getString("prefix");
        for (Object prohibited_channels : BotConfigs.json.getJSONArray("prohibited channels")) {
            if (prohibited_channels instanceof Long)
                CommandHandler.prohibitedChannels.add((Long) prohibited_channels);
        }
        JSONObject permissions = BotConfigs.json.getJSONObject("permissions");
        CommandHandler.modRole = permissions.getLong("mod");
        CommandHandler.adminRole = permissions.getLong("admin");

        logger.info("Loading commands...");

        CommandHandler.register(new HelpCommand());
        CommandHandler.register(new MilkCommand());
        CommandHandler.register(new ConfigCommand());

        logger.info("Commands loaded");

        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logger.info("Loading boards");
        Boards.load();

        guild = jda.getGuildById(BotConfigs.json.getLong("guild"));
    }

    public static JDA getJDA() {
        return jda;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static BotProperties getProperties() {
        return properties;
    }

    public static Guild getGuild() {
        return guild;
    }
}
