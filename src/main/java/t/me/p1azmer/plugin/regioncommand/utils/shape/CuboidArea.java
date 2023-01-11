package t.me.p1azmer.plugin.regioncommand.utils.shape;

import org.bukkit.Location;
import org.bukkit.World;

public class CuboidArea  {
   protected Location highPoints;
   protected Location lowPoints;
   protected String worldName;

   protected CuboidArea() {
   }

   public CuboidArea clone() {
      return new CuboidArea(this.lowPoints, this.highPoints);
   }

   public CuboidArea(Location var1, Location var2) {
      int var3;
      int var6;
      if (var1.getBlockX() > var2.getBlockX()) {
         var3 = var1.getBlockX();
         var6 = var2.getBlockX();
      } else {
         var3 = var2.getBlockX();
         var6 = var1.getBlockX();
      }

      int var4;
      int var7;
      if (var1.getBlockY() > var2.getBlockY()) {
         var4 = var1.getBlockY();
         var7 = var2.getBlockY();
      } else {
         var4 = var2.getBlockY();
         var7 = var1.getBlockY();
      }

      int var5;
      int var8;
      if (var1.getBlockZ() > var2.getBlockZ()) {
         var5 = var1.getBlockZ();
         var8 = var2.getBlockZ();
      } else {
         var5 = var2.getBlockZ();
         var8 = var1.getBlockZ();
      }

      this.highPoints = new Location(var1.getWorld(), (double)var3, (double)var4, (double)var5);
      this.lowPoints = new Location(var1.getWorld(), (double)var6, (double)var7, (double)var8);
      this.worldName = var1.getWorld().getName();
   }

   public long getSize() {
      int var1 = this.highPoints.getBlockX() - this.lowPoints.getBlockX() + 1;
      int var2 = this.highPoints.getBlockZ() - this.lowPoints.getBlockZ() + 1;
      int var3 = this.highPoints.getBlockY() - this.lowPoints.getBlockY() + 1;
      return (long)(var1 * var3 * var2);
   }

   public int getXSize() {
      return this.highPoints.getBlockX() - this.lowPoints.getBlockX() + 1;
   }

   public int getYSize() {
      return this.highPoints.getBlockY() - this.lowPoints.getBlockY() + 1;
   }

   public int getZSize() {
      return this.highPoints.getBlockZ() - this.lowPoints.getBlockZ() + 1;
   }

   public Location getHighLoc() {
      return this.highPoints;
   }

   public Location getLowLoc() {
      return this.lowPoints;
   }

   public World getWorld() {
      return this.highPoints.getWorld();
   }
}