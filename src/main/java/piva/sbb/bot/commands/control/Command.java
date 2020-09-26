package piva.sbb.bot.commands.control;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    String name();
    Permission permission() default Permission.NONE;
    String[] aliases() default "";
    boolean async() default false;
}