/*  
 *  Copyright (C) 2010  Andrew Oberstar.  All rights reserved.
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.andrewoberstar.library.meta;

import java.util.List;

public abstract class AbstractMetadata implements Metadata {
	private TagSupport tags = new TagSupportImpl();
	
	public TagSupport getTags() {
		return tags;
	}
	
	public void setTags(TagSupport tags) {
		this.tags = tags;
	}
	
	protected <T extends AbstractMetadata> void tidy(List<T> children) {
		for (Tag tag : GenericTag.values()) {
			if (GenericTag.DISC_NUMBER.equals(tag) || GenericTag.TRACK_NUMBER.equals(tag)) {
				continue;
			}
			List<String> values = this.getTags().getAll(tag);
			if (values == null || values.size() == 0) {
				values = null;
				for (AbstractMetadata child : children) {
					List<String> temp = child.getTags().getAll(tag);
					if (values == null) {
						values = temp;
					} else if (!values.equals(temp)) {
						values = null;
						break;
					}
				}
				
				if (values != null) {
					this.getTags().add(tag, values);
					for (AbstractMetadata child : children) {
						child.getTags().remove(tag);
					}
				}
			}
		}
	}
}
