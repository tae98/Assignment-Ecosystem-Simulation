import java.util.List;
import java.util.Iterator;
import java.util.HashMap;
import java.lang.reflect.*;
import java.util.Random;
/**
 * An interface that can be implemented by animals who
 * has the ability to hunt. 
 * @2018-02
 */
public interface Hunting
{
    /**
     * Unimplemented method - animals that implement this interface will have to implement this method
     * This method is used to eat animals that have been found near a predator.
       */
    void eatPrey(Organism food);
    
    /**
     * Java 8's default method, allows a pre-implemented method in an interface
     * This reduces overall coupling between classes.
     * This method searches for nearby animals and return their location and the animal itself
     * so the predator can eat them.
       */
    default Pair<Position, Organism> findFood(Field field, Position currentPosition, List<String> preyType)
    {
        List<Position> adjacent = field.adjacentPositions(currentPosition);
        Iterator<Position> it = adjacent.iterator();
        while(it.hasNext()) {
            Position where = it.next();
            Object organism = field.getObjectAt(where);
            if(organism == null)//add a check for plants
                continue;
            Class preyClass = organism.getClass();
            for(String prey : preyType)
            {
                if(preyClass.toString().contains(prey) )
                {
                  try
                  {
                      Method alive = preyClass.getSuperclass().getDeclaredMethod("isAlive");
                      Method setDead = preyClass.getSuperclass().getDeclaredMethod("setDead");
                      if((boolean)alive.invoke(organism, null))
                      {
                          setDead.invoke(organism, null);
                          return new Pair(where, (Organism)organism);
                      }
                  }
                  catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException e){}
                }
            }
        }
        return null;
    }
    
}
