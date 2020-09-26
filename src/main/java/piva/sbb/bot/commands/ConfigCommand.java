package piva.sbb.bot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import piva.sbb.bot.BotConfigs;
import piva.sbb.bot.commands.control.*;
import piva.sbb.bot.utils.Emoji;
import piva.sbb.bot.interfaces.Interface;

import java.time.LocalDateTime;

@Command(name = "config", async = true)
@HelpDescription(
        category = HelpDescription.Category.ADMIN,
        description = "Configura algumas chaves do Bot",
        useModes = {
                "",
                "Padrão"
        }
)
public class ConfigCommand implements CommandExecutable {
    @Override
    public void run(Member member, TextChannel textChannel, Message message, String[] args, LocalDateTime time) {
        new ConfigInterface(member, textChannel, time).setup();
    }

    static class ConfigInterface extends Interface {
        public ConfigInterface(Member member, TextChannel channel, LocalDateTime time) {
            super(member, channel, time, InterfaceID.CONFIG_INTERFACE, true);
        }

        @Override
        protected MessageEmbed build(EmbedBuilder embedBuilder) {
            embedBuilder.setDescription("Selecione o que deseja configurar");

            embedBuilder.addField(":asterisk: Prefixo", "", true);

            return embedBuilder.build();
        }

        @Override
        protected Emoji[] reactEmojis() {
            return new Emoji[]{Emoji.ASTERISK};
        }

        @Override
        protected void reactEvent(String unicode) {
            if (unicode.equals(Emoji.ASTERISK.unicode)) {
                this.delete();
                ChatInput.Input input = ChatInput.ask(member, channel, " o novo prefixo do Bot no bate-papo.");

                if (input.timeout || input.cancelled) {
                    if (input.timeout) {
                        this.remove();
                        return;
                    }

                    this.setup();
                    return;
                }

                BotConfigs.json.put("prefix", input.response.getContentDisplay());
                BotConfigs.save();
                CommandHandler.prefix = input.response.getContentDisplay();

                channel.sendMessage(":white_check_mark: Prefixo do Bot definido para ``" + input.response.getContentDisplay() + "``").queue();

                this.setup();
            }
        }
    }
}