package it.polito.tdp.newufosightings.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.newufosightings.model.Evento.Tipo;

public class Simulatore {
	
	
	
	PriorityQueue<Evento> coda;
	Model m;
	
	SimpleWeightedGraph<State,DefaultWeightedEdge> grafo=null;
	List<Sighting> avvistamenti;
	
	int giorni;
	int alpha;
	
	public Simulatore(Model model) {
		m=model;
	}
	
	
	
	public void init() {
		coda=new PriorityQueue<Evento>();
		this.grafo=m.grafo;
		this.avvistamenti=m.avvistamenti;
		
		//inserisco tutti gli avvistamenti nella coda
		for(Sighting s: avvistamenti) {
			coda.add(new Evento(s.getDatetime(),Tipo.AVVISTAMENTO,s.getState()));
			//System.out.println("AGGIUNTO AVVISTAMENTO");
		}
		
		//coda caricata
	}
	
	public void run() {
		while(!coda.isEmpty()) {
			Evento e=coda.poll();
			ProcessEvent(e);
		}
		
	}
	
	public void ProcessEvent(Evento e) {
		switch(e.getTipo()) {
		case AVVISTAMENTO:
			if(e.getS().getDefcon()>1.0) {
				//possiamo decrementare il defcon
				e.getS().setDefcon(e.getS().getDefcon()-1.0);
				//System.out.println("DIMINUITO DEFCON");
				if(e.getS().getDefcon()<1.0) {
					e.getS().setDefcon(1);
				}
				//genera l'evento di cessata allerta (il sightin non serve, ma mi serve lo stato e quindi lo passo e lo estraggo da li')
				coda.add(new Evento(e.getTime().plusDays(giorni),Tipo.CESSATA_ALLERTA,e.getS()));
				//dobbiamo controllare se anche negli stati vicini abbiamo una variazione
				List<State> vicini=new ArrayList<>();
				vicini=Graphs.neighborListOf(grafo, e.getS());
				for(State s: vicini) {
					double rand=Math.random();
					if(rand<(((double) alpha)/100.0)) {
						if(s.getDefcon()>1.0) {
							//possiamo decrementare il defcon
							s.setDefcon(s.getDefcon()-0.5);
							//genera l'evento di cessata allerta (il sightin non serve, ma mi serve lo stato e quindi lo passo e lo estraggo da li')
							coda.add(new Evento(e.getTime().plusDays(giorni),Tipo.CESSATA_ALLERTA_VICINO,s));
						}
					}
				}
				
			}
			break;
		case CESSATA_ALLERTA:
			if(e.getS().getDefcon()<5.0) {
				//possiamo incrementare il defcon
				e.getS().setDefcon(e.getS().getDefcon()+1.0);
				if(e.getS().getDefcon()>5.0) {
					e.getS().setDefcon(5);
				}
			}
			break;
		case CESSATA_ALLERTA_VICINO:
			if(e.getS().getDefcon()<5.0) {
				//possiamo incrementare il defcon
				e.getS().setDefcon(e.getS().getDefcon()+0.5);
				
			}
			break;
		}
	}



	public void setGiorni(int giorni) {
		this.giorni = giorni;
	}



	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}
	
	
	

}
