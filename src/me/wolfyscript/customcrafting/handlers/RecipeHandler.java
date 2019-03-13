package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_configs.CraftConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.CustomConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.FurnaceConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.ItemConfig;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.recipes.*;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;

import java.io.File;
import java.security.Key;
import java.util.*;

public class RecipeHandler {

    private List<Recipe> allRecipes = new ArrayList<>();

    private List<CustomRecipe> customRecipes = new ArrayList<>();
    private List<CustomItem> customItems = new ArrayList<>();

    private ArrayList<String> disabledRecipes = new ArrayList<>();
    private HashMap<String, List<String>> overrideRecipes = new HashMap<>();
    private HashMap<String, List<String>> extendRecipes = new HashMap<>();

    private ConfigAPI configAPI;
    private WolfyUtilities api;

    public RecipeHandler(WolfyUtilities api) {
        this.configAPI = api.getConfigAPI();
        this.api = api;
    }

    private void loadConfig(String subfolder, String type) {
        File workbench = new File(CustomCrafting.getInst().getDataFolder() + File.separator + "recipes" + File.separator + subfolder + File.separator + type);
        File[] files = workbench.listFiles((dir, name) -> name.split("\\.").length > 1 && name.split("\\.")[name.split("\\.").length - 1].equalsIgnoreCase("yml") && !name.split("\\.")[0].equals("example"));
        if (files != null) {
            api.sendConsoleMessage("    " + type + ":");
            for (File file : files) {
                String key = file.getParentFile().getParentFile().getName().toLowerCase();
                String name = file.getName().split("\\.")[0].toLowerCase();
                try {
                    switch (type) {
                        case "workbench":
                            CraftConfig config = new CraftConfig(configAPI, key, name);
                            addOverrideRecipe(config.getId(), config.getOverrides());
                            addExtendRecipe(config.getId(), config.getExtend());
                            if (config.isShapeless()) {
                                ShapelessCraftRecipe recipe = new ShapelessCraftRecipe(config);
                                recipe.load();
                                registerRecipe(recipe);
                            } else {
                                ShapedCraftRecipe recipe = new ShapedCraftRecipe(config);
                                recipe.load();
                                registerRecipe(recipe);
                            }
                            break;
                        case "furnace":
                            FurnaceConfig furnaceConfig = new FurnaceConfig(configAPI, key, name);
                            addOverrideRecipe(furnaceConfig.getId(), furnaceConfig.getOverrides());
                            addExtendRecipe(furnaceConfig.getId(), furnaceConfig.getExtend());

                            FurnaceCRecipe furnaceCRecipe = new FurnaceCRecipe(furnaceConfig);


                            registerRecipe(furnaceCRecipe);
                            break;
                        case "items":
                            customItems.add(new CustomItem(new ItemConfig(configAPI, key, name)));
                            break;
                        case "fuel":

                            break;
                    }
                } catch (Exception ex) {
                    api.sendConsoleMessage("-------------------------------------------------");
                    api.sendConsoleMessage("Error loading Contents for: " + key + ":" + name);
                    api.sendConsoleMessage("    Type: " + type);
                    api.sendConsoleMessage("    Message: " + ex.getMessage());
                    api.sendConsoleMessage("    Cause: " + ex.getCause());
                    api.sendConsoleMessage("You should check the config for empty settings ");
                    api.sendConsoleMessage("e.g. No set Result or Source Item!");
                    api.sendConsoleMessage("-------------------------------------------------");
                    ex.printStackTrace();
                }
                api.sendConsoleMessage("      - " + name);
            }
        }
    }

    public void onSave(){
        CustomCrafting.getConfigHandler().getConfig().setDisabledrecipes(disabledRecipes);
        CustomCrafting.getConfigHandler().getConfig().save();
    }

    public void loadConfigs() {
        if (!CustomCrafting.getConfigHandler().getConfig().getDisabledRecipes().isEmpty()) {
            disabledRecipes.addAll(CustomCrafting.getConfigHandler().getConfig().getDisabledRecipes());
        }
        api.sendConsoleMessage("________[Loading Recipes/Items]________");
        File recipesFolder = new File(CustomCrafting.getInst().getDataFolder() + File.separator + "recipes");
        List<File> subFolders = null;
        File[] dirs = recipesFolder.listFiles((dir, name) -> !name.split("\\.")[name.split("\\.").length - 1].equalsIgnoreCase("yml"));
        if (dirs != null) {
            subFolders = new ArrayList<>(Arrays.asList(dirs));
        }
        if (subFolders != null) {
            api.sendConsoleMessage("");
            api.sendConsoleMessage("---------[ITEMS]---------");
            for (File folder : subFolders) {
                api.sendConsoleMessage("- " + folder.getName());
                loadConfig(folder.getName(), "items");
            }
            api.sendConsoleMessage("");
            api.sendConsoleMessage("--------[RECIPES]--------");
            for (File folder : subFolders) {
                api.sendConsoleMessage("- " + folder.getName());
                loadConfig(folder.getName(), "workbench");
                loadConfig(folder.getName(), "furnace");
            }
        }
    }

    public void registerRecipe(CustomRecipe recipe) {
        Bukkit.addRecipe(recipe);
        customRecipes.add(recipe);
    }

    public void injectRecipe(CustomRecipe recipe){
        unregisterRecipe(recipe);
        Bukkit.addRecipe(recipe);
        customRecipes.add(recipe);
    }

