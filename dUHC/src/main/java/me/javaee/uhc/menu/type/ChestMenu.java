package me.javaee.uhc.menu.type;

import lombok.Getter;
import me.javaee.uhc.menu.IMenu;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

@Getter
public abstract class ChestMenu<E extends JavaPlugin> implements IMenu {

    protected E plugin;

    public final Inventory inventory;

    public ChestMenu(int size) {
        loadPlugin();

        inventory = plugin.getServer().createInventory(this, size, getTitle().length() > 32 ? getTitle().substring(0, 32) : getTitle());
    }

    private void loadPlugin() {
        Class<E> clazz = ((Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);

        Method method = null;
        try {
            method = clazz.getDeclaredMethod("getInstance", null);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        try {
            plugin = (E) method.invoke(null, method.getParameterTypes());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}