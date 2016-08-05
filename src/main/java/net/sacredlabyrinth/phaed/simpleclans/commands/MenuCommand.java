package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.LanguageManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.PermissionsManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;

/**
 * @author phaed
 */
public class MenuCommand {
    private List<String> menuItems = new LinkedList<>();

    public void execute(Player player) {
        SimpleClans plugin = SimpleClans.getInstance();
        SettingsManager settings = plugin.getSettingsManager();
        PermissionsManager perms = plugin.getPermissionsManager();
        LanguageManager lang = plugin.getLanguageManager();
        
        String headColor = settings.getPageHeadingsColor();
        String subColor = settings.getPageSubTitleColor();
        String clanCommand = settings.getCommandClan();

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);
        Clan clan = cp == null ? null : cp.getClan();

        boolean isLeader = cp != null && cp.isLeader();
        boolean isTrusted = cp != null && cp.isTrusted();
        boolean isVerified = clan != null && clan.isVerified();
        boolean isNonVerified = clan != null && !clan.isVerified();

        ChatBlock chatBlock = new ChatBlock();

        if (clan == null && perms.has(player, "simpleclans.leader.create")) {
            if (settings.isePurchaseCreation()) {
                chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("0.create.tag.name.1.purchase.a.new.clan"), clanCommand, ChatColor.WHITE));
            }
            else {
                chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("0.create.tag.name.1.create.a.new.clan"), clanCommand, ChatColor.WHITE));
            }
        }
        if (isNonVerified && settings.isRequireVerification() && settings.isePurchaseVerification()) {
            chatBlock.addRow(ChatColor.DARK_RED + "  " + MessageFormat.format(lang.get("0.verify.1.purchase.verification.of.your.clan"), clanCommand, ChatColor.WHITE));
        }
        if (perms.has(player, "simpleclans.anyone.list")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("0.list.1.lists.all.clans"), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && perms.has(player, "simpleclans.member.profile")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("0.profile.1.view.your.clan.s.profile"), clanCommand, ChatColor.WHITE));
        }
        if (perms.has(player, "simpleclans.anyone.profile")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("0.profile.tag.1.view.a.clan.s.profile"), clanCommand, ChatColor.WHITE));
        }
        if (perms.has(player, "simpleclans.member.lookup")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("0.lookup.1.lookup.your.info"), clanCommand, ChatColor.WHITE));
        }
        if (perms.has(player, "simpleclans.anyone.lookup")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("0.lookup.player.1.lookup.a.player.s.info"), clanCommand, ChatColor.WHITE));
        }
        if (perms.has(player, "simpleclans.anyone.leaderboard")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("0.leaderboard.1.view.leaderboard"), clanCommand, ChatColor.WHITE));
        }
        if (perms.has(player, "simpleclans.anyone.alliances")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("0.alliances.1.view.all.clan.alliances"), clanCommand, ChatColor.WHITE));
        }
        if (perms.has(player, "simpleclans.anyone.rivalries")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("0.rivalries.1.view.all.clan.rivalries"), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && perms.has(player, "simpleclans.member.roster")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("0.roster.1.view.your.clan.s.member.list"), clanCommand, ChatColor.WHITE));
        }
        if (perms.has(player, "simpleclans.anyone.roster")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("0.roster.tag.1.view.a.clan.s.member.list"), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && isTrusted && perms.has(player, "simpleclans.member.vitals")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("0.vitals.1.view.your.clan.member.s.vitals"), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && isTrusted && perms.has(player, "simpleclans.member.coords")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("0.coords.1.view.your.clan.member.s.coordinates"), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && isTrusted && perms.has(player, "simpleclans.member.stats")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("0.stats.1.view.your.clan.member.s.stats"), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && isTrusted && perms.has(player, "simpleclans.member.kills")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("0.kills"), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && isTrusted && perms.has(player, "simpleclans.member.kills")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("0.killsplayer"), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && isLeader && perms.has(player, "simpleclans.leader.ally")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("0.ally.add.remove.tag.1.add.remove.an.ally.clan"), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && isLeader && perms.has(player, "simpleclans.leader.rival")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("0.rival.add.remove.tag.1.add.remove.a.rival.clan"), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && perms.has(player, "simpleclans.member.home")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("home-menu"), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && isLeader && perms.has(player, "simpleclans.leader.home-set")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("home-set-menu"), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && isLeader && perms.has(player, "simpleclans.leader.home-set")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("home-clear-menu"), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && isLeader && perms.has(player, "simpleclans.leader.war")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("0.war"), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && perms.has(player, "simpleclans.member.bb")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("0.bb.1.display.bulletin.board"), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && isTrusted && perms.has(player, "simpleclans.member.bb-add")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("0.bb.msg.1.add.a.message.to.the.bulletin.board"), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && isLeader && perms.has(player, "simpleclans.leader.modtag")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("0.modtag.tag.1.modify.the.clan.s.tag"), clanCommand, ChatColor.WHITE));
        }

        String toggles = "";

        if (isVerified && isTrusted && perms.has(player, "simpleclans.member.bb-toggle")) {
            toggles += "bb/";
        }

        if (isVerified && isTrusted && perms.has(player, "simpleclans.member.tag-toggle")) {
            toggles += "tag/";
        }

        if (!toggles.isEmpty()) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("0.toggle.command"), clanCommand, ChatColor.WHITE, Helper.stripTrailing(toggles, "/")));
        }

        if (isLeader && perms.has(player, "simpleclans.leader.invite")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("0.invite.player.1.invite.a.player"), clanCommand, ChatColor.WHITE));
        }
        if (isLeader && perms.has(player, "simpleclans.leader.kick")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("0.kick.player.1.kick.a.player.from.the.clan"), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && isLeader && perms.has(player, "simpleclans.leader.setrank")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("0.trust.setrank"), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && isLeader && perms.has(player, "simpleclans.leader.settrust")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("0.trust.untrust.player.1.set.trust.level1"), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && isLeader && perms.has(player, "simpleclans.leader.settrust")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("0.trust.untrust.player.1.set.trust.level2"), clanCommand, ChatColor.WHITE));
        }
        if (isLeader && perms.has(player, "simpleclans.leader.promote")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("0.promote.member.1.promote.a.member.to.leader"), clanCommand, ChatColor.WHITE));
        }
        if (isLeader && perms.has(player, "simpleclans.leader.demote")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("0.demote.leader.1.demote.a.leader.to.member"), clanCommand, ChatColor.WHITE));
        }
        if (isLeader && perms.has(player, "simpleclans.leader.ff")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("0.clanff.allow.block.1.toggle.clan.s.friendly.fire"), clanCommand, ChatColor.WHITE));
        }
        if (isLeader && perms.has(player, "simpleclans.leader.disband")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("0.disband.1.disband.your.clan"), clanCommand, ChatColor.WHITE));
        }
        if (perms.has(player, "simpleclans.member.ff")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("0.ff.allow.auto.1.toggle.personal.friendly.fire"), clanCommand, ChatColor.WHITE));
        }
        if (perms.has(player, "simpleclans.member.resign")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang.get("0.resign.1.resign.from.the.clan"), clanCommand, ChatColor.WHITE));
        }

        for (String item : menuItems) {
            chatBlock.addRow(ChatColor.AQUA + "  " + item);
        }

        if (perms.has(player, "simpleclans.mod.verify") && settings.isRequireVerification()) {
            chatBlock.addRow(ChatColor.DARK_RED + "  " + MessageFormat.format(lang.get("0.verify.tag.1.verify.an.unverified.clan"), clanCommand, ChatColor.WHITE));
        }
        if (perms.has(player, "simpleclans.mod.place")) {
            chatBlock.addRow(ChatColor.DARK_RED + "  " + MessageFormat.format(lang.get("0.place"), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && isTrusted && perms.has(player, "simpleclans.mod.mostkilled")) {
            chatBlock.addRow(ChatColor.DARK_RED + "  " + MessageFormat.format(lang.get("0.mostkilled"), clanCommand, ChatColor.WHITE));
        }
        if (perms.has(player, "simpleclans.mod.disband")) {
            chatBlock.addRow(ChatColor.DARK_RED + "  " + MessageFormat.format(lang.get("0.disband.tag.1.disband.a.clan"), clanCommand, ChatColor.WHITE));
        }
        if (perms.has(player, "simpleclans.mod.ban")) {
            chatBlock.addRow(ChatColor.DARK_RED + "  " + MessageFormat.format(lang.get("0.ban.unban.player.1.ban.unban.a.player"), clanCommand, ChatColor.WHITE));
        }
        if (perms.has(player, "simpleclans.mod.hometp")) {
            chatBlock.addRow(ChatColor.DARK_RED + "  " + MessageFormat.format(lang.get("0.hometp.clan.1.tp.home.a.clan"), clanCommand, ChatColor.WHITE));
        }
        if (perms.has(player, "simpleclans.mod.globalff")) {
            chatBlock.addRow(ChatColor.DARK_RED + "  " + MessageFormat.format(lang.get("0.globalff.allow.auto.1.set.global.friendly.fire"), clanCommand, ChatColor.WHITE));
        }
        if (perms.has(player, "simpleclans.admin.reload")) {
            chatBlock.addRow(ChatColor.DARK_RED + "  " + MessageFormat.format(lang.get("0.reload.1.reload.configuration"), clanCommand, ChatColor.WHITE));
        }
        if (chatBlock.isEmpty()) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang.get("insufficient.permissions"));
            return;
        }

        ChatBlock.sendBlank(player);
        ChatBlock.saySingle(player, settings.getServerName(), subColor, " ", lang.get("clan.commands"), " ", headColor, Helper.generatePageSeparator(settings.getPageSep()));
        ChatBlock.sendBlank(player);

        boolean more = chatBlock.sendBlock(player, settings.getPageSize());

        if (more) {
            plugin.getStorageManager().addChatBlock(player, chatBlock);
            ChatBlock.sendBlank(player);
            ChatBlock.sendMessage(player, headColor, MessageFormat.format(lang.get("view.next.page"), settings.getCommandMore()));
        }

        ChatBlock.sendBlank(player);
    }

    /**
     * Execute the command
     *
     * @param sender
     */
    public void executeSender(CommandSender sender) {
        SimpleClans plugin = SimpleClans.getInstance();
        SettingsManager settings = plugin.getSettingsManager();
        LanguageManager lang = plugin.getLanguageManager();

        String headColor = settings.getPageHeadingsColor();
        String subColor = settings.getPageSubTitleColor();

        String clanCommand = settings.getCommandClan();

        ChatBlock.sendBlank(sender);
        ChatBlock.saySingle(sender, settings.getServerName(), subColor, " ", lang.get("clan.commands"), " " , headColor, Helper.generatePageSeparator(settings.getPageSep()));
        ChatBlock.sendBlank(sender);
        
        ChatBlock chatBlock = new ChatBlock();

        chatBlock.addRow(ChatColor.DARK_RED.toString(), "  ", MessageFormat.format(lang.get("0.verify.tag.1.verify.an.unverified.clan"), clanCommand, ChatColor.WHITE));
        chatBlock.addRow(ChatColor.DARK_RED.toString(), "  ", MessageFormat.format(lang.get("0.reload.1.reload.configuration"), clanCommand, ChatColor.WHITE));
        chatBlock.addRow(ChatColor.DARK_RED.toString(), "  ", MessageFormat.format(lang.get("0.list.1.lists.all.clans"), clanCommand, ChatColor.WHITE));
        
        chatBlock.sendBlock(sender, settings.getPageSize());
        
        ChatBlock.sendBlank(sender);
    }
}
