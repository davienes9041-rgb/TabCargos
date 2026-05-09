package com.tabcargos.plugin.managers;

import com.tabcargos.plugin.TabCargos;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class TabManager {

    private final TabCargos plugin;
    private Scoreboard scoreboard;

    public TabManager(TabCargos plugin) {
        this.plugin = plugin;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    }

    public void atualizarTodosTAB() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            atualizarTAB(player);
        }
    }

    public void atualizarTAB(Player player) {
        CargoManager cm = plugin.getCargoManager();
        String cargo = cm.getCargo(player.getUniqueId());
        String prefixo = cm.getPrefixo(cargo);
        String corNome = cm.getCorNome(cargo);
        int prioridade = cm.getPrioridade(cargo);

        // Nome do team: prioridade formatada + uuid curto (unico)
        String teamName = String.format("%03d", prioridade) + player.getName().substring(0, Math.min(12, player.getName().length()));
        // Team names max 16 chars no Scoreboard
        if (teamName.length() > 16) teamName = teamName.substring(0, 16);

        // Remove player de qualquer team anterior
        removerDeTeamsAntigos(player);

        // Cria ou pega o team
        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }

        team.setPrefix(prefixo + " ");
        team.setSuffix("");
        team.addEntry(player.getName());

        // Aplica o scoreboard para todos os jogadores
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.setScoreboard(scoreboard);
        }

        // Header e footer do TAB
        atualizarHeaderFooter(player, cargo, prefixo);
    }

    private void removerDeTeamsAntigos(Player player) {
        for (Team team : scoreboard.getTeams()) {
            if (team.hasEntry(player.getName())) {
                team.removeEntry(player.getName());
                if (team.getEntries().isEmpty()) {
                    team.unregister();
                }
                break;
            }
        }
    }

    private void atualizarHeaderFooter(Player player, String cargo, String prefixo) {
        String header = "§6§l✦ §fServidor §6§l✦\n§7Seu cargo: " + prefixo;
        String footer = "\n§7Jogadores online: §a" + Bukkit.getOnlinePlayers().size();
        player.sendPlayerListHeaderAndFooter(
            net.kyori.adventure.text.Component.text(colorir(header)),
            net.kyori.adventure.text.Component.text(colorir(footer))
        );
    }

    public void removerJogador(Player player) {
        removerDeTeamsAntigos(player);
    }

    private String colorir(String texto) {
        return texto.replace("&", "§");
    }
}
