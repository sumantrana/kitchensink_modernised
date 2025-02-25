package org.jboss.as.quickstarts.kitchensink.member.internal;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "email"))

public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @NotNull(message = "Must not be null.")
    @Size(min = 1, max = 25, message = "Length must be between 1 and 25.")
    @Pattern(regexp = "[^0-9]*", message = "Must not contain numbers.")
    String name;

    @NotNull(message = "Must not be null.")
    @NotEmpty(message = "Must not be empty.")
    @Email(message = "Must be a well-formed email address.")
    String email;

    @NotNull(message = "Must not be null.")
    @Size(min = 10, max = 12, message = "Length must be between 10 and 12.")
    @Digits(fraction = 0, integer = 12, message = "Must be an whole number.")
    String phoneNumber;

}
