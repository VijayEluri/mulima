package com.andrewoberstar.library.meta;

public abstract class AbstractMetadata implements Metadata {
	private TagSupport tags = new TagSupportImpl();
	
	public TagSupport getTags() {
		return tags;
	}
	
	public void setTags(TagSupport tags) {
		this.tags = tags;
	}
}
