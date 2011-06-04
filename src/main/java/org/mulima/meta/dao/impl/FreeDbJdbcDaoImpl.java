/*  
 *  Copyright (C) 2011  Andrew Oberstar.  All rights reserved.
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
package org.mulima.meta.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import org.mulima.api.meta.Disc;
import org.mulima.api.meta.GenericTag;
import org.mulima.api.meta.Track;
import org.mulima.meta.dao.FreeDbDao;
import org.mulima.util.ProgressBar;
import org.mulima.util.SLF4JProgressBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;

/**
 * Provides access to a JDBC source containing FreeDb information.
 * @author Andrew Oberstar
 * @version 0.1.0
 * @since 0.1.0
 */
public class FreeDbJdbcDaoImpl extends NamedParameterJdbcDaoSupport implements FreeDbDao {
	private final Logger logger = LoggerFactory.getLogger(FreeDbJdbcDaoImpl.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Disc> getDiscsById(String cddbId) {
		final String sql = "SELECT `discs`.`id` FROM `discs`, `cddb_ids` "
			+ "WHERE `cddb_ids`.`cddb_id`=:cddb_id AND `discs`.`id`=`cddb_ids`.`disc_id`";
		SqlParameterSource parms = new MapSqlParameterSource("cddb_id", cddbId);
		List<Integer> result = this.getNamedParameterJdbcTemplate().queryForList(sql, parms, Integer.class);
		List<Disc> discs = new ArrayList<Disc>();
		for (Integer id : result) {
			discs.add(getDisc(id));
		}
		return discs;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Disc> getDiscsById(List<String> cddbIds) {
		List<Disc> discs = new ArrayList<Disc>();
		
		for (String cddbId : cddbIds) {
			discs.addAll(getDiscsById(cddbId));
		}
		
		return discs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Disc> getAllDiscs() {
		final String sql = "SELECT `id` FROM `discs`";
		List<Integer> result = this.getNamedParameterJdbcTemplate().queryForList(sql, 
			(SqlParameterSource) null, Integer.class);
		List<Disc> discs = new ArrayList<Disc>();
		for (Integer id : result) {
			discs.add(getDisc(id));
		}
		return discs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Disc> getAllDiscsFromOffset(int startNum, int numToRead) {
		throw new UnsupportedOperationException("Not implemented in this DAO.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void addDisc(Disc disc) {
		logger.trace("Entering addDisc");
		final String sql = "INSERT INTO `discs` VALUES(null, :artist, :title, :year, :genre)";
		MapSqlParameterSource parms = new MapSqlParameterSource();
		parms.addValue("artist", disc.getFlat(GenericTag.ARTIST));
		parms.addValue("title", disc.getFlat(GenericTag.ALBUM));
		String year = disc.getFirst(GenericTag.DATE);
		if (year == null || "".equals(year) || year.length() > 4) {
			parms.addValue("year", null);
		} else {
			parms.addValue("year", year + "-01-01");
		}
			
		parms.addValue("genre", disc.getFlat(GenericTag.GENRE));
			
		KeyHolder keys = new GeneratedKeyHolder();
		this.getNamedParameterJdbcTemplate().update(sql, parms, keys);
			
		int newDiscId = keys.getKey().intValue();
		addCddbIds(newDiscId, disc);
		addTracks(newDiscId, disc.getTracks());
		logger.trace("Exiting addDisc");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void addAllDiscs(List<Disc> discs) {
		logger.trace("Entering addAllDiscs");
		ProgressBar progress = new SLF4JProgressBar("JDBC addDiscs",  discs.size());
		for (Disc disc : discs) {
			try {
				addDisc(disc);
			} catch (DataAccessException e) {
				logger.debug(disc.toString());
				logger.error("JDBC call failed.", e);
				throw e;
			}
			progress.next();
		}
		logger.trace("Exiting addAllDiscs");
	}

	/**
	 * Gets the disc specified by the id.
	 * @param id the id of the disc
	 * @return the disc
	 */
	private Disc getDisc(int id) {
		final String sql = "SELECT * FROM `discs` WHERE `id`=:id";
		SqlParameterSource parms = new MapSqlParameterSource("id", id);
		List<Disc> result = this.getNamedParameterJdbcTemplate().query(sql, parms, new DiscRowMapper());
		Disc disc = result.get(0);
		disc.getTracks().addAll(getTracksForDisc(id));
		
		for (String cddbId : getCddbIdsForDisc(id)) {
			disc.add(GenericTag.CDDB_ID, cddbId);
		}
		return disc;
	}
	
	/**
	 * Gets the tracks for the disc id.
	 * @param id the id of the disc
	 * @return the list of tracks
	 */
	private List<Track> getTracksForDisc(int id) {
		final String sql = "SELECT * FROM `tracks` WHERE `disc_id`=:id";
		SqlParameterSource parms = new MapSqlParameterSource("id", id);
		return this.getNamedParameterJdbcTemplate().query(sql, parms, new TrackRowMapper());
	}
	
	/**
	 * Gets the CDDB ids for the disc id.
	 * @param id the id of the disc
	 * @return a list of CDDB ids
	 */
	private List<String> getCddbIdsForDisc(int id) {
		final String sql = "SELECT `cddb_id` FROM `cddb_ids` WHERE `disc_id`=:id";
		SqlParameterSource parms = new MapSqlParameterSource("id", id);
		return this.getNamedParameterJdbcTemplate().queryForList(sql, parms, String.class);
	}
	
	/**
	 * Adds the tracks for the disc id.
	 * @param discId the disc's id
	 * @param tracks the tracks to add
	 */
	private void addTracks(int discId, SortedSet<Track> tracks) {
		logger.trace("Entering addTracks");
		final String sql = "INSERT INTO `tracks` VALUES(null, :disc_id, :num, :title)";
		
		MapSqlParameterSource[] trackParms = new MapSqlParameterSource[tracks.size()];
		
		for (Track track : tracks) {
			int i = track.getNum() - 1;
			trackParms[i] = new MapSqlParameterSource();
			trackParms[i].addValue("disc_id", discId);
			trackParms[i].addValue("num", track.getNum());
			trackParms[i].addValue("title", track.getFlat(GenericTag.TITLE));	
		}
		
		this.getNamedParameterJdbcTemplate().batchUpdate(sql, trackParms);
		logger.trace("Exiting addTracks");
	}
	
	/**
	 * Adds CDDB ids for the disc id.
	 * @param discId the disc id
	 * @param disc the disc with the cddb ids
	 */
	private void addCddbIds(int discId, Disc disc) {
		logger.trace("Entering addCddbIds");
		final String sql = "INSERT INTO `cddb_ids` VALUES(null, :disc_id, :cddb_id)";
		List<String> cddbIds = disc.getAll(GenericTag.CDDB_ID);
		MapSqlParameterSource[] parms = new MapSqlParameterSource[cddbIds.size()];
		
		for (int i = 0; i < cddbIds.size(); i++) {
			parms[i] = new MapSqlParameterSource();
			parms[i].addValue("disc_id", discId);
			parms[i].addValue("cddb_id", cddbIds.get(i));
		}
		this.getNamedParameterJdbcTemplate().batchUpdate(sql, parms);
		logger.trace("Exiting addCddbIds");
	}
	
	/**
	 * Map a row to a Disc.
	 */
	private static class DiscRowMapper implements RowMapper<Disc> {
		@Override
		public Disc mapRow(ResultSet rs, int rowNum) throws SQLException {
			Disc disc = new Disc();
			disc.add(GenericTag.ARTIST, rs.getString("artist"));
			disc.add(GenericTag.ALBUM, rs.getString("title"));
			disc.add(GenericTag.DATE, rs.getString("year"));
			disc.add(GenericTag.GENRE, rs.getString("genre"));
			return disc;
		}
	}
	
	/**
	 * Map a row to a Track.
	 */
	private static class TrackRowMapper implements RowMapper<Track> {
		@Override
		public Track mapRow(ResultSet rs, int rowNum) throws SQLException {
			Track track = new Track();
			track.add(GenericTag.TRACK_NUMBER, Integer.toString(rs.getInt("num") + 1));
			track.add(GenericTag.TITLE, rs.getString("title"));
			return track;
		}
	}
}
