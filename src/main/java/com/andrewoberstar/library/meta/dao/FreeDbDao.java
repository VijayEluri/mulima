package com.andrewoberstar.library.meta.dao;

import java.util.List;

import com.andrewoberstar.library.meta.Disc;

public interface FreeDbDao {
	List<Disc> getDiscsById(String cddbId);
	List<Disc> getDiscsById(List<String> cddbIds);
	List<Disc> getAllDiscs();
	List<Disc> getAllDiscsFromOffset(int startNum, int numToRead);
	void addDisc(Disc disc);
	void addAllDiscs(List<Disc> discs);
}
