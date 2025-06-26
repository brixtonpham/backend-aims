package Project_ITSS.common.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DVD extends Product {
    private long dvdId;
    private String releaseDate;
    private String dvdType;
    private String genre;
    private String studio;
    private String director;

    public String getType(){
        return "dvd";
    }
}