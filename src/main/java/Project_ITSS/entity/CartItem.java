package Project_ITSS.entity;

import Project_ITSS.common.entity.Product;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CartItem {
    Product product;
    int quantity;

    public CartItem(Product product,int quantity){
        this.product = product;
        this.quantity = quantity;
    }

}
