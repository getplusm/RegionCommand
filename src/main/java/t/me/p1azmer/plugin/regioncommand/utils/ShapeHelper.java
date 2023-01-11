package t.me.p1azmer.plugin.regioncommand.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import t.me.p1azmer.plugin.regioncommand.region.CuboidRegion;
import t.me.p1azmer.plugin.regioncommand.utils.shape.CuboidArea;
import t.me.p1azmer.plugin.regioncommand.utils.shape.CuboidSide;
import t.me.p1azmer.plugin.regioncommand.utils.vector.BlockVector3;
import t.me.p1azmer.plugin.regioncommand.utils.vector.Vector3;

import java.util.*;

public class ShapeHelper {

    public static Collection<Location> getLocationsFromCuboid(CuboidRegion region) {
        List<Vector> vectors = new ArrayList<>();
        if (region != null) {
            BlockVector3 min = region.getMinimumPoint();
            BlockVector3 max = region.getMaximumPoint().add(1, 1, 1);
            int height = region.getHeight();
            Vector p1;
            Vector p2;
            Vector center1;
            Vector p5;
                List<Vector> bottomCorners = new ArrayList<>();
                bottomCorners.add(new Vector(min.getX(), min.getY(), min.getZ()));
                bottomCorners.add(new Vector(max.getX(), min.getY(), min.getZ()));
                bottomCorners.add(new Vector(max.getX(), min.getY(), max.getZ()));
                bottomCorners.add(new Vector(min.getX(), min.getY(), max.getZ()));

                for(int i = 0; i < bottomCorners.size(); ++i) {
                    if (i + 1 < bottomCorners.size()) {
                        p1 = bottomCorners.get(i + 1);
                    } else {
                        p1 = bottomCorners.get(0);
                    }

                    p2 = p1.add(new Vector(0, height, 0));
                    center1 = p1.add(new Vector(0, height, 0));
                    vectors.addAll(plotLine(p1, p1));
                    vectors.addAll(plotLine(p2, center1));
                    vectors.addAll(plotLine(p1, p2));

                        for(double offset = 1; offset < (double)height; offset += 1) {
                            p5 = p1.add(new Vector(0.0D, offset, 0.0D));
                            vectors.addAll(plotLine(p5, p5));
                        }

                }
        }

        Collection<Location> locations = new ArrayList<>();
        if (vectors.size() > 0 && region.getWorld() != null) {
            World world = Bukkit.getWorld(region.getWorld().getName());
            for (Vector vector : vectors) {
                locations.add(new Location(world, vector.getX(), vector.getY(), vector.getZ()));
            }
        }


        return locations;
    }

    private static List<Vector> plotLine(Vector p1, Vector p2) {
        List<Vector> vectors = new ArrayList<>();
        int points = (int) (p1.distance(p2) / 0.5) + 1;
        double length = p1.distance(p2);
        double gap = length / (double) (points - 1);
        Vector gapVector = p2.subtract(p1).normalize().multiply(gap);

        for (int i = 0; i < points; ++i) {
            Vector currentPoint = p1.add(gapVector.multiply(i));
            vectors.add(currentPoint);
        }

        return vectors;
    }

    private static List<Vector> plotEllipse(Vector center, Vector radius) {
        List<Vector> vectors = new ArrayList<>();
        double biggestR = Math.max(radius.getX(), Math.max(radius.getY(), radius.getZ()));
        double circleCircumference = 2.0D * biggestR * 3.141592653589793D;
        double deltaTheta = 0.5 / circleCircumference;

        for (double i = 0.0D; i < 1.0D; i += deltaTheta) {
            double x = center.getX();
            double y = center.getY();
            double z = center.getZ();
            if (radius.getX() == 0.0D) {
                y = center.getY() + Math.cos(i * 2.0D * 3.141592653589793D) * radius.getY();
                z = center.getZ() + Math.sin(i * 2.0D * 3.141592653589793D) * radius.getZ();
            } else if (radius.getY() == 0.0D) {
                x = center.getX() + Math.cos(i * 2.0D * 3.141592653589793D) * radius.getX();
                z = center.getZ() + Math.sin(i * 2.0D * 3.141592653589793D) * radius.getZ();
            } else if (radius.getZ() == 0.0D) {
                x = center.getX() + Math.cos(i * 2.0D * 3.141592653589793D) * radius.getX();
                y = center.getY() + Math.sin(i * 2.0D * 3.141592653589793D) * radius.getY();
            }

            Vector loc = new Vector(x, y, z);
            vectors.add(loc);
        }

        return vectors;
    }

