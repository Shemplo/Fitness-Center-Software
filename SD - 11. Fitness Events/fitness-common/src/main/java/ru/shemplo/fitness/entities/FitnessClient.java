package ru.shemplo.fitness.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.*;
import ru.shemplo.fitness.services.FitnessClientService;

@ToString
@Getter @Setter
@NoArgsConstructor
public class FitnessClient implements Updatable {
    
    private Integer id;
    
    private String name, secondName, lastName;
    
    private LocalDate birthday;
    
    /**
     * Only for update via {@link FitnessClientService#updateClient(FitnessClient)} purposes
     */
    private LocalDateTime lastTimeUpdated;
    
}
