import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A simple model of a fox.
 * Foxes age, move, eat rabbits, and die.
 * Foxes will die faster if they are diseased
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Fox extends Organism implements Hunting, Sex
{
    // Characteristics shared by all foxes (class variables).
    
    // The age at which a fox can start to breed.
    private static final int BREEDING_AGE = 30;
    // The maximum age that fox can live.
    private static final int MAX_AGE = 70;
    // The posibility of breeding to suceed
    private static final double BREEDING_PROBABILITY = 0.31;
    // The probability of disease to be found from fox
    private static final double DISEASE_PROBABILITY = 0.19;
    
    // The maximum value that can be achieved for foxes birth.
    private static final int MAX_LITTER_SIZE = 4;
    // The food value of a single rabbit which shows how many tiles the fox can travel after its consumption
    private static final int RABBIT_FOOD_VALUE = 15;
    private static final int SQUIRREL_FOOD_VALUE = 10;
    // A Random number generator to control the breeding of the rabbit population..
    private static final Random rand = Randomizer.getRandom();
    
    private static final List<String> canEat = new ArrayList<String>(){{
        add("Rabbit"); 
        add("Squirrel");
    }};
    // Individual characteristics (instance fields).
    // The fox's age.
    private int age;
    // The fox's food level.
    private int foodLevel;
    // Individual characteristics (gender) of the organism.
    private Gender gender;
    /**
     * Create a fox. A fox can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the fox will have random age and hunger level.
     * @param field The field currently occupied.
     * @param position The position within the field.
     */
    public Fox(boolean randomAge, Field field, Position position)
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
     * Returns gender of fox
       */
    public Gender getGender()
    {
        return gender;
    }
   
    /**
     * Determines what the fox has hunted and eat the hunted prey
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
    }
    
    /**
     * This is what the fox does most of the time: it hunts for
     * rabbits. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newFoxes A list to return newly born foxes.
     */
    public void act(List<Organism> newFoxes)
    {
         if(!isAlive())
          return;
        if(getField().getTime().equals("night"))
        {return;}
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            reproduce(newFoxes, true, BREEDING_PROBABILITY, MAX_LITTER_SIZE);           
            // Move towards a source of food if found.
            Pair<Position, Organism> hunt = null;
            if (getField().getWeather() == WeatherState.THUNDER)
            {
                if (rand.nextDouble() <= 0.4)
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
     * Increase the age. This could result in the fox's death.
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
     * Make this fox more hungry. This could result in the fox's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    

}
