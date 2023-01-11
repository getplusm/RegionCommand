package t.me.p1azmer.plugin.regioncommand.region;

public class RegionOperationException extends Throwable {

    private final String message;

    public RegionOperationException(String msg) {
        this.message = msg;
    }

    public String getMessage() {
        return message;
    }
}