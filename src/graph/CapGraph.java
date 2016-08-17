/**
 * 
 */
package graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import util.GraphLoader;

/**
 * @author Your name here.
 * 
 * For the warm up assignment, you must implement your Graph in a class
 * named CapGraph.  Here is the stub file.
 *
 */
public class CapGraph implements Graph {
	private int numVertices;
	private int numEdges;
	private Map<Integer, ArrayList<Integer>> adjListsMap;
	private int dist[][];
	private int next[][];
	private Map<Integer, String> choiceMap;

	public CapGraph() {
		numVertices = 0;
		numEdges = 0;
		adjListsMap = new HashMap<Integer, ArrayList<Integer>>();
		choiceMap = new HashMap<Integer, String>();
	}
	
	/* (non-Javadoc)
	 * @see graph.Graph#addVertex(int)
	 */
	@Override
	public void addVertex(int num) {
		// TODO Auto-generated method stub
		ArrayList<Integer> neighbors = new ArrayList<Integer>();
		adjListsMap.put(num,  neighbors);
		choiceMap.put(num, "XBOX");
		numVertices++;
	}

	/* (non-Javadoc)
	 * @see graph.Graph#addEdge(int, int)
	 */
	@Override
	public void addEdge(int from, int to) {
		// TODO Auto-generated method stub
		(adjListsMap.get(from)).add(to);
		numEdges++;
	}

	/* (non-Javadoc)
	 * @see graph.Graph#getEgonet(int)
	 */
	@Override
	public Graph getEgonet(int center) {
		// TODO Auto-generated method stub
		Graph returnGraph = new CapGraph();
		returnGraph.addVertex(center);
		List<Integer> neighbors = adjListsMap.get(center);
		for(int n1 : neighbors) {
			returnGraph.addVertex(n1);
			returnGraph.addEdge(center, n1);
			List<Integer> newNeighbors = adjListsMap.get(n1);
			for(int n2 : newNeighbors) {
				if(neighbors.contains(n2) || n2 == center) {
					returnGraph.addEdge(n1, n2);
				}
			}
		}
		return returnGraph;
	}

	/* (non-Javadoc)
	 * @see graph.Graph#getSCCs()
	 */
	@Override
	public List<Graph> getSCCs() {
		// TODO Auto-generated method stub
		List<Graph> returnList = new ArrayList<Graph>();
		CapGraph transpose = new CapGraph();
		Set<Integer> verticesSet = adjListsMap.keySet();
		List<Integer> verticesList = new ArrayList<Integer>(verticesSet);
		Stack<Integer> vertices = new Stack<Integer>();
		Collections.sort(verticesList);
		Collections.reverse(verticesList);
		for(int i : verticesList)
			vertices.push(i);
		Stack<Integer> finished = dfs(vertices);
		for(int s : verticesSet) {	
			transpose.addVertex(s);
		}
		for(int s : verticesSet) {
			List<Integer> neighbors = adjListsMap.get(s);
			if(!neighbors.isEmpty())
			{
				for(int n : neighbors) {
					transpose.addEdge(n, s);
				}
			}
		}
		Stack<Integer> finishedTwo = dfs(transpose, finished, returnList);
		return returnList;
	}
	
	public Stack<Integer> dfs(Stack<Integer> vertices) {
		Set<Integer> visited = new HashSet<Integer>();
		Stack<Integer> finished = new Stack<Integer>();
		while(!vertices.isEmpty()) {
			int v = vertices.pop();
			if(!visited.contains(v)) {
				dfsVisit(v, visited, finished);
			}
		}
		return finished;
	}
	
	public void dfsVisit(int v, Set<Integer> visited, Stack<Integer> finished) {
		visited.add(v);
		for(int n : adjListsMap.get(v)) {
			if(!visited.contains(n)) {
				dfsVisit(n, visited, finished);
			}
		}
		finished.push(v);
	}
	
