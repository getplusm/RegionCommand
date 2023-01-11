package t.me.p1azmer.plugin.regioncommand.utils.polygon;

import t.me.p1azmer.plugin.regioncommand.utils.vector.BlockVector2;
import t.me.p1azmer.plugin.regioncommand.utils.vector.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper method for anything related to polygons.
 */
public final class Polygons {

    private Polygons() {
    }

    /**
     * Calculates the polygon shape of a cylinder which can then be used for e.g. intersection detection.
     *
     * @param center the center point of the cylinder
     * @param radius the radius of the cylinder
     * @param maxPoints max points to be used for the calculation
     * @return a list of {@link BlockVector2} which resemble the shape as a polygon
     */
    public static List<BlockVector2> polygonizeCylinder(BlockVector2 center, Vector2 radius, int maxPoints) {
        int nPoints = (int) Math.ceil(Math.PI * radius.length());

        // These strange semantics for maxPoints are copied from the selectSecondary method.
        if (maxPoints >= 0 && nPoints >= maxPoints) {
            nPoints = maxPoints - 1;
        }

        final List<BlockVector2> points = new ArrayList<>(nPoints);
        for (int i = 0; i < nPoints; ++i) {
            double angle = i * (2.0 * Math.PI) / nPoints;
            final Vector2 pos = Vector2.at(Math.cos(angle), Math.sin(angle));
            final BlockVector2 blockVector2D = pos.multiply(radius).toBlockPoint().add(center);
            points.add(blockVector2D);
        }

        return points;
    }

}