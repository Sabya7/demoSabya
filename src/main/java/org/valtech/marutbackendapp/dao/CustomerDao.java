package org.valtech.marutbackendapp.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "CUSTOMER")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDao {

    @Id
    private String id;

    @NotNull
    @Email
    private String email;

    @NotNull
    private String password;

    private String firstName;

    private String lastName;

    private String shoppingList_id;

    private String FcmToken;

    public CustomerDao(String id, String email, String password, String firstName, String lastName, String shoppingList_id) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.shoppingList_id = shoppingList_id;
    }
}
