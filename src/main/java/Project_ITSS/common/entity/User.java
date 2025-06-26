package Project_ITSS.common.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class User {
    private int userId;
    private int password;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String registrationDate;
    private double salary;
}