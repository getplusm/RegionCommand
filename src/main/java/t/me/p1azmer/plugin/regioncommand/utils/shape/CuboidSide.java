package t.me.p1azmer.plugin.regioncommand.utils.shape;

import org.bukkit.util.Vector;

public enum CuboidSide {
   North(new Vector(0, 0, 0), new Vector(0, 0, 0), new Vector(1, 1, 0)),
   South(new Vector(0, 0, 1), new Vector(0, 0, 0), new Vector(1, 1, 0)),
   West(new Vector(0, 0, 0), new Vector(0, 0, 0), new Vector(0, 1, 1)),
   East(new Vector(1, 0, 0), new Vector(0, 0, 0), new Vector(0, 1, 1)),
   Top(new Vector(0, 1, 0), new Vector(0, 0, 0), new Vector(1, 0, 1)),
   Bottom(new Vector(0, 0, 0), new Vector(0, 0, 0), new Vector(1, 0, 1));

   private Vector v1;
   private Vector v2;
   private Vector v3;

   private CuboidSide(Vector var3, Vector var4, Vector var5) {
      this.v1 = var3;
      this.v2 = var4;
      this.v3 = var5;
   }

   public Vector getV1() {
      return this.v1;
   }

   public Vector getV2() {
      return this.v2;
   }

   public Vector getV3() {
      return this.v3;
   }
}