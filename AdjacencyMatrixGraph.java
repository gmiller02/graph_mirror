package graph;

import static support.graph.Constants.MAX_VERTICES;

import java.util.*;

import net.datastructures.NodeDeque;
import support.graph.CS16Edge;
import support.graph.CS16Vertex;
import support.graph.Graph;
import support.graph.GraphEdge;
import support.graph.GraphVertex;
import support.graph.DirectionException;
import support.graph.InvalidEdgeException;
import support.graph.InvalidVertexException;
import support.graph.NoSuchEdgeException;
import support.graph.NoSuchVertexException;
import support.graph.NodeSequence;
    
/**
 * This class defines a Graph that tracks its edges through the use of an
 * adjacency matrix. Please review the lecture slides and the book before
 * attempting to write this program. An adjacency matrix consists of a 2D array
 * of Vertices, with each vertex of the graph appearing in both dimensions.
 *
 * Since we are using an adjacency matrix, each vertex must have a 'number', so
 * that it can represent an index in one of the dimensional arrays. This
 * assignment is not as trivial as it may appear. Remember that your arrays have
 * a maximum index. Thus, you cannot just up the number for each vertex. Why
 * not? Think about what happens when you constantly add and delete new
 * vertices. You will soon exceed the size of your adjacency matrix array. Note
 * further that this number must be unique.
 * 
 * Make sure your AdjacencyMatrixGraph can be both directed and undirected!
 *
 * Good luck, and as always, start early, start today, start yesterday!
 */
public class AdjacencyMatrixGraph<V> implements Graph<V> {

    // The underlying data structure of your graph: the adjacency matrix
    private CS16Edge<V>[][] _adjMatrix;
    // Sets to store the vertices and edges of your graph
    private Set<CS16Vertex<V>> _vertices;
    private Set<CS16Edge<V>> _edges;
    //number of vertices
    private int _numVertices;
    // boolean that keeps track of directedness of graph
    private boolean _directed;
    private NodeDeque<Integer> _deque;

    /**
     * Constructor for your Graph, where among other things, you will most
     * likely want to instantiate your matrix array and your Sets.
     *
     * Takes in a boolean that represents whether the graph will be directed.
     *
     * This must run in O(1) time.
     */
    public AdjacencyMatrixGraph(boolean directed) {
        _adjMatrix = this.makeEmptyEdgeArray();
        _directed = directed;
        _vertices = new HashSet<>();
        _edges = new HashSet<>();
        _deque = new NodeDeque<>();
        _numVertices = 0;

    }

    /**
     * Returns an iterator holding all the Vertices of the graph.
     *
     * <p>
     * This must run in O(1) time.
     * </p>
     * * Note that the visualizer uses this method to display the graph's
     * vertices, so you should implement it first.
     *
     * @return an Iterator containing the vertices of the Graph.
     */
    @Override
    public Iterator<CS16Vertex<V>> vertices() {
        return _vertices.iterator();
    }

    /**
     * Returns an iterator holding all the edges of the graph.
     *
     * <p>
     * This must run in O(|1|) time.
     * </p>
     *
     * Note that the visualizer uses this method to display the graph's edges,
     * so you should implement it first.
     *
     * @return an Iterator containing the edges of the Graph.
     */
    @Override
    public Iterator<CS16Edge<V>> edges() {
        return _edges.iterator();
    }

    /**
     * Inserts a new Vertex into your Graph. You will want to first generate a
     * unique number for your vertex that falls within the range of your
     * adjacency array. You will then have to add the Vertex to your set of
     * vertices.
     *
     * <p>
     * You will not have to worry about the case where *more* than MAX_VERTICES
     * vertices are in your graph. Your code should, however, be able to hold
     * MAX_VERTICES vertices at any time.
     * </p>
     *
     * <p>
     * This must run in O(1) time.
     * </p>
     * 
     * @param vertElement
     *            the element to be added to the graph as a vertex
     */
    @Override
    public CS16Vertex<V> insertVertex(V vertElement) {
        GraphVertex<V> vertex = new GraphVertex<>(vertElement);
        if (this.getNumVertices() <= MAX_VERTICES){ // if the verticies in the graph are less than or equal to the max number
            _vertices.add(vertex); // add a vertex!
            _numVertices++; // increment the number
            if (_deque.isEmpty()){ // if the deque I created is empty, set the vertex num to the incremented number
                vertex.setVertexNumber(_numVertices);
            }
            else{ // if there's something in the deque
                vertex.setVertexNumber(_deque.removeFirst()); // remove a number from the deque and set it to the new vertex
            }
        }
        return vertex;
    }

