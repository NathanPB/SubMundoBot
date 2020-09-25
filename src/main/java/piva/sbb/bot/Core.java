package piva.sbb.bot;

import me.piva.utils.properties.PropertiesLoader;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import piva.sbb.bot.commands.HelpCommand;
import piva.sbb.bot.commands.MilkCommand;
import piva.sbb.bot.commands.control.CommandHandler;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class Core {
    private static JDA jda;
    private static Logger logger;
    private static BotProperties properties;

    public static void start() {
        logger = LoggerFactory.getLogger("Bot");

        logger.info("Building bot...");

        try {
            jda = JDABuilder.createDefault("NzQ2MDg2ODg1NTc0NzA1MjA0.Xz7Njw.8sAV3PSOLr5QzAB0RzUjH08HY84")
                    .setAutoReconnect(true)
                    .addEventListeners(new CommandHandler())
                    .build();
        } catch (LoginException e) {
            logger.error("An login error occurred while building the JDA");
            e.printStackTrace();
            return;
        }

        logger.info("Loading properties...");

        properties = new PropertiesLoader<BotProperties>().load(BotProperties.class);

        logger.info("Properties loaded");

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

        logger.info("Loading commands...");

        CommandHandler.register(new HelpCommand());
        CommandHandler.register(new MilkCommand());

        logger.info("Commands loaded");

        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static JDA getJda() {
        return jda;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static BotProperties getProperties() {
        return properties;
    }
}