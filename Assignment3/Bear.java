import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Iterator;
/**
 * A simple model of a bear.
 * bears age, move, eat rabbits, and die.
 * 
 * @2018-02
 */
public class Bear extends Organism implements Hunting, Sex
{
    // Characteristics shared by all bears (class variables).
    
    // The age at which a bear can start to breed
    private static final int BREEDING_AGE = 70;
    // The maximum age that bear can live.
    private static final int MAX_AGE = 350;
    // The posibility of breeding to suceed
    private static final double BREEDING_PROBABILITY = 0.15;
     // The probability of disease to be found from bear
    private static final double DISEASE_PROBABILITY = 0.15;
    // The maximum value that can be achieved for bears birth
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single rabbit,squirrel and fox.Shows how many tiles the bear can travel after its consumption
    private static final int RABBIT_FOOD_VALUE =7 ;// need to optimise this
    private static final int SQUIRREL_FOOD_VALUE = 5;
    private static final int FOX_FOOD_VALUE = 15;
    // A Random number generator to control the breeding of the rabbit population.
    private static final Random rand = Randomizer.getRandom();
    // List of animals that can be consumed by the bear
    private static final List<String> canEat = new ArrayList<String>()
    {{
        add("Rabbit"); 
        add("Squirrel");
        add("Fox");
    }};
    // The bear's age.
    private int age;
    // The food level of the bear.
    private int foodLevel;
    // Individual characteristics (gender) of the organism.
    private Gender gender;
    /**
     * Create a bear. A bear can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     */
    public Bear(boolean randomAge, Field field, Position position)
    {
        super(field, position);
        gender = Sex.super.determineGender();
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(RABBIT_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = RABBIT_FOOD_VALUE;
        }
    }
    
    /**
     * Returns the gender of bear
       */
    public Gender getGender()
    {
        return gender;
    }
    
    /**
     * This is what the bear does most of the time: it hunts for
     * rabbits, rats, snakes and foxes. 
     * In the process, it might breed, die of hunger,
     * or die of old age.
     */
    public void act(List<Organism> newBears)
    {
         if(!isAlive())
          return;
        if(!getField().getTime().equals("night"))
        {return;}
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            reproduce(newBears, true, BREEDING_PROBABILITY, MAX_LITTER_SIZE);            
            // Move towards a source of food if found.
             Pair<Position, Organism> hunt = null;
            if (getField().getWeather() == WeatherState.THUNDER)
            {
                if (rand.nextDouble() <= 0.5)
                    hunt = Hunting.super.findFood(getField(), getPosition(), canEat);
            }
            else
            {
                hunt = Hunting.super.findFood(getField(), getPosition(), canEat);
            }
            
            Position newPosition = null;
            if(hunt != null)
                newPosition = hunt.getKey();
            if(newPosition != null)
            {
                eatPrey(hunt.getValue());
                if(hunt.getValue().getDisease())
                {
                    setDisease(true);
                }   
            }
                
            if(newPosition == null) { 
                // No food found - try to move to a free position.
                newPosition = getField().freeAdjacentPosition(getPosition(), this);
            }
            // See if it was possible to move.
            if(newPosition != null) {
                setPosition(newPosition);
                if(this.getDisease()){return;}
                 List<Position> potentialDisease = getField().getFreeAdjacentPositions(getPosition(), this);
                for(Position loc : potentialDisease)
                {
                    Organism pD = (Organism)getField().getObjectAt(loc);
                    if(pD == null)
                        continue;
                    if(pD.getDisease() && rand.nextDouble() <= DISEASE_PROBABILITY)
                    {
                        setDisease(true);
                        break;
                    }
                }
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }
    
    /**
     * Determines what animal was hunted and eat them.
       */
    public void eatPrey(Organism food)
    {
        if(food instanceof Rabbit)
        {
            foodLevel = RABBIT_FOOD_VALUE;
        }
        else if(food instanceof Squirrel)
        {
            foodLevel = SQUIRREL_FOOD_VALUE;
        }
        else if(food instanceof Fox)
        {
            foodLevel = FOX_FOOD_VALUE;
        }
    }
    
    /**
     * Increase the age. This could result in the bear's death.
     */
    private void incrementAge()
    {
        if (getDisease())
        {age *= 2;}
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this bear more hungry. This could result in the bear's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
}
