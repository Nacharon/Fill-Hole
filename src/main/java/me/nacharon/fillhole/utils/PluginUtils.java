package me.nacharon.fillhole.utils;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;


/**
 * Utility class for creating colored text components.
 * Provides helper methods to generate text in different colors.
 */
public class PluginUtils {

    /**
     * Creates a red-colored text component.
     *
     * @param message The message to display.
     * @return A red-colored TextComponent.
     */
    public static @NotNull TextComponent textRed(String message) {
        return Component.text(message).color(TextColor.color(NamedTextColor.RED));
    }

    /**
     * Creates a green-colored text component.
     *
     * @param message The message to display.
     * @return A green-colored TextComponent.
     */
    public static @NotNull TextComponent textGreen(String message) {
        return Component.text(message).color(TextColor.color(NamedTextColor.GREEN));
    }

    /**
     * Creates a blue-colored text component.
     *
     * @param message The message to display.
     * @return A blue-colored TextComponent.
     */
    public static @NotNull TextComponent textBlue(String message) {
        return Component.text(message).color(TextColor.color(NamedTextColor.BLUE));
    }

    /**
     * Creates a yellow-colored text component.
     *
     * @param message The message to display.
     * @return A yellow-colored TextComponent.
     */
    public static @NotNull TextComponent textYellow(String message) {
        return Component.text(message).color(TextColor.color(NamedTextColor.YELLOW));
    }

    /**
     * Creates a gray-colored text component.
     *
     * @param message The message to display.
     * @return A gray-colored TextComponent.
     */
    public static @NotNull TextComponent textGray(String message) {
        return Component.text(message).color(TextColor.color(NamedTextColor.GRAY));
    }
}