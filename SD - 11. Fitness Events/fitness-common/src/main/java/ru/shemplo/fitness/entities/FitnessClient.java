package ru.shemplo.fitness.entities;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.shemplo.fitness.services.FitnessClientService;

@ToString
@Getter @Setter
@NoArgsConstructor
public class FitnessClient implements Updatable, Identifiable {
    
    private Integer id;
    
    private String name, secondName, lastName;
    
    private Date birthday;
    
    /**
     * Only for update via {@link FitnessClientService#updateClient(FitnessClient)} purposes
     */
    private Date lastTimeUpdated;
    
}
