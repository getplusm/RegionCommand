package t.me.p1azmer.plugin.regioncommand.region;

import org.bukkit.World;
import t.me.p1azmer.plugin.regioncommand.utils.vector.BlockVector2;
import t.me.p1azmer.plugin.regioncommand.utils.vector.BlockVector3;
import t.me.p1azmer.plugin.regioncommand.utils.vector.Vector3;

import java.util.*;

public abstract class AbstractRegion implements Territory {

    protected World world;

    public AbstractRegion(World world) {
        this.world = world;
    }

    @Override
    public Vector3 getCenter() {
        return getMinimumPoint().add(getMaximumPoint()).toVector3().divide(2);
    }

    /**
     * Get the iterator.
     *
     * @return iterator of points inside the region
     */
    @Override
    public Iterator<BlockVector3> iterator() {
        return new RegionIterator(this);
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    public void shift(BlockVector3 change) throws RegionOperationException {
        expand(change);
        contract(change);
    }

    @Override
    public AbstractRegion clone() {
        try {
            return (AbstractRegion) super.clone();
        } catch (CloneNotSupportedException exc) {
            return null;
        }
    }

    @Override
    public List<BlockVector2> polygonize(int maxPoints) {
        if (maxPoints >= 0 && maxPoints < 4) {
            throw new IllegalArgumentException("Cannot polygonize an AbstractRegion with no overridden polygonize method into less than 4 points.");
        }

        final BlockVector3 min = getMinimumPoint();
        final BlockVector3 max = getMaximumPoint();

        final List<BlockVector2> points = new ArrayList<>(4);

        points.add(BlockVector2.at(min.getX(), min.getZ()));
        points.add(BlockVector2.at(min.getX(), max.getZ()));
        points.add(BlockVector2.at(max.getX(), max.getZ()));
        points.add(BlockVector2.at(max.getX(), min.getZ()));

        return points;
    }

    @Override
    public long getVolume() {
        BlockVector3 min = getMinimumPoint();
        BlockVector3 max = getMaximumPoint();

        return (max.getX() - min.getX() + 1L)
                * (max.getY() - min.getY() + 1L)
                * (max.getZ() - min.getZ() + 1L);
    }

    /**
     * Get X-size.
     *
     * @return width
     */
    @Override
    public int getWidth() {
        BlockVector3 min = getMinimumPoint();
        BlockVector3 max = getMaximumPoint();

        return max.getX() - min.getX() + 1;
    }

    /**
     * Get Y-size.
     *
     * @return height
     */
    @Override
    public int getHeight() {
        BlockVector3 min = getMinimumPoint();
        BlockVector3 max = getMaximumPoint();

        return max.getY() - min.getY() + 1;
    }

    /**
     * Get Z-size.
     *
     * @return length
     */
    @Override
    public int getLength() {
        BlockVector3 min = getMinimumPoint();
        BlockVector3 max = getMaximumPoint();

        return max.getZ() - min.getZ() + 1;
    }

    /**
     * Get a list of chunks.
     *
     * @return a set of chunks
     */
    @Override
    public Set<BlockVector2> getChunks() {
        final Set<BlockVector2> chunks = new HashSet<>();

        final BlockVector3 min = getMinimumPoint();
        final BlockVector3 max = getMaximumPoint();

        final int minY = min.getBlockY();

        for (int x = min.getBlockX(); x <= max.getBlockX(); ++x) {
            for (int z = min.getBlockZ(); z <= max.getBlockZ(); ++z) {
                if (!contains(BlockVector3.at(x, minY, z))) {
                    continue;
                }

                chunks.add(BlockVector2.at(
                        x >> 4,
                        z >> 4
                ));
            }
        }

        return chunks;
    }

    @Override
    public Set<BlockVector3> getChunkCubes() {
        final Set<BlockVector3> chunks = new HashSet<>();

        final BlockVector3 min = getMinimumPoint();
        final BlockVector3 max = getMaximumPoint();

        for (int x = min.getBlockX(); x <= max.getBlockX(); ++x) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); ++y) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); ++z) {
                    if (!contains(BlockVector3.at(x, y, z))) {
                        continue;
                    }

                    chunks.add(BlockVector3.at(
                            x >> 4,
                            y >> 4,
                            z >> 4
                    ));
                }
            }
        }

        return chunks;
    }

    // Sub-class utilities

    protected final int getWorldMinY() {
        return world == null ? Integer.MIN_VALUE : 0;
    }

    protected final int getWorldMaxY() {
        return world == null ? Integer.MAX_VALUE : 255;
    }

}