    public void unregisterRecipe(String key){
        CustomRecipe customRecipe = getRecipe(key);
        if(customRecipe != null){
            customRecipes.remove(customRecipe);
        }
    }

    public void unregisterRecipe(CustomRecipe customRecipe) {
        customRecipes.remove(customRecipe);
        Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
        List<Recipe> recipes = new ArrayList<>();

        while(recipeIterator.hasNext()){
            Recipe recipe = recipeIterator.next();
            if(!((Keyed) recipe).getKey().toString().equals(customRecipe.getID())){
                recipes.add(recipe);
            }
        }
        Bukkit.clearRecipes();

        Collections.reverse(recipes);

        for(Recipe recipe : recipes){
            Bukkit.addRecipe(recipe);
        }


    }

    /*
        Get all the ShapedRecipes from this group
     */
    public List<CustomRecipe> getRecipeGroup(String group) {
        List<CustomRecipe> groupRecipes = new ArrayList<>();
        for (CustomRecipe recipe : customRecipes) {
            if (recipe.getGroup().equals(group))
                groupRecipes.add(recipe);
        }
        return groupRecipes;
    }

    public List<CustomRecipe> getRecipeGroup(CraftingRecipe recipe) {
        List<CustomRecipe> groupRecipes = new ArrayList<>(getRecipeGroup(recipe.getID()));
        groupRecipes.remove(recipe);
        return groupRecipes;
    }

    public CustomRecipe getRecipe(Recipe recipe) {
        for (CustomRecipe craftingRecipe : customRecipes) {
            if (recipe instanceof Keyed) {
                if (craftingRecipe.getID().equals(((Keyed) recipe).getKey().toString())) {
                    return craftingRecipe;
                }
            }
        }
        return null;
    }

    public CustomRecipe getRecipe(String key) {
        for (CustomRecipe craftingRecipe : customRecipes) {
            if (craftingRecipe.getID().equals(key)) {
                return craftingRecipe;
            }
        }
        return null;
    }

    public CraftingRecipe getCraftingRecipe(String key) {
        CustomRecipe customRecipe = getRecipe(key);
        return customRecipe instanceof CraftingRecipe ? (CraftingRecipe) customRecipe : null;
    }

    public List<FurnaceCRecipe> getFurnaceRecipes(ItemStack source) {
        List<FurnaceCRecipe> recipes = new ArrayList<>();
        for (FurnaceCRecipe recipe : getFurnaceRecipes()) {
            if (recipe.getSource().getType() == source.getType()) {
                recipes.add(recipe);
            }
        }
        return recipes;
    }

    public List<FurnaceCRecipe> getFurnaceRecipes() {
        List<FurnaceCRecipe> recipes = new ArrayList<>();
        for (CustomRecipe recipe : customRecipes) {
            if (recipe instanceof FurnaceCRecipe) {
                recipes.add((FurnaceCRecipe) recipe);
            }
        }
        return recipes;
    }

    public List<CustomRecipe> getRecipes() {
        return customRecipes;
    }



    public FurnaceCRecipe getFurnaceRecipe(String key) {
        for (FurnaceCRecipe recipe : getFurnaceRecipes()) {
            if (recipe.getID().equals(key)) {
                return recipe;
            }
        }
        return null;
    }

    public FurnaceCRecipe getFurnaceRecipe(ItemStack source) {
        for (FurnaceCRecipe recipe : getFurnaceRecipes()) {
            if (recipe.getSource().getType() == source.getType()) {
                return recipe;
            }
        }
        return null;
    }

    public List<CustomItem> getCustomItems() {
        return customItems;
    }

    public CustomItem getCustomItem(String key) {
        for (CustomItem customItem : customItems) {
            if (customItem.getId().equals(key)) {
                return customItem;
            }
        }
        return null;
    }

    public void addCustomItem(CustomItem item) {
        customItems.add(item);
    }

    public void removeCustomItem(String id) {
        customItems.remove(getCustomItem(id));
    }

    public void removeCustomItem(CustomItem item) {
        customItems.remove(item);
    }

    public CustomItem getCustomItem(String key, String name) {
        return getCustomItem(key + ":" + name);
    }

    public ArrayList<String> getDisabledRecipes() {
        return disabledRecipes;
    }

    public HashMap<String, List<String>> getOverrideRecipes() {
        return overrideRecipes;
    }

    public void addOverrideRecipe(String key, List<String> toOverride) {
        if(!toOverride.isEmpty()){
            for (String override : toOverride) {
                List<String> overrides = overrideRecipes.getOrDefault(override, new ArrayList<>());
                if (!overrides.contains(key))
                    overrides.add(key);
                overrideRecipes.put(override, overrides);
            }
        }
    }

    public HashMap<String, List<String>> getExtendRecipes() {
        return extendRecipes;
    }

    public void addExtendRecipe(String key, String toExtend) {
        if(!toExtend.isEmpty()){
            List<String> extendsList = extendRecipes.getOrDefault(toExtend, new ArrayList<>());
            if(!extendsList.contains(key))
                extendsList.add(key);
            extendRecipes.put(toExtend, extendsList);
        }
    }

    public List<Recipe> getAllRecipes() {
        allRecipes.clear();
        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        while(iterator.hasNext()){
            Recipe recipe = iterator.next();
            if(!(recipe instanceof FurnaceRecipe)){
                allRecipes.add(recipe);
            }
        }
        return allRecipes;
    }
}
