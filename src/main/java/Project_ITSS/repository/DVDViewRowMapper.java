package Project_ITSS.repository;

import Project_ITSS.common.entity.DVD;
import Project_ITSS.common.entity.Product;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DVDViewRowMapper implements RowMapper<Product> {
    @Override
    public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
        DVD dvd = new DVD();
        dvd.setProductId(rs.getLong("product_id"));
        dvd.setTitle(rs.getString("title"));
        dvd.setPrice(rs.getInt("price"));
        dvd.setWeight(rs.getFloat("weight"));
        dvd.setRushOrderSupported(rs.getBoolean("rush_order_supported"));
        dvd.setImageUrl(rs.getString("image_url"));
        dvd.setBarcode(rs.getString("barcode"));
        dvd.setImportDate(rs.getString("import_date"));
        dvd.setIntroduction(rs.getString("introduction"));
        dvd.setQuantity(rs.getInt("quantity"));
        dvd.setType(rs.getString("type"));
        dvd.setDvdId(rs.getLong("dvd_id"));
        dvd.setDirector(rs.getString("director"));
        dvd.setStudio(rs.getString("studio"));
        dvd.setReleaseDate(rs.getString("release_date"));
        dvd.setDvdType(rs.getString("dvd_type"));
        dvd.setGenre(rs.getString("genre"));
        return dvd;
    }
}