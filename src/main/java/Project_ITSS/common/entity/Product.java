package Project_ITSS.common.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Book.class, name = "book"),
    @JsonSubTypes.Type(value = CD.class, name = "cd"),
    @JsonSubTypes.Type(value = DVD.class, name = "dvd")
})
public class Product {
    private long productId;
    private String title;
    private int price;
    private float weight;
    private boolean rushOrderSupported;
    private String imageUrl;
    private String barcode;
    private String importDate;
    private String introduction;
    private int quantity;
    private String type;
}