package com.tabcargos.plugin;

import com.tabcargos.plugin.commands.CargoCommand;
import com.tabcargos.plugin.listeners.PlayerListener;
import com.tabcargos.plugin.managers.CargoManager;
import com.tabcargos.plugin.managers.TabManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TabCargos extends JavaPlugin {

    private static TabCargos instance;
    private CargoManager cargoManager;
    private TabManager tabManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        this.cargoManager = new CargoManager(this);
        this.tabManager = new TabManager(this);

        cargoManager.carregarDados();

        getCommand("cargo").setExecutor(new CargoCommand(this));
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        tabManager.atualizarTodosTAB();

        getLogger().info("TabCargos ativado com sucesso!");
    }

    @Override
    public void onDisable() {
        if (cargoManager != null) {
            cargoManager.salvarDados();
        }
        getLogger().info("TabCargos desativado.");
    }

    public static TabCargos getInstance() {
        return instance;
    }

    public CargoManager getCargoManager() {
        return cargoManager;
    }

    public TabManager getTabManager() {
        return tabManager;
    }
}
