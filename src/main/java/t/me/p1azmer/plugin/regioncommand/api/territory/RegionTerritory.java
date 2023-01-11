package t.me.p1azmer.plugin.regioncommand.api.territory;

import com.google.common.collect.Lists;
import t.me.p1azmer.plugin.regioncommand.api.type.RegionType;
import t.me.p1azmer.plugin.regioncommand.utils.Normal;
import t.me.p1azmer.plugin.regioncommand.utils.vector.BlockVector2;
import t.me.p1azmer.plugin.regioncommand.utils.vector.BlockVector3;

import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class RegionTerritory // implements Comparable<RegionTerritory>
{
    private static final Pattern VALID_ID_PATTERN = Pattern.compile("^[À-ß-à-ÿA-Za-z0-9_,'\\-\\+/]{1,}$");
    protected BlockVector3 min;
    protected BlockVector3 max;

    private final String id;

    /**
     * Construct a new instance of this region.
     *
     * @param id the name of this region
     * @throws IllegalArgumentException thrown if the ID is invalid (see {@link #isValidId(String)}
     */
    RegionTerritory(String id) { // Package private because we can't have people creating their own region types
        checkNotNull(id);

        if (!isValidId(id)) {
            throw new IllegalArgumentException("Invalid region ID: " + id);
        }

        this.id = Normal.normalize(id);
    }


    /**
     * Return whether this type of region encompasses physical area.
     *
     * @return Whether physical area is encompassed
     */
    public abstract boolean isPhysicalArea();

    /**
     * Set the minimum and maximum points of the bounding box for a region
     *
     * @param points the points to set with at least one entry
     */
    protected void setMinMaxPoints(List<BlockVector3> points) {
        int minX = points.get(0).getBlockX();
        int minY = points.get(0).getBlockY();
        int minZ = points.get(0).getBlockZ();
        int maxX = minX;
        int maxY = minY;
        int maxZ = minZ;

        for (BlockVector3 v : points) {
            int x = v.getBlockX();
            int y = v.getBlockY();
            int z = v.getBlockZ();

            if (x < minX) minX = x;
            if (y < minY) minY = y;
            if (z < minZ) minZ = z;

            if (x > maxX) maxX = x;
            if (y > maxY) maxY = y;
            if (z > maxZ) maxZ = z;
        }

//        setDirty(true);
        min = BlockVector3.at(minX, minY, minZ);
        max = BlockVector3.at(maxX, maxY, maxZ);
    }

    /**
     * Get a vector containing the highest X, Y, and Z components for the
     * corner of the axis-aligned bounding box that contains this region.
     *
     * @return the maximum point
     */
    public BlockVector3 getMaximumPoint() {
        return max;
    }

    /**
     * Get a vector containing the smallest X, Y, and Z components for the
     * corner of the axis-aligned bounding box that contains this region.
     *
     * @return the minimum point
     */
    public BlockVector3 getMinimumPoint() {
        return min;
    }

    /**
     * Check to see if a position is contained within this region.
     *
     * @param position the position to check
     * @return whether {@code position} is in this region
     */
    public boolean contains(BlockVector2 position) {
        checkNotNull(position);
        return contains(BlockVector3.at(position.getBlockX(), min.getBlockY(), position.getBlockZ()));
    }

    /**
     * Check to see if a point is inside this region.
     *
     * @param x the x coordinate to check
     * @param y the y coordinate to check
     * @param z the z coordinate to check
     * @return whether this region contains the point
     */
    public boolean contains(int x, int y, int z) {
        return contains(BlockVector3.at(x, y, z));
    }

    /**
     * Check to see if any of the points are inside this region projected
     * onto the X-Z plane.
     *
     * @param positions a list of positions
     * @return true if contained
     */
    public boolean containsAny(List<BlockVector2> positions) {
        checkNotNull(positions);

        for (BlockVector2 pt : positions) {
            if (contains(pt)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Return a list of regions from the given list of regions that intersect
     * with this region.
     *
     * @param regions a list of regions to source from
     * @return the elements of {@code regions} that intersect with this region
     */
    public List<RegionTerritory> getIntersectingRegions(Collection<RegionTerritory> regions) {
        checkNotNull(regions, "regions");

        List<RegionTerritory> intersecting = Lists.newArrayList();
        Area thisArea = toArea();

        for (RegionTerritory region : regions) {
            if (!region.isPhysicalArea()) continue;

            if (intersects(region, thisArea)) {
                intersecting.add(region);
            }
        }

        return intersecting;
    }

    /**
     * Test whether the given region intersects with this area.
     *
     * @param region the region to test
     * @param thisArea an area object for this region
     * @return true if the two regions intersect
     */
    protected boolean intersects(RegionTerritory region, Area thisArea) {
        if (intersectsBoundingBox(region)) {
            Area testArea = region.toArea();
            testArea.intersect(thisArea);
            return !testArea.isEmpty();
        } else {
            return false;
        }
    }

    /**
     * Checks if the bounding box of a region intersects with with the bounding
     * box of this region.
     *
     * @param region the region to check
     * @return whether the given region intersects
     */
    protected boolean intersectsBoundingBox(RegionTerritory region) {
        BlockVector3 rMaxPoint = region.getMaximumPoint();
        BlockVector3 min = getMinimumPoint();

        if (rMaxPoint.getBlockX() < min.getBlockX()) return false;
        if (rMaxPoint.getBlockY() < min.getBlockY()) return false;
        if (rMaxPoint.getBlockZ() < min.getBlockZ()) return false;

        BlockVector3 rMinPoint = region.getMinimumPoint();
        BlockVector3 max = getMaximumPoint();

        if (rMinPoint.getBlockX() > max.getBlockX()) return false;
        if (rMinPoint.getBlockY() > max.getBlockY()) return false;
        if (rMinPoint.getBlockZ() > max.getBlockZ()) return false;

        return true;
    }

    /**
     * Compares all edges of two regions to see if any of them intersect.
     *
     * @param region the region to check
     * @return whether any edges of a region intersect
     */
    protected boolean intersectsEdges(RegionTerritory region) {
        List<BlockVector2> pts1 = getPoints();
        List<BlockVector2> pts2 = region.getPoints();
        BlockVector2 lastPt1 = pts1.get(pts1.size() - 1);
        BlockVector2 lastPt2 = pts2.get(pts2.size() - 1);
        for (BlockVector2 aPts1 : pts1) {
            for (BlockVector2 aPts2 : pts2) {

                Line2D line1 = new Line2D.Double(
                        lastPt1.getBlockX(),
                        lastPt1.getBlockZ(),
                        aPts1.getBlockX(),
                        aPts1.getBlockZ());

                if (line1.intersectsLine(
                        lastPt2.getBlockX(),
                        lastPt2.getBlockZ(),
                        aPts2.getBlockX(),
                        aPts2.getBlockZ())) {
                    return true;
                }
                lastPt2 = aPts2;
            }
            lastPt1 = aPts1;
        }
        return false;
    }

    /**
     * Return the AWT area, otherwise null if
     * {@link #isPhysicalArea()} if false.
     *
     * @return The shape version
     */
    abstract Area toArea();

    /**
     * Get points of the region projected onto the X-Z plane.
     *
     * @return the points
     */
    public abstract List<BlockVector2> getPoints();

    /**
     * Checks to see if the given ID is a valid ID.
     *
     * @param id the id to check
     * @return whether the region id given is valid
     */
    public static boolean isValidId(String id) {
        checkNotNull(id);
        return VALID_ID_PATTERN.matcher(id).matches();
    }

    /**
     * Get the number of blocks in this region.
     *
     * @return the volume of this region in blocks
     */
    public abstract int volume();

    /**
     * Check to see if a point is inside this region.
     *
     * @param pt The point to check
     * @return Whether {@code pt} is in this region
     */
    public abstract boolean contains(BlockVector3 pt);

    /**
     * Get the type of region.
     *
     * @return the type
     */
    public abstract RegionType getType();

//    @Override
//    public int compareTo(RegionTerritory other) {
//        if (getPriority() > other.getPriority()) {
//            return -1;
//        } else if (getPriority() < other.getPriority()) {
//            return 1;
//        }
//
//        return getId().compareTo(other.getId());
//    }

    /**
     * Gets the name of this region
     *
     * @return the name
     */
    public String getId() {
        return id;
    }
}
