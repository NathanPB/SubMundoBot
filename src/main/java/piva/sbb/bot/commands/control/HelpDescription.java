package piva.sbb.bot.commands.control;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface HelpDescription {
    Category category();
    String description() default "";
    String[] useModes() default "";

    enum Category {
        UTILS(":wrench:", "Utilitários"),
        FUN(":partying_face:", "Diversão"),
        ADMIN("<:admin:759113291292606465>", "Administrativos");

        Category(String emoji, String name) {
            this.emoji = emoji;
            this.name = name;
        }

        String emoji;
        String name;

        public String getEmoji() {
            return emoji;
        }

        public String getName() {
            return name;
        }
    }
}