package me.imlukas.devnicschatplugin.utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GuiUtil {


    public static void fillBorder(Inventory inv, ItemStack border_item, int rows) {
        for (int row = 0; row < rows; row++) {
            int index = row * 9;
            for (int slot = index; slot < index + 9; slot++) {
                if (row == 0 || row == rows - 1 || (index == slot || slot == index + 8)) {
                    inv.setItem(slot, border_item);
                }
            }
        }
    }

    public static void makeCenterGrid(Inventory inv, ItemStack item, int rows) {
        for (int row = 0; row < rows; row++) {
            int index = row * 9;
            for (int slot = index; slot < index + 9; slot++) {
                if ((rows == 4 && row == 1 || rows == 4 && row == 2) || (rows == 6 && row == 2 || rows == 6 && row == 3) &&
                        (slot == index + 3 || slot == index + 4 || slot == index + 5)) {
                    inv.setItem(slot, item);
                } else if ((rows == 3 && row == 1) || (rows == 5 && row == 1 || rows == 5 && row == 2 || rows == 5 && row == 3) &&
                        (slot == index + 3 || slot == index + 4 || slot == index + 5)) {
                    inv.setItem(slot, item);
                }
            }
        }
    }

    public static List<Integer> obtainBorders(int rows) {
        List<Integer> positions = new ArrayList<>();
        for (int row = 0; row < rows; row++) {
            int index = row * 9;
            for (int slot = index; slot < index + 9; slot++) {
                if (row == 0 || row == rows - 1 || (index == slot || slot == index + 8)) {
                    positions.add(slot);
                }
            }
        }
        return positions;
    }


}
