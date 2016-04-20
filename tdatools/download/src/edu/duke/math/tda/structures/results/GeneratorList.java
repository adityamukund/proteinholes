package edu.duke.math.tda.structures.results;

import java.util.ArrayList;

public class GeneratorList {

	protected int indexForAssociatedEdge_ = -1;
	protected ArrayList<Integer> generators_ = new ArrayList<Integer>();

	// Setting up an "empty" GeneratorList
	public GeneratorList() {}
	
	public GeneratorList( final GeneratorList _otherGeneratorList ) {

		this.indexForAssociatedEdge_ = _otherGeneratorList.indexForAssociatedEdge_;
		this.generators_ = _otherGeneratorList.generators_;
	}

	public GeneratorList( final int _indexForAssociatedEdge, 
			ArrayList<Integer> _generators ) {

		this.indexForAssociatedEdge_ = _indexForAssociatedEdge;
		this.generators_ = _generators;
	}
	
	public ArrayList<Integer> getGeneratorList() {
		
		return this.generators_;
	}
	
	public int getIndexForAssociatedEdge() {
		
		return this.indexForAssociatedEdge_;
	}
		
	public String toStringPlain() {
		
		StringBuffer strGeneratorList = new StringBuffer();
		
		// TODO: this still includes the []-brackets of the ArrayList's toString
		// method, which we may not want
		strGeneratorList.append( this.generators_.toString() );		
		
		return strGeneratorList.toString();
	}
		
	public String toStringFormatted() {
			
		// For now, we haven't decided on a special format, so:
		return this.toString();
	}
	
	public String toString() {
		
		StringBuffer strGeneratorList = new StringBuffer();
		
		// add the index of the associated edge
		strGeneratorList.append( indexForAssociatedEdge_ + ":  " );
		
		strGeneratorList.append( "( " + this.generators_.toString() + " )" );		
		
		return strGeneratorList.toString();
	}
}
