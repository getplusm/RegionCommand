package t.me.p1azmer.plugin.regioncommand.region;

import t.me.p1azmer.plugin.regioncommand.region.Territory;
import t.me.p1azmer.plugin.regioncommand.utils.vector.BlockVector3;

import java.util.Iterator;

import static com.google.common.base.Preconditions.checkNotNull;

public class RegionIterator implements Iterator<BlockVector3> {

    private final Territory region;
    private final int maxX;
    private final int maxY;
    private final int maxZ;
    private final BlockVector3 min;
    private int nextX;
    private int nextY;
    private int nextZ;

    public RegionIterator(Territory region) {
        checkNotNull(region);

        this.region = region;

        BlockVector3 max = region.getMaximumPoint();
        this.maxX = max.getBlockX();
        this.maxY = max.getBlockY();
        this.maxZ = max.getBlockZ();

        this.min = region.getMinimumPoint();
        this.nextX = min.getBlockX();
        this.nextY = min.getBlockY();
        this.nextZ = min.getBlockZ();

        forward();
    }

    @Override
    public boolean hasNext() {
        return nextX != Integer.MIN_VALUE;
    }

    private void forward() {
        while (hasNext() && !region.contains(BlockVector3.at(nextX, nextY, nextZ))) {
            forwardOne();
        }
    }

    @Override
    public BlockVector3 next() {
        if (!hasNext()) {
            throw new java.util.NoSuchElementException();
        }

        BlockVector3 answer = BlockVector3.at(nextX, nextY, nextZ);

        forwardOne();
        forward();

        return answer;
    }

    private void forwardOne() {
        if (++nextX <= maxX) {
            return;
        }
        nextX = min.getBlockX();

        if (++nextY <= maxY) {
            return;
        }
        nextY = min.getBlockY();

        if (++nextZ <= maxZ) {
            return;
        }
        nextX = Integer.MIN_VALUE;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}