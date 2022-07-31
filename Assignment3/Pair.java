
/**
 * A generic class that takes in two generic types
 * Instead of using hashmap which stores a list of paired objects
 * This class stores a single pair.
 * @2018-02
 */
public class Pair<K,V>
{
    K k;
    V v;
    /**
     * Constructor of Pair class, initialises generic type values
       */
    public Pair(K key, V value)
    {
        k = key;
        v = value;
    }
    
    /**
     * Returns the key of the pair
       */
    public K getKey()
    {
        return k;
    }
    
    /**
     * Returns the value of the pair
       */
    public V getValue()
    {
        return v;
    }
    
    /**
     * Change the key and value of the pair
       */
    public void set(K key, V value)
    {
        k = key;
        v = value;
    }
}
