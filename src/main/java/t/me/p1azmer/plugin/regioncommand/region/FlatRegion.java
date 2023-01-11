package t.me.p1azmer.plugin.regioncommand.region;

import t.me.p1azmer.plugin.regioncommand.utils.vector.BlockVector2;

public interface FlatRegion extends Territory {

    /**
     * Gets the minimum Y value.
     *
     * @return the Y value
     */
    int getMinimumY();

    /**
     * Gets the maximum Y value.
     *
     * @return the Y value
     */
    int getMaximumY();

    /**
     * Get this region as an iterable flat region.
     *
     * @return a flat region iterable
     */
    Iterable<BlockVector2> asFlatRegion();
}