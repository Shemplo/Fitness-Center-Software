package ru.shemplo.fitness.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.shemplo.fitness.services.FitnessClientService;

@ToString
@Getter @Setter
@NoArgsConstructor
public class FitnessClient implements Updatable, Identifiable, Completable {
    
    private Integer id;
    
    private String name, secondName, lastName;
    
    private LocalDate birthday;
    
    /**
     * Only for update via {@link FitnessClientService#updateClient(FitnessClient)} purposes
     */
    private LocalDateTime lastTimeUpdated;

    @Override
    public boolean isCompleted () {
        return id != null && name != null && lastName != null;
    }
    
}
