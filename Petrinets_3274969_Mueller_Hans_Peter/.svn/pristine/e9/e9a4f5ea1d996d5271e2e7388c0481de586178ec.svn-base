package io.petermueller.petrinetz.models.rgraph;

public class TransitionArc {
	public Marking source;
	public Marking target;
	public String id;
	public String nakedId;
	public boolean isLatest;
	public boolean isOnDetectionPath;

	public TransitionArc(
			String id,
			String nakedId,
			Marking source,
			Marking target) {
		this.id = id;
		this.source = source;
		this.target = target;
		this.isOnDetectionPath = false;
		this.nakedId = nakedId;
	}
}
