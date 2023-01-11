package t.me.p1azmer.plugin.regioncommand.region;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.aves.engine.api.manager.IPlaceholder;
import t.me.p1azmer.plugin.regioncommand.Placeholders;
import t.me.p1azmer.plugin.regioncommand.utils.vector.BlockVector2;
import t.me.p1azmer.plugin.regioncommand.utils.vector.BlockVector3;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.UnaryOperator;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class CuboidRegion extends AbstractRegion implements FlatRegion, IPlaceholder {

    private BlockVector3 pos1;
    private BlockVector3 pos2;

    /**
     * Construct a new instance of this cuboid using two corners of the cuboid.
     *
     * @param pos1 the first position
     * @param pos2 the second position
     */
    public CuboidRegion(BlockVector3 pos1, BlockVector3 pos2) {
        this(null, pos1, pos2);
    }

    /**
     * Construct a new instance of this cuboid using two corners of the cuboid.
     *
     * @param world the world
     * @param pos1  the first position
     * @param pos2  the second position
     */
    public CuboidRegion(World world, BlockVector3 pos1, BlockVector3 pos2) {
        super(world);
        checkNotNull(pos1);
        checkNotNull(pos2);
        this.pos1 = pos1;
        this.pos2 = pos2;
        recalculate();
    }

    /**
     * Get the first cuboid-defining corner.
     *
     * @return a position
     */
    public BlockVector3 getPos1() {
        return pos1;
    }

    /**
     * Set the first cuboid-defining corner.
     *
     * @param pos1 a position
     */
    public void setPos1(BlockVector3 pos1) {
        this.pos1 = pos1;
    }

    /**
     * Get the second cuboid-defining corner.
     *
     * @return a position
     */
    public BlockVector3 getPos2() {
        return pos2;
    }

    /**
     * Set the second cuboid-defining corner.
     *
     * @param pos2 a position
     */
    public void setPos2(BlockVector3 pos2) {
        this.pos2 = pos2;
    }

    /**
     * Clamps the cuboid according to boundaries of the world.
     */
    private void recalculate() {
        pos1 = pos1.clampY(getWorldMinY(), getWorldMaxY());
        pos2 = pos2.clampY(getWorldMinY(), getWorldMaxY());
    }

    /**
     * Get a region that contains the faces of this cuboid.
     *
     * @return a new complex region
     */
    public Territory getFaces() {
        BlockVector3 min = getMinimumPoint();
        BlockVector3 max = getMaximumPoint();

        return new RegionIntersection(
                // Project to Z-Y plane
                new CuboidRegion(pos1.withX(min.getX()), pos2.withX(min.getX())),
                new CuboidRegion(pos1.withX(max.getX()), pos2.withX(max.getX())),

                // Project to X-Y plane
                new CuboidRegion(pos1.withZ(min.getZ()), pos2.withZ(min.getZ())),
                new CuboidRegion(pos1.withZ(max.getZ()), pos2.withZ(max.getZ())),

                // Project to the X-Z plane
                new CuboidRegion(pos1.withY(min.getY()), pos2.withY(min.getY())),
                new CuboidRegion(pos1.withY(max.getY()), pos2.withY(max.getY())));
    }

    /**
     * Get a region that contains the walls (all faces but the ones parallel to
     * the X-Z plane) of this cuboid.
     *
     * @return a new complex region
     */
    public Territory getWalls() {
        BlockVector3 min = getMinimumPoint();
        BlockVector3 max = getMaximumPoint();

        return new RegionIntersection(
                // Project to Z-Y plane
                new CuboidRegion(pos1.withX(min.getX()), pos2.withX(min.getX())),
                new CuboidRegion(pos1.withX(max.getX()), pos2.withX(max.getX())),

                // Project to X-Y plane
                new CuboidRegion(pos1.withZ(min.getZ()), pos2.withZ(min.getZ())),
                new CuboidRegion(pos1.withZ(max.getZ()), pos2.withZ(max.getZ())));
    }

    @Override
    public BlockVector3 getMinimumPoint() {
        return pos1.getMinimum(pos2);
    }

    @Override
    public BlockVector3 getMaximumPoint() {
        return pos1.getMaximum(pos2);
    }

    @Override
    public CuboidRegion getBoundingBox() {
        return this;
    }

    @Override
    public int getMinimumY() {
        return Math.min(pos1.getBlockY(), pos2.getBlockY());
    }

    @Override
    public int getMaximumY() {
        return Math.max(pos1.getBlockY(), pos2.getBlockY());
    }

    @Override
    public void expand(BlockVector3... changes) {
        checkNotNull(changes);

        for (BlockVector3 change : changes) {
            if (change.getX() > 0) {
                if (Math.max(pos1.getX(), pos2.getX()) == pos1.getX()) {
                    pos1 = pos1.add(change.getX(), 0, 0);
                } else {
                    pos2 = pos2.add(change.getX(), 0, 0);
                }
            } else {
                if (Math.min(pos1.getX(), pos2.getX()) == pos1.getX()) {
                    pos1 = pos1.add(change.getX(), 0, 0);
                } else {
                    pos2 = pos2.add(change.getX(), 0, 0);
                }
            }

            if (change.getY() > 0) {
                if (Math.max(pos1.getY(), pos2.getY()) == pos1.getY()) {
                    pos1 = pos1.add(0, change.getY(), 0);
                } else {
                    pos2 = pos2.add(0, change.getY(), 0);
                }
            } else {
                if (Math.min(pos1.getY(), pos2.getY()) == pos1.getY()) {
                    pos1 = pos1.add(0, change.getY(), 0);
                } else {
                    pos2 = pos2.add(0, change.getY(), 0);
                }
            }

            if (change.getZ() > 0) {
                if (Math.max(pos1.getZ(), pos2.getZ()) == pos1.getZ()) {
                    pos1 = pos1.add(0, 0, change.getZ());
                } else {
                    pos2 = pos2.add(0, 0, change.getZ());
                }
            } else {
                if (Math.min(pos1.getZ(), pos2.getZ()) == pos1.getZ()) {
                    pos1 = pos1.add(0, 0, change.getZ());
                } else {
                    pos2 = pos2.add(0, 0, change.getZ());
                }
            }
        }

        recalculate();
    }

    @Override
    public void contract(BlockVector3... changes) {
        checkNotNull(changes);

        for (BlockVector3 change : changes) {
            if (change.getX() < 0) {
                if (Math.max(pos1.getX(), pos2.getX()) == pos1.getX()) {
                    pos1 = pos1.add(change.getX(), 0, 0);
                } else {
                    pos2 = pos2.add(change.getX(), 0, 0);
                }
            } else {
                if (Math.min(pos1.getX(), pos2.getX()) == pos1.getX()) {
                    pos1 = pos1.add(change.getX(), 0, 0);
                } else {
                    pos2 = pos2.add(change.getX(), 0, 0);
                }
            }

            if (change.getY() < 0) {
                if (Math.max(pos1.getY(), pos2.getY()) == pos1.getY()) {
                    pos1 = pos1.add(0, change.getY(), 0);
                } else {
                    pos2 = pos2.add(0, change.getY(), 0);
                }
            } else {
                if (Math.min(pos1.getY(), pos2.getY()) == pos1.getY()) {
                    pos1 = pos1.add(0, change.getY(), 0);
                } else {
                    pos2 = pos2.add(0, change.getY(), 0);
                }
            }

            if (change.getZ() < 0) {
                if (Math.max(pos1.getZ(), pos2.getZ()) == pos1.getZ()) {
                    pos1 = pos1.add(0, 0, change.getZ());
                } else {
                    pos2 = pos2.add(0, 0, change.getZ());
                }
            } else {
                if (Math.min(pos1.getZ(), pos2.getZ()) == pos1.getZ()) {
                    pos1 = pos1.add(0, 0, change.getZ());
                } else {
                    pos2 = pos2.add(0, 0, change.getZ());
                }
            }
        }

        recalculate();
    }

    @Override
    public void shift(BlockVector3 change) throws RegionOperationException {
        pos1 = pos1.add(change);
        pos2 = pos2.add(change);

        recalculate();
    }

    @Override
    public Set<BlockVector2> getChunks() {
        Set<BlockVector2> chunks = new HashSet<>();

        BlockVector3 min = getMinimumPoint();
        BlockVector3 max = getMaximumPoint();

        for (int x = min.getBlockX() >> 4; x <= max.getBlockX() >> 4; ++x) {
            for (int z = min.getBlockZ() >> 4; z <= max.getBlockZ() >> 4; ++z) {
                chunks.add(BlockVector2.at(x, z));
            }
        }

        return chunks;
    }

    @Override
    public Set<BlockVector3> getChunkCubes() {
        Set<BlockVector3> chunks = new HashSet<>();

        BlockVector3 min = getMinimumPoint();
        BlockVector3 max = getMaximumPoint();

        for (int x = min.getBlockX() >> 4; x <= max.getBlockX() >> 4; ++x) {
            for (int z = min.getBlockZ() >> 4; z <= max.getBlockZ() >> 4; ++z) {
                for (int y = min.getBlockY() >> 4; y <= max.getBlockY() >> 4; ++y) {
                    chunks.add(BlockVector3.at(x, y, z));
                }
            }
        }

        return chunks;
    }

    @Override
    public boolean contains(BlockVector3 position) {
        BlockVector3 min = getMinimumPoint();
        BlockVector3 max = getMaximumPoint();

        return position.containedWithin(min, max);
    }

    public boolean cross(BlockVector3 pos){
        return this.getMinimumPoint().cross(pos) != null || this.getMaximumPoint().cross(pos) != null;
    }

    public boolean isIn(final Location loc) {
        return loc.getWorld() == this.world &&
                loc.getBlockX() >= this.getMinimumPoint().getBlockX() &&
                loc.getBlockX() <= this.getMaximumPoint().getBlockX() &&
                loc.getBlockY() >= this.getMinimumY() &&
                loc.getBlockY() <= this.getMaximumY() &&
                loc.getBlockZ() >= this.getMinimumPoint().getBlockZ() &&
                loc.getBlockZ() <= this.getMaximumPoint().getBlockZ();
    }

    public boolean isIn(final Player player) {
        return this.isIn(player.getLocation());
    }

    public boolean isInWithMarge(final Location loc, final double marge) {
        return loc.getWorld() == this.world &&
                loc.getX() >= this.getMinimumPoint().getBlockX() - marge &&
                loc.getX() <= this.getMaximumPoint().getBlockX() + marge &&
                loc.getY() >= getMinimumY() - marge &&
                loc.getY() <= getMaximumY() + marge &&
                loc.getZ() >= this.getMinimumPoint().getBlockZ() - marge &&
                loc.getZ() <= this.getMaximumPoint().getBlockZ() + marge;
    }

    @Override
    public Iterator<BlockVector3> iterator() {
        return new Iterator<BlockVector3>() {
            private final BlockVector3 min = getMinimumPoint();
            private final BlockVector3 max = getMaximumPoint();
            private int nextX = min.getBlockX();
            private int nextY = min.getBlockY();
            private int nextZ = min.getBlockZ();

            @Override
            public boolean hasNext() {
                return (nextX != Integer.MIN_VALUE);
            }

            @Override
            public BlockVector3 next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                BlockVector3 answer = BlockVector3.at(nextX, nextY, nextZ);
                if (++nextX > max.getBlockX()) {
                    nextX = min.getBlockX();
                    if (++nextZ > max.getBlockZ()) {
                        nextZ = min.getBlockZ();
                        if (++nextY > max.getBlockY()) {
                            nextX = Integer.MIN_VALUE;
                        }
                    }
                }
                return answer;
            }
        };
    }

    @Override
    public Iterable<BlockVector2> asFlatRegion() {
        return () -> new Iterator<BlockVector2>() {
            private final BlockVector3 min = getMinimumPoint();
            private final BlockVector3 max = getMaximumPoint();
            private int nextX = min.getBlockX();
            private int nextZ = min.getBlockZ();

            @Override
            public boolean hasNext() {
                return (nextX != Integer.MIN_VALUE);
            }

            @Override
            public BlockVector2 next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                BlockVector2 answer = BlockVector2.at(nextX, nextZ);
                if (++nextX > max.getBlockX()) {
                    nextX = min.getBlockX();
                    if (++nextZ > max.getBlockZ()) {
                        nextX = Integer.MIN_VALUE;
                    }
                }
                return answer;
            }
        };
    }

    @Override
    public @NotNull UnaryOperator<String> replacePlaceholders() {
        return s -> s
                .replace(Placeholders.PLACEHOLDER_REGION_CUBOID_MIN, "&6Мир: " + this.world.getName()
                        + ", X=" + this.getMinimumPoint().getX()
                        + ", Y=" + this.getMinimumPoint().getY()
                        + ", Z=" + this.getMinimumPoint().getX())
                .replace(Placeholders.PLACEHOLDER_REGION_CUBOID_MAX, "&6Мир: " + this.world.getName()
                        + ", X=" + this.getMaximumPoint().getX()
                        + ", Y=" + this.getMaximumPoint().getY()
                        + ", Z=" + this.getMaximumPoint().getZ())
                ;
    }

    @Override
    public String toString() {
        return getMinimumPoint() + " - " + getMaximumPoint();
    }

    @Override
    public CuboidRegion clone() {
        return (CuboidRegion) super.clone();
    }

    /**
     * Make a cuboid region out of the given region using the minimum and maximum
     * bounds of the provided region.
     *
     * @param region the region
     * @return a new cuboid region
     */
    public static CuboidRegion makeCuboid(Territory region) {
        checkNotNull(region);
        return new CuboidRegion(region.getMinimumPoint(), region.getMaximumPoint());
    }

    /**
     * Make a cuboid from the center.
     *
     * @param origin  the origin
     * @param apothem the apothem, where 0 is the minimum value to make a 1x1 cuboid
     * @return a cuboid region
     */
    public static CuboidRegion fromCenter(BlockVector3 origin, int apothem) {
        checkNotNull(origin);
        checkArgument(apothem >= 0, "apothem => 0 required");
        BlockVector3 size = BlockVector3.ONE.multiply(apothem);
        return new CuboidRegion(origin.subtract(size), origin.add(size));
    }

}