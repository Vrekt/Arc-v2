package me.vrekt.arc.lag;

import me.vrekt.arc.Arc;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class ReflectionTick {

    private final String PACKAGE_NAME = Bukkit.getServer().getClass().getPackage().getName();
    private final String SERVER_VERSION = PACKAGE_NAME.substring(PACKAGE_NAME.lastIndexOf('.') + 1);

    private Object server;
    private Field TPS;

    public ReflectionTick() {
        try {
            server = getServerClass("MinecraftServer").getMethod("getServer").invoke(null);
            TPS = server.getClass().getField("recentTps");
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException exception) {
            Arc.getPlugin().getLogger().warning("Could not use reflection to get the server TPS.");
            exception.printStackTrace();
        }
    }

    /**
     * @return the class representing the server.
     */
    private Class<?> getServerClass(String className) {
        try {
            return Class.forName("net.minecraft.server." + SERVER_VERSION + "." + className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return the servers TPS.
     */
    public double getTPS() {
        try {
            double serverTPS[] = ((double[]) TPS.get(server));
            return serverTPS[0];
        } catch (IllegalAccessException exception) {
            Arc.getPlugin().getLogger().warning("Could not get TPS via reflection.");
            exception.printStackTrace();
        }
        return 0;
    }

}
