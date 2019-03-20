package ru.shemplo.fitness.entities;

import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter @Setter
@NoArgsConstructor
public class FitnessClient {
    
    private Integer id;
    
    private String name, secondName, lastName;
    
    private LocalDate birthday;
    
}
