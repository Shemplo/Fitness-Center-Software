package ru.shemplo.fitness.entities;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
    
    private String firstName, 
                   secondName, 
                   lastName,
                   
                   organization,
                   position,
                   
                   country,
                   state,
                   city,
                   district,
                   
                   phone,
                   email,
                   homePage,
                   
                   remark;
    
    private LocalDate birthday;
    
    /**
     * Only for update via {@link FitnessClientService#updateClient(FitnessClient)} purposes
     */
    private LocalDateTime lastTimeUpdated;

    @Override
    public boolean isCompleted () {
        return id != null && firstName != null && lastName != null;
    }
    
    public String getFullName () {
        String second = Optional.ofNullable (getSecondName ()).orElse ("");
        return String.format ("%s %s %s", lastName, firstName, second);
    }
    
    public Map <String, String> findDiffWith (FitnessClient client) {
        Map <String, String> diff = new HashMap <> ();
        for (Field field : this.getClass ().getDeclaredFields ()) {
            try {
                Object thisValue = field.get (this), clientValue = field.get (client);
                if (!Objects.equals (thisValue, clientValue) && clientValue != null) {
                    final String name = field.getName ().toLowerCase ();
                    if (!"id".equalsIgnoreCase (name)) { // this is redundant
                        diff.put (name, Objects.toString (clientValue));                        
                    }
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {}
        }
        
        return diff;
    }
    
}
