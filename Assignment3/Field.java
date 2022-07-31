import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Represent a rectangular grid of field positions.
 * Each position is able to store a single animal.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29
 */
public class Field
{
    // A random number generator for providing random positions.
    private static final Random rand = Randomizer.getRandom();
    
    // The depth and width of the field.
    private int depth, width;
    // Storage for the animals.
    private Object[][] field;
    //Storage for plants and potentially other organisms that can be walked on
    private ArrayList<Object> walkableObj;
    //Current weather of this field
    private Weather weather;
    //Current time of this field
    private String time = "";
    
    /**
     * Represent a field of the given dimensions.
     * @param depth The depth of the field.
     * @param width The width of the field.
     */
    public Field(int depth, int width)
    {
        weather = new Weather();
        weather.weatherChange();
        this.depth = depth;
        this.width = width;
        field = new Object[depth][width];
        walkableObj = new ArrayList<>();
    }
    
    /**
     * Calculates time based on steps
     * This uses base 100 for day, noon and night time
       */
    public void time(int step)
    {
        if(step%100 <= 33)
        {
            time = "morning";
        }
        else if(step%100 >= 66)
        {
            time = "night";
        }
        else
        {
            time = "noon";
        }
    }
    
    /**
     * Returns the time of the field
       */
    public String getTime()
    {
        return time;
    }
    
    /**
     * Returns the weather of the field
       */
    public WeatherState getWeather()
    {
        return weather.getCurrentWeather();
    }
    
    /**
     * Change the weather of the field
       */
    public void changeWeather()
    {
        weather.weatherChange();
    }
    
    /**
     * Add plants to field
       */
    public void addWalkable(Object walkable)
    {
        walkableObj.add(walkable);
    }
    
    /**
     * Checks if a plant already exist within the list
       */
    public boolean existingWalkable(Position path)
    {
       walkableObj.trimToSize();
       for(int i = 0; i < walkableObj.size(); i++)
       {
           Organism walkable = (Organism)walkableObj.get(i);
           if(walkable.getPosition() == null)
               continue;
           if(walkable.getPosition().equals(path)){
                return true;
           }
       }
       return false;
    }
    
    /**
     * Remove plant from list using remove from array list
     * A modification was required in the Organism class to allow
     * the remove method to function properly
       */
    public void removeWalkable(Object walkable)
    {
        walkableObj.trimToSize();
        walkableObj.remove(walkable);
    }
    
    /**
     * Returns a plant by position
       */
    public Object getWalkable(Position l)
    {
        walkableObj.trimToSize();
        for(Object walkable : walkableObj)
        {
            Organism walkableOrganism = (Organism) walkable;
            if(walkableOrganism.getPosition() == null)
                continue;
            if(walkableOrganism.getPosition().equals(l))
            {
                return walkableOrganism;
            }
        }
        return null;
    }
    
    /**
     * Empty the field.
     */
    public void clear()
    {
        for(int row = 0; row < depth; row++) {
            for(int col = 0; col < width; col++) {
                field[row][col] = null;
            }
        }
    }
    
    /**
     * Clear the given position.
     * @param position The position to clear.
     */
    public void clear(Position position)
    {
        field[position.getRow()][position.getCol()] = null;
    }
    
    /**
     * Place an animal at the given position.
     * If there is already an animal at the position it will
     * be lost.
     * @param animal The animal to be placed.
     * @param row Row coordinate of the position.
     * @param col Column coordinate of the position.
     */
    public void place(Object animal, int row, int col)
    {
        place(animal, new Position(row, col));
    }
    
    /**
     * Place an animal at the given position.
     * If there is already an animal at the position it will
     * be lost.
     * @param animal The animal to be placed.
     * @param position Where to place the animal.
     */
    public void place(Object animal, Position position)
    {
        field[position.getRow()][position.getCol()] = animal;
    }
    
    /**
     * Return the animal at the given position, if any.
     * @param position Where in the field.
     * @return The animal at the given position, or null if there is none.
     */
    public Object getObjectAt(Position position)
    {
        return getObjectAt(position.getRow(), position.getCol());
    }
    
    /**
     * Return the animal at the given position, if any.
     * @param row The desired row.
     * @param col The desired column.
     * @return The animal at the given position, or null if there is none.
     */
    public Object getObjectAt(int row, int col)
    {
        return field[row][col];
    }
    
    /**
     * Generate a random position that is adjacent to the
     * given position, or is the same position.
     * The returned position will be within the valid bounds
     * of the field.
     * @param position The position from which to generate an adjacency.
     * @return A valid position within the grid area.
     */
    public Position randomAdjacentPosition(Position position)
    {
        List<Position> adjacent = adjacentPositions(position);
        return adjacent.get(0);
    }
    
    /**
     * Get a shuffled list of the free adjacent positions.
     * There maybe some requirement as to how a free space is obtained 
     * @param position Get positions adjacent to this.
     * @return A list of free adjacent positions.
     */
    public List<Position> getFreeAdjacentPositions(Position position, Organism potentialWalkable)
    {
        List<Position> free = new LinkedList<>();
        List<Position> adjacent = adjacentPositions(position);
        for(Position next : adjacent) {
            // plants check if a position has already been occupied by a plant
            if(potentialWalkable instanceof Plant)
            {
                if(!existingWalkable(next))
                {
                    free.add(next);
                }
            }
            else if(getObjectAt(next) == null) {
                free.add(next);
            }
        }
        return free;
    }
    
    // problem being when an animal dies, the position that it has pinpointing a plant is set to null
    // which means that we have no way of retrieving such plant from that position
    // one way is to have field store a list of positions that contains object, and when we need to reference a object
    // we get it from field.
    
    /**
     * Try to find a free position that is adjacent to the
     * given position. If there is none, return null.
     * The returned position will be within the valid bounds
     * of the field.
     * @param position The position from which to generate an adjacency.
     * @return A valid position within the grid area.
     */
    public Position freeAdjacentPosition(Position position, Organism potentialWalkable)
    {
        // The available free ones.
        List<Position> free = getFreeAdjacentPositions(position, potentialWalkable);
        if(free.size() > 0) {
            return free.get(0);
        }
        else {
            return null;
        }
    }

    /**
     * Return a shuffled list of positions adjacent to the given one.
     * The list will not include the position itself.
     * All positions will lie within the grid.
     * @param position The position from which to generate adjacencies.
     * @return A list of positions adjacent to that given.
     */
    public List<Position> adjacentPositions(Position position)
    {
        assert position != null : "Null position passed to adjacentPositions";
        // The list of positions to be returned.
        List<Position> positions = new LinkedList<>();
        if(position != null) {
            int row = position.getRow();
            int col = position.getCol();
            for(int roffset = -1; roffset <= 1; roffset++) {
                int nextRow = row + roffset;
                if(nextRow >= 0 && nextRow < depth) {
                    for(int coffset = -1; coffset <= 1; coffset++) {
                        int nextCol = col + coffset;
                        // Exclude invalid positions and the original position.
                        if(nextCol >= 0 && nextCol < width && (roffset != 0 || coffset != 0)) {
                            positions.add(new Position(nextRow, nextCol));
                        }
                    }
                }
            }
            
            // Shuffle the list. Several other methods rely on the list
            // being in a random order.
            Collections.shuffle(positions, rand);
        }
        return positions;
    }

    /**
     * Return the depth of the field.
     * @return The depth of the field.
     */
    public int getDepth()
    {
        return depth;
    }
    
    /**
     * Return the width of the field.
     * @return The width of the field.
     */
    public int getWidth()
    {
        return width;
    }
}
