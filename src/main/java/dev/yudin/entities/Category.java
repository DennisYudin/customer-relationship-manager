package dev.yudin.entities;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class Category {

    private long id;

    @NotNull(message = "cannot be empty")
    @Size(min = 1, message = "cannot be less then 1 char")
    @Pattern(regexp = "[A-Z]{1}\\D+", message = "only chars with first capital letter")
    private String title;
}
