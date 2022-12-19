package t.me.p1azmer.plugin.regioncommand.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.aves.engine.api.manager.IPlaceholder;
import t.me.p1azmer.plugin.regioncommand.Placeholders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.UnaryOperator;

public class Cuboid implements IPlaceholder {

    private int xMin;
    private int xMax;
    private int yMin;
    private int yMax;
    private int zMin;
    private int zMax;
    private double xMinCentered;
    private double xMaxCentered;
    private double yMinCentered;
    private double yMaxCentered;
    private double zMinCentered;
    private double zMaxCentered;
    private World world;

    public Cuboid(final Location point1, final Location point2) {
        this.xMin = Math.min(point1.getBlockX(), point2.getBlockX());
        this.xMax = Math.max(point1.getBlockX(), point2.getBlockX());
        this.yMin = Math.min(point1.getBlockY(), point2.getBlockY());
        this.yMax = Math.max(point1.getBlockY(), point2.getBlockY());
        this.zMin = Math.min(point1.getBlockZ(), point2.getBlockZ());
        this.zMax = Math.max(point1.getBlockZ(), point2.getBlockZ());
        this.world = point1.getWorld();
        this.xMinCentered = this.xMin + 0.5;
        this.xMaxCentered = this.xMax + 0.5;
        this.yMinCentered = this.yMin + 0.5;
        this.yMaxCentered = this.yMax + 0.5;
        this.zMinCentered = this.zMin + 0.5;
        this.zMaxCentered = this.zMax + 0.5;
    }

    public void setFirst(Location point1) {
        this.xMin = point1.getBlockX();
        this.yMin = point1.getBlockY();
        this.zMin = point1.getBlockZ();
        this.world = point1.getWorld();
        this.xMinCentered = this.xMin + 0.5;
        this.yMinCentered = this.yMin + 0.5;
        this.zMinCentered = this.zMin + 0.5;
    }

    public void setSecond(Location point2) {
        this.xMax = point2.getBlockX();
        this.yMax = point2.getBlockY();
        this.zMax = point2.getBlockZ();
        this.xMaxCentered = this.xMax + 0.5;
        this.yMaxCentered = this.yMax + 0.5;
        this.zMaxCentered = this.zMax + 0.5;
    }

    public Iterator<Block> blockList() {
        List<Block> bL = new ArrayList<>(this.getTotalBlockSize());
        for (int x = this.xMin; x <= this.xMax; ++x) {
            for (int y = this.yMin; y <= this.yMax; ++y) {
                for (int z = this.zMin; z <= this.zMax; ++z) {
                    Block b = this.world.getBlockAt(x, y, z);
                    bL.add(b);
                }
            }
        }
        return bL.iterator();
    }

    public Location getCenter() {
        return new Location(this.world, (this.xMax - this.xMin) / 2 + this.xMin, (this.yMax - this.yMin) / 2 + this.yMin, (this.zMax - this.zMin) / 2 + this.zMin);
    }

    public double getDistance() {
        return this.getPoint1().distance(this.getPoint2());
    }

    public double getDistanceSquared() {
        return this.getPoint1().distanceSquared(this.getPoint2());
    }

    public int getHeight() {
        return this.yMax - this.yMin + 1;
    }

    public Location getPoint1() {
        return new Location(this.world, this.xMin, this.yMin, this.zMin);
    }

    public Location getPoint2() {
        return new Location(this.world, this.xMax, this.yMax, this.zMax);
    }

    public Location getRandomLocation() {
        final Random rand = new Random();
        final int x = rand.nextInt(Math.abs(this.xMax - this.xMin) + 1) + this.xMin;
        final int y = rand.nextInt(Math.abs(this.yMax - this.yMin) + 1) + this.yMin;
        final int z = rand.nextInt(Math.abs(this.zMax - this.zMin) + 1) + this.zMin;
        return new Location(this.world, x, y, z);
    }

    public int getTotalBlockSize() {
        return this.getHeight() * this.getXWidth() * this.getZWidth();
    }

    public int getXWidth() {
        return this.xMax - this.xMin + 1;
    }

    public int getZWidth() {
        return this.zMax - this.zMin + 1;
    }

    public boolean isIn(final Location loc) {
        return loc.getWorld() == this.world && loc.getBlockX() >= this.xMin && loc.getBlockX() <= this.xMax && loc.getBlockY() >= this.yMin && loc.getBlockY() <= this.yMax && loc
                .getBlockZ() >= this.zMin && loc.getBlockZ() <= this.zMax;
    }

    public boolean isIn(final Player player) {
        return this.isIn(player.getLocation());
    }

    public boolean isInWithMarge(final Location loc, final double marge) {
        return loc.getWorld() == this.world &&
                loc.getX() >= this.xMinCentered - marge &&
                loc.getX() <= this.xMaxCentered + marge &&
                loc.getY() >= this.yMinCentered - marge &&
                loc.getY() <= this.yMaxCentered + marge &&
                loc.getZ() >= this.zMinCentered - marge &&
                loc.getZ() <= this.zMaxCentered + marge;
    }

    @Override
    public @NotNull UnaryOperator<String> replacePlaceholders() {
        return s -> s
                .replace(Placeholders.PLACEHOLDER_REGION_CUBOID_FIRST, "&6Мир: " + this.world.getName() + ", X=" + this.xMin + ", Y=" + yMin + ", Z=" + zMin)
                .replace(Placeholders.PLACEHOLDER_REGION_CUBOID_SECOND, "&6Мир: " + this.world.getName() + ", X=" + this.xMax + ", Y=" + yMax + ", Z=" + zMax)
                ;
    }
}