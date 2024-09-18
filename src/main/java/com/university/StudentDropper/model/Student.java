package com.university.StudentDropper.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static com.university.StudentDropper.service.GeneticAlgorithmService.PREFERENCE_SCALE;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "student")
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    String surname;
    Integer age;
    String email;
    String password;
    String role;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<Preference> preferences;

    public int getPreferencesRank(Destination destination) {
        return this.preferences.stream()
                .filter(preference -> preference.getDestination().equals(destination))
                .findFirst()
                .map(Preference::getRank)
                .orElse(PREFERENCE_SCALE + 1);
    }
}
