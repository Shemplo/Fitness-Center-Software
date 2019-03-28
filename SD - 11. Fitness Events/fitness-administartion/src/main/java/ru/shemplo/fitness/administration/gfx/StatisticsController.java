package ru.shemplo.fitness.administration.gfx;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;

import ru.shemplo.fitness.statistics.StatisticsModule;
import ru.shemplo.snowball.annot.Snowflake;
import ru.shemplo.snowball.annot.processor.Snowball;
import ru.shemplo.snowball.annot.processor.SnowflakeInitializer;

public class StatisticsController implements Initializable {

    @Snowflake (manual = true)
    @FXML private BarChart <LocalDate, Double> dayVisits, monthVisits, averageVisits;
    
    private StatisticsModule statisticsModule;
    
    @Override
    public void initialize (URL location, ResourceBundle resources) {
        SnowflakeInitializer.initFields (Snowball.getContext (), this);
        
        statisticsModule.getAverageMonthStatistics (LocalDateTime.now ());
    }
    
}