    /**
     * Inserts a new Edge into your Graph. You need to update your adjacency
     * matrix to reflect this new added Edge. In addition, the Edge needs to be
     * added to the edge set. 
     *
     * If the graph is directed, you will only want an edge
     * starting from the first vertex ending at the second vertex. If the graph is
     * undirected, you will want an edge both ways.
     * 
     * <p>
     * This must run in O(1) time.
     * </p>
     * 
     * @param v1
     *            The first vertex of the edge connection.
     * @param v2
     *            The second vertex of the edge connection.
     * @param edgeElement
     *            The element of the newly inserted edge.
     * @return Returns the newly inserted Edge.
     * @throws InvalidVertexException
     *             Thrown when either Vertex is null.
     */
    @Override
    public CS16Edge<V> insertEdge(CS16Vertex<V> v1, CS16Vertex<V> v2, Integer edgeElement)
            throws InvalidVertexException {
        if (v1 == null || v2 == null) {
            throw new InvalidVertexException("vertex is null");
        }
        else { // when both verticies are valid
            GraphEdge<V> e = new GraphEdge<V>(edgeElement, v1, v2); // create edge between verticies
            _edges.add(e); // add
            _adjMatrix[v1.getVertexNumber()][v2.getVertexNumber()] = e;
            if(_directed = false) { // if graph is undirected
                e.setVertexOne(v2);
                e.setVertexTwo(v1);
            }
            else{ // if graph is directed
                int v2Num = v2.getVertexNumber();
                int v1Num = v1.getVertexNumber();
                _adjMatrix[v2Num][v1Num] = e; // make sure edge is directed by adding to matrix
            }
            return e;
        }
    }

    /**
     * Removes a Vertex from your graph. You will first have to remove all edges
     * that are connected to this Vertex. (Perhaps you can use other methods you
     * will eventually write to make this easier?) Finally, remove the Vertex
     * from the vertex set.
     * <p>
     * This must run in O(|V|) time.
     * </p>
     *
     * @param vert
     *            The Vertex to remove.
     * @return The element of the removed Vertex.
     * @throws InvalidVertexException
     *             Thrown when the Vertex is null.
     */
    @Override
    public V removeVertex(CS16Vertex<V> vert) throws InvalidVertexException {
        if (vert == null) {
            throw new InvalidVertexException("vertex is null");
        }
        else{
            _vertices.remove(vert);
            _deque.addFirst(vert.getVertexNumber()); // add removed number to deque
            _numVertices--; // decrease vertex number
            Iterator<CS16Edge<V>> incoming = this.incomingEdges(vert); // create iterator for incoming edges
            while (incoming.hasNext()){
                this.removeEdge(incoming.next());
            }
            Iterator<CS16Edge<V>> outgoing = this.outgoingEdges(vert);// create iterator for outgoing edges
            while(outgoing.hasNext()){
                this.removeEdge(outgoing.next());
            }
            return vert.element();
        }
    }

    /**
     * Removes an Edge from your Graph. You will want to remove all references
     * to it from your adjacency matrix. Don't forget to remove it from the edge
     * set. Make sure to remove only the correct edge if the graph is directed.
     *
     * <p>
     * This must run in O(1) time.
     * </p>
     *
     * @param edge
     *            The Edge to remove.
     * @return The element of the removed Edge.
     * @throws InvalidEdgeException
     *             Thrown when the Edge is null.
     */
    @Override
    public Integer removeEdge(CS16Edge<V> edge) throws InvalidEdgeException {
        if (edge == null) {
            throw new InvalidEdgeException("edge is null");
        }
        else { // when graph is directed
            _edges.remove(edge);
            int edgev1 = edge.getVertexOne().getVertexNumber();
            int edgev2 = edge.getVertexTwo().getVertexNumber();
            _adjMatrix[edgev1][edgev2] = null; //set directed edge to null
            if(_directed = false) { // if graph is undirected
                _adjMatrix[edgev2][edgev1] = null; // set undirected edge to null
            }
        }
        return edge.element();
    }

