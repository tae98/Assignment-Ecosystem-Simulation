import java.util.Random;
/**
 * A simple interface that determines the sex of an organism.
 * Not all organism requires both sex to reproduce such as plants therefore only
 * those that requires the opposite sex to reproduce need to implement this interface
 * @author (your name)
 * @version (a version number or a date)
 */
public interface Sex
{
    /**
     * Unimplemented method that will be required to be implemented by classes.
       */
    public Gender getGender();
    
    /**
     * Default interface method which calculates what gender an organism will be. 
     * So far the chances are 50% for each.
       */
    default Gender determineGender()
    {
        Random r = new Random();
        double x = r.nextDouble();
        if(x <= 0.5)
        {
            return Gender.MALE;
        }
        else
        {
            return Gender.FEMALE;
        }
    }
}
