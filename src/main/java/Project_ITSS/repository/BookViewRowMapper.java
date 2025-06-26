package Project_ITSS.repository;

import Project_ITSS.common.entity.Book;
import Project_ITSS.common.entity.Product;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BookViewRowMapper implements RowMapper<Product> {
    @Override
    public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
        Book book = new Book();
        book.setProductId(rs.getLong("product_id"));
        book.setTitle(rs.getString("title"));
        book.setPrice(rs.getInt("price"));
        book.setWeight(rs.getFloat("weight"));
        book.setRushOrderSupported(rs.getBoolean("rush_order_supported"));
        book.setImageUrl(rs.getString("image_url"));
        book.setBarcode(rs.getString("barcode"));
        book.setImportDate(rs.getString("import_date"));
        book.setIntroduction(rs.getString("introduction"));
        book.setQuantity(rs.getInt("quantity"));
        book.setType(rs.getString("type"));
        book.setBookId(rs.getLong("book_id"));
        book.setAuthors(rs.getString("authors"));
        book.setGenre(rs.getString("genre"));
        book.setPublishers(rs.getString("publishers"));
        book.setCoverType(rs.getString("cover_type"));
        book.setPageCount(rs.getInt("page_count"));
        book.setPublicationDate(rs.getString("publication_date"));
        return book;
    }
}