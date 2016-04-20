package edu.duke.math.tda.structures;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Face implementation class FaceZ2
 * 
 * <p><strong>Details:</strong> <br>
 * 
 * 
 * 
 * <p><strong>Change History:</strong> <br>
 * Created April 18, 2014
 * 
 * 
 * @author Jurgen Sladeczek (hjs) <br>
 * For the latest info, please visit www.math.duke.edu.
 */

public class FaceZ2 extends Face {
	
	// hjs 1/9/2015
	// Constructor with explicit listing of the vertices that describe the face
	// and the specified index of the face in the tracking list
	// Note: the FaceFactory will take care of determining and tracking
	// the faceListIndex
	public FaceZ2( final EdgeI _currentEdge, 
				final int _vertexIndex3,
				final int _faceListIndex ) {
		
		arrVertices_ = new int[ 3 ];
		int v1 = _currentEdge.getVertexIndex1();
		int v2 = _currentEdge.getVertexIndex2();

		arrVertices_[ 0 ] = v1;
		arrVertices_[ 1 ] = v2;
		arrVertices_[ 2 ] = _vertexIndex3;
		
		faceListIndex_ = _faceListIndex;
		dag2List_.put( _faceListIndex, 1 );
	}

	// keep only for legacy test code
	public FaceZ2( final int _vertexIndex1, 
				final int _vertexIndex2, 
				final int _vertexIndex3,
				final int _faceListIndex ) {
		
		arrVertices_ = new int[ 3 ];

		arrVertices_[ 0 ] = _vertexIndex1;
		arrVertices_[ 1 ] = _vertexIndex2;
		arrVertices_[ 2 ] = _vertexIndex3;
		
		faceListIndex_ = _faceListIndex;
		dag2List_.put( _faceListIndex, 1 );
	}
	
	// 
	public FaceZ2( final FaceI _orig ) {
		
		arrVertices_ = new int[ 3 ];

		arrVertices_[ 0 ] = _orig.getVertexIndex( 0 );
		arrVertices_[ 1 ] = _orig.getVertexIndex( 1 );
		arrVertices_[ 2 ] = _orig.getVertexIndex( 2 );
		
		faceListIndex_ = _orig.getFaceListIndex();
		
		// What to do with dag2List in this case?
		dag2List_.put( faceListIndex_, 1 );
	}

	@Override
	public void addToDagList( final Integer _key, final Integer _value ) {
    	
    	if ( _key != null && _key.intValue() >= 0 ) {
    	
    		if ( dag2List_.containsKey( _key )) {
    			
    			// TODO: throw exception if there's already an entry?
    		}
    		else {
    		
    			dag2List_.put( _key, _value );
    		}
    	}
    	else {
    		
    		// throw exception? likely a DEV error
    	}
	}

	@Override
	public void mergeIntoDagList( final SortedMap<Integer, Integer> _dagListToMerge ) {

    	SortedMap<Integer, Integer> itemsToAdd = new TreeMap<Integer, Integer>();
    	SortedMap<Integer, Integer> itemsToRemove = new TreeMap<Integer, Integer>();

    	Integer key;
		Integer value;
    	
    	// if daglist is empty, just add all items in the toMerge list
    	if ( dag2List_.size() == 0 ) {
    		
    		for( SortedMap.Entry<Integer,Integer> dagListItem : 
    				_dagListToMerge.entrySet()) {
    			  
    			key = dagListItem.getKey();
    			value = dagListItem.getValue();
    			dag2List_.put( key, value );
    			  
//    			System.out.println( "[mergeIntoDagList 1] Adding to dag2List: " + key + " => " + value );
    		}
    	}
    	
	    // here both lists are non-empty
	    else {    	
		    if ( _dagListToMerge != null && _dagListToMerge.size() > 0 ) {
		    	
		    	for( SortedMap.Entry<Integer,Integer> dagListItem : _dagListToMerge.entrySet()) {
	    			  
	    			key = dagListItem.getKey();
	    			value = dagListItem.getValue();
	    			
	    			if ( dag2List_.containsKey( key ) ) {
		    			
		    			// since any adding is done 'modulo 2', we need to remove the item (from both lists)
		    			itemsToRemove.put( key, value );
		    		}
		    		else {
		    			
		    			itemsToAdd.put( key, value );
		    		}
	    		}
		    }
		    else {
		    	
		    	// may or may NOT want to throw an exception?
		    }
	
		    for( SortedMap.Entry<Integer,Integer> dagListItem : itemsToRemove.entrySet()) {
  			  
    			key = dagListItem.getKey();
    			value = dagListItem.getValue();
    	
    			dag2List_.remove( key );
		    }
	
		    for( SortedMap.Entry<Integer,Integer> dagListItem : itemsToAdd.entrySet()) {
  			  
    			key = dagListItem.getKey();
    			value = dagListItem.getValue();
    	
    			dag2List_.put( key, value );
		    }
	    }
	}

	@Override
	public void mergeIntoDagList( final SortedMap<Integer, Integer> _dagListToMerge,
				final int _edgeOrientation ) {
	
		SortedMap<Integer, Integer> itemsToAdd = new TreeMap<Integer, Integer>();
		SortedMap<Integer, Integer> itemsToRemove = new TreeMap<Integer, Integer>();
	
		Integer key;
		Integer value;
		
		// if daglist is empty, just add all items in the toMerge list
		if ( dag2List_.size() == 0 ) {
			
			for( SortedMap.Entry<Integer,Integer> dagListItem : _dagListToMerge.entrySet()) {
				  
				key = dagListItem.getKey();
				value = dagListItem.getValue();
				dag2List_.put(key, value);
				  
//    			System.out.println( "[mergeIntoDagList 2] Adding to dag2List: " + key + " => " + value );
			}
		}
		
	    // here both lists are non-empty
	    else {
		    if ( _dagListToMerge != null && _dagListToMerge.size() > 0 ) {
		    	
		    	for( SortedMap.Entry<Integer,Integer> dagListItem : _dagListToMerge.entrySet()) {
	    			  
	    			key = dagListItem.getKey();
	    			value = dagListItem.getValue();
	    			
	    			if ( dag2List_.containsKey( key ) ) {
		    			
		    			// since any adding is done 'modulo 2', we need to remove the item (from both lists)
		    			itemsToRemove.put( key, value );
		    		}
		    		else {
		    			
		    			itemsToAdd.put( key, value );
		    		}
	    		}
		    }
		    else {
		    	
		    	// may or may NOT want to throw an exception?
		    }
	
		    for( SortedMap.Entry<Integer,Integer> dagListItem : itemsToRemove.entrySet()) {
				  
				key = dagListItem.getKey();
				value = dagListItem.getValue();
		
				dag2List_.remove( key );
		    }
	
		    for( SortedMap.Entry<Integer,Integer> dagListItem : itemsToAdd.entrySet()) {
				  
				key = dagListItem.getKey();
				value = dagListItem.getValue();
		
				dag2List_.put( key, value );
		    }
	    }
	}
}
