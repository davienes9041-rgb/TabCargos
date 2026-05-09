package com.tabcargos.plugin.commands;

import com.tabcargos.plugin.TabCargos;
import com.tabcargos.plugin.managers.CargoManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class CargoCommand implements CommandExecutor, TabCompleter {

    private final TabCargos plugin;
    private final CargoManager cm;

    public CargoCommand(TabCargos plugin) {
        this.plugin = plugin;
        this.cm = plugin.getCargoManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("tabcargos.admin")) {
            sender.sendMessage(colorir("&cVoce nao tem permissao para usar este comando."));
            return true;
        }

        if (args.length == 0) {
            enviarAjuda(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {

            // /cargo set <jogador> <cargo>
            case "set" -> {
                if (args.length < 3) {
                    sender.sendMessage(colorir("&cUso: /cargo set <jogador> <cargo>"));
                    return true;
                }
                Player alvo = Bukkit.getPlayer(args[1]);
                if (alvo == null) {
                    sender.sendMessage(colorir("&cJogador &e" + args[1] + " &cnao encontrado ou offline."));
                    return true;
                }
                String cargo = args[2];
                if (!cm.cargoExiste(cargo)) {
                    sender.sendMessage(colorir("&cCargo &e" + cargo + " &cnao existe! Use /cargo criar para criar um."));
                    return true;
                }
                cm.setCargo(alvo.getUniqueId(), cargo);
                plugin.getTabManager().atualizarTAB(alvo);
                plugin.getTabManager().atualizarTodosTAB();
                sender.sendMessage(colorir("&aCargo de &e" + alvo.getName() + " &adefinido para &e" + cargo + "&a!"));
                alvo.sendMessage(colorir("&aSeu cargo foi alterado para: &e" + cm.getPrefixo(cargo)));
            }

            // /cargo ver <jogador>
            case "ver" -> {
                if (args.length < 2) {
                    sender.sendMessage(colorir("&cUso: /cargo ver <jogador>"));
                    return true;
                }
                Player alvo = Bukkit.getPlayer(args[1]);
                if (alvo == null) {
                    sender.sendMessage(colorir("&cJogador &e" + args[1] + " &cnao encontrado ou offline."));
                    return true;
                }
                String cargo = cm.getCargo(alvo.getUniqueId());
                sender.sendMessage(colorir("&eCargo de &f" + alvo.getName() + "&e: " + cm.getPrefixo(cargo) + " &7(" + cargo + ")"));
            }

            // /cargo lista
            case "lista" -> {
                Set<String> cargos = cm.getCargosDisponiveis();
                sender.sendMessage(colorir("&6=== Cargos Disponiveis ==="));
                for (String c : cargos) {
                    sender.sendMessage(colorir("  &7- " + cm.getPrefixo(c) + " &8(prioridade: " + cm.getPrioridade(c) + ")"));
                }
                sender.sendMessage(colorir("&6========================="));
            }

            // /cargo criar <nome> <prefixo> <cor-nome> <prioridade>
            case "criar" -> {
                if (args.length < 5) {
                    sender.sendMessage(colorir("&cUso: /cargo criar <nome> <prefixo> <cor-nome> <prioridade>"));
                    sender.sendMessage(colorir("&7Exemplo: /cargo criar VIP &a[VIP] &a 90"));
                    return true;
                }
                String nome = args[1];
                if (cm.cargoExiste(nome)) {
                    sender.sendMessage(colorir("&cEsse cargo ja existe!"));
                    return true;
                }
                String prefixo = args[2].replace("_", " ");
                String corNome = args[3];
                int prioridade;
                try {
                    prioridade = Integer.parseInt(args[4]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(colorir("&cPrioridade deve ser um numero inteiro!"));
                    return true;
                }
                cm.criarCargo(nome, prefixo, corNome, prioridade);
                sender.sendMessage(colorir("&aCargo &e" + nome + " &acriado com sucesso!"));
                plugin.getTabManager().atualizarTodosTAB();
            }

            // /cargo deletar <nome>
            case "deletar" -> {
                if (args.length < 2) {
                    sender.sendMessage(colorir("&cUso: /cargo deletar <nome>"));
                    return true;
                }
                String nome = args[1];
                if (!cm.cargoExiste(nome)) {
                    sender.sendMessage(colorir("&cEsse cargo nao existe!"));
                    return true;
                }
                if (nome.equalsIgnoreCase(cm.getCargoPadrao())) {
                    sender.sendMessage(colorir("&cVoce nao pode deletar o cargo padrao!"));
                    return true;
                }
                cm.deletarCargo(nome);
                sender.sendMessage(colorir("&aCargo &e" + nome + " &adeletado! Jogadores com esse cargo foram resetados para o padrao."));
                plugin.getTabManager().atualizarTodosTAB();
            }

            // /cargo reload
            case "reload" -> {
                plugin.reloadConfig();
                cm.carregarDados();
                plugin.getTabManager().atualizarTodosTAB();
                sender.sendMessage(colorir("&aTabCargos recarregado com sucesso!"));
            }

            default -> enviarAjuda(sender);
        }

        return true;
    }

    private void enviarAjuda(CommandSender sender) {
        sender.sendMessage(colorir("&6=== TabCargos - Ajuda ==="));
        sender.sendMessage(colorir("&e/cargo set <jogador> <cargo> &7- Define o cargo de um jogador"));
        sender.sendMessage(colorir("&e/cargo ver <jogador> &7- Ve o cargo de um jogador"));
        sender.sendMessage(colorir("&e/cargo lista &7- Lista todos os cargos"));
        sender.sendMessage(colorir("&e/cargo criar <nome> <prefixo> <cor> <prioridade> &7- Cria um novo cargo"));
        sender.sendMessage(colorir("&e/cargo deletar <nome> &7- Deleta um cargo"));
        sender.sendMessage(colorir("&e/cargo reload &7- Recarrega o plugin"));
        sender.sendMessage(colorir("&6========================"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(Arrays.asList("set", "ver", "lista", "criar", "deletar", "reload"));
        } else if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "set", "ver" -> Bukkit.getOnlinePlayers().forEach(p -> completions.add(p.getName()));
                case "deletar" -> completions.addAll(cm.getCargosDisponiveis());
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
            completions.addAll(cm.getCargosDisponiveis());
        }

        return completions;
    }

    private String colorir(String texto) {
        return texto.replace("&", "§");
    }
}
