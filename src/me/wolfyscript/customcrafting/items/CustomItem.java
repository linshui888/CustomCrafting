package me.wolfyscript.customcrafting.items;

import me.wolfyscript.customcrafting.configs.custom_configs.ItemConfig;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class CustomItem extends ItemStack{

    private ItemConfig config;
    private String id;
    private List<String> customData;

    public CustomItem(ItemConfig config){
        super(config.getCustomItem());
        this.config = config;
        this.customData = new ArrayList<>();
        this.id = config.getId();
        this.customData.add("id::"+this.id);
    }

    public CustomItem(ItemStack itemStack){
        super(itemStack);
        this.config = null;
        this.customData = new ArrayList<>();
        this.id = "NULL";
    }

    public CustomItem(Material material){
        this(new ItemStack(material));
    }

    public List<String> getCustomData() {
        return customData;
    }

    public void setCustomData(List<String> customData) {
        this.customData = customData;
    }

    public String getId() {
        return id;
    }

    public ItemConfig getConfig() {
        return config;
    }

    public ItemStack getIDItem(){
        if(getType().equals(Material.AIR)){
           return new ItemStack(Material.AIR);
        }
        ItemStack idItem = new ItemStack(this.clone());
        ItemMeta idItemMeta = idItem.getItemMeta();
        if(idItemMeta.hasDisplayName() && !idItemMeta.getDisplayName().endsWith(":id_item")){
            idItemMeta.setDisplayName(idItemMeta.getDisplayName()+ WolfyUtilities.hideString(":id_item"));
        }else{
            idItemMeta.setDisplayName(WolfyUtilities.hideString("%NO_NAME%")+"§r"+WordUtils.capitalizeFully(idItem.getType().name().replace("_", " ")) + WolfyUtilities.hideString(":id_item"));
        }
        List<String> lore = idItemMeta.hasLore() ? idItemMeta.getLore() : new ArrayList<>();
        lore.add("");
        lore.add("§7[§3§lID_ITEM§r§7]");
        lore.add("§3"+this.id);

        idItemMeta.setLore(lore);
        idItem.setItemMeta(idItemMeta);
        return idItem;
    }
}