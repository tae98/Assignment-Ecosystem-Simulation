import java.lang.reflect.*;

import java.util.Random;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 * 
 * @version 2016.02.29 (2)
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a fox will be created in any given grid position.
    private static final double FOX_CREATION_PROBABILITY = 0.04;//0.02
    // The probability that a rabbit will be created in any given grid position.
    private static final double RABBIT_CREATION_PROBABILITY = 0.2;
    // The probability of different animals and plants created in any given grid position
    private static final double RAT_CREATION_PROBABILITY = 0.03;  
    private static final double SNAKE_CREATION_PROBABILITY = 0.02;
    private static final double OWL_CREATION_PROBABILITY = 0.2; 
    private static final double PLANT_CREATION_PROBABILITY = 0.25;  
    // List of organisms in the field.
    private ArrayList<Organism> organisms;
    // The current state of the field.
    private Field field;
    // The current number of the step of the simulation.
    private int step;
    // A graphic visual of the simulation.
    private SimulatorView view;
    
    
    public static void main(String[] args) 
    {
    	Simulator s = new Simulator();
    	s.simulate(1000);
    }
    
    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }
    
    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }
        
        organisms = new ArrayList<>();
        field = new Field(depth, width);

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        view.setColor(Rabbit.class, Color.ORANGE);
        view.setColor(Fox.class, Color.BLUE);
        view.setColor(Squirrel.class, Color.CYAN);
        view.setColor(Eagle.class, Color.YELLOW);
        view.setColor(Bear.class, Color.RED);
        view.setColor(Plant.class, Color.GREEN);
        // Setup a valid starting point.
        Reset();
    }
    
    /**
     * Run the simulation for extensive amount from current state,5500 step.
     */
    public void runExtensiveSimulation()
    {
        simulate(5500);
    }
    
    /**
     * Run the simulation from current state to speicific number of steps.
     */
    public void simulate(int numSteps)
    {
        for(int step = 1; step <= numSteps && view.isViable(field); step++) {
            OneSimulation();
            //delay(60);   // uncomment this to run more slowly
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * fox and rabbit.
     */
    public void OneSimulation()
    {
        step++;
        //calculate time based on steps
        field.time(step);
        // Provide space for newborn organisms.
        List<Organism> newOrganisms = new ArrayList<>();        
        // Let all rabbits act.
        int x =0;
        for(Iterator<Organism> it = organisms.iterator(); it.hasNext(); ) {
            Organism organism = it.next();
            organism.act(newOrganisms);
            if(!organism.isAlive()) {
                it.remove();
            }
        }
        
        if(step%50 == 0)
        {
            field.changeWeather();
        }
        // Add the newly born foxes and rabbits to the main lists.
        organisms.addAll(newOrganisms);
        organisms.trimToSize();
        view.showStatus(step, field);
    }
        
    /**
     * Reset the simulation to the value on which it had at the begining.
     */
    public void Reset()
    {
        step = 0;
        organisms.clear();
        populate();
        field.time(step);
        // Show the starting state in the view.
        view.showStatus(step, field);
    }
    
    /**
     * Randomly populate the field with foxes and rabbits.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= PLANT_CREATION_PROBABILITY)
                {
                    Position position = new Position(row, col);
                    Plant plant = new Plant(true, field, position);
                    organisms.add(plant);
                    
                }
                
                if(rand.nextDouble() <= FOX_CREATION_PROBABILITY) {
                    Position location = new Position(row, col);
                    Fox fox = new Fox(true, field, location);
                    organisms.add(fox);
                   
                }
                else if(rand.nextDouble() <= RABBIT_CREATION_PROBABILITY) {
                    Position location = new Position(row, col);
                    Rabbit rabbit = new Rabbit(true, field, location);
                    organisms.add(rabbit);
                    
                }
                else if(rand.nextDouble() <= SNAKE_CREATION_PROBABILITY) {
                    Position location = new Position(row, col);
                    Eagle eagle = new Eagle(true, field, location);
                    organisms.add(eagle);
                    
                }
                else if(rand.nextDouble() <= RAT_CREATION_PROBABILITY) {
                    Position location = new Position(row, col);
                    Squirrel squirrel = new Squirrel(true, field, location);
                    organisms.add(squirrel);
                    
                }
                else if(rand.nextDouble() <= OWL_CREATION_PROBABILITY) {
                    Position location = new Position(row, col);
                    Bear bear = new Bear(true, field, location);
                    organisms.add(bear);
                }
                
                // else leave the location empty.
            }
        }
    }
    
    /**
     * Pause the simulation for a given period of time in milliseconds.
     */
    private void delay(int millisec)
    {
        try {
            Thread.sleep(millisec);
        }
        catch (InterruptedException ie) {
            // wake up
        }
    }
}
