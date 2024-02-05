package com.evoapartments.accommodationbe.domain.accommodation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class Type {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String typeName;
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "type", fetch = FetchType.EAGER)
    private List<Accommodation> accommodations;

    public Type() {
        this.accommodations = new ArrayList<>();
    }
}
