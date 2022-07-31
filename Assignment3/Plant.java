import java.util.List;
import java.util.Random;
/**
 * Write a description of class Plant here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Plant extends Organism
{
    private static final Random rand = Randomizer.getRandom();
    //plans characteristic
    private static final double PLANT_GROWTH_RATE = 2.5;
    private static final double SPREAD_PROBABILITY = 0.85;
    private static final int MAX_SPREAD = 3;
    private static final int DEATH_INDEX = 450;
    private int PLANT_MATURE_INDEX = 105;
    private int plantGrowth;
    
    /**
     * Plant constructor
       */
    public Plant(boolean randomGrowth, Field field, Position position)
    {
        super(field, position);
        if(randomGrowth)
        {
            plantGrowth = rand.nextInt(100);
        }
        else
        {
            plantGrowth = 0;
        }
    }
    
    /**
     * Plants rate of plants growth and its maximum age.
       */
    private void grow()
    {
        if(plantGrowth < DEATH_INDEX)
            plantGrowth += PLANT_GROWTH_RATE;
        else
            setDead();
    }
    
    /**
     * Plants will not be able to travel around but instead grow when the weather is rainy
       */
    public void act(List<Organism> newPlants)
    {
        grow();        
        if(isAlive())
        {
            if(plantGrowth >= PLANT_MATURE_INDEX && getField().getWeather()== WeatherState.RAINY)
            {
                reproduce(newPlants, false, SPREAD_PROBABILITY, MAX_SPREAD);
            }
         
        }
    }
}