    /**
     * Returns the edge that connects the two vertices. You will want to consult
     * your adjacency matrix to see if they are connected. If so, return that
     * edge, otherwise throw a NoSuchEdgeException.
     * 
     * If the graph is directed, then two nodes are connected if there is an
     * edge from the first vertex to the second. 
     * If the graph is undirectd, then two nodes are connected if there is an
     * edge from the first vertex to the second and vice versa. 
     *
     * <p>
     * This must run in O(1) time.
     * </p>
     *
     * @param v1
     *            The first vertex that may be connected.
     * @param v2
     *            The second vertex that may be connected.
     * @return The edge that connects the first and second vertices.
     * @throws InvalidVertexException
     *             Thrown when either vertex is null.
     * @throws NoSuchEdgeException
     *             Thrown when no edge connects the vertices.
     */
    @Override
    public CS16Edge<V> connectingEdge(CS16Vertex<V> v1, CS16Vertex<V> v2)
            throws InvalidVertexException, NoSuchEdgeException {

        if (v1 == null || v2 == null) {
            throw new InvalidVertexException("vertex is null");
        }
        else {
            CS16Edge<V> connect;
            int num1 = v1.getVertexNumber();
            int num2 = v2.getVertexNumber();
            if(this.areAdjacent(v1, v2)){ // if the two edges are connected
                connect = _adjMatrix[num1][num2]; // set connect to the edge in between
            }
            else{
                throw new NoSuchEdgeException("no edge connecting verticies");
            }
            return connect;
        }
    }

    /**
     * Returns an Iterator over all the Edges that are incoming to this Vertex.
     * <p>
     * This must run in O(|V|) time;
     * </p>
     * 
     *
     * @param vert
     *            The vertex to find the incoming edges on.
     * @return Returns an Iterator holding the incoming edges on v.
     * @throws InvalidVertexException
     *             Thrown when the Vertex is null.
     */
    @Override
    public Iterator<CS16Edge<V>> incomingEdges(CS16Vertex<V> vert) throws InvalidVertexException {
        if (vert == null) {
            throw new InvalidVertexException("vertex is null");
        }
        else{
            NodeSequence<CS16Edge<V>> inList = new NodeSequence<>(); //create new list of edges
            int num = vert.getVertexNumber();
            for(int i = 0; i < _adjMatrix.length; i++){ // loop through the incoming matrix edges
                if(_adjMatrix[i][num] != null){
                    inList.addFirst(_adjMatrix[i][num]);// add incoming edges to the list
                }
            }
            return inList.iterator();
        }
    }
    
    /**
     * Returns an Iterator of all the Edges that are outgoing from this vertex.
     * <p>
     * This must run in O(|V|) time;
     * </p>

     * @param vert
     *            The vertex to find the outgoing edges on.
     * @return Returns an Iterator holding the outgoing edges on v.
     * @throws InvalidVertexException
     *             Thrown when the Vertex is null.
     */
    @Override
    public Iterator<CS16Edge<V>> outgoingEdges(CS16Vertex vert) throws InvalidVertexException {
        if (vert == null) {
            throw new InvalidVertexException("vertex null");
        }
        else{
            NodeSequence<CS16Edge<V>> outList = new NodeSequence<>(); // create new list of edges
            int num = vert.getVertexNumber();
            for(int i = 0; i < _adjMatrix.length; i++){ // loop through outgoing edges
                if(_adjMatrix[num][i] != null){
                    outList.addFirst(_adjMatrix[num][i]); // add outgoing edges to list
                }
            }
            return outList.iterator();
        }
    }

    /**
     * Returns an int of the number Edges that are leaving from this Vertex. This should only
     * work if called on a directed graph. This method will be used in MyPageRank.
     * 
     * @param vert
     *            The vertex to to find the outgoing edges on.
     * @return an int
     * @throws InvalidVertexException
     *             Thrown when the Vertex is not valid.
     * @throws DirectionException
     *             Thrown when this method is called on an undirected graph.
     */
    @Override
    public int numOutgoingEdges(CS16Vertex<V> vert) throws InvalidVertexException, DirectionException {
        if(vert == null){
            throw new InvalidVertexException("vertex is not valid");
        }
        if(_directed = false){ // only works on directed
            throw new DirectionException("graph is undirected");
        }
        int size = 0;
        Iterator<CS16Edge<V>> edges = this.outgoingEdges(vert); // create iterator that loops through outgoing edges
        while(edges.hasNext()){
            edges.next();
            size++; // increment size counter by the number of edges
        }
        return size;
    }

