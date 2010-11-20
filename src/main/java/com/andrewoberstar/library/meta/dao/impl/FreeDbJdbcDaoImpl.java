package com.andrewoberstar.library.meta.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

import com.andrewoberstar.library.meta.Disc;
import com.andrewoberstar.library.meta.GenericTag;
import com.andrewoberstar.library.meta.Track;
import com.andrewoberstar.library.meta.dao.FreeDbDao;
import com.andrewoberstar.library.util.ProgressBar;

public class FreeDbJdbcDaoImpl extends NamedParameterJdbcDaoSupport implements FreeDbDao {
	private final Logger logger = LoggerFactory.getLogger(FreeDbJdbcDaoImpl.class);

	@Override
	public List<Disc> getDiscsById(String cddbId) {
		final String sql = "SELECT `discs`.`id` FROM `discs`, `cddb_ids` WHERE `cddb_ids`.`cddb_id`=:cddb_id AND `discs`.`id`=`cddb_ids`.`disc_id`";
		SqlParameterSource parms = new MapSqlParameterSource("cddb_id", cddbId);
		List<Integer> result = this.getNamedParameterJdbcTemplate().queryForList(sql, parms, Integer.class);
		List<Disc> discs = new ArrayList<Disc>();
		for (Integer id : result) {
			discs.add(getDisc(id));
		}
		return discs;
	}
	
	@Override
	public List<Disc> getDiscsById(List<String> cddbIds) {
		List<Disc> discs = new ArrayList<Disc>();
		
		for (String cddbId : cddbIds) {
			discs.addAll(getDiscsById(cddbId));
		}
		
		return discs;
	}

	@Override
	public List<Disc> getAllDiscs() {
		final String sql = "SELECT `id` FROM `discs`";
		List<Integer> result = this.getNamedParameterJdbcTemplate().queryForList(sql, (SqlParameterSource) null, Integer.class);
		List<Disc> discs = new ArrayList<Disc>();
		for (Integer id: result) {
			discs.add(getDisc(id));
		}
		return discs;
	}

	@Override
	public List<Disc> getAllDiscsFromOffset(int startNum, int numToRead) {
		throw new UnsupportedOperationException("Not implemented in this DAO.");
	}

	@Override
	@Transactional
	public void addDisc(Disc disc) {
		logger.trace("Entering addDisc");
		final String sql = "INSERT INTO `discs` VALUES(null, :artist, :title, :year, :genre)";
		MapSqlParameterSource parms = new MapSqlParameterSource();
		parms.addValue("artist", disc.getTags().getFlat(GenericTag.ARTIST));
		parms.addValue("title", disc.getTags().getFlat(GenericTag.ALBUM));
		String year = disc.getTags().getFirst(GenericTag.DATE);
		if (year == null || "".equals(year) || year.length() > 4)
			parms.addValue("year", null);
		else
			parms.addValue("year", year + "-01-01");
			
		parms.addValue("genre", disc.getTags().getFlat(GenericTag.GENRE));
			
		KeyHolder keys = new GeneratedKeyHolder();
		this.getNamedParameterJdbcTemplate().update(sql, parms, keys);
			
		int newDiscId = keys.getKey().intValue();
		addCddbIds(newDiscId, disc);
		addTracks(newDiscId, disc.getTracks());
		logger.trace("Exiting addDisc");
	}

	@Override
	@Transactional
	public void addAllDiscs(List<Disc> discs) {
		logger.trace("Entering addAllDiscs");
		ProgressBar progress = new ProgressBar("JDBC addDiscs",  discs.size());
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

	private Disc getDisc(int id) {
		final String sql = "SELECT * FROM `discs` WHERE `id`=:id";
		SqlParameterSource parms = new MapSqlParameterSource("id", id);
		List<Disc> result = this.getNamedParameterJdbcTemplate().query(sql, parms, new DiscRowMapper());
		Disc disc = result.get(0);
		disc.getTracks().addAll(getTracksForDisc(id));
		
		for (String cddbId : getCddbIdsForDisc(id)) {
			disc.getTags().add(GenericTag.CDDB_ID, cddbId);
		}
		return disc;
	}
	
	private List<Track> getTracksForDisc(int id) {
		final String sql = "SELECT * FROM `tracks` WHERE `disc_id`=:id";
		SqlParameterSource parms = new MapSqlParameterSource("id", id);
		return this.getNamedParameterJdbcTemplate().query(sql, parms, new TrackRowMapper());
	}
	
	private List<String> getCddbIdsForDisc(int id) {
		final String sql = "SELECT `cddb_id` FROM `cddb_ids` WHERE `disc_id`=:id";
		SqlParameterSource parms = new MapSqlParameterSource("id", id);
		return this.getNamedParameterJdbcTemplate().queryForList(sql, parms, String.class);
	}
	
	private void addTracks(int discId, List<Track> tracks) {
		logger.trace("Entering addTracks");
		final String sql = "INSERT INTO `tracks` VALUES(null, :disc_id, :num, :title)";
		
		MapSqlParameterSource[] trackParms = new MapSqlParameterSource[tracks.size()];
		
		for (int i = 0; i < tracks.size(); i++) {
			trackParms[i] = new MapSqlParameterSource();
			trackParms[i].addValue("disc_id", discId);
			trackParms[i].addValue("num", tracks.get(i).getNum());
			trackParms[i].addValue("title", tracks.get(i).getTags().getFlat(GenericTag.TITLE));	
		}
		this.getNamedParameterJdbcTemplate().batchUpdate(sql, trackParms);
		logger.trace("Exiting addTracks");
	}
	
	private void addCddbIds(int discId, Disc disc) {
		logger.trace("Entering addCddbIds");
		final String sql = "INSERT INTO `cddb_ids` VALUES(null, :disc_id, :cddb_id)";
		List<String> cddbIds = disc.getTags().getAll(GenericTag.CDDB_ID);
		MapSqlParameterSource[] parms = new MapSqlParameterSource[cddbIds.size()];
		
		for (int i = 0; i < cddbIds.size(); i++) {
			parms[i] = new MapSqlParameterSource();
			parms[i].addValue("disc_id", discId);
			parms[i].addValue("cddb_id", cddbIds.get(i));
		}
		this.getNamedParameterJdbcTemplate().batchUpdate(sql, parms);
		logger.trace("Exiting addCddbIds");
	}
	
	private static class DiscRowMapper implements RowMapper<Disc> {
		@Override
		public Disc mapRow(ResultSet rs, int rowNum) throws SQLException {
			Disc disc = new Disc();
			disc.getTags().add(GenericTag.ARTIST, rs.getString("artist"));
			disc.getTags().add(GenericTag.ALBUM, rs.getString("title"));
			disc.getTags().add(GenericTag.DATE, rs.getString("year"));
			disc.getTags().add(GenericTag.GENRE, rs.getString("genre"));
			return disc;
		}
	}
	
	private static class TrackRowMapper implements RowMapper<Track> {
		@Override
		public Track mapRow(ResultSet rs, int rowNum) throws SQLException {
			Track track = new Track();
			track.getTags().add(GenericTag.TRACK_NUMBER, Integer.toString(rs.getInt("num") + 1));
			track.getTags().add(GenericTag.TITLE, rs.getString("title"));
			return track;
		}
	}
}
