package com.tabcargos.plugin.listeners;

import com.tabcargos.plugin.TabCargos;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final TabCargos plugin;

    public PlayerListener(TabCargos plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        // Pequeno delay para garantir que o cliente carregou o TAB
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            plugin.getTabManager().atualizarTAB(event.getPlayer());
            // Atualiza TAB de todos para o novo jogador aparecer certo
            plugin.getTabManager().atualizarTodosTAB();
        }, 5L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getTabManager().removerJogador(event.getPlayer());
    }
}
