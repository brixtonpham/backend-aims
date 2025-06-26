package Project_ITSS.repository;

import Project_ITSS.common.entity.CD;
import Project_ITSS.common.entity.Product;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CDViewRowMapper implements RowMapper<Product> {
    @Override
    public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
        CD cd = new CD();
        cd.setProductId(rs.getLong("product_id"));
        cd.setTitle(rs.getString("title"));
        cd.setPrice(rs.getInt("price"));
        cd.setWeight(rs.getFloat("weight"));
        cd.setRushOrderSupported(rs.getBoolean("rush_order_supported"));
        cd.setImageUrl(rs.getString("image_url"));
        cd.setBarcode(rs.getString("barcode"));
        cd.setImportDate(rs.getString("import_date"));
        cd.setIntroduction(rs.getString("introduction"));
        cd.setQuantity(rs.getInt("quantity"));
        cd.setType(rs.getString("type"));
        cd.setCdId(rs.getLong("cd_id"));
        cd.setGenre(rs.getString("genre"));
        cd.setArtists(rs.getString("artists"));
        cd.setTrackList(rs.getString("track_list"));
        cd.setReleaseDate(rs.getString("release_date"));
        cd.setRecordLabel(rs.getString("record_label"));
        return cd;
    }
}