package com.andrewoberstar.library.meta.dao;

import java.io.File;

import com.andrewoberstar.library.meta.Metadata;

public interface FileMetadataDao<T extends Metadata> {
	public T read(File file);
	public void write(File file, T meta);
}
