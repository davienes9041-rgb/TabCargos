package com.tabcargos.plugin.managers;

import com.tabcargos.plugin.TabCargos;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CargoManager {

    private final TabCargos plugin;
    private final Map<UUID, String> jogadorCargos = new HashMap<>();
    private File dadosFile;
    private FileConfiguration dadosConfig;

    public CargoManager(TabCargos plugin) {
        this.plugin = plugin;
    }

    public void carregarDados() {
        dadosFile = new File(plugin.getDataFolder(), "dados.yml");
        if (!dadosFile.exists()) {
            try {
                dadosFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Erro ao criar dados.yml: " + e.getMessage());
            }
        }
        dadosConfig = YamlConfiguration.loadConfiguration(dadosFile);

        if (dadosConfig.contains("jogadores")) {
            for (String uuid : dadosConfig.getConfigurationSection("jogadores").getKeys(false)) {
                String cargo = dadosConfig.getString("jogadores." + uuid);
                jogadorCargos.put(UUID.fromString(uuid), cargo);
            }
        }
        plugin.getLogger().info("Dados carregados: " + jogadorCargos.size() + " jogador(es).");
    }

    public void salvarDados() {
        for (Map.Entry<UUID, String> entry : jogadorCargos.entrySet()) {
            dadosConfig.set("jogadores." + entry.getKey().toString(), entry.getValue());
        }
        try {
            dadosConfig.save(dadosFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Erro ao salvar dados.yml: " + e.getMessage());
        }
    }

    // Retorna cargo do jogador (ou o padrao se nao tiver)
    public String getCargo(UUID uuid) {
        return jogadorCargos.getOrDefault(uuid, getCargoPadrao());
    }

    public void setCargo(UUID uuid, String cargo) {
        jogadorCargos.put(uuid, cargo);
        salvarDados();
    }

    public boolean cargoExiste(String nome) {
        FileConfiguration config = plugin.getConfig();
        return config.contains("cargos." + nome);
    }

    public String getPrefixo(String cargo) {
        String prefixo = plugin.getConfig().getString("cargos." + cargo + ".prefixo", "&7[" + cargo + "]");
        return colorir(prefixo);
    }

    public String getCorNome(String cargo) {
        String cor = plugin.getConfig().getString("cargos." + cargo + ".cor-nome", "&f");
        return colorir(cor);
    }

    public int getPrioridade(String cargo) {
        return plugin.getConfig().getInt("cargos." + cargo + ".prioridade", 100);
    }

    public String getCargoPadrao() {
        return plugin.getConfig().getString("cargo-padrao", "Membro");
    }

    public Set<String> getCargosDisponiveis() {
        return plugin.getConfig().getConfigurationSection("cargos").getKeys(false);
    }

    public void criarCargo(String nome, String prefixo, String corNome, int prioridade) {
        plugin.getConfig().set("cargos." + nome + ".prefixo", prefixo);
        plugin.getConfig().set("cargos." + nome + ".cor-nome", corNome);
        plugin.getConfig().set("cargos." + nome + ".prioridade", prioridade);
        plugin.saveConfig();
    }

    public void deletarCargo(String nome) {
        plugin.getConfig().set("cargos." + nome, null);
        plugin.saveConfig();
        // Resetar jogadores que tinham esse cargo
        for (Map.Entry<UUID, String> entry : jogadorCargos.entrySet()) {
            if (entry.getValue().equals(nome)) {
                entry.setValue(getCargoPadrao());
            }
        }
        salvarDados();
    }

    private String colorir(String texto) {
        return texto.replace("&", "§");
    }
}
