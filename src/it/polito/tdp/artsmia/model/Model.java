package it.polito.tdp.artsmia.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import it.polito.tdp.artsmia.db.ArtsmiaDAO;

public class Model {

	private List <ArtObject> artObjects;
	
	private Graph <ArtObject, DefaultWeightedEdge>  grafo;
	
	/**
	 * Popola la lista ArtObjects leggendola da DB e crea grafo.
	 * 
	 */
	
	public void creaGrafo() {
		
		//leggi la lista degli oggetti da DB
		
		ArtsmiaDAO dao =  new ArtsmiaDAO();
		
		artObjects = dao.listObjects();
		
		System.out.format("Oggetti caricati: %d oggetti\n", this.artObjects.size());

			
		//crea grafo
		
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		//aggiungi vertex
		
	/*	for (ArtObject ao : artObjects) 		FA CIO' CHE FA IL METODO SUCCESSIVO
			
			grafo.addVertex(ao);*/
		
		Graphs.addAllVertices(grafo, artObjects);
		
		//aggiungi edges con rispettivo peso
		
		
/*  VERSIONE 1 POCO EFFICENTE: FA UN DOPPIO CICLO SU OGNI NODO, TEMPO ELABORAZIONE LUNGO SE GRAFO E' GRANDE
		
		
			for (ArtObject aoPartenza : artObjects) {
				
				for (ArtObject aoArrivo : artObjects) {
					
									//grafo NON ORIENTATO, EVITA DI CONSIDERARE DUE VOLTE OGNI COPIA ALTRIMENTI TI DA UNA NULL POINTER E PERDO TEMPO
					
					if( !aoPartenza.equals(aoArrivo) && aoPartenza.getId() < aoArrivo.getId() ) { //escludo i loop, ora mi chiedo se sono connessi
						
						int peso = exhibitionComuni(aoPartenza, aoArrivo);
						
			
						if(peso!=0) {
							
							Graphs.addEdge(grafo, aoPartenza, aoArrivo, peso);
													
							/*OPPURE USO DefaultWeightedEdge e = grafo.addEdge(aoPartenza, aoPartenza);
							
							grafo.setEdgeWeight(e, peso); 
							
							
								addEdge tornerebbe null se cercassi di crare multigrafo perciò dovrei inserire dei controlli
							
						}		
				}
			}
		}     */
		
		for (ArtObject ao : artObjects) {
				
		List<ArtObjectAndCount> connessi = dao.listArtObjectAndCount(ao);
		
			for (ArtObjectAndCount c : connessi) {
				
				ArtObject destinazione = new ArtObject(c.getId(), null, null, null , 0 , null, null, null, null, null, 0, null, null, null, null, null);
				
				//HO CREATO UN OGGETTO DEBOLE INIZIALIZZATO SOLO PER UN PEZZO CHE SO CHE SERVE AL PROGRAMMA
								
				Graphs.addEdge( grafo , ao , destinazione  , c.getCount() );
			}	
		}	
	}

	private int exhibitionComuni(ArtObject aoPartenza, ArtObject aoArrivo) { // serve nella prima versione

		ArtsmiaDAO dao = new ArtsmiaDAO();
		
		int numeroMostreComuni = dao.contaExhibitionComuni(aoPartenza, aoArrivo);
	
		return numeroMostreComuni;
	}

	public Object getNumeVert() {
		
		return grafo.vertexSet().size();
	}

	public Object getNumEdg() {
		
		return grafo.edgeSet().size();
	}

	public boolean idIsValid(int id) {
		
		//potrei fare un identityMap dell'oggetto per fare piu' rapidamente questa operazione di ricerca

		if(artObjects == null) //utente potrebbe cercare prima componente connessa che crare grafo. sarebbe meglio lanciare eccezione
			return false;
			
		for (ArtObject ao : artObjects) {
			
			if(ao.getId() == id)
				
				return true;
		}
		
		return false;
	}

	public int calcolaDimensioneCC(int id) {
		
		//trova vertice partenza
		
		ArtObject vertPart = null;
		
		for(ArtObject ao : artObjects) {
			
			if(ao.getId() == id)
				
				vertPart = ao;
			//break;
		}
		
			if(vertPart == null) {
				
				//eccezione che non deve verificarsi mai avendo già controllato che quell'id esiste con un metodo isValid
				throw new IllegalArgumentException("vertice"+id+"non esiste");
			}
			
		//visita grafo
		
		Set<ArtObject> visitati =  new HashSet<>() ;
		
		DepthFirstIterator<ArtObject, DefaultWeightedEdge> dfv =  new DepthFirstIterator<>(grafo,vertPart);
		
			while (dfv.hasNext()) {
				
				visitati.add(dfv.next());
			}
	
		//conta elementi
		
		return visitati.size();
	}	
}
