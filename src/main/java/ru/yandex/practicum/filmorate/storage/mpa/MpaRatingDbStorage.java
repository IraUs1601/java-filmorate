package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;
import java.util.Optional;

@Repository
public class MpaRatingDbStorage implements MpaRatingStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaRatingDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean existsById(int id) {
        String sql = "SELECT COUNT(*) FROM mpa_ratings WHERE mpa_rating_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public List<MpaRating> findAll() {
        String sql = "SELECT mpa_rating_id, rating FROM mpa_ratings";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new MpaRating(
                rs.getInt("mpa_rating_id"),
                rs.getString("rating")
        ));
    }

    @Override
    public Optional<MpaRating> findById(int id) {
        String sql = "SELECT mpa_rating_id, rating FROM mpa_ratings WHERE mpa_rating_id = ?";
        return jdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                return Optional.of(new MpaRating(
                        rs.getInt("mpa_rating_id"),
                        rs.getString("rating")
                ));
            } else {
                return Optional.empty();
            }
        }, id);
    }
}