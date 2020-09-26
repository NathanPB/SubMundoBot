package piva.sbb.bot.commands.control;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import piva.sbb.bot.Core;

import java.util.List;

public enum Permission {
    NONE,
    MOD,
    ADMIN;

    public static Permission getPermission(Member member) {
        List<Role> roles = member.getRoles();

        if (Core.getGuild().getOwnerIdLong() == member.getIdLong() || roles.contains(Core.getJDA().getRoleById(CommandHandler.adminRole)))
            return Permission.ADMIN;
        if (roles.contains(Core.getJDA().getRoleById(CommandHandler.modRole)))
            return Permission.MOD;

        return Permission.NONE;
    }

    public static boolean hasPermission(Member member, Permission permission) {
        if (permission == NONE)
            return true;

        Permission memberPermissison = getPermission(member);

        if (memberPermissison == NONE)
            return false;

        return memberPermissison != MOD || permission != ADMIN;
    }

    public static boolean hasPermission(Permission memberPermission, Permission permission) {
        if (permission == NONE)
            return true;

        if (memberPermission == NONE)
            return false;

        return memberPermission != MOD || permission != ADMIN;
    }
}