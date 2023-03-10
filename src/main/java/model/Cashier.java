package model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class Cashier {

    private int id;
    @NotNull(message = "Name cannot be null")
    @Pattern(regexp = "[a-zA-Z\\-]+ [a-zA-Z\\-]+", message = "The name should contain only letters and dash. " +
            "First and last name should be separated by a space")
    private String name;
    private String position;
    @Email(message = "Email should be valid")
    private String email;

    public Cashier() {}
    public Cashier(int id, String name, String position, String email) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Cashier{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", position='" + position + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
