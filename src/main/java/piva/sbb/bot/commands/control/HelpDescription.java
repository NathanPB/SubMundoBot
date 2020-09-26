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
        ADMIN("<:admin:759113291292606465>", "Administrativos", Permission.ADMIN);

        Category(String emoji, String name) {
            this.emoji = emoji;
            this.name = name;
            this.permission = Permission.NONE;
        }

        Category(String emoji, String name, Permission permission) {
            this.emoji = emoji;
            this.name = name;
            this.permission = permission;
        }

        String emoji;
        String name;
        Permission permission;

        public String getEmoji() {
            return emoji;
        }

        public String getName() {
            return name;
        }

        public Permission getPermission() {
            return permission;
        }
    }
}