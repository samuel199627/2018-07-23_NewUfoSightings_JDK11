package it.polito.tdp.newufosightings.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.newufosightings.db.NewUfoSightingsDAO;

public class Model {

	NewUfoSightingsDAO dao;
	SimpleWeightedGraph<State,DefaultWeightedEdge> grafo=null;
	Map<String,State> stati;
	List<Sighting> avvistamenti;
	
	public Model() {
		dao= new NewUfoSightingsDAO();
	}
	
	
	public List<String> loadShape(int year){
		return dao.loadShape(year);
	}
	
	public String creaGrafo(String forma, int anno) {
		String ritornare="";
		
		grafo= new SimpleWeightedGraph<State,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		stati=new HashMap<String,State>();
		stati=dao.loadAllStates();
		
		Graphs.addAllVertices(grafo, stati.values());
		
		//aggiungo gli archi
		List<Adiacenza> adiacenze=new ArrayList<>();
		adiacenze=dao.loadAdiancenze(forma, stati, anno);
		avvistamenti=new ArrayList<>();
		avvistamenti=dao.loadAllSightingsShapeYear(forma, stati, anno);
		
		for(Adiacenza a: adiacenze) {
			Graphs.addEdge(grafo, a.getS1(), a.getS2(), a.getPeso());
		}
		
		
		ritornare="GRAFO CREATO CON "+grafo.vertexSet().size()+" vertici e "+grafo.edgeSet().size()+" archi. \n\n";
		
		//somma pesi archi adiacenti per ogni stato
		for(State s: stati.values()) {
			int pesoStato=0;
			for(DefaultWeightedEdge e: grafo.incomingEdgesOf(s)) {
				pesoStato=pesoStato+(int) grafo.getEdgeWeight(e);
				
			}
			
			ritornare=ritornare+" "+s.toString()+" somma pesi archi adiacenti "+pesoStato+"\n";
		}
		
		
		return ritornare;
	}
	
	public String simula(int giorni, int alpha) {
		String ritornare="";
		
		Simulatore sim=new Simulatore(this);
		sim.init();
		sim.setAlpha(alpha);
		sim.setGiorni(giorni);
		sim.run();
		
		
		ritornare="SIMULAZIONE: \n\n";
		
		for(State s: grafo.vertexSet()) {
			ritornare=ritornare+s.toString()+" DEFCON "+s.getDefcon()+"\n";
		}

		return ritornare;
	}
	
	
	
}
