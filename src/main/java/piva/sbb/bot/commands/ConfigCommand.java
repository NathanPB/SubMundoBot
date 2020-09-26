package piva.sbb.bot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import org.json.JSONArray;
import piva.sbb.bot.BotConfigs;
import piva.sbb.bot.Core;
import piva.sbb.bot.commands.control.*;
import piva.sbb.bot.utils.Emoji;
import piva.sbb.bot.interfaces.Interface;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.stream.Collectors;

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
            super(member, channel, time, true);
        }

        @Override
        protected MessageEmbed build(EmbedBuilder embedBuilder) {
            embedBuilder.setDescription("Selecione o que deseja configurar");

            embedBuilder.addField(":asterisk: Prefixo", "", true);
            embedBuilder.addField(":octagonal_sign: Canais proibidos", "", true);
            embedBuilder.addField(":white_check_mark: Terminar", "", true);

            return embedBuilder.build();
        }

        @Override
        protected Emoji[] reactEmojis() {
            return new Emoji[]{Emoji.ASTERISK, Emoji.OCTAGONAL_SIGN, Emoji.WHITE_CHECK_MARK};
        }

        @Override
        protected void reactEvent(String unicode) {
            if (unicode.equals(Emoji.ASTERISK.unicode)) {
                this.delete();
                ChatInput.Input input = ChatInput.ask(member, channel, "Digite o novo prefixo do Bot no bate-papo.");

                if (reactToAsk(input))
                    return;

                BotConfigs.json.put("prefix", input.response.getContentDisplay());
                BotConfigs.save();
                CommandHandler.prefix = input.response.getContentDisplay();

                channel.sendMessage(":white_check_mark: Prefixo do Bot definido para ``" + input.response.getContentDisplay() + "``").queue();

                this.setup();
            } else if (unicode.equals(Emoji.OCTAGONAL_SIGN.unicode)) {
                new ConfigChannelsInterface(member, channel, time).setup();
            } else if (unicode.equals(Emoji.WHITE_CHECK_MARK.unicode))
                this.finish();
        }
    }

    static class ConfigChannelsInterface extends Interface {
        public ConfigChannelsInterface(Member member, TextChannel channel, LocalDateTime time) {
            super(member, channel, time, true);
        }

        @Override
        protected MessageEmbed build(EmbedBuilder eb) {
            String description = "Configure os canais proibidos para comandos do Bot.\n";

            if (CommandHandler.prohibitedChannels.size() == 0)
                eb.setDescription(description + "Não há canais proibidos");
            else {
                StringBuilder sb = new StringBuilder();
                sb.append("Canais atualmente proibidos: ");
                Core.getJda().getTextChannels().stream()
                        .filter(textChannel -> CommandHandler.prohibitedChannels.stream().anyMatch(textChannel1 -> textChannel.getIdLong() == textChannel1))
                        .collect(Collectors.toList())
                        .forEach(textChannel -> sb.append(textChannel.getAsMention()).append(", "));
                sb.deleteCharAt(sb.length() - 1);
                sb.deleteCharAt(sb.length() - 1);

                eb.setDescription(description + sb.toString());
            }

            eb.addField(":heavy_plus_sign: Adicionar canais", "", true);
            eb.addField(":heavy_minus_sign: Remover canais", "", true);
            eb.addBlankField(true);
            eb.addField(":arrow_left: Voltar", "", true);
            eb.addField(":white_check_mark: Terminar", "", true);

            return eb.build();
        }

        @Override
        protected Emoji[] reactEmojis() {
            return new Emoji[]{Emoji.HEAVY_PLUS_SIGN, Emoji.HEAVY_MINUS_SIGN, Emoji.ARROW_LEFT, Emoji.WHITE_CHECK_MARK};
        }

        @Override
        protected void reactEvent(String unicode) {
            if (unicode.equals(Emoji.HEAVY_PLUS_SIGN.unicode)) {
                this.delete();

                ChatInput.Input input = ChatInput.ask(member, channel, "Mencione todos os canais que você deseja adicionar aos canais proibidos.");

                if (reactToAsk(input))
                    return;

                CommandHandler.prohibitedChannels.addAll(input.response.getMentionedChannels().stream()
                        .filter(textChannel -> CommandHandler.prohibitedChannels.stream().noneMatch(textChannel1 -> textChannel.getIdLong() == textChannel1))
                        .map(TextChannel::getIdLong)
                        .collect(Collectors.toList()));

                BotConfigs.json.remove("prohibited channels");
                BotConfigs.json.put("prohibited channels", new JSONArray(CommandHandler.prohibitedChannels));
                BotConfigs.save();

                channel.sendMessage(":white_check_mark: Canais proibidos.").queue();
                this.setup();
            } else if (unicode.equals(Emoji.HEAVY_MINUS_SIGN.unicode)) {
                this.delete();

                ChatInput.Input input = ChatInput.ask(member, channel, "Mencione todos os canais que você deseja remover dos canais proibidos.");

                if (reactToAsk(input))
                    return;

                CommandHandler.prohibitedChannels.removeAll(input.response.getMentionedChannels().stream().map(TextChannel::getIdLong).collect(Collectors.toList()));

                BotConfigs.json.remove("prohibited channels");
                BotConfigs.json.put("prohibited channels", new JSONArray(CommandHandler.prohibitedChannels));
                BotConfigs.save();

                channel.sendMessage(":white_check_mark: Canais removidos.").queue();
                this.setup();
            }
            else if (unicode.equals(Emoji.ARROW_LEFT.unicode)) {
                new ConfigInterface(member, channel, time).setup();
            } else if (unicode.equals(Emoji.WHITE_CHECK_MARK.unicode)) {
                this.finish();
            }
        }
    }
}