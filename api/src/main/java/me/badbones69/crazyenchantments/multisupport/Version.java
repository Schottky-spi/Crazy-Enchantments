package me.badbones69.crazyenchantments.multisupport;

import org.bukkit.Bukkit;

public enum Version implements Comparable<Version> {

    TOO_OLD(-1),
    v1_7_R1(171), v1_7_R2(172), v1_7_R3(173), v1_7_R4(174),
    v1_8_R1(181), v1_8_R2(182), v1_8_R3(183),
    v1_9_R1(191), v1_9_R2(192),
    v1_10_R1(1101),
    v1_11_R1(1111),
    v1_12_R1(1121),
    v1_13_R2(1132),
    v1_14_R1(1141),
    v1_15_R1(1161),
    v1_16_R1(1161), v1_16_R2(1162), v_1_16_R3(1163),
    TOO_NEW(Integer.MAX_VALUE);

    private static Version currentVersion;
    private final int versionInteger;

    Version(int versionInteger) {
        this.versionInteger = versionInteger;
    }

    /**
     *
     * @return Get the server's Minecraft version.
     */
    public static Version getCurrentVersion() {
        if (currentVersion == null) {
            String ver = Bukkit.getServer().getClass().getPackage().getName();
            int v = Integer.parseInt(ver.substring(ver.lastIndexOf('.') + 1).replace("_", "").replace("R", "").replace("v", ""));
            for (Version version : values()) {
                if (version.getVersionInteger() == v) {
                    currentVersion = version;
                    break;
                }
            }
            if (v > Version.getLatestVersion().getVersionInteger()) {
                currentVersion = Version.getLatestVersion();
            }
            if (currentVersion == null) {
                currentVersion = Version.TOO_NEW;
            }
        }
        return currentVersion;
    }

    public boolean isSupported() {
        return this != TOO_NEW && this != TOO_OLD;
    }

    /**
     * Get the latest version allowed by the Version class.
     * @return The latest version.
     */
    public static Version getLatestVersion() {
        final Version[] versions = values();
        return versions[versions.length - 2];
    }

    /**
     *
     * @return The server's minecraft version as an integer.
     */
    public int getVersionInteger() {
        return this.versionInteger;
    }

    /**
     * Checks to see if the current version is newer then the checked version.
     * @param version The version you are checking.
     * @return True if newer then the checked version and false if the same or older.
     */
    public static boolean isNewer(Version version) {
        return getCurrentVersion().compareTo(version) > 0;
    }

    /**
     * Checks to see if the current version is the same as the checked version.
     * @param version The version you are checking.
     * @return True if both the current and checked version is the same and false if otherwise.
     */
    public static boolean isSame(Version version) {
        return getCurrentVersion() == version;
    }

    /**
     * Checks to see if the current version is older then the checked version.
     * @param version The version you are checking.
     * @return True if older then the checked version and false if the same or newer.
     */
    public static boolean isOlder(Version version) {
        return getCurrentVersion().compareTo(version) < 0;
    }

}