    public static List<Location> calculateLocations(Location playerLocation, CuboidArea cuboidArea) {
            Location var1 = playerLocation.clone();
            List<Location> var2 = new ArrayList<>();

        Set<CuboidSide> var6 = new HashSet<>(Arrays.asList(CuboidSide.values()));
                var1 = var1.add(0.0D, 0.5D, 0.0D);
                cuboidArea = cuboidArea.clone();
                double var7 = var1.getBlockX() - 0;
                if ((double)cuboidArea.getLowLoc().getBlockX() < var7) {
                    cuboidArea.getLowLoc().setX(var7);
                    var6.remove(CuboidSide.West);
                }

                double var9 = var1.getBlockX() + 0;
                if ((double)cuboidArea.getHighLoc().getBlockX() > var9) {
                    cuboidArea.getHighLoc().setX(var9);
                    var6.remove(CuboidSide.East);
                }

                double var11 = var1.getBlockZ() - 0;
                if ((double)cuboidArea.getLowLoc().getBlockZ() < var11) {
                    cuboidArea.getLowLoc().setZ(var11);
                    var6.remove(CuboidSide.North);
                }

                double var13 = var1.getBlockZ() + 0;
                if ((double)cuboidArea.getHighLoc().getBlockZ() > var13) {
                    cuboidArea.getHighLoc().setZ(var13);
                    var6.remove(CuboidSide.South);
                }

                double var15 = var1.getBlockY() - 0;
                if ((double)cuboidArea.getLowLoc().getBlockY() < var15) {
                    cuboidArea.getLowLoc().setY(var15);
                    var6.remove(CuboidSide.Bottom);
                }

                double var17 = var1.getBlockY() + 0;
                if ((double)cuboidArea.getHighLoc().getBlockY() > var17) {
                    cuboidArea.getHighLoc().setY(var17);
                    var6.remove(CuboidSide.Top);
                }

//                var6.removeAll(hiddenSides);
                double var19 = cuboidArea.getXSize() - 1;
                double var21 = cuboidArea.getYSize() - 1;
                double var23 = cuboidArea.getZSize() - 1;
                var2.addAll(getSidesLocations(var1, new Vector(var19, var21, var23), cuboidArea.getLowLoc().clone(), var6));
                var2.addAll(getEdgeLocations(var1, new Vector(var19, var21, var23), cuboidArea.getLowLoc().clone(), var6));
                return var2;
//            setLocations(svEffectType.Effect1, var2);
//            setLocations(svEffectType.Effect2, var3);

    }

    public static List<Location> getSidesLocations(Location var1, Vector var2, Location var3, Set<CuboidSide> var4) {
        List<Location> var5 = new ArrayList<>();
        for (CuboidSide cuboidSide : var4) {
            Location var8 = var3.clone();
            var8.add(cuboidSide.getV1().getX() * var2.getX(), cuboidSide.getV1().getY() * var2.getY(), cuboidSide.getV1().getZ() * var2.getZ());
            Location var9 = var1.clone();
            var9.add(cuboidSide.getV2().getX() * var2.getX(), cuboidSide.getV2().getY() * var2.getY(), cuboidSide.getV2().getZ() * var2.getZ());
            Vector var10 = new Vector(cuboidSide.getV3().getX() * var2.getX(), cuboidSide.getV3().getY() * var2.getY(), cuboidSide.getV3().getZ() * var2.getZ());
            var5.addAll(getSidesLocations(var8, var9, var10));
        }

        return var5;
    }