	public Stack<Integer> dfs(CapGraph transpose, Stack<Integer> vertices, List<Graph> returnList) {
		Set<Integer> visited = new HashSet<Integer>();
		Stack<Integer> finished = new Stack<Integer>();
		while(!vertices.isEmpty()) {
			int v = vertices.pop();
			if(!visited.contains(v)) {
				CapGraph subgraph = new CapGraph();
				dfsVisit(transpose, v, visited, finished, subgraph);
				for(int i : subgraph.adjListsMap.keySet()){
					List<Integer> neighbors = adjListsMap.get(i);
					for(int n : neighbors) {
						if(subgraph.adjListsMap.keySet().contains(n))
							subgraph.addEdge(i, n);
					}
				}
				returnList.add(subgraph);
			}
		}
		return finished;
	}
	
	public void dfsVisit(CapGraph transpose, int v, Set<Integer> visited, Stack<Integer> finished, CapGraph subgraph) {
		visited.add(v);
		for(int n : transpose.adjListsMap.get(v)) {
			if(!visited.contains(n)) {
				dfsVisit(transpose, n, visited, finished, subgraph);
			}
		}
		subgraph.addVertex(v);
		finished.push(v);
	}
	
	/**
	 * Implements the Distance Matrix and the Next Matrix(Used for Path Reconstruction) 
	 * for the concerned Graph.
	 */
	public void makeDist() {
		dist = new int[numVertices][numVertices];
		next = new int[numVertices][numVertices];
		int i, j, k;
		Set<Integer> verticesSet = adjListsMap.keySet();
		List<Integer> vertices = new ArrayList<Integer>(verticesSet);
		Collections.sort(vertices);
		for(i = 0; i < numVertices; i++){
			for(j = 0; j < numVertices; j++){
				dist[i][j] = 999;
				next[i][j] = 0;
			}
		}
		for(int item : vertices) {
			List<Integer> neighbors = adjListsMap.get(item);
			i = vertices.indexOf(item);
			for(int neighbor : neighbors) {
				j = vertices.indexOf(neighbor);
				dist[i][j] = 1;
				next[i][j] = neighbor;
			}
			dist[i][i] = 0;
		}
		for (k = 0; k < numVertices; k++) {
			for (i = 0; i < numVertices; i++) {
				for (j = 0; j < numVertices; j++) {
					if (dist[i][k] + dist[k][j] < dist[i][j]){
						dist[i][j] = dist[i][k] + dist[k][j];
						next[i][j] = next[i][k];
					}
	            }
	        }
	    }
	}
	
	/**
	 * Implements the Floyd-Warshall algorithm to find the all pairs shortest paths between the Nodes.
	 * @param start The start node of the path.
	 * @param goal The end node of the path.
	 * @return Returns a list of integers which contains all the nodes which lie along the shortest path.
	 */
	public List<Integer> floydWarshall(int start, int goal) {
		Set<Integer> verticesSet = adjListsMap.keySet();
		List<Integer> vertices = new ArrayList<Integer>(verticesSet);
		Collections.sort(vertices);
		if(vertices.indexOf(start) == -1 || vertices.indexOf(goal) == -1)
			return null;
		return pathFloydWarshall(vertices.indexOf(start), vertices.indexOf(goal), next, vertices);
	}
	
	/**
	 * Used to reconstruct the path from the start node to the end node.
	 * @param start The start node of the path.
	 * @param goal The end node of the path.
	 * @param next The matrix which contains all the nodes which each node traverses next along the path.
	 * @param vertices The list of all the vertices in the graph in a sorted order.
	 * @return Returns a list of integers which contains all the nodes which lie along the shortest path.
	 */
	public List<Integer> pathFloydWarshall(int start, int goal, int next[][], List<Integer> vertices) {
		if(next[start][goal] == 0) {
			return null;
		}
		List<Integer> path = new ArrayList<Integer>();
		path.add(vertices.get(start));
		while(vertices.get(start) != vertices.get(goal)) {
			start = vertices.indexOf(next[start][goal]);
			path.add(vertices.get(start));
		}
		return path;
	}