    /**
     * Returns the Vertex that is on the other side of Edge e opposite of Vertex
     * v. Consulting the adjacency matrix may result in a running time that is
     * too high.
     * 
     * If the edge is not incident on v, then throw a NoSuchVertexException.
     *
     * <p>
     * This must run in O(1) time.
     * </p>
     *
     * @param vert
     *            The first vertex on Edge e.
     * @param edge
     *            The edge connecting Vertex v and the unknown opposite Vertex.
     * @return The opposite Vertex of v across Edge e.
     * @throws InvalidVertexException
     *             Thrown when the Vertex is not valid.
     * @throws InvalidEdgeException
     *             Thrown when the Edge is not valid.
     * @throws NoSuchVertexException
     *             Thrown when Edge e is not incident on v.
     */
    @Override
    public CS16Vertex<V> opposite(CS16Vertex<V> vert, CS16Edge<V> edge)
            throws InvalidVertexException, InvalidEdgeException, NoSuchVertexException {
        if (edge.getVertexOne() == vert){ // if the vertex inputted is one, return the other
            return edge.getVertexTwo();
        }
        else if (edge.getVertexTwo() == vert){ // vice versa
            return edge.getVertexOne();
        }
        else if (edge == null) {
            throw new InvalidEdgeException("edge is not valid");
        }
        else if (vert == null){
            throw new InvalidVertexException("vertex is not valid");
        }
        else{
            throw new NoSuchVertexException("Edge is not incident on v");
        }


    }

    /**
     * Returns the two Vertices that the Edge e is connected to.
     * 
     * Checking the adjacency matrix may be too costly for this method.
     *
     * <p>
     * This must run in O(1) time.
     * </p>
     * 
     * Note that the visualizer uses this method to display the graph's edges.
     *
     * @param e
     *            The edge to find the connecting Vertex's on.
     * @return a list of Vertex's holding the two connecting vertices.
     * @throws InvalidEdgeException
     *             Thrown when the Edge e is null.
     */
    @Override
    public List<CS16Vertex<V>> endVertices(CS16Edge<V> e) throws InvalidEdgeException {
        if (e == null) {
            throw new InvalidEdgeException("edge is null");
        }
        else{
            ArrayList<CS16Vertex<V>> list = new ArrayList<>(); // create arraylist of verticies
            list.add(e.getVertexOne()); // return verticies of inputted edge
            list.add(e.getVertexTwo());

            return list;
        }

    }

    /**
     * Returns true if there exists an Edge that starts from Vertex v1 and ends
     * at Vertex v2 for both a directed and undirected graph. For a directed graph
     * two vertices are adjacent if there is an edge from the first vertex to the 
     * second vertex.
     * 
     * <p>
     * This must run in O(1) time.
     * </p>
     * 
     * @param v1
     *            The first Vertex to test adjacency.
     * @param v2
     *            The second Vertex to test adjacency.
     * @return Returns true if the vertices are adjacent.
     * @throws InvalidVertexException
     *             Thrown if either vertex is null.
     */
    @Override
    public boolean areAdjacent(CS16Vertex<V> v1, CS16Vertex<V> v2) throws InvalidVertexException {
        if (v1 == null || v2 == null) {
            throw new InvalidVertexException("vertex is null");
        }
        else{
            if (_directed = true) { // if graph is directed
                if (_adjMatrix[v1.getVertexNumber()][v2.getVertexNumber()] == null){ // if there is no edge
                    return false;
                }
                else{ // if there is an edge
                    return true;
                }
            }
            else{ // if graph is undirected, you must account for both possibilites
                if (_adjMatrix[v1.getVertexNumber()][v2.getVertexNumber()] == null && _adjMatrix[v2.getVertexNumber()][v1.getVertexNumber()] == null){
                    return false;
                }
                else {
                    return true;
                }
            }
        }
    }

    /**
     * Toggles the directedness of the graph.
     */
    @Override
    public void toggleDirected() {
        if (_directed == true){
            _directed = false;
        }
        else{
            _directed = true;
        }

    }

    /**
     * Clears all the vertices and edges from the graph. You will want to also
     * clear the adjacency matrix. Remember the power of Java's garbage
     * collection mechanism. If you re-instantiate something, the instance of
     * what that Object used to be disappears.
     *
     * <p>
     * This must run in O(1) time.
     * </p>
     */
    @Override
    public void clear() {
        _adjMatrix = this.makeEmptyEdgeArray();
        _vertices.clear();
        _edges.clear();
        _numVertices = 0;
    }

    /**
     * Returns the number of vertices in the graph.
     */
    @Override
	public int getNumVertices() {
		return _numVertices;
	}

    // Do not change this method!
    @SuppressWarnings("unchecked")
    private CS16Edge<V>[][] makeEmptyEdgeArray() {
        return new CS16Edge[MAX_VERTICES][MAX_VERTICES];
    }
}