    public static List<Location> getEdgeLocations(Location var1, Vector var2, Location var3, Set<CuboidSide> var4) {
        ArrayList var5 = new ArrayList();
        if (var4.contains(CuboidSide.Bottom) && var4.contains(CuboidSide.North)) {
            var5.addAll(getEdgeLocations(var3.clone(), var1.clone(), new Vector(var2.getX(), 0.0D, 0.0D)));
        }

        if (var4.contains(CuboidSide.Top) && var4.contains(CuboidSide.North)) {
            var5.addAll(getEdgeLocations(var3.clone().add(0.0D, var2.getY(), 0.0D), var1.clone(), new Vector(var2.getX(), 0.0D, 0.0D)));
        }

        if (var4.contains(CuboidSide.Bottom) && var4.contains(CuboidSide.South)) {
            var5.addAll(getEdgeLocations(var3.clone().add(0.0D, 0.0D, var2.getZ()), var1.clone(), new Vector(var2.getX(), 0.0D, 0.0D)));
        }

        if (var4.contains(CuboidSide.Top) && var4.contains(CuboidSide.South)) {
            var5.addAll(getEdgeLocations(var3.clone().add(0.0D, var2.getY(), var2.getZ()), var1.clone(), new Vector(var2.getX(), 0.0D, 0.0D)));
        }

        if (var4.contains(CuboidSide.West) && var4.contains(CuboidSide.North)) {
            var5.addAll(getEdgeLocations(var3.clone().add(0.0D, 0.0D, 0.0D), var1.clone(), new Vector(0.0D, var2.getY(), 0.0D)));
        }

        if (var4.contains(CuboidSide.East) && var4.contains(CuboidSide.North)) {
            var5.addAll(getEdgeLocations(var3.clone().add(var2.getX(), 0.0D, 0.0D), var1.clone(), new Vector(0.0D, var2.getY(), 0.0D)));
        }

        if (var4.contains(CuboidSide.South) && var4.contains(CuboidSide.West)) {
            var5.addAll(getEdgeLocations(var3.clone().add(0.0D, 0.0D, var2.getZ()), var1.clone(), new Vector(0.0D, var2.getY(), 0.0D)));
        }

        if (var4.contains(CuboidSide.South) && var4.contains(CuboidSide.East)) {
            var5.addAll(getEdgeLocations(var3.clone().add(var2.getX(), 0.0D, var2.getZ()), var1.clone(), new Vector(0.0D, var2.getY(), 0.0D)));
        }

        if (var4.contains(CuboidSide.West) && var4.contains(CuboidSide.Bottom)) {
            var5.addAll(getEdgeLocations(var3.clone().add(0.0D, 0.0D, 0.0D), var1.clone(), new Vector(0.0D, 0.0D, var2.getZ())));
        }

        if (var4.contains(CuboidSide.East) && var4.contains(CuboidSide.Bottom)) {
            var5.addAll(getEdgeLocations(var3.clone().add(var2.getX(), 0.0D, 0.0D), var1.clone(), new Vector(0.0D, 0.0D, var2.getZ())));
        }

        if (var4.contains(CuboidSide.West) && var4.contains(CuboidSide.Top)) {
            var5.addAll(getEdgeLocations(var3.clone().add(0.0D, var2.getY(), 0.0D), var1.clone(), new Vector(0.0D, 0.0D, var2.getZ())));
        }

        if (var4.contains(CuboidSide.East) && var4.contains(CuboidSide.Top)) {
            var5.addAll(getEdgeLocations(var3.clone().add(var2.getX(), var2.getY(), 0.0D), var1.clone(), new Vector(0.0D, 0.0D, var2.getZ())));
        }

        return var5;
    }

//    private List<Location> getSidesLocations(Location var1, Location var2, Vector var3) {
//        return getSidesLocations(var1, var2, var3
////                , getSideEffect()
//        );
//    }

    private static List<Location> getEdgeLocations(Location var1, Location var2, Vector var3) {
        return getLocations(var1, var2, var3
//                , getEdgeEffect()
        );
    }

