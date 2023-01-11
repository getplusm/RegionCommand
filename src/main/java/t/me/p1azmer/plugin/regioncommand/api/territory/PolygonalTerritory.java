package t.me.p1azmer.plugin.regioncommand.api.territory;

import com.google.common.collect.ImmutableList;
import t.me.p1azmer.plugin.regioncommand.api.type.RegionType;
import t.me.p1azmer.plugin.regioncommand.utils.vector.BlockVector2;
import t.me.p1azmer.plugin.regioncommand.utils.vector.BlockVector3;

import java.awt.*;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class PolygonalTerritory extends RegionTerritory{

    private final ImmutableList<BlockVector2> points;
    private final int minY;
    private final int maxY;


    /**
     * Construct a new instance of this polygonal region.
     *
     * @param id the region id
     * @param points a {@link List} of points that this region should contain
     * @param minY the minimum y coordinate
     * @param maxY the maximum y coordinate
     */
    public PolygonalTerritory(String id, List<BlockVector2> points, int minY, int maxY) {
        super(id);
        ImmutableList<BlockVector2> immutablePoints = ImmutableList.copyOf(points);
        setMinMaxPoints(immutablePoints, minY, maxY);
        this.points = immutablePoints;
        this.minY = min.getBlockY();
        this.maxY = max.getBlockY();
    }

    /**
     * Sets the min and max points from all the 2d points and the min/max Y values
     *
     * @param points2D A {@link List} of points that this region should contain
     * @param minY The minimum y coordinate
     * @param maxY The maximum y coordinate
     */
    private void setMinMaxPoints(List<BlockVector2> points2D, int minY, int maxY) {
        checkNotNull(points2D);

        List<BlockVector3> points = new ArrayList<>();
        int y = minY;
        for (BlockVector2 point2D : points2D) {
            points.add(BlockVector3.at(point2D.getBlockX(), y, point2D.getBlockZ()));
            y = maxY;
        }
        setMinMaxPoints(points);
    }

    @Override
    public boolean isPhysicalArea() {
        return true;
    }

    @Override
    public List<BlockVector2> getPoints() {
        return points;
    }

    @Override
    public boolean contains(BlockVector3 position) {
        checkNotNull(position);

        int targetX = position.getBlockX(); // Width
        int targetY = position.getBlockY(); // Height
        int targetZ = position.getBlockZ(); // Depth

        if (targetY < minY || targetY > maxY) {
            return false;
        }
        //Quick and dirty check.
        if (targetX < min.getBlockX() || targetX > max.getBlockX() || targetZ < min.getBlockZ() || targetZ > max.getBlockZ()) {
            return false;
        }
        boolean inside = false;
        int npoints = points.size();
        int xNew, zNew;
        int xOld, zOld;
        int x1, z1;
        int x2, z2;
        long crossproduct;
        int i;

        xOld = points.get(npoints - 1).getBlockX();
        zOld = points.get(npoints - 1).getBlockZ();

        for (i = 0; i < npoints; i++) {
            xNew = points.get(i).getBlockX();
            zNew = points.get(i).getBlockZ();
            //Check for corner
            if (xNew == targetX && zNew == targetZ) {
                return true;
            }
            if (xNew > xOld) {
                x1 = xOld;
                x2 = xNew;
                z1 = zOld;
                z2 = zNew;
            } else {
                x1 = xNew;
                x2 = xOld;
                z1 = zNew;
                z2 = zOld;
            }
            if (x1 <= targetX && targetX <= x2) {
                crossproduct = ((long) targetZ - (long) z1) * (long) (x2 - x1)
                        - ((long) z2 - (long) z1) * (long) (targetX - x1);
                if (crossproduct == 0) {
                    if ((z1 <= targetZ) == (targetZ <= z2)) return true; // on edge
                } else if (crossproduct < 0 && (x1 != targetX)) {
                    inside = !inside;
                }
            }
            xOld = xNew;
            zOld = zNew;
        }

        return inside;
    }

    @Override
    public RegionType getType() {
        return RegionType.POLYGON;
    }

    @Override
    Area toArea() {
        List<BlockVector2> points = getPoints();
        int numPoints = points.size();
        int[] xCoords = new int[numPoints];
        int[] yCoords = new int[numPoints];

        int i = 0;
        for (BlockVector2 point : points) {
            xCoords[i] = point.getBlockX();
            yCoords[i] = point.getBlockZ();
            i++;
        }

        Polygon polygon = new Polygon(xCoords, yCoords, numPoints);
        return new Area(polygon);
    }

    @Override
    public int volume() {
        // TODO: Fix this -- the previous algorithm returned incorrect results, but the current state of this method is even worse
        return 0;
    }
}
