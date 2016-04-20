/*
 * Created 2012
 * 
 * This file is part of Topological Data Analysis
 * edu.duke.math.tda
 * TDA is licensed from Duke University.
 * Copyright (c) 2012-2014 by John Harer
 * All rights reserved.
 * 
 */
package edu.duke.math.tda.utility;

import java.util.Random;

/**
 * Basic utility class for providing random sequences, based on either a fixed seed
 * or a system time-based seed. 
 * 
 * <p><strong>Details:</strong> <br>
 * By using a regular (instead of a static) class we can use separate random sequences 
 * for multiple threads, and still get repeatable results for testing.
 *  
 * <p><strong>Change History:</strong> <br>
 * Created 2012
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */
public class TdaRandomNumber {

    // random seed used for generating the random sequence
    // note: we default to a random start point, which will be used unless it is
    // explicitly overridden by a call to setRandomSeed
    protected long randomSeed = System.currentTimeMillis();
    
    // sequence of random numbers
    protected Random randomSequence;
    
    // We use this constructor in our client code, before we know what seed the
    // user may have specified
    public TdaRandomNumber() {
        
        // Use the default seed
        setRandomSeed( randomSeed );
    }
    
    // This is supplied for convenience
    public TdaRandomNumber( long _seed ) {

        // Use the supplied seed
        setRandomSeed( _seed );
    }
    
    // This shields the "customer" class from making any decision about what random sequence to use
    // so we can use a basic switch between regular and debug/test mode
    public Random getRandomSequence() {

        return randomSequence;
    }
    
    public long getRandomSeed() {
        
        return randomSeed;
    }
    
    public void setRandomSeed( long _randomSeed ) {
        
        randomSeed = _randomSeed;
        
        // Reset the random sequence based on the supplied seed
        randomSequence = new Random( randomSeed );
    }
}
