package Project_ITSS.common.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Book extends Product {
    private long bookId;
    private String genre;
    private int pageCount;
    private String publicationDate;
    private String authors;
    private String publishers;
    private String coverType;

    public String getType(){
        return "book";
    }
}