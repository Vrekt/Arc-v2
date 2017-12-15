package me.vrekt.arc.config;

import org.bukkit.BanList;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

public class ArcConfiguration {

    private BanList.Type banType;
    private Date banDate;
    private int banTime;

    private int tpsLimit;

    /**
     * Get the data from config file.
     *
     * @param config the configuration file.
     */
    public void read(FileConfiguration config) {

        // read type and time.
        banType = BanList.Type.valueOf(config.getString("ban-type"));
        banTime = config.getInt("ban-time", banTime);

        // convert the days into a date.
        String days = config.getString("ban-days");
        if (Objects.isNull(days) || Objects.equals(days, "0")) {
            banDate = null;
        } else {
            GregorianCalendar c = new GregorianCalendar();
            c.add(GregorianCalendar.DATE, Integer.parseInt(days));
            banDate = c.getTime();
        }

        tpsLimit = config.getInt("tps-limit", tpsLimit);

    }

    /**
     * @return the ban type;
     */
    public BanList.Type getBanType() {
        return banType;
    }

    /**
     * @return the ban date
     */
    public Date getBanDate() {
        return banDate;
    }

    /**
     * @return the ban time;
     */
    public int getBanTime() {
        return banTime;
    }

    /**
     * @return the tps limit.
     */
    public int getTpsLimit() {
        return tpsLimit;
    }
}
