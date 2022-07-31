import java.util.List;
import java.util.Random;
import java.lang.reflect.*;
/**
 * A class representing shared characteristics of organisms.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public abstract class Organism 
{
    private static int organismID = 0;
    private int thisID;
    // Whether the organism is alive or not.
    private boolean alive;
    // The organism's field.
    private Field field;
    // The organism's position in the field.
    private Position position;
    
    private boolean disease;
    
    /**
     * Create a new organism at position in field.
     * 
     * @param field The field currently occupied.
     * @param position The position within the field.
     */
    public Organism(Field field, Position position)
    {
        ++organismID;
        thisID = organismID;
        alive = true;
        this.field = field;
        setPosition(position);
    }
    
    
    /**
     * Make this organism act - that is: make it do
     * whatever it wants/needs to do.
     * @param newOrganisms A list to receive newly born organisms.
     */
    abstract public void act(List<Organism> newOrganisms);
    
    /**
     * Make an organism reproduce, they have probability of reproducing, maximum reproduction
     * size, a list to store new organisms and whether if they require a partner to reproduce.
       */
    protected void reproduce(List<Organism> newOrganism, boolean needPartner, double productionProb, int maxProduction)
    {
        Random r = new Random();
        List<Position> free = field.getFreeAdjacentPositions(position, this);
        Pair<Boolean, Organism> outcome = canReproduce(needPartner, productionProb);
        if(outcome == null)
            return;
        boolean canProduce = outcome.getKey();
        if(canProduce)
        {
            int maxNewOrganism = r.nextInt(maxProduction) + 1;
            for(int x = 0; x < maxNewOrganism && free.size() > 0; x++)
            {
                Position loc = free.remove(0);
                try{
                    //try to get the constructor of a class and create a new instance of it.
                    newOrganism.add(
                    outcome.getValue().getClass().getConstructor(boolean.class,Field.class, Position.class).newInstance(false,field,loc));
                }
                catch(InstantiationException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException
                | InvocationTargetException e){} 
            }
        }
    }
    
    /**
     * Checks if an organism can reproduce. If it requires a partner it will check nearby objects
     * for opposite sex of the same species, if it doesnt then it will just check the probability.
     * It returns a Pair class which stores a boolean - whether if they can reproduce and an Organism - the species that 
     * is reproducing.
       */
    private Pair<Boolean, Organism> canReproduce(boolean needPartner, double productionProb)// can use Object then get class
    {
        Random r = new Random();
        Pair<Boolean, Organism> outcome = new Pair<Boolean, Organism>(false, null);
        List<Position> nearbyObject = field.adjacentPositions(position);
        if(needPartner)
        {
           boolean foundMate = false; 
           for(Position adjacent : nearbyObject)
           {
               Object potentialMate  = field.getObjectAt(adjacent);
               if(potentialMate == null)
               {
                   continue;
               }
               Class<?> pM = potentialMate.getClass();
               Class<?> thisOrganism = this.getClass();
               if(this.getClass() == potentialMate.getClass())
               {
                  try
                  { //from a class, try to get a method from such class.
                      Method genderThis = thisOrganism.getDeclaredMethod("getGender");
                      Method genderNext = pM.getDeclaredMethod("getGender");
                      if(!genderThis.invoke(this,null).toString().equals(genderNext.invoke(potentialMate, null).toString()))
                      {
                          outcome.set(true, (Organism)potentialMate);//Only requires a reference, doesnt matter which.
                          foundMate = true;
                          break;
                      }
                  }
                  catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException e){}
                  
               }
           }
           
           if(foundMate && r.nextDouble() <= productionProb)
           {
               return outcome;
           }
        }
        else
        {
            if(r.nextDouble() <= productionProb)
            {
                outcome.set(true, this);
                return outcome;
            }
        }
        return null;
    }
    
    /**
     * Returns whether if an organism is carrying a disease or not
       */
    protected boolean getDisease()
    {
        return disease;
    }
    
    /**
     *Give organism disease
       */
    protected void setDisease(boolean disease)
    {
        this.disease = disease;
    }
    
    /**
     * Check whether the organism is alive or not.
     * @return true if the organism is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the organism is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if(position != null) {
            if(!(this instanceof Plant))
            {
                field.clear(position);
            }
            else
            {
                field.removeWalkable(this);
            }
            position = null;
            field = null;
        }
    }

    /**
     * Return the organism's position.
     * @return The organism's position.
     */
    protected Position getPosition()
    {
        return position;
    }
    
    /**
     * Place the organism at the new position in the given field.
     * @param newPosition The organism's new position.
     */
    protected void setPosition(Position newPosition)
    {
        if(this instanceof Plant){
            position = newPosition;
            field.addWalkable(this);
            return;
        }
        
        if(position != null) {
            field.clear(position);
        }
        position = newPosition;
        field.place(this, newPosition);
    }
    
    /**
     * Return the organism's field.
     * @return The organism's field.
     */
    protected Field getField()
    {
        return field;
    }
    
    /**
     * Each organism is given a unique id.
       */
    protected int getID(){return thisID;}
    
    /**
     * Arraylist remove works by comparing objects, so we want to compare the ID of
     * the organism to check whether if there is a match.
       */
    public boolean equals(Object obj)
    {
        if(obj instanceof Organism)
        {
            Organism compare = (Organism)obj;
            if(compare.getID() == thisID)
            {
                return true;
            }
            else
                return false;
        }
        return false;
    }
    
}
