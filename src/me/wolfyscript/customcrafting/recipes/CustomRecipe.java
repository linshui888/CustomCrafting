package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.items.CustomItem;
import org.bukkit.inventory.Recipe;

public interface CustomRecipe extends Recipe {

    String getID();
    String getGroup();

    CustomItem getCustomResult();
    String getExtends();

    void load();
    void save();

}
