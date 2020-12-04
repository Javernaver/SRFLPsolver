package solver;
import genetic.*;

public class Main {
	/**
	 * Clase principal solver para single row facility layout problem (srflp)
	 * el algoritmo esta construido utilizando la matahuristica de algoritmos geneticos,
	 * el codigo fuente utilizado es el visto en clases pero modificado para adaptarse
	 * este problema
	 * 
	 *  
	 * 
	 * 	Autor Codigo Original: Leslie Perez Caceres
	 * 	@autor Nicolas Figueroa, Jorge Polanco, Javier del Canto 
	 * 	@version 1.0
	 *
	 *
	 * INF3144 Investigacion de Operaciones
	 * * 
	 *  La clase GeneticAlgorithm implementa los operadores disponibles para
	 *  definir un algoritmo genetico
	 *  
	 *  La clases Population implementa metodos para el manejo de la poblacion
	 *  en el algoritmo genetico
	 *
	 *  La clase Tour consiste en un tour de SRFLP junto con su costo. 
	 *
	
	 * 
	 * @param args
	 */
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		 /* Leer opciones desde la linea de comando */
	     AlgorithmOptions opciones = new AlgorithmOptions(args);
		
		SRFLP problem = new SRFLP(opciones.filename);
		//problem.printInstance();
		
		/* Crear solver genetic algorithm */
	    GeneticAlgorithm solver = new GeneticAlgorithm (problem, opciones.pselection_type,
	  		           opciones.crossover_type, opciones.mutation_type, 
	  		           opciones.selection_strategy, opciones.gselection_type,
	  		           opciones.pop_size, opciones.offspring_size, opciones.mutation_prob);
	    

		/* Ejecutar la busqueda */
	    solver.search( opciones.max_evaluations, opciones.max_iterations);
	    
	    /* Mostrar la mejor solucion encontrada */
	    System.out.println("\nTerminando ejecucion ...");
	    solver.print_best_solution (true);
		
	}
	
	
}
