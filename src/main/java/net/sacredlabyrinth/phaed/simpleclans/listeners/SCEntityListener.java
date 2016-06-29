package net.sacredlabyrinth.phaed.simpleclans.listeners;

import java.text.MessageFormat;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;

/**
 * @author phaed
 */
public class SCEntityListener implements Listener {

    private SimpleClans plugin = SimpleClans.getInstance();

    public SCEntityListener() {}

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDeath(EntityDeathEvent event) {
        SettingsManager settings = plugin.getSettingsManager();
        
        if (event.getEntity() instanceof Player) {
            Player victim = (Player) event.getEntity();

            if (settings.isBlacklistedWorld(victim.getLocation().getWorld().getName())) {
                return;
            }

            Player attacker = null;

            // find attacker
            EntityDamageEvent lastDamageCause = victim.getLastDamageCause();

            if (lastDamageCause instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent) lastDamageCause;

                if (entityEvent.getDamager() instanceof Player) {
                    attacker = (Player) entityEvent.getDamager();
                } else if (entityEvent.getDamager() instanceof Arrow) {
                    Arrow arrow = (Arrow) entityEvent.getDamager();

                    if (arrow.getShooter() instanceof Player) {
                        attacker = (Player) arrow.getShooter();
                    }
                }
            }

            if (attacker != null ) {
                ClanPlayer acp = plugin.getClanManager().getCreateClanPlayer(attacker.getUniqueId());
                ClanPlayer vcp = plugin.getClanManager().getCreateClanPlayer(victim.getUniqueId());
                
                double reward = 0;
                double multipier = settings.getKDRMultipliesPerKill();
                float kdr = acp.getKDR();

                Clan vClan = vcp.getClan();
                Clan aClan = acp.getClan();
                
                if (vClan == null || aClan == null || !vClan.isVerified() || !aClan.isVerified()) {
                    acp.addCivilianKill();
                    plugin.getStorageManager().insertKill(attacker, acp.getTag(), victim, "", "c");
                } else if (aClan.isRival(vClan)) {
                    if (aClan.isWarring(vClan)) {
                        reward = kdr * multipier * 4;
                    } else {
                        reward = kdr * multipier * 2;
                    }
                    acp.addRivalKill();
                    plugin.getStorageManager().insertKill(attacker, acp.getTag(), victim, vcp.getTag(), "r");
                } else if (aClan.isAlly(vClan)) {
                    reward = kdr * multipier * - 1;
                } else {
                    reward = kdr * multipier;
                    acp.addNeutralKill();
                    plugin.getStorageManager().insertKill(attacker, acp.getTag(), victim, vcp.getTag(), "n");
                }

                if (aClan != null && reward != 0 && settings.isMoneyPerKill()) {
                    List<ClanPlayer> list = aClan.getOnlineMembers();
                    for (ClanPlayer cp : list) {
                        double money = Math.round((reward / list.size()) * 100D) / 100D;
                        cp.toPlayer().sendMessage(ChatColor.AQUA + MessageFormat.format(plugin.getLang("player.got.money"), money, victim.getName(), kdr));
                        plugin.getPermissionsManager().playerGrantMoney(cp.toPlayer(), money);
                    }
                }
                // record death for victim
                vcp.addDeath();
                plugin.getStorageManager().updateClanPlayerAsync(vcp);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEntityEvent event) {

        Entity entity = event.getRightClicked();
        
        if (plugin.getSettingsManager().isTamableMobsSharing() && entity instanceof Tameable) {
        	
            Player player = event.getPlayer();
            Tameable tamed = (Tameable) entity;

            if (tamed.isTamed()) {
                if(entity instanceof Wolf && !((Wolf) entity).isSitting()) {
                	return;
                }
                ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);
                AnimalTamer owner = tamed.getOwner();
                if ( cp != null && owner instanceof Player && cp.getClan().isMember((Player) owner) ) {
                    tamed.setOwner(player);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityTarget(EntityTargetLivingEntityEvent event) {
        
        if (plugin.getSettingsManager().isTamableMobsSharing()) {
            
            if (event.getEntity() instanceof Tameable && event.getTarget() instanceof Player) {
                ClanPlayer cp = plugin.getClanManager().getClanPlayer((Player) event.getTarget());
                Tameable wolf = (Tameable) event.getEntity();

                if (cp != null && wolf.isTamed() && cp.getClan().isMember((Player) wolf.getOwner())) {
                	// cancels the event if the attacker is one out of his clan
                    event.setCancelled(true);
                }
            }
        }
    }

    @SuppressWarnings( "null" )
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        SettingsManager settings = plugin.getSettingsManager();
        
        Player attacker = null;
        Player victim = null;

        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent sub = (EntityDamageByEntityEvent) event;

            if (sub.getEntity() instanceof Player && sub.getDamager() instanceof Player) {
                attacker = (Player) sub.getDamager();
                victim = (Player) sub.getEntity();
            }

            if (settings.isTamableMobsSharing()) {
                
                if (sub.getEntity() instanceof Wolf && sub.getDamager() instanceof Player) {
                    
                    attacker = (Player) sub.getDamager();
                    Wolf wolf = (Wolf) sub.getEntity();
                    ClanPlayer cp = plugin.getClanManager().getClanPlayer(attacker);
                    
                    if (cp != null && wolf.isTamed() && cp.getClan().isMember((Player) wolf.getOwner())) {
                    	// Sets the wolf to friendly if the attacker is one out of his clan
                        wolf.setAngry(false);
                    }
                }
            }

            if (sub.getEntity() instanceof Player && sub.getDamager() instanceof Arrow) {
                Arrow arrow = (Arrow) sub.getDamager();

                if (arrow.getShooter() instanceof Player) {
                    attacker = (Player) arrow.getShooter();
                    victim = (Player) sub.getEntity();
                }
            }
        }

        if (victim != null && settings.isBlacklistedWorld(victim.getLocation().getWorld().getName())) {
        	return;
        }

        if (attacker != null && victim != null) {
            ClanPlayer acp = plugin.getClanManager().getClanPlayer(attacker);
            ClanPlayer vcp = plugin.getClanManager().getClanPlayer(victim);


            Clan vclan = vcp == null ? null : vcp.getClan();
            Clan aclan = acp == null ? null : acp.getClan();


            if (settings.isPvpOnlywhileInWar()) {
               
                // if one doesn't have clan then they cant be at war
                if (aclan == null || vclan == null) {
                    event.setCancelled(true);
                    return;
                }

                if ( plugin.getPermissionsManager().has(victim, "simpleclans.mod.nopvpinwar") ) {
                    event.setCancelled(true);
                    return;
                }

                // if not warring no pvp
                if (!aclan.isWarring(vclan)) {
                    event.setCancelled(true);
                    return;
                }
            }

            if (vclan != null && aclan != null) {
                    // personal ff enabled, allow damage
                    if (vcp.isFriendlyFire()) return;

                    // clan ff enabled, allow damage
                    if (vclan.isFriendlyFire()) return;

                    // same clan, deny damage
                    if (vclan.equals(aclan)) {
                        event.setCancelled(true);
                        return;
                    }
                    // ally clan, deny damage
                    if (vclan.isAlly(aclan)) event.setCancelled(true);
                    
            } else {
                // not part of a clan - check if safeCivilians is set
                if (settings.getSafeCivilians()) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
