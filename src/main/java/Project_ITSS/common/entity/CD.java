package Project_ITSS.common.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CD extends Product {
    private long cdId;
    private String trackList;
    private String genre;
    private String recordLabel;
    private String artists;
    private String releaseDate;

    public String getType(){
        return "cd";
    }
}