package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.IngredientContainerButton;
import me.wolfyscript.customcrafting.recipes.conditions.Condition;
import me.wolfyscript.customcrafting.recipes.conditions.Conditions;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import me.wolfyscript.customcrafting.utils.recipe_item.Result;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.entity.Item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomRecipeCauldron extends CustomRecipe<CustomRecipeCauldron> {

    private int cookingTime;
    private int waterLevel;
    private float xp;
    private CustomItem handItem;
    private Ingredient ingredients;
    private boolean dropItems;
    private boolean needsFire;
    private boolean needsWater;

    public CustomRecipeCauldron(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        this.xp = node.path("exp").floatValue();
        this.cookingTime = node.path("cookingTime").asInt(60);
        this.waterLevel = node.path("waterLevel").asInt(1);
        this.needsWater = node.path("water").asBoolean(true);
        this.needsFire = node.path("fire").asBoolean(true);
        {
            JsonNode dropNode = node.path("dropItems");
            this.dropItems = dropNode.path("enabled").asBoolean();
            this.handItem = ItemLoader.load(dropNode.path("handItem"));
        }
        this.ingredients = ItemLoader.loadIngredient(node.path("ingredients"));
    }

    public CustomRecipeCauldron() {
        super();
        this.result = new Result();
        this.ingredients = new Ingredient();
        this.dropItems = true;
        this.xp = 0;
        this.cookingTime = 80;
        this.needsFire = false;
        this.waterLevel = 0;
        this.needsWater = true;
        this.handItem = new CustomItem(Material.AIR);
    }

    public CustomRecipeCauldron(CustomRecipeCauldron customRecipeCauldron) {
        super(customRecipeCauldron);
        this.result = customRecipeCauldron.getResult();
        this.ingredients = customRecipeCauldron.getIngredient();
        this.dropItems = customRecipeCauldron.dropItems();
        this.xp = customRecipeCauldron.getXp();
        this.cookingTime = customRecipeCauldron.getCookingTime();
        this.needsFire = customRecipeCauldron.needsFire();
        this.waterLevel = customRecipeCauldron.getWaterLevel();
        this.needsWater = customRecipeCauldron.needsWater();
        this.handItem = customRecipeCauldron.getHandItem();
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
    }

    public boolean needsFire() {
        return needsFire;
    }

    public void setNeedsFire(boolean needsFire) {
        this.needsFire = needsFire;
    }

    public int getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel(int waterLevel) {
        this.waterLevel = waterLevel;
    }

    public boolean needsWater() {
        return needsWater;
    }

    public void setNeedsWater(boolean needsWater) {
        this.needsWater = needsWater;
    }

    public float getXp() {
        return xp;
    }

    public void setXp(float xp) {
        this.xp = xp;
    }

    public boolean dropItems() {
        return dropItems;
    }

    public void setDropItems(boolean dropItems) {
        this.dropItems = dropItems;
    }

    public List<Item> checkRecipe(List<Item> items) {
        List<Item> validItems = new ArrayList<>();
        for (CustomItem customItem : getIngredient().getChoices()) {
            for (Item item : items) {
                if (customItem.isSimilar(item.getItemStack(), isExactMeta()) && customItem.getAmount() == item.getItemStack().getAmount()) {
                    validItems.add(item);
                    break;
                }
            }
        }
        if (validItems.size() >= ingredients.size()) {
            return validItems;
        }
        return new ArrayList<>();
    }

    public CustomItem getHandItem() {
        return handItem;
    }

    public void setHandItem(CustomItem handItem) {
        this.handItem = handItem;
    }

    @Override
    public RecipeType<CustomRecipeCauldron> getRecipeType() {
        return RecipeType.CAULDRON;
    }

    public Ingredient getIngredient() {
        return getIngredient(0);
    }

    @Override
    public Ingredient getIngredient(int slot) {
        return this.ingredients;
    }

    public void setIngredient(Ingredient ingredients) {
        setIngredient(0, ingredients);
    }

    @Override
    public void setIngredient(int slot, Ingredient ingredient) {
        this.ingredients = ingredient;
    }

    @Override
    public CustomRecipeCauldron clone() {
        return new CustomRecipeCauldron(this);
    }

    @Override
    public void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        super.writeToJson(gen, serializerProvider);
        gen.writeObjectFieldStart("dropItems");
        gen.writeBooleanField("enabled", dropItems);
        gen.writeObjectField("handItem", handItem.getApiReference());
        gen.writeEndObject();
        gen.writeNumberField("exp", xp);
        gen.writeNumberField("cookingTime", cookingTime);
        gen.writeNumberField("waterLevel", waterLevel);
        gen.writeBooleanField("water", needsWater);
        gen.writeBooleanField("fire", needsFire);
        gen.writeObjectField("result", this.result);
        gen.writeObjectField("ingredients", ingredients);
    }

    @Override
    public void prepareMenu(GuiHandler<CCCache> guiHandler, GuiCluster<CCCache> cluster) {
        Ingredient ingredients = getIngredient();
        int invSlot;
        for (int i = 0; i < 6; i++) {
            invSlot = 10 + i + (i / 3) * 6;
            if (i < ingredients.size()) {
                ((IngredientContainerButton) cluster.getButton("ingredient.container_" + invSlot)).setVariants(guiHandler, Collections.singletonList(ingredients.getChoices().get(i)));
            } else {
                ((IngredientContainerButton) cluster.getButton("ingredient.container_" + invSlot)).setVariants(guiHandler, Collections.singletonList(new CustomItem(Material.AIR)));
            }
        }
        ((IngredientContainerButton) cluster.getButton("ingredient.container_25")).setVariants(guiHandler, getResult());
    }

    @Override
    public void renderMenu(GuiWindow<CCCache> guiWindow, GuiUpdate<CCCache> event) {
        int invSlot;
        for (int i = 0; i < 6; i++) {
            invSlot = 10 + i + (i / 3) * 6;
            event.setButton(invSlot, new NamespacedKey("recipe_book", "ingredient.container_" + invSlot));
        }
        List<Condition> conditions = getConditions().getValues().stream().filter(condition -> !condition.getOption().equals(Conditions.Option.IGNORE) && !condition.getId().equals("permission")).toList();
        int startSlot = 9 / (conditions.size() + 1);
        int slot = 0;
        for (Condition condition : conditions) {
            if (!condition.getOption().equals(Conditions.Option.IGNORE)) {
                event.setButton(36 + startSlot + slot, new NamespacedKey("recipe_book", "conditions." + condition.getId()));
                slot += 2;
            }
        }
        event.setButton(23, new NamespacedKey("recipe_book", needsWater() ? "cauldron.water.enabled" : "cauldron.water.disabled"));
        event.setButton(32, new NamespacedKey("recipe_book", needsFire() ? "cauldron.fire.enabled" : "cauldron.fire.disabled"));
        event.setButton(25, new NamespacedKey("recipe_book", "ingredient.container_25"));
    }
}