    private static List<Location> getSidesLocations(Location var1, Location var2, Vector var3
//            , svEffect var4
    ) {
        double var5 = 0; //var4.getCollumnSpacing();
        double var7 = 0; //var4.getRowSpacing();
        double var9 = var3.getX();
        double var11 = var3.getY();
        double var13 = var3.getZ();
        if (var9 == 0.0D) {
            var9 = var5 + var5 * 0.1D;
        }

        if (var11 == 0.0D) {
            var11 = var7 + var7 * 0.1D;
        }

        if (var13 == 0.0D) {
            var13 = var5 + var5 * 0.1D;
        }

        ArrayList var15 = new ArrayList();
        if (var1.getWorld() != var2.getWorld()) {
            return var15;
        } else {
            int var16 = 0;

            for(double var17 = var5; var17 < var9; var17 += var5) {
                Location var19 = var1.clone();
                if (var9 > var5 + var5 * 0.1D) {
                    var19.add(var17, 0.0D, 0.0D);
                }

                for(double var20 = var7; var20 < var11; var20 += var7) {
                    Location var22 = var19.clone();
                    if (var11 > var7 + var7 * 0.1D) {
                        var22.add(0.0D, var20, 0.0D);
                    }

                    for(double var23 = var5; var23 < var13; var23 += var5) {
                        ++var16;
                        if (var16 > 10000) {
                            break;
                        }

                        Location var25 = var22.clone();
                        if (var13 > var5 + var5 * 0.1D) {
                            var25.add(0.0D, 0.0D, var23);
                        }

                        if (inRange(var2, var25)) {
                            var15.add(var25.clone());
                        }
                    }
                }
            }

            return var15;
        }
    }

    private static List<Location> getLocations(Location var1, Location var2, Vector var3
//            , svEffect var4)
    ){
        double var5 = 0;// var4.getCollumnSpacing();
        double var7 = 0; //var4.getRowSpacing();
        double var9 = var3.getX();
        double var11 = var3.getY();
        double var13 = var3.getZ();
        if (var9 == 0.0D) {
            var9 = var5 + var5 * 0.1D;
        }

        if (var11 == 0.0D) {
            var11 = var7 + var7 * 0.1D;
        }

        if (var13 == 0.0D) {
            var13 = var5 + var5 * 0.1D;
        }

        ArrayList var15 = new ArrayList();
        if (var1.getWorld() != var2.getWorld()) {
            return var15;
        } else {
            int var16 = 0;

            for(double var17 = 0.0D; var17 < var9; var17 += var5) {
                Location var19 = var1.clone();
                if (var9 > var5 + var5 * 0.1D) {
                    var19.add(var17, 0.0D, 0.0D);
                }

                for(double var20 = 0.0D; var20 < var11; var20 += var7) {
                    Location var22 = var19.clone();
                    if (var11 > var7 + var7 * 0.1D) {
                        var22.add(0.0D, var20, 0.0D);
                    }

                    for(double var23 = 0.0D; var23 <= var13; var23 += var5) {
                        ++var16;
                        if (var16 > 10000) {
                            break;
                        }

                        Location var25 = var22.clone();
                        if (var13 > var5 + var5 * 0.1D) {
                            var25.add(0.0D, 0.0D, var23);
                        }

                        if (inRange(var2, var25)) {
                            var15.add(var25.clone());
                        }
                    }
                }
            }

            return var15;
        }
    }

    private static boolean inRange(Location var1, Location var2) {
        double var3 = (double)(var2.getBlockX() - var1.getBlockX());
        if (var3 < 0.0D) {
            var3 = -var3;
        }

        double var5 = (double)(var2.getBlockZ() - var1.getBlockZ());
        if (var5 < 0.0D) {
            var5 = -var5;
        }

        double var7 = (double)(var2.getBlockY() - var1.getBlockY());
        if (var7 < 0.0D) {
            var7 = -var7;
        }

        return !(var3 > (double)0) && !(var7 > (double)0) && !(var5 > (double)0);
    }
}