package me.wolfyscript.customcrafting.configs.custom_data;

import me.wolfyscript.utilities.api.custom_items.custom_data.CustomData;
import org.bukkit.util.NumberConversions;

import java.util.HashMap;
import java.util.Map;

public class EliteWorkbench extends CustomData {

    private boolean enabled;
    private int gridSize;

    public EliteWorkbench() {
        super("elite_workbench");
        this.enabled = false;
        this.gridSize = 3;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getGridSize() {
        return gridSize;
    }

    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }

    @Override
    public CustomData getDefaultCopy() {
        return new EliteWorkbench();
    }

    @Override
    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("enabled", enabled);
        map.put("gridSize", gridSize);
        return map;
    }

    @Override
    public EliteWorkbench fromMap(Map<String, Object> map) {
        EliteWorkbench eliteWorkbench = new EliteWorkbench();
        eliteWorkbench.setEnabled((Boolean) map.get("enabled"));
        eliteWorkbench.setGridSize(NumberConversions.toInt(map.get("gridSize")));
        return eliteWorkbench;
    }


}