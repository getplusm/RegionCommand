package t.me.p1azmer.plugin.regioncommand.api.territory;

import t.me.p1azmer.plugin.regioncommand.api.RegionAPI;
import t.me.p1azmer.plugin.regioncommand.api.type.RegionType;
import t.me.p1azmer.plugin.regioncommand.utils.MathUtils;
import t.me.p1azmer.plugin.regioncommand.utils.vector.BlockVector2;
import t.me.p1azmer.plugin.regioncommand.utils.vector.BlockVector3;

import java.awt.*;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class CuboidTerritory extends RegionTerritory {

    /**
     * Construct a new instance of this cuboidRegion region.
     *
     * @param id  the region id
     * @param pt1 the first point of this region
     * @param pt2 the second point of this region
     */
    public CuboidTerritory(String id, BlockVector3 pt1, BlockVector3 pt2) {
        super(id);
        setMinMaxPoints(pt1, pt2);
    }

    /**
     * Given any two points, sets the minimum and maximum points.
     *
     * @param position1 the first point of this region
     * @param position2 the second point of this region
     */
    private void setMinMaxPoints(BlockVector3 position1, BlockVector3 position2) {
        checkNotNull(position1);
        checkNotNull(position2);

        List<BlockVector3> points = new ArrayList<>();
        points.add(position1);
        points.add(position2);
        setMinMaxPoints(points);
    }

    /**
     * Set the lower point of the cuboidRegion.
     *
     * @param position the point to set as the minimum point
     * @deprecated ProtectedRegion bounds should never be mutated. Regions must be redefined to move them.
     * This method will be removed in a future release.
     */
    @Deprecated
    public void setMinimumPoint(BlockVector3 position) {
        RegionAPI.PLUGIN.warn("CuboidTerritory#setMinimumPoint call ignored. Mutating regions leads to undefined behavior.");
    }

    /**
     * Set the upper point of the cuboidRegion.
     *
     * @param position the point to set as the maximum point
     * @deprecated ProtectedRegion bounds should never be mutated. Regions must be redefined to move them.
     * This method will be removed in a future release.
     */
    @Deprecated
    public void setMaximumPoint(BlockVector3 position) {
        RegionAPI.PLUGIN.warn("CuboidTerritory#setMaximumPoint call ignored. Mutating regions leads to undefined behavior.");
    }

    @Override
    public boolean isPhysicalArea() {
        return true;
    }

    @Override
    public List<BlockVector2> getPoints() {
        List<BlockVector2> pts = new ArrayList<>();
        int x1 = min.getBlockX();
        int x2 = max.getBlockX();
        int z1 = min.getBlockZ();
        int z2 = max.getBlockZ();

        pts.add(BlockVector2.at(x1, z1));
        pts.add(BlockVector2.at(x2, z1));
        pts.add(BlockVector2.at(x2, z2));
        pts.add(BlockVector2.at(x1, z2));

        return pts;
    }

    @Override
    public boolean contains(BlockVector3 pt) {
        final double x = pt.getX();
        final double y = pt.getY();
        final double z = pt.getZ();
        return x >= min.getBlockX() && x < max.getBlockX() + 1
                && y >= min.getBlockY() && y < max.getBlockY() + 1
                && z >= min.getBlockZ() && z < max.getBlockZ() + 1;
    }

    @Override
    public RegionType getType() {
        return RegionType.CUBOID;
    }

    @Override
    Area toArea() {
        int x = getMinimumPoint().getBlockX();
        int z = getMinimumPoint().getBlockZ();
        int width = getMaximumPoint().getBlockX() - x + 1;
        int height = getMaximumPoint().getBlockZ() - z + 1;
        return new Area(new Rectangle(x, z, width, height));
    }

    @Override
    protected boolean intersects(RegionTerritory region, Area thisArea) {
        if (region instanceof CuboidTerritory) {
            return intersectsBoundingBox(region);
        } else {
            return super.intersects(region, thisArea);
        }
    }

    @Override
    public int volume() {
        int xLength = max.getBlockX() - min.getBlockX() + 1;
        int yLength = max.getBlockY() - min.getBlockY() + 1;
        int zLength = max.getBlockZ() - min.getBlockZ() + 1;

        try {
            long v = MathUtils.checkedMultiply(xLength, yLength);
            v = MathUtils.checkedMultiply(v, zLength);
            if (v > Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            } else {
                return (int) v;
            }
        } catch (ArithmeticException e) {
            return Integer.MAX_VALUE;
        }
    }
}
