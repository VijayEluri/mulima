package x.org.mulima.api.meta;

public interface Track extends Metadata, Comparable<Track> {
	public int getNum();
	public int getDiscNum();
	public CuePoint getStartPoint();
	public CuePoint getEndPoint();
}
