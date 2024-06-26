/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.customcrafting.data.cache.items;

import com.wolfyscript.utilities.bukkit.world.items.reference.BukkitStackIdentifier;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackReference;
import com.wolfyscript.utilities.bukkit.world.items.reference.WolfyUtilsStackIdentifier;
import me.wolfyscript.customcrafting.gui.item_creator.tabs.ItemCreatorTab;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class Items implements Serializable {

    private static final long serialVersionUID = 420L;

    private int listPage;
    private String listNamespace;
    private boolean editorWasPreviouslyCancelled = false;

    private int page;

    private ItemStack playerHeadSetting;

    private StackReference original;
    private CustomItem item;
    private boolean recipeItem;
    private NamespacedKey namespacedKey;
    private int craftSlot, variantSlot;
    private boolean saved;

    private EquipmentSlot attributeSlot;
    private AttributeModifier.Operation attribOperation;
    private double attribAmount;
    private String attributeUUID;
    private String attributeName;

    private ItemCreatorTab currentTab;

    public Items() {
        this.listPage = 0;
        this.listNamespace = null;

        this.page = 0;
        this.playerHeadSetting = new ItemStack(Material.AIR);

        this.item = new CustomItem(Material.AIR);
        this.original = StackReference.of(new ItemStack(Material.AIR));
        this.recipeItem = false;
        this.namespacedKey = null;
        this.saved = false;
        this.craftSlot = -1;
        this.variantSlot = -1;

        this.attributeSlot = null;
        this.attribOperation = AttributeModifier.Operation.ADD_NUMBER;
        this.attribAmount = 0.5;
        this.attributeUUID = "";
        this.attributeName = "";

        this.currentTab = null;
    }

    public boolean editorWasPreviouslyCancelled() {
        return editorWasPreviouslyCancelled;
    }

    public void editorWasPreviouslyCancelled(boolean editorWasPreviouslyCancelled) {
        this.editorWasPreviouslyCancelled = editorWasPreviouslyCancelled;
    }

    public StackReference originalReference() {
        return original;
    }

    public Optional<BukkitStackIdentifier> asBukkitIdentifier() {
        return item.stackReference().identifier().map(identifier -> {
            if (identifier instanceof BukkitStackIdentifier bukkitIdentifier) {
                return bukkitIdentifier;
            }
            return null;
        });
    }

    public void modifyOriginalStack(Consumer<ItemStack> stackConsumer) {
        if (item.stackReference().identifier().map(stackIdentifier -> stackIdentifier instanceof BukkitStackIdentifier).orElse(false)) {
            if (ItemUtils.isAirOrNull(item.stackReference().originalStack())) return;
            stackConsumer.accept(item.stackReference().originalStack());
            item.stackReference().swapParser(item.stackReference().parser());
        }
    }

    public Optional<WolfyUtilsStackIdentifier> asWolfyUtilsIdentifier() {
        return item.stackReference().identifier().map(identifier -> {
            if (identifier instanceof WolfyUtilsStackIdentifier wolfyUtilsStackIdentifier) {
                return wolfyUtilsStackIdentifier;
            }
            return null;
        });
    }

    public boolean isBukkitIdentifier() {
        return item.stackReference().identifier().map(identifier -> identifier instanceof BukkitStackIdentifier).orElse(false);
    }

    public void editCustomItem(CustomItem customItem) {
        editorWasPreviouslyCancelled(false);
        setItem(customItem);
        setNamespacedKey(customItem.getNamespacedKey());
        setSaved(true);
    }

    public void createCustomItem(StackReference reference) {
        this.original = reference;
        editorWasPreviouslyCancelled(false);
        setItem(new CustomItem(reference));
        setNamespacedKey(null);
        setSaved(false);
    }

    public void setItem(boolean recipeItem, StackReference reference) {
        this.original = reference;
        setRecipeItem(recipeItem);
        if (reference.identifier().isEmpty()) return;
        if (reference.identifier().get() instanceof WolfyUtilsStackIdentifier wolfyUtilsStackIdentifier) {
            wolfyUtilsStackIdentifier.customItem().ifPresent(this::editCustomItem);
        } else {
            createCustomItem(reference);
        }
    }

    public void editRecipeStackVariant(int variantSlot, StackReference reference) {
        this.variantSlot = variantSlot;
        setRecipeItem(true);
        createCustomItem(reference);
    }

    public CustomItem getItem() {
        return item;
    }

    public void setItem(CustomItem item) {
        this.original = item.stackReference();
        this.item = item;
    }

    public void setRecipeItem(boolean recipeItem) {
        this.recipeItem = recipeItem;
    }

    public boolean isRecipeItem() {
        return recipeItem;
    }

    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    public void setNamespacedKey(NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public int getCraftSlot() {
        return craftSlot;
    }

    public void setCraftSlot(int craftSlot) {
        this.craftSlot = craftSlot;
    }

    public EquipmentSlot getAttributeSlot() {
        return attributeSlot;
    }

    public boolean isAttributeSlot(EquipmentSlot equipmentSlot) {
        return getAttributeSlot() != null && getAttributeSlot().equals(equipmentSlot);
    }

    public void setAttributeSlot(EquipmentSlot attributeSlot) {
        this.attributeSlot = attributeSlot;
    }

    public AttributeModifier.Operation getAttribOperation() {
        return attribOperation;
    }

    public void setAttribOperation(AttributeModifier.Operation attribOperation) {
        this.attribOperation = attribOperation;
    }

    public double getAttribAmount() {
        return attribAmount;
    }

    public void setAttribAmount(double attribAmount) {
        this.attribAmount = attribAmount;
    }

    public String getAttributeUUID() {
        return attributeUUID;
    }

    public void setAttributeUUID(String attributeUUID) {
        this.attributeUUID = attributeUUID;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public AttributeModifier getAttributeModifier() {
        double amount = getAttribAmount();
        EquipmentSlot slot = getAttributeSlot();
        String name = getAttributeName().isEmpty() ? "customcrafting" : getAttributeName();
        UUID uuid = getAttributeUUID().isEmpty() ? UUID.randomUUID() : UUID.fromString(getAttributeUUID());
        AttributeModifier.Operation operation = getAttribOperation();
        return new AttributeModifier(uuid, name, amount, operation, slot);
    }

    public ItemStack getPlayerHeadSetting() {
        return playerHeadSetting;
    }

    public void setPlayerHeadSetting(ItemStack playerHeadSetting) {
        this.playerHeadSetting = playerHeadSetting;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getVariantSlot() {
        return variantSlot;
    }

    public void setVariantSlot(int variantSlot) {
        this.variantSlot = variantSlot;
    }

    public int getListPage() {
        return listPage;
    }

    public int getListPage(int maxPages) {
        if (listPage > maxPages) {
            this.listPage = maxPages;
        }
        return this.listPage;
    }

    public void setListPage(int listPage) {
        this.listPage = listPage;
    }

    public String getListNamespace() {
        return listNamespace;
    }

    public void setListNamespace(String listNamespace) {
        this.listNamespace = listNamespace;
    }

    public ItemCreatorTab getCurrentTab() {
        return currentTab;
    }

    public void setCurrentTab(ItemCreatorTab currentTab) {
        this.currentTab = currentTab;
    }
}
