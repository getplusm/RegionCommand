package t.me.p1azmer.plugin.regioncommand.region;

import org.bukkit.World;
import t.me.p1azmer.plugin.regioncommand.utils.vector.BlockVector2;
import t.me.p1azmer.plugin.regioncommand.utils.vector.BlockVector3;
import t.me.p1azmer.plugin.regioncommand.utils.vector.Vector3;

import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Represents a physical shape.
 */
public interface Territory extends Iterable<BlockVector3>, Cloneable {

    /**
     * Get the lower point of a region.
     *
     * @return min. point
     */
    BlockVector3 getMinimumPoint();

    /**
     * Get the upper point of a region.
     *
     * @return max. point
     */
    BlockVector3 getMaximumPoint();

    /**
     * Get the bounding box of this region as a {@link CuboidRegion}.
     *
     * @return the bounding box
     */
    default CuboidRegion getBoundingBox() {
        return new CuboidRegion(getMinimumPoint(), getMaximumPoint());
    }

    /**
     * Get the center point of a region.
     * Note: Coordinates will not be integers
     * if the corresponding lengths are even.
     *
     * @return center point
     */
    Vector3 getCenter();

    /**
     * Get the number of blocks in the region.
     *
     * @return number of blocks
     * @deprecated use {@link Territory#getVolume()} to prevent overflows
     */
    @Deprecated
    default int getArea() {
        return (int) getVolume();
    }

    /**
     * Get the number of blocks in the region.
     *
     * @return number of blocks
     */
    default long getVolume() {
        return getArea();
    }

    /**
     * Get X-size.
     *
     * @return width
     */
    int getWidth();

    /**
     * Get Y-size.
     *
     * @return height
     */
    int getHeight();

    /**
     * Get Z-size.
     *
     * @return length
     */
    int getLength();

    /**
     * Expand the region.
     *
     * @param changes array/arguments with multiple related changes
     * @throws RegionOperationException if the operation cannot be performed
     */
    void expand(BlockVector3... changes) throws RegionOperationException;

    /**
     * Contract the region.
     *
     * @param changes array/arguments with multiple related changes
     * @throws RegionOperationException if the operation cannot be performed
     */
    void contract(BlockVector3... changes) throws RegionOperationException;

    /**
     * Shift the region.
     *
     * @param change the change
     * @throws RegionOperationException if the operation cannot be performed
     */
    void shift(BlockVector3 change) throws RegionOperationException;

    /**
     * Returns true based on whether the region contains the point.
     *
     * @param position the position
     * @return true if contained
     */
    boolean contains(BlockVector3 position);

    /**
     * Get a list of chunks.
     *
     * @return a list of chunk coordinates
     */
    Set<BlockVector2> getChunks();

    /**
     * Return a list of 16*16*16 chunks in a region.
     *
     * @return the chunk cubes this region overlaps with
     */
    Set<BlockVector3> getChunkCubes();

    /**
     * Sets the world that the selection is in.
     *
     * @return the world, or null
     */
    @Nullable
    World getWorld();

    /**
     * Sets the world that the selection is in.
     *
     * @param world the world, which may be null
     */
    void setWorld(@Nullable World world);

    /**
     * Make a clone of the region.
     *
     * @return a cloned version
     */
    Territory clone();

    /**
     * Polygonizes a cross-section or a 2D projection of the region orthogonal to the Y axis.
     *
     * @param maxPoints maximum number of points to generate. -1 for no limit.
     * @return the points.
     */
    List<BlockVector2> polygonize(int maxPoints);
}