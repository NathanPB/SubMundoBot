package piva.sbb.bot;

import me.piva.utils.properties.PropertiesLoader;
import me.piva.utils.task.PyvaTask;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;

public class Core extends ListenerAdapter {
    private static JDA jda;
    private static Logger logger;
    private static BotProperties properties;

    public static void main(String[] args) {
        logger = LoggerFactory.getLogger("Bot");

        logger.info("Building bot...");

        try {
            jda = JDABuilder.createDefault("NzQ2MDg2ODg1NTc0NzA1MjA0.Xz7Njw.8sAV3PSOLr5QzAB0RzUjH08HY84")
                    .setAutoReconnect(true)
                    .addEventListeners(new Core())
                    .build();

            PyvaTask.builder().runnable(task -> {
                try {
                    jda.awaitReady();
                    logger.info("JDA Built");
                } catch (InterruptedException e) {
                    logger.error("An error occurred while waiting for the JDA");
                    e.printStackTrace();
                }
            }).buildAndStart();
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

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        event.getChannel().sendMessage("O corno **" + event.getUser().getName() + "** reagiu numa mensagem com " + event.getReactionEmote().getEmoji()).complete();
    }
}