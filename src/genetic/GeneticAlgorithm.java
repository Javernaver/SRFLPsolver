package genetic;

import solver.SRFLP;

public class GeneticAlgorithm {
	
	enum SelectionStrategy 
	/* Estrategias de seleccion de individuos
	 *   MU_LAMBDA: estrategia (mu, lambda)
	 *   MUPLUSLAMBDA estrategia (mu+lambda)
	 */
	{
		MULAMBDA,
		MUPLUSLAMBDA
	};
	
    /* Problema */
	static SRFLP problem;
	
	/* Parametro tamaño de la poblacion */
	int pop_size;
	
	/* Cantidad de hijos */
	int offspring_size;
	
	/* Seleccion de padres */
	Population.SelectionType pselection_type;
	
	/* Cruzamiento */
	Population.CrossoverType crossover_type;
	
	/* Mutacion */
	Population.MutationType mutation_type;
	
	/* Probabilidad de mutacion */
	Double mutation_prob;
	
    /* Estrategia de seleccion de la nueva poblacion */
	SelectionStrategy selection_strategy;
	
	/* Tipo de seleccion de la poblacion */
	Population.SelectionType gselection_type;
	
	/* Soluciones seleccionadas por elitismo */
	int elitism;
	
	/* Mejor tour */
	Tour best_tour;
	
	public GeneticAlgorithm (SRFLP _problem, Population.SelectionType _pselection_type,
		   Population.CrossoverType _crossover_type, Population.MutationType _mutation_type, 
		   SelectionStrategy _selection_strategy, Population.SelectionType _gselection_type,
		   int _pop_size, int _offspring_size, Double _mutation_prob) 
  /* FUNCTION: constructor de la clase GeneticAlgorithm
   * INPUT: instancia de TSP: _problem, tipo de seleccion de padres: _pselection_type, 
   *        tipo de crossover: _crossover_type, tipo de mutacion: _mutation_type, 
   *        estrategia de seleccion de problación: _selection_strategy, tipo de 
   *        seleccion de poblacion: _gselection_type, tamaño de la poblacion: _pop_size,
   *        cantidad de hijos: _offspring_size, probabilidad de mutacion: _mutation_prob
   * 
   */
	{
		problem   = _problem;
		pselection_type = _pselection_type;
		crossover_type   = _crossover_type;
		mutation_type = _mutation_type;
		mutation_prob = _mutation_prob;
		selection_strategy = _selection_strategy;
		gselection_type = _gselection_type;
		pop_size  = _pop_size;
		best_tour = null;
		offspring_size = _offspring_size;
		System.out.println("\nInicializando Algoritmo Genetico ...");
	};
	
    public void print_best_solution (boolean full)
    /* FUNCTION: print_best_solution
     * INPUT: booleano que indica si la solucion debe ser impresa completa: full
     * OUTPUT: ninguno
     */
    {
      System.out.println("\nMejor solucion Algoritmo Genetico: ");
      if (full) {
        best_tour.print();
      } else {
        best_tour.printCost();
      }
    };
	
	public void search (int max_evaluations, int max_iterations) 
    /* 
     * FUNCTION: search: funcion que ejecuta la busqueda del algoritmo genetico
     * INPUT: numero maximo de evaluaciones: max_evaluations, 
     *        numero maximo de iteraciones: max_iterations
     * OUTPUT: ninguno
     * COMMENT: esta funcion ejecuta la busqueda del algoritmo genetico
     *          desde una poblacion generada aleatoriamente. La mejora
     *          solución final puede ser encontrado en best_tour)
     */
	{
		int evaluation = 0;
		int iteration = 0;
		int[] parents;
		
		/* Inicializar poblacion */
		System.out.println("Generando poblacion inicial ...");
		Population population = new Population(pop_size, problem);
		
		/* Inicializar poblacion de hijos */
		Population offspring = new Population (problem);
		
		/* Imprimir mejor solución encontrada */
		System.out.println("... mejor individuo ...");
		population.getBestTour().print();
        System.out.println("");
        
        /* Guardar la mejor solución en best_tour */
        if (best_tour == null) 
        	best_tour = new Tour(population.getBestTour());
        else {
        	best_tour.Copy(population.getBestTour());
        }
        
        /* bucle principal del algoritmo */
        System.out.println("\nComenzando busqueda \n");
		while (terminationCondition(evaluation, max_evaluations, iteration, max_iterations)) {

			/* Aplicar cruzamiento para generar poblacion de hijos */
			while (offspring.size() < offspring_size) {
				parents = population.selectParents(pselection_type); 
				offspring.add(population.crossover(parents, crossover_type,false));
			}
			
			/* Aplicar mutacion */
	        offspring.mutation(mutation_prob, mutation_type, false);
			
	        /* Reportar el mejor hijo */
		    System.out.print("Generacion " + iteration );
		    System.out.print(", mejor hijo: " + offspring.getBestTour().getCost());
			
		    /* Seleccionar nueva poblacion */
		    if (selection_strategy == SelectionStrategy.MULAMBDA) {
		    	/* Seleccionar solo desde los hijos */
		    	if (offspring_size > pop_size) { 
		    		/* Seleccionar desde los hijos */
		    		offspring.selectPopulation(pop_size, gselection_type);
		    	}
		    	population.Copy(offspring);

		    } else if (selection_strategy == SelectionStrategy.MUPLUSLAMBDA) {
		    	/* Seleccionar de los hijos y los padres */
		    	/* Unir ambas poblaciones (hijos y padres) */
		    	offspring.joinPopulation(population);
		    	/*Seleccionar de estas poblaciones */
		    	offspring.selectPopulation(pop_size, gselection_type);
		    	population.Copy(offspring);
		    }
			
			/* Revisar si la nueva solucion es la mejor hasta el momento */
			if (population.getBestTour().getCost() < best_tour.getCost()) {
				System.out.print(", mejor actual: " + best_tour.getCost() + 
						 " -> "+ population.getBestTour().getCost()+ " (actualizado) \n");	
				best_tour.Copy(population.getBestTour());
			} else {
				
				System.out.print(", mejor actual: " + best_tour.getCost() +"\n");
				
			}
			
			/* Incrementar contadores */
			iteration = iteration + 1;
			evaluation = evaluation + offspring_size;
			offspring.clear();
		}
	};

  
	private boolean terminationCondition (int evaluations, int max_evaluations, 
			                              int iterations, int max_iterations) 
    /* 
     * FUNCTION: terminationCondition
     * INPUT: numero actual de evaluaciones: evaluations, numero maximo de evaluaciones:
     *         max_evaluations, numero de iteraciones: iterations, numero maximo de 
     *         iteraciones: max_iterations
     * OUTPUT: booleano que indica si se debe continuar la ejecucion o no. 
     *         True: si se debe continuar, False: si no se debe continuar
     */	
	{
		
		/* criterio de termino de las evaluaciones */
		if (max_evaluations > 0) {
			if (evaluations >= max_evaluations) return (false);
		}
		if (max_iterations > 0) {
			if (iterations >= max_iterations) return (false);
		}
		
		return (true);
	};

	
}
