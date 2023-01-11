package t.me.p1azmer.plugin.regioncommand.utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.aves.engine.api.manager.IPlaceholder;
import t.me.p1azmer.plugin.regioncommand.Placeholders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.UnaryOperator;

import static com.google.common.base.Preconditions.checkNotNull;


public class CuboidRegion implements IPlaceholder {

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

    private final List<ParticlePoint> particlePoints = new ArrayList<>();

    public CuboidRegion(@NotNull Location min, @NotNull Location max) {
        checkNotNull(min);
        checkNotNull(max);
        this.xMin = Math.min(min.getBlockX(), max.getBlockX());
        this.xMax = Math.max(max.getBlockX(), min.getBlockX());
        this.yMin = Math.min(min.getBlockY(), max.getBlockY());
        this.yMax = Math.max(max.getBlockY(), min.getBlockY());
        this.zMin = Math.min(min.getBlockZ(), max.getBlockZ());
        this.zMax = Math.max(max.getBlockZ(), min.getBlockZ());
        this.world = min.getWorld();
        this.xMinCentered = this.xMin + 0.5;
        this.xMaxCentered = this.xMax + 0.5;
        this.yMinCentered = this.yMin + 0.5;
        this.yMaxCentered = this.yMax + 0.5;
        this.zMinCentered = this.zMin + 0.5;
        this.zMaxCentered = this.zMax + 0.5;

        calculateParticlesPoint();
    }

    public void setMin(Location pos) {
        if (this.yMax <= pos.getBlockY()) {
            throw new IllegalArgumentException(ChatColor.RED + "[REGION ERROR]: Minimal point of region has more that Y coordinate");
        }
        this.xMin = pos.getBlockX();
        this.yMin = pos.getBlockY();
        this.zMin = pos.getBlockZ();
        this.xMinCentered = this.xMin + 0.5;
        this.yMinCentered = this.yMin + 0.5;
        this.zMinCentered = this.zMin + 0.5;
    }

    public void setMax(Location location) {
        if (this.yMin >= location.getBlockY()) {
            throw new IllegalArgumentException(ChatColor.RED + "[REGION ERROR]: Minimal point of region has more that Y coordinate");
        }
        this.xMax = location.getBlockX();
        this.yMax = location.getBlockY();
        this.zMax = location.getBlockZ();
        this.xMaxCentered = this.xMax + 0.5;
        this.yMaxCentered = this.yMax + 0.5;
        this.zMaxCentered = this.zMax + 0.5;
        calculateParticlesPoint();
    }

    public void calculateParticlesPoint() {
        Vector A = new Vector(0, 0, 0);
        Vector B = new Vector(xMin, 0, 0);
        Vector C = new Vector(0, 0, zMin);
        Vector D = new Vector(0, yMax, 0);
        Vector E = new Vector(0, yMax, zMax);
        Vector F = new Vector(xMax, yMax, 0);
        Vector G = new Vector(xMin, 0, zMin);
        particlePoints.clear();
        particlePoints.add(new ParticlePoint(A, B));
        particlePoints.add(new ParticlePoint(A, C));
        particlePoints.add(new ParticlePoint(A, D));
        particlePoints.add(new ParticlePoint(C, D));
        particlePoints.add(new ParticlePoint(B, D));
        particlePoints.add(new ParticlePoint(C, B));
        particlePoints.add(new ParticlePoint(B, C));
        particlePoints.add(new ParticlePoint(D, C));
        particlePoints.add(new ParticlePoint(D, B));
        particlePoints.add(new ParticlePoint(E, B));
        particlePoints.add(new ParticlePoint(F, C));
        particlePoints.add(new ParticlePoint(G, D));
    }

    public List<ParticlePoint> getParticlePoints() {
        return particlePoints;
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

    public Vector getPostion(double blocksAway, Vector origin, Vector direction) {
        return origin.clone().add(direction.clone().normalize().multiply(blocksAway));
    }

    public ArrayList<Vector> traverse(Vector origin, Vector direction) {
        ArrayList<Vector> positions = new ArrayList<>();
        for (double d = 0; d <= direction.length(); d += 3) { // 3 - blocks after next block
            positions.add(getPostion(d, origin, direction));
        }
        return positions;
    }

    @Override
    public @NotNull UnaryOperator<String> replacePlaceholders() {
        return s -> s
                .replace(Placeholders.PLACEHOLDER_REGION_CUBOID_MIN, "&6Мир: " + this.world.getName() + ", X=" + this.xMin + ", Y=" + this.yMin + ", Z=" + this.zMin)
                .replace(Placeholders.PLACEHOLDER_REGION_CUBOID_MAX, "&6Мир: " + this.world.getName() + ", X=" + this.xMax + ", Y=" + this.yMax + ", Z=" + this.zMax)
                ;
    }

    public static class ParticlePoint {
        final Vector origin, direction;

        ParticlePoint(Vector origin, Vector direction) {
            this.origin = origin;
            this.direction = direction;
        }

        public Vector getDirection() {
            return direction;
        }

        public Vector getOrigin() {
            return origin;
        }
    }
}