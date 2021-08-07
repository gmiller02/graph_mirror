package graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import support.graph.CS16Edge;
import support.graph.CS16Vertex;
import support.graph.Graph;
import support.graph.PageRank;

/**
 * In this class you will implement one of many different versions
 * of the PageRank algorithm. This algorithm will only work on
 * directed graphs. Keep in mind that there are many different ways
 * to handle sinks.
 *
 * Make sure you review the help slides and handout for details on
 * the PageRank algorithm.
 *
 */
public class MyPageRank<V> implements PageRank<V> {
	private Graph<V> _g;
	private List<CS16Vertex<V>> _vertices;
	private Map<CS16Vertex<V>, Double> _vertsToRanks;
	private static final double _dampingFactor = 0.85;
	private static final int _maxIterations = 100;
	private static final double _error = 0.01;

	/**
	 * TODO: Feel free to add in anything else necessary to store the information
	 * needed to calculate the rank. Maybe make something to keep track of sinks,
	 * your ranks, and your outgoing edges?
	 */

	/**
	 * The main method that does the calculations! You'll want to call the methods
	 * that initialize your variables here. You'll also want to decide on a
	 * type of loop - for loop, do while, or while loop - for your calculations.
	 *
	 * @return A Map of every Vertex to its corresponding rank
	 *
	 */
	@Override
	public Map<CS16Vertex<V>, Double> calcPageRank(Graph<V> g) {
		_g = g;
		_vertsToRanks = new HashMap<>();
		_vertices = new ArrayList<>();

		MyDecorator<CS16Vertex<V>, Double> rank = new MyDecorator<>(); // decorator that decorates all verticies with their rank
		MyDecorator<CS16Vertex<V>,Double> prev = new MyDecorator<>(); // keeps track of previous rankings

		Iterator<CS16Vertex<V>> vIterator = g.vertices();
		this.handleSinks(); // call handleSinks

		while (vIterator.hasNext()) { // for all verticies in the graph
			CS16Vertex<V> v = vIterator.next();
			_vertices.add(v); // add all verticies to graph
			rank.setDecoration(v, (1.0 / g.getNumVertices())); // decorate these verticies
		}

		for (int i = 0; i <= _maxIterations; i++) { // loop through maxIterations
			Iterator<CS16Vertex<V>> vIterator2 = g.vertices();

			while (vIterator2.hasNext()) { // loop through verticies again
				CS16Vertex<V> v2 = vIterator2.next();
				prev.setDecoration(v2, rank.getDecoration(v2)); // decorate previous verticies
			}

			Iterator<CS16Vertex<V>> vIterator3 = g.vertices();
			while(vIterator3.hasNext()) {
				CS16Vertex<V> v3 = vIterator3.next();
				rank.setDecoration(v3, 0.0);
				Iterator<CS16Edge<V>> incoming = g.incomingEdges(v3);
				double rankNum = 0;
				double finalRank = 0;

				while (incoming.hasNext()) { // loop through all incoming edges
					CS16Edge<V> edge = incoming.next();
					CS16Vertex<V> vertex = g.opposite(v3, edge); // get opposite vertex

					double rankD = rank.getDecoration(v3); // get the value of the vertex
					double prevD = prev.getDecoration(vertex); // get the value of the opposite vertex

					double damp = (1 - _dampingFactor) / _g.getNumVertices();
					double help = rankD + (prevD / g.numOutgoingEdges(vertex));

					rankNum += _dampingFactor * help;
					finalRank = damp + rankNum;
				}
				rank.setDecoration(v3, finalRank);
				i++;
			}
		}

	Iterator<CS16Vertex<V>> vIterator4 = g.vertices();
	while(vIterator4.hasNext()){ // for all verticies in the graph
		CS16Vertex<V> v3 = vIterator4.next(); //create new vertex
		_vertsToRanks.put(v3, rank.getDecoration(v3)); //store pagerank at that vertex
	}
		return _vertsToRanks;
	}

	/**
	 * Method used to account for sink pages (those with no outgoing
	 * edges). There are multiple ways you can implement this, check
	 * the lecture and help slides!
	 *
	 * This method loops through the vertices arraylist and inserts an edge when the vertex in question has no outgoing
	 * edges.
	 */
	private void handleSinks() {
		for (int i = 0; i < _vertices.size(); i++) { // for every vertex in the graph
			CS16Vertex<V> vertex = _vertices.get(i);
			if (_g.numOutgoingEdges(vertex) == 0){ // if a vertex has no outgoing edges
				for (int j = 0; j < _vertices.size(); j++){
					CS16Vertex<V> vertex2 = _vertices.get(j);
					_g.insertEdge(vertex, vertex2, null); //insert an edge between verticies
				}
			}
		}

	}

}
