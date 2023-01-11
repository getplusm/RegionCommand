package t.me.p1azmer.plugin.regioncommand.region;

import com.google.common.collect.Iterators;
import org.bukkit.World;
import t.me.p1azmer.api.libs.net.kyori.text.TranslatableComponent;
import t.me.p1azmer.plugin.regioncommand.utils.vector.BlockVector3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An intersection of several other regions. Any location that is contained in one
 * of the child regions is considered as contained by this region.
 *
 * <p>{@link #iterator()} returns a special iterator that will iterate through
 * the iterators of each region in an undefined sequence. Some positions may
 * be repeated if the position is contained in more than one region, but this cannot
 * be guaranteed to occur.</p>
 */
public class RegionIntersection extends AbstractRegion {

    private final List<Territory> regions = new ArrayList<>();

    /**
     * Create a new instance with the included list of regions.
     *
     * @param regions a list of regions, which is copied
     */
    public RegionIntersection(List<Territory> regions) {
        this(null, regions);
    }

    /**
     * Create a new instance with the included list of regions.
     *
     * @param regions a list of regions, which is copied
     */
    public RegionIntersection(Territory... regions) {
        this(null, regions);
    }

    /**
     * Create a new instance with the included list of regions.
     *
     * @param world   the world
     * @param regions a list of regions, which is copied
     */
    public RegionIntersection(World world, List<Territory> regions) {
        super(world);
        checkNotNull(regions);
        checkArgument(!regions.isEmpty(), "empty region list is not supported");
        this.regions.addAll(regions);
    }

    /**
     * Create a new instance with the included list of regions.
     *
     * @param world   the world
     * @param regions an array of regions, which is copied
     */
    public RegionIntersection(World world, Territory... regions) {
        super(world);
        checkNotNull(regions);
        checkArgument(regions.length > 0, "empty region list is not supported");
        Collections.addAll(this.regions, regions);
    }

    @Override
    public BlockVector3 getMinimumPoint() {
        BlockVector3 minimum = regions.get(0).getMinimumPoint();
        for (int i = 1; i < regions.size(); i++) {
            minimum = regions.get(i).getMinimumPoint().getMinimum(minimum);
        }
        return minimum;
    }

    @Override
    public BlockVector3 getMaximumPoint() {
        BlockVector3 maximum = regions.get(0).getMaximumPoint();
        for (int i = 1; i < regions.size(); i++) {
            maximum = regions.get(i).getMaximumPoint().getMaximum(maximum);
        }
        return maximum;
    }

    @Override
    public void expand(BlockVector3... changes) throws RegionOperationException {
        checkNotNull(changes);
        throw new RegionOperationException(TranslatableComponent.of("worldedit.selection.intersection.error.cannot-expand").key());
    }

    @Override
    public void contract(BlockVector3... changes) throws RegionOperationException {
        checkNotNull(changes);
        throw new RegionOperationException(TranslatableComponent.of("worldedit.selection.intersection.error.cannot-contract").key());
    }

    @Override
    public boolean contains(BlockVector3 position) {
        checkNotNull(position);

        for (Territory region : regions) {
            if (region.contains(position)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Iterator<BlockVector3> iterator() {
        return Iterators.concat(Iterators.transform(regions.iterator(), r -> r.iterator()));
    }

}