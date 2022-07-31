import java.util.Random;
/**
 * Class that determines the weather of the simulation
 * Animals and plants will behave differently under different weathers.
 * @2018-02
 */
public class Weather
{
    // The  current weather state
    private WeatherState currentWeather;
    // Probability of certain weather.
    private static final double windProb = 0.2;
    private static final double rainProb = 0.25;
    private static final double thunderProb = 0.15;

    /**
     * Constructor for object Weather
     */
    public Weather(){}
    
    /**
     * A method to determine weather by it probability
       */
    public void weatherChange()
    {
        Random r = new Random();
        if (r.nextDouble() <= windProb)
        {
            currentWeather = WeatherState.WINDY;
        }
        else if(r.nextDouble() >= windProb && r.nextDouble() <= (windProb+rainProb))
        {
            currentWeather = WeatherState.RAINY;
        }
        else if(r.nextDouble() >= (windProb+rainProb) && r.nextDouble() <=
        (windProb+rainProb+thunderProb))
        {
            currentWeather = WeatherState.THUNDER;
        }
        else
        {currentWeather = WeatherState.SUNNY;}
    }
    
    public WeatherState getCurrentWeather()
    {
        return currentWeather;
    }
    

}
