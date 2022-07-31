import java.util.Random;
import java.util.Iterator;
import java.util.List;
/**
 * A simple model of a squirrel.
 * squirrels age, move, breed, and die.
 * 
 * @2018-02
 */
public class Squirrel extends Organism implements Sex
{
    // Characteristics shared by all squirrels (class variables).

    // The age at which a squirrel can start to breed.
    private static final int BREEDING_AGE = 30;
    // The maximum age that squirrel can live
    private static final int MAX_AGE = 210;
    // The posibility of breeding to suceed
    private static final double BREEDING_PROBABILITY = 0.5;
    // The probability of disease to be found from squirrel
    private static final double DISEASE_PROBABILITY = 0.2;
    // The maximum value that can be achieved for squirrels birth.
    private static final int MAX_LITTER_SIZE = 4;
    // The food value of the food consumtion. 
    private static final int PLANT_FOOD_VALUE = 8;
    // A Random number generator to control the breeding of the rabbit population..
    private static final Random rand = Randomizer.getRandom();
    
    // The age of the rabbit.
    private int age;
    // Individual characteristics (gender) of the organism.
    private Gender gender;
    private int foodLevel;
    /**
     * Create a new squirrel. A squirrel may be created with age
     * zero (a new born) or with a random age.
     * 
     */
    public Squirrel(boolean randomAge, Field field, Position position)
    {
        super(field, position);
        age = 0;
        foodLevel = PLANT_FOOD_VALUE;
        gender = Sex.super.determineGender();
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(PLANT_FOOD_VALUE);
        }
        
        //squirrel has a chance of carrying disease upon creation or birth
        if(rand.nextDouble() <= DISEASE_PROBABILITY)
        {setDisease(true);}
        
    }
    
    /**
     * Returns the gender of squirrel
       */
    public Gender getGender()
    {
        return gender;
    }
    
    /**
     * This is what the squirrel does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     */
    public void act(List<Organism> newSquirrels)
    {
         if(!isAlive())
          return;
        if(getField().getTime().equals("night"))
        {return;}
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            reproduce(newSquirrels, true, BREEDING_PROBABILITY, MAX_LITTER_SIZE); 
            Position newPosition = findPlants();
            // Try to move into a free position.
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
    
    /**
     * squirrels will get hungry for each step
     * and will die if they do not find food in time.
       */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel < 0)
        {
            setDead();
        }
    }
    
    /**
     * Checks nearby positions for plants and eat them.
       */
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
     * This could result in the squirrel's death.
     * If they are carrying a disease they die faster;
     */
    private void incrementAge()
    {
        if (getDisease())
        {age *= 2;}
        else
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
        
    }
    

}

