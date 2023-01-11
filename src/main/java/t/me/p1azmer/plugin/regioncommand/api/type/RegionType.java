package t.me.p1azmer.plugin.regioncommand.api.type;

public enum RegionType {
    CUBOID("cuboidRegion"),
    POLYGON("poly2d"),
    ;

    private final String name;

    RegionType(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }
}
