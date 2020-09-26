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
import piva.sbb.bot.interfaces.EmojiReact;
import piva.sbb.bot.utils.Emoji;
import piva.sbb.bot.interfaces.Interface;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Command(name = "config", permission = Permission.MOD, async = true)
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
            embedBuilder.setTitle("Configurações");
            embedBuilder.setDescription("Selecione o que deseja configurar");

            embedBuilder.addField(":asterisk: Prefixo", "", true);
            embedBuilder.addField(":octagonal_sign: Canais proibidos", "", true);
            embedBuilder.addField(":scales: Permissões", "", true);
            embedBuilder.addField(":white_check_mark: Terminar", "", true);

            return embedBuilder.build();
        }

        @Override
        protected Emoji[] reactEmojis() {
            return new Emoji[]{Emoji.ASTERISK, Emoji.OCTAGONAL_SIGN, Emoji.SCALES, Emoji.WHITE_CHECK_MARK};
        }

        @Override
        protected void reactEvent(EmojiReact emojiReact) {
            if (emojiReact.unicode.equals(Emoji.ASTERISK.id)) {
                this.delete();
                ChatInput.Input input = ChatInput.ask(member, channel, "Digite o novo prefixo do Bot no bate-papo.\nPrefixo atual: ``" + CommandHandler.prefix + "``");

                if (reactToAsk(input))
                    return;

                BotConfigs.json.put("prefix", input.response.getContentDisplay());
                BotConfigs.save();
                CommandHandler.prefix = input.response.getContentDisplay();

                channel.sendMessage(":white_check_mark: Prefixo do Bot definido para ``" + input.response.getContentDisplay() + "``").queue();

                this.setup();
            } else if (emojiReact.unicode.equals(Emoji.SCALES.id))
                new ConfigPermissionsInterface(member, channel, time).setup();
            else if (emojiReact.unicode.equals(Emoji.OCTAGONAL_SIGN.id))
                new ConfigChannelsInterface(member, channel, time).setup();
            else if (emojiReact.unicode.equals(Emoji.WHITE_CHECK_MARK.id))
                this.finish();
        }
    }

    static class ConfigPermissionsInterface extends Interface {
        public ConfigPermissionsInterface(Member member, TextChannel channel, LocalDateTime time) {
            super(member, channel, time, true);
        }

        @Override
        protected MessageEmbed build(EmbedBuilder eb) {
            eb.setTitle("Permissões");
            eb.setDescription("Selecione a permissões que deseja configurar o cargo\n" +
                    "As permissões atuais são: \n" +
                    "Moderador: " + (Core.getJDA().getRoleById(CommandHandler.modRole) == null ? "Não definido" : Core.getJDA().getRoleById(CommandHandler.modRole).getAsMention()) + "\n" +
                    "Administrador: " + (Core.getJDA().getRoleById(CommandHandler.adminRole) == null ? "Não definido" : Core.getJDA().getRoleById(CommandHandler.adminRole).getAsMention()) + "\n");

            eb.addField("<:mod:759526449370628117> Moderador", "", true);
            eb.addField("<:admin:759113291292606465> Administrador", "", true);
            eb.addBlankField(false);
            eb.addField(":arrow_left: Voltar", "", true);
            eb.addField(":white_check_mark: Terminar", "", true);

            return eb.build();
        }

        @Override
        protected Emoji[] reactEmojis() {
            return new Emoji[]{Emoji.MOD, Emoji.ADMIN, Emoji.ARROW_LEFT, Emoji.WHITE_CHECK_MARK};
        }

        @Override
        protected void reactEvent(EmojiReact emojiReact) {
            if (emojiReact.emoteId.equals("759526449370628117") || emojiReact.emoteId.equals("759113291292606465")) {
                boolean mod = emojiReact.emoteId.equals("759526449370628117");

                ChatInput.Input input = ChatInput.ask(member, channel, "Digite o ID do cargo para conceder as permissões de " + (mod ? "Moderador" : "Administrador"));

                if (reactToAsk(input))
                    return;

                long id;
                try {
                    id = Long.parseLong(input.response.getContentDisplay().split(" ")[0]);
                } catch (NumberFormatException e) {
                    channel.sendMessage(":x: Você precisa digitar um número válido").queue();
                    return;
                }

                if (mod) {
                    CommandHandler.modRole = id;
                    BotConfigs.json.getJSONObject("permissions").put("mod", id);
                } else {
                    CommandHandler.adminRole = id;
                    BotConfigs.json.getJSONObject("permissions").put("admin", id);
                }

                BotConfigs.save();

                channel.sendMessage(":white_check_mark: Permissões para " + (mod ? "moderador" : "administrador") + " definidas.").queue();
                this.setup();
            } else if (emojiReact.unicode.equals(Emoji.ARROW_LEFT.id)) {
                new ConfigInterface(member, channel, time).setup();
            } else if (emojiReact.unicode.equals(Emoji.WHITE_CHECK_MARK.id)) {
                this.finish();
            }
        }
    }

    static class ConfigChannelsInterface extends Interface {
        public ConfigChannelsInterface(Member member, TextChannel channel, LocalDateTime time) {
            super(member, channel, time, true);
        }

        @Override
        protected MessageEmbed build(EmbedBuilder eb) {
            eb.setTitle("Canais proibidos");
            String description = "Configure os canais proibidos para comandos do Bot.\n";

            if (CommandHandler.prohibitedChannels.size() == 0)
                eb.setDescription(description + "Não há canais proibidos");
            else {
                StringBuilder sb = new StringBuilder();
                sb.append("Canais atualmente proibidos: ");
                Core.getJDA().getTextChannels().stream()
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
        protected void reactEvent(EmojiReact emojiReact) {
            if (emojiReact.unicode.equals(Emoji.HEAVY_PLUS_SIGN.id)) {
                this.delete();

                ChatInput.Input input = ChatInput.ask(member, channel, "Mencione todos os canais que você deseja adicionar aos canais proibidos.");

                if (reactToAsk(input))
                    return;

                CommandHandler.prohibitedChannels.addAll(input.response.getMentionedChannels().stream()
                        .filter(textChannel -> CommandHandler.prohibitedChannels.stream().noneMatch(textChannel1 -> textChannel.getIdLong() == textChannel1))
                        .map(TextChannel::getIdLong)
                        .collect(Collectors.toList()));

                BotConfigs.json.put("prohibited channels", new JSONArray(CommandHandler.prohibitedChannels));
                BotConfigs.save();

                channel.sendMessage(":white_check_mark: Canais proibidos.").queue();
                this.setup();
            } else if (emojiReact.unicode.equals(Emoji.HEAVY_MINUS_SIGN.id)) {
                this.delete();

                ChatInput.Input input = ChatInput.ask(member, channel, "Mencione todos os canais que você deseja remover dos canais proibidos.");

                if (reactToAsk(input))
                    return;

                CommandHandler.prohibitedChannels.removeAll(input.response.getMentionedChannels().stream().map(TextChannel::getIdLong).collect(Collectors.toList()));

                BotConfigs.json.put("prohibited channels", new JSONArray(CommandHandler.prohibitedChannels));
                BotConfigs.save();

                channel.sendMessage(":white_check_mark: Canais removidos.").queue();
                this.setup();
            }
            else if (emojiReact.unicode.equals(Emoji.ARROW_LEFT.id)) {
                new ConfigInterface(member, channel, time).setup();
            } else if (emojiReact.unicode.equals(Emoji.WHITE_CHECK_MARK.id)) {
                this.finish();
            }
        }
    }
}