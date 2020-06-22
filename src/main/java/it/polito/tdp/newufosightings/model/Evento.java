package it.polito.tdp.newufosightings.model;

import java.time.LocalDateTime;

public class Evento implements Comparable<Evento> {
	
	public enum Tipo{
		AVVISTAMENTO,
		CESSATA_ALLERTA,
		CESSATA_ALLERTA_VICINO
	}

	private LocalDateTime time;
	private Tipo tipo;
	private State s;
	
	public Evento(LocalDateTime time, Tipo tipo, State s) {
		super();
		this.time = time;
		this.tipo = tipo;
		this.s = s;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

	public Tipo getTipo() {
		return tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	public State getS() {
		return s;
	}

	public void setS(State s) {
		this.s = s;
	}

	@Override
	public int compareTo(Evento o) {
		// TODO Auto-generated method stub
		return this.getTime().compareTo(o.getTime());
	}
	
	
	
	
}
