import java.util.List;
import java.util.Random;
import java.util.Iterator;
/**
 * A simple model of a rabbit.
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Rabbit extends Organism implements Sex
{
    // Characteristics shared by all rabbits (class variables).

    // The age at which a rabbit can start to breed.
    private static final int BREEDING_AGE = 5;
    // The maximum age that rabbits can live
    private static final int MAX_AGE = 42;
    // The posibility of breeding to suceed
    private static final double BREEDING_PROBABILITY = 0.70;
    // The probability of disease to be found from rabbit
    private static final double DISEASE_PROBABILITY = 0.2;
    // The maximum value that can be achieved for rabbits birth.
    private static final int MAX_LITTER_SIZE = 5;
    // The food value of the food consumption 
    private static final int PLANT_FOOD_VALUE = 13;
    // A Random number generator to control the breeding of the rabbit population.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (gender) of the organism.
    private Gender gender;
    // The age of the rabbit.
    private int age;
    private int foodLevel;
    /**
     * Create a new rabbit. A rabbit may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the rabbit will have a random age.
     * @param field The field currently occupied.
     * @param position The position within the field.
     */
    public Rabbit(boolean randomAge, Field field, Position Position)
    {
        super(field, Position);
        gender = Sex.super.determineGender();
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(PLANT_FOOD_VALUE);
        }
    }
    
    /**
     * Returns gender of rabbit
       */
    public Gender getGender()
    {
        return gender;
    }
    
    /**
     * This is what the rabbit does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newRabbits A list to return newly born rabbits.
     */
    public void act(List<Organism> newRabbits)
    {
        if(!isAlive())
          return;
        if(getField().getTime().equals("night"))
        {
            return;
        }
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            reproduce(newRabbits, true, BREEDING_PROBABILITY, MAX_LITTER_SIZE);            
            Position newPosition = findPlants();
            // Try to move into a free Position.
            if(newPosition == null)
                newPosition = getField().freeAdjacentPosition(getPosition(), this);
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

    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel < 0)
        {
            setDead();
        }
    }
    
    private Position findPlants()
    {
        List<Position> adjacent = getField().adjacentPositions(getPosition());
        Iterator<Position> it = adjacent.iterator();
        while(it.hasNext())
        {
            Position loc = it.next();
            Plant potentialFood = (Plant)getField().getWalkable(loc);
            if(potentialFood == null)
                continue;
            else
            {
                foodLevel = PLANT_FOOD_VALUE;
                potentialFood.setDead();
                getField().removeWalkable(potentialFood);
                return loc;
            }
        }
        return null;
    }
    
    /**
     * Increase the age.
     * This could result in the rabbit's death.
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
    
}