	/**
	 * Returns the List of Nodes in the Graph which will switch after the specified number of generations.
	 * @param iswitchList The initial list of Nodes which have switched their choice.
	 * @param a The reward associated with the new choice.
	 * @param b The reward associated with the original choice.
	 * @param generations The number of generations that have to be simulated.
	 * @return Returns a list of nodes which will switch after the specified number of generations.
	 */
	public List<Integer> informationFlow (List<Integer> iswitchList, int a, int b, int generations) {
		List<Integer> fswitchList = new ArrayList<Integer>();
		for(int item : iswitchList) {
			choiceMap.put(item, "PS4");
		}
		for(int i = 1; i <= generations; i++) {
			double p = 0;
			Set<Integer> verticesSet = adjListsMap.keySet();
			List<Integer> vertices = new ArrayList<Integer>(verticesSet);
			Collections.sort(vertices);
			for(int item : vertices) {
				if(iswitchList.contains(item))
					continue;
				else {
					if(!fswitchList.contains(item)) {
						List<Integer> neighbors = adjListsMap.get(item);
						int count = 0;
						for(int neighbor : neighbors) {
							if((choiceMap.get(neighbor)).equals("PS4")) {
								count++;
							}
						}
						p = (((double) count) / neighbors.size());
						double d = ((double) b / (a + b));
						if(p > d) {
							fswitchList.add(item);
						}
					}
				}
			}
			for(int item : fswitchList)
				choiceMap.put(item, "PS4");
		}
		return fswitchList;
	}
	
	/* (non-Javadoc)
	 * @see graph.Graph#exportGraph()
	 */
	@Override
	public HashMap<Integer, HashSet<Integer>> exportGraph() {
		// TODO Auto-generated method stub
		HashMap<Integer, HashSet<Integer>> returnMap = new HashMap<Integer, HashSet<Integer>>();
		for(int i : adjListsMap.keySet()){
			HashSet<Integer> neighbors = new HashSet<Integer>(adjListsMap.get(i));
			returnMap.put(i, neighbors);
		}
		return returnMap;
	}
	
	public String toString() {
		String s = "\nGraph with " + numVertices + " vertices and " + numEdges + " edges.\n";
		s += adjacencyString();
		return s;
	}
	
	public String adjacencyString() {
		String s = "Adjacency list";
		s += " (size " + numVertices + "+" + numEdges + " integers):";

		for (int v : adjListsMap.keySet()) {
			s += "\n\t"+v+": ";
			for (int w : adjListsMap.get(v)) {
				s += w+", ";
			}
		}
		return s;
	}
	
	public static void main (String[] args) {
		CapGraph graphFromFile = new CapGraph();
		GraphLoader.loadGraph(graphFromFile, "data/facebook_1000.txt");
		//System.out.println(graphFromFile.getEgonet(18));
		//List<Graph> SCC = graphFromFile.getSCCs();
		//System.out.println(SCC);
		System.out.println(graphFromFile);
		System.out.println("\n");
		
		graphFromFile.makeDist();
		//floydWarshall(start node, destionation node);
		List<Integer> path = graphFromFile.floydWarshall(65, 32);
		if(path == null)
			System.out.println("No Path Exists! or Wrong Node Entered!");
		else {
			System.out.println("<== THE SHORTEST PATH B/W TWO NODES USING FLOYD-WARSHALL IS ==>");
			for(int item : path)
				System.out.print(item + " -> ");
				System.out.println("\n");
		}
		
		List<Integer> iswitchList = new ArrayList<Integer>();
		iswitchList.add(25);
		iswitchList.add(18);
		//informationFlow(List of Nodes which have switched, reward a, reward b, number of generations);
		List<Integer> switchers = graphFromFile.informationFlow(iswitchList, 10, 1, 1);
		System.out.println("<== THE NUMBER OF NODES WHICH SWITCHED AFTER THE SPECIFIED GENERATIONS ARE ==>");
		System.out.println(switchers);
	}
}
