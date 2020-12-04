package genetic;
/* Population class
* Clase que manjera la poblacion de un 
* algoritmo genetico
*
* @autor  Leslie Perez Caceres
* @version 1.0
*
*/
import java.util.Vector;
import solver.SRFLP;

public class Population {
	enum SelectionType
	/* Tipos de seleccion de individuos
	 *   RANDOM: seleccion random
	 *   BEST: seleccion de los mejores (elitismo)
	 *   ROULETTE: seleccion proporcional al fitness
	 *   TOURNAMENT: seleccion de torneos k=3
	 */
	{
	  RANDOM,
	  BEST,
	  ROULETTE,
	  TOURNAMENT
	};
	
	enum CrossoverType
	/* Tipos de cruzamiento disponibles
	 *   PMX: (partially-mapped crossover) hace swap adaptando los tours
	 *   O1X: (order 1 crossover) 
	 *   OPX: (one point crossover) se realiza cruzamiento en un punto 
	 *        utilizando una lista de referencia 
	 */
	{
	  PMX,
	  OX, 
	  OPX
	};
	
	enum MutationType
	/* Tipos de mutacion disponibles
	 *   SWAP: mutacion que aplica un movimiento swap aleatoriamente
	 *   TWO_OPT: mutacion que aplica un movimiento 2-opt aleatoriamente
	 */
    {
	    SWAP,
	    TWO_OPT
	};  
   
    /* instancia del tsp */
	private static SRFLP problem;
	
	/* poblacion */
	private Vector <Tour> pop;
	
	/* tamaño poblacion */
	private int pop_size;
	
	/* indice del mejor_individuo */
	private int best_index;

    /******************************************/
    /*              Constructores             */ 
    /******************************************/
	
	public Population (int _pop_size, SRFLP _problem)
    /*
     * FUNCTION: Constructor clase Population
     * INPUT: tamaño poblacion: pop_size, instancia
     *        del TSP: _problem
     */
	{
		problem = _problem;
		pop_size = _pop_size;
		pop = new Vector<Tour> ();
		best_index = -1;
		
		/* Agregar individuos a la poblacion*/
		for (int i=0; i<pop_size; i++) {
		    pop.add(new Tour(Tour.InitialSolution.RANDOM, problem));
		}
		
		/* Identificar el mejor individuo en la poblacion*/
		searchBest();
	};
	
	public Population (SRFLP _problem) 
	/*
	 * FUNCTION: Constructor clase Population
	 * INPUT: instancia del TSP: _problem
	 * COMMENTS: este constructor no inicializa la poblacion
	 *           por lo tanto esta tiene tamaño 0
	 */
	{
			problem = _problem;
			pop_size = 0;
			pop = new Vector<Tour> ();
			best_index = -1;
	};
	
	public Population (Vector<Tour> _pop, SRFLP _problem) 
	/*
     * FUNCTION: Constructor clase Population
	 * INPUT: vector de soluciones para asignar a la poblacion: _pop,
	 *        instancia del TSP: _problem
     * COMMENTS: este constructor copia las soluciones en _pop
	*/
	{
		problem = _problem;
		pop_size = _pop.size();
		pop = new Vector<Tour> ();
		pop.addAll(_pop);
		best_index = -1;
		for (int i=0; i< pop_size;i++) {
			if (best_index == -1) {
		        best_index = i;		
		    } else {
		    	if (pop.get(i).getCost() < pop.get(best_index).getCost()) {
		    		best_index = i;
		    	}
		    }
		}
	};
	
	public Population (Population _population) 
    /*
     * FUNCTION: Constructor de copia clase Population
     */
	{
		problem = _population.problem;
		pop_size = _population.pop_size;
		pop = new Vector<Tour> ();
		if (pop_size > 0) {
			pop.addAll(_population.pop);
			best_index = _population.best_index;
		} else {
			best_index = -1;
		}
	};
	
    /******************************************/
    /*         Manejo de poblacion            */ 
    /******************************************/
	
	public void Copy (Population _population) 
    /* 
     * FUNCTION: Copy: copia el contenido de una poblacion
     * INPUT: instancia de poblacion
     * OUTPUT: ninguno
     */
	{
		problem = _population.problem;
		pop_size = _population.pop_size;
		pop.removeAllElements();
		if ( pop_size > 0) {
			pop.addAll(_population.pop);
			best_index = _population.best_index;
		} else {
			best_index = -1;
		}
	};
	
	public void start () 	
	/*
	 * FUNCTION: start: inicializa la poblacion
     * INPUT: ninguno
	 * OUTPUT: ninguno
	 */
	{

		pop.removeAllElements();
		/* Agregar individuos a la poblacion*/
		for (int i=0; i<pop_size; i++) {
		    pop.add(new Tour(Tour.InitialSolution.RANDOM, problem));
		    pop.get(i).print();
		}
		
		/* Identificar el mejor individuo en la poblacion*/
		searchBest();
	};
	
	public void add (Tour individual) 
	/*
     * FUNCTION: add: añade un individuo a la solucion
	 * INPUT: solucion a añadir como individuo: individual
	 * OUTPUT: ninguno
	 */
	{
		pop.add(individual);
		pop_size++;
		searchBest();
	};
	
	public void add (Vector<Tour> individuals) 
	/*
	 * FUNCTION: add: añade un vector de individuos a la solucion
     * INPUT: vector de soluciones a añadir como individuos: individuals
	 * OUTPUT: ninguno
	 */
	{
		for (int i=0; i< individuals.size(); i++) {
		  pop.add(individuals.get(i));
		  pop_size++;
		}
		
		searchBest();
	};
	
    public void joinPopulation (Population p) 
    /*
     * FUNCTION: joinPopulation: une dos poblaciones
     * INPUT: poblacion a unir a la actual: p
     * OUTPUT: ninguno
     */
    {
    	pop_size = pop_size + p.pop_size;
    	pop.addAll(p.pop);
    	searchBest();
    };
	
	public void clear() 	
	/*
	 * FUNCTION: clear: elimina todos los indivuos de una poblacion
	 * INPUT: ninguno
	 * OUTPUT: ninguno
	 */
	{

		pop.removeAllElements();
		pop_size = 0;
		best_index = -1;
	};
  
	private void searchBest ()
	/*
	 * FUNCTION: searchBest: busca el mejor individuo en la poblacion
	 * INPUT: ninguno
	 * OUTPUT: ninguno
	 * COMMENTS: el mejor individuo se guarda como index (best_index)
     */
	{
		for (int i=0; i<pop_size; i++) {
			/* guardar el mejor individuo encontrado por el algoritmo */
			if (best_index == -1) {
				best_index = i;		
			} else {
				if (pop.get(i).getCost() < pop.get(best_index).getCost()) {
					best_index = i;
				}
			}
		}
	};
	
	public void orderPopulation ()
	/*
	 * FUNCTION: orderPopulation: ordena la poblacion 
	 * INPUT: ninguno
	 * OUTPUT: ninguno
	 * COMMENTS: orden de mejor a peor fitness
     */
	{
		double[] fx = new double[pop_size];
    	int[] ix = new int[pop_size];
    	Vector<Tour> ordered = new Vector<Tour> ();
    	
    	/* ordenar la poblacion */
    	for (int i=0; i<pop_size; i++) {
    		fx[i] = pop.get(i).getCost();
    		ix[i] = i;
    	}
    	Utilities.sort2(fx, ix);
    	
    	/* seleccionar los mejores */
    	for (int i=0; i<pop_size; i++) {
    		ordered.add(pop.get(ix[i]));
    	}
    	
    	/* eliminar todos los individuos */
    	pop.removeAllElements();
    	
    	/* añadir seleccionados */
    	pop.addAll(ordered);
    	
    	/* actualizar poblacion */
    	best_index = -1;
    	searchBest();
	};
	
    /******************************************/
    /*        Impresion de soluciones         */ 
    /******************************************/
	
	public void print () 
	/*
	 * FUNCTION: print: imprime la poblacion (soluciones + costos)
     * INPUT: ninguno
     * OUTPUT: ninguno
	 */
	{

		for (int i=0; i<pop.size(); i++) {
			System.out.println("Individuo " +i +" ");
			pop.get(i).print();
		}
		System.out.println("");
	};
	
	public void print (int index) 
	/*
	 * FUNCTION: print: imprime un individuo (solucion + costo)
     * INPUT: index del individuo a imprimir: index
     * OUTPUT: ninguno
	 */
	{

		System.out.println("Individuo " + index +" ");
		pop.get(index).print();
		System.out.println("");
	};
	
	public void lprint () 
	/*
	 * FUNCTION: lprint: imprime la poblacion (soluciones + costos)
	 *           en una linea
     * INPUT: ninguno
     * OUTPUT: ninguno
	 */
	{

		for (int i=0; i<pop.size(); i++) {
			System.out.print("Individuo " +i +": ");
			pop.get(i).lprint();
		}
		System.out.println("");
	};
	
	public void lprint (int index) 
	/*
	 * FUNCTION: lprint: imprime un individuo (solucion + costo)
	 *           en una linea
     * INPUT: index del individuo a imprimir: index
     * OUTPUT: ninguno
	 */
	{

		System.out.print("Individuo " + index +": ");
		pop.get(index).lprint();
	};
	
	public void printAllFitness () 
	/*
	 * FUNCTION: printAllFitness: imprime el fitness de todos los individuos
     * INPUT: ninguno
     * OUTPUT: ninguno
	 */
	{

		System.out.println("Poblacion: ");
		for (int i=0; i<pop.size(); i++) {
			System.out.println("  individuo 1:" + pop.get(i).getCost());
		}
	};
	
    /******************************************/
    /*             Metodos varios             */ 
    /******************************************/
	
	public long getFitness (int index) 
	/*
	 * FUNCTION: getFitness: retorna el fitness de un individuo
	 * INPUT: index de un individuo en la poblacion: index
	 * OUTPUT: Fitness del individuo index
     */
	{
		return (long) (pop.get(index).getCost());
	};
	
	public Tour getBestTour () 
	/*
	 * FUNCTION: getBestTour: retorna el mejor individuo
	 * INPUT: ninguno
	 * OUTPUT: mejor Tour en la poblacion
     */
	{
		return(pop.get(best_index));
	};
	
	private Vector<Tour> getIndividuals (int[] index) 
	/*
	 * FUNCTION: getIndividuals : retorna individuos
	 * INPUT: index de un individuos en la poblacion: index
	 * OUTPUT: vector de individuos seleccionados
     */
	{
		Vector <Tour> selected = new Vector<Tour> ();
		for (int i=0; i<index.length; i++) {
			selected.add(pop.get(index[i]));
		}
		return (selected);
	};
	
    public int size () 
    /*
     * FUNCTION: size: retorna el tamaño de la poblacion
     * INPUT: ninguno
     * OUTPUT: ninguno
     */
    {

		return (pop_size);
	};
	
 	private double[] generateRouletteWheel(Vector<Tour> candidates) 
	/*
	 * FUNCTION: generateRouletteWheel: retorna la torta de probabilidades
	 *           asociadas al fitness de las soluciones entregadas
	 * INPUT: vector de soluciones a ser seleccionadas: candidates
	 * OUTPUT: arreglo de double que posee las probabilidad acumulada
	 *         para la seleecion de soluciones
	 * COMMENTS: seleccion propocional al fitness
     */
 	/* Ruleta para minimizacion:
 	 * p(x1) = (min + max - f(x1)) / sum(f(x))
 	 */
 	{
		double sum = 0.0;
		double min = candidates.get(0).getCost();
		double max = candidates.get(0).getCost();
		double[] roulette = new double[candidates.size()];
		
		/* Encontrar el minimo y el máximo en los individuos */
		for (int i=1; i<candidates.size(); i++) {
			if (candidates.get(i).getCost() < min) 
				min = candidates.get(i).getCost();
			if (candidates.get(i).getCost() > max) 
				max = candidates.get(i).getCost();
		}
		
		for (int i=0; i<candidates.size(); i++) {
			roulette[i] = (double) (min + max - candidates.get(i).getCost());
			sum = sum + roulette[i];
		}
		
		roulette[0] = roulette[0]/sum;
		for (int i=1; i<candidates.size(); i++) {
			roulette[i] = roulette[i] / sum;
			roulette[i] = roulette[i-1] + roulette[i];
		}
		return(roulette);
	};
	
    /******************************************/
    /*           Metodos de mutacion          */ 
    /******************************************/
    
    private void SwapMutation (Double mut_probability, boolean test) 
	/*
	 * FUNCTION: SwapMutation: aplica swap aleatoriamente a 
	 *           toda la poblacion
	 * INPUT: probabilidad de mutacion: mut_probability
	 * OUTPUT: ninguno
	 * COMMENTS: 
     */
    {
    	double r; 
		for (int i=0; i<pop.size(); i++) {
			if (test) {
				r = 0.5;
			} else {
				r = Utilities.ran01();
			}
			/* Mutacion solo si la probabilidad lo indica */
			if (mut_probability > r) {
				if (test) {
					pop.get(i).swap(2,8);
				} else {
					pop.get(i).random_swap();
				}
			}
		}
    };
    
    private void TwoOptMutation (Double mut_probability, boolean test) 
	/*
	 * FUNCTION: TwoOptMutation: aplica two_opt aleatoriamente a 
	 *           toda la poblacion
	 * INPUT: probabilidad de mutacion: mut_probability
	 * OUTPUT: ninguno
	 * COMMENTS: 
     */
    {
    	double r;
		for (int i=0; i<pop.size(); i++) {
			if (test) {
				r = 0.5;
			} else {
				r = Utilities.ran01();
			}
			/* Mutacion solo si la probabilidad lo indica */
			if (mut_probability > r) {
				if (test) {
				  pop.get(i).twoOptSwap(2,8);	
				} else {
				  pop.get(i).random_two_opt();
				}
			}
		}  	
    };
    
    public void mutation (Double mut_probability, MutationType mtype, boolean test) 
	/*
	 * FUNCTION: mutation: aplica operador de mutacion
	 * INPUT: probabilidad de mutacion: mut_probability,
	 *        tipo de mutacion: mtype
	 * OUTPUT: ninguno
	 * COMMENTS: por defecto se aplica operador swap
     */
    {
    	if (mtype == MutationType.SWAP) {
    		SwapMutation(mut_probability, test);
    	} else if (mtype == MutationType.TWO_OPT){
    		TwoOptMutation (mut_probability, test); 
    	} else {
    		SwapMutation(mut_probability, test);
    	}
    	searchBest();
    };
 
    
    /******************************************/
    /*         Metodos de cruzamiento         */ 
    /******************************************/   
    
    private Vector<Tour> PMXCrossover (Vector<Tour> parents) 
	/*
	 * FUNCTION: PMXCrossover: aplica el operador PMX a los
	 *           padres
	 * INPUT: vector con 2 individuos padres: parents
	 * OUTPUT: vector con 2 individuos hijos
	 * COMMENTS: 
     */
    {
    	Vector<Tour> offspring = new Vector<Tour> ();
    	Tour p1 = parents.get(0);
    	Tour p2 = parents.get(1);
    	Tour o1 = new Tour (p1);
    	Tour o2 = new Tour (p2);
    	
    	int cpoint;
    	int aux;
    	
    	/* Obtener punto de crossover */
    	cpoint = Utilities.random_n(problem.getSize()-1);
    	
    	System.out.println("cpoint: "+ cpoint);
    	/* Generar el primer hijo */
    	for (int i=0; i<cpoint; i++) {
    		aux = o1.getPosition(p2.getNode(i));
    		o1.swap(i,aux);
    	}
    		
    	/* Generar el segundo hijo */
    	for (int i=cpoint; i<problem.getSize(); i++) {
    		aux = o2.getPosition(p1.getNode(i));
    		o2.swap(i,aux);
    	}
    	
    	offspring.add(o1);
    	offspring.add(o2);
    	return(offspring);
    };
    
    private Vector<Tour> OXCrossover (Vector<Tour> parents) 
	/*
	 * FUNCTION: O1XCrossover: aplica el operador ORDER 1 a los
	 *           padres
	 * INPUT: vector con 2 individuos padres: parents
	 * OUTPUT: vector con 2 individuos hijos
	 * COMMENTS: 
     */
    {
    	Tour p1 = parents.get(0);
    	Tour p2 = parents.get(1);
    	int[] h1 = new int[problem.getSize()];
    	int[] h2 = new int[problem.getSize()];
    	Vector<Integer> aux1in = new Vector<Integer>();
    	Vector<Integer> aux1out = new Vector<Integer>();
    	Vector<Integer> aux2in = new Vector<Integer>();
    	Vector<Integer> aux2out = new Vector<Integer>();
    	Vector<Tour> hijos = new Vector<Tour> ();
    	int r1, r2;
    	
    	do{
    		r1 = Utilities.random_n(problem.getSize());
    		r2 = Utilities.random_n(problem.getSize());
    	} while (r1 >= r2);
    	
    	for (int i=r1; i<=r2; i++) {
    		aux1in.add(p1.getNode(i));
    		aux2in.add(p2.getNode(i));
    	}
    	
    	for (int i=0; i<problem.getSize();i++) {
    		if (!aux1in.contains(p2.getNode(i)))
    			aux1out.add(p2.getNode(i));
    		if (!aux2in.contains(p1.getNode(i)))
    			aux2out.add(p1.getNode(i));
    	}
    	
    	for (int i=0; i<r1; i++) {
    		h1[i] = aux1out.get(0);
			aux1out.remove(0);
			h2[i] = aux2out.get(0);
			aux2out.remove(0);
    	}
    	
    	for (int i=r1; i<=r2; i++) {
			h1[i] = aux1in.get(0);
			aux1in.remove(0);
			h2[i] = aux2in.get(0);
			aux2in.remove(0);    		
    	}
    
    	for (int i=(r2+1); i<problem.getSize(); i++) {
    		h1[i] = aux1out.get(0);
			aux1out.remove(0);
			h2[i] = aux2out.get(0);
			aux2out.remove(0);
    	}
     //	h1[problem.getSize()] = h1[0];
    //	h2[problem.getSize()] = h2[0];
    	
    	hijos.add(new Tour(h1, problem));
    	hijos.add(new Tour(h2, problem));
    	return(hijos);
    };

    private Vector<Tour> OPXCrossover (Vector<Tour> parents, boolean test)
	/*
	 * FUNCTION: OPXCrossover: aplica el operador de cruzamiento
	 *           en un punto a los padres
	 * INPUT: vector con 2 individuos padres: parents
	 * OUTPUT: vector con 2 individuos hijos
	 * COMMENTS: para realizar el cruzamiento se utiliza una
	 *           representacion ordinal intermedia
     */
    {
    	Tour p1 = parents.get(0);
    	Tour p2 = parents.get(1);
    	Vector<Integer> r1 = new Vector<Integer>();
    	Vector<Integer> r2 = new Vector<Integer>();
    	Vector<Tour> hijos = new Vector<Tour>();
    	int aux;
    	
    	int[] rp1 = new int[problem.getSize()];
    	int[] rp2 = new int[problem.getSize()];
    	int[] rh1 = new int[problem.getSize()];
    	int[] rh2 = new int[problem.getSize()];
    	int[] h1 = new int[problem.getSize()];
    	int[] h2 = new int[problem.getSize()];

    	/* Codificacion con lista de referencia */
    	for (int i=0; i<problem.getSize(); i++) {
    		r1.add(i);
    		r2.add(i);
    	}
    	
    	for (int i=0; i<problem.getSize(); i++) {
    		rp1[i] = r1.indexOf(p1.getNode(i));
    		r1.remove(rp1[i]);
    		rp2[i] = r2.indexOf(p2.getNode(i));
    		r2.remove(rp2[i]);
    	}
    	    	  	
    	/* Crossover */
    	if (test)
          aux = 5;
    	else
    	  aux = Utilities.random_n(problem.getSize());
    	for (int i=0; i<problem.getSize(); i++) {
    		if (i<aux) {
    			rh1[i] = rp1[i];
    			rh2[i] = rp2[i];
    		} else {
    			rh1[i] = rp2[i];
    			rh2[i] = rp1[i];  			
    		}
    	}
    	
    	/* Decodificación con lista de referencia */
    	r1.clear();
    	r2.clear();
    	for (int i=0; i<problem.getSize(); i++) {
    		r1.add(i);
    		r2.add(i);
    	}
    	
    	for (int i=0; i<problem.getSize(); i++) {
    		h1[i] = r1.get(rh1[i]);
    		r1.remove(rh1[i]);
    		h2[i] = r2.get(rh2[i]);
    		r2.remove(rh2[i]);
    	}	
    	//h1[problem.getSize()] = h1[0];
    	//h2[problem.getSize()] = h2[0];
    	
    	hijos.add(new Tour(h1, problem));
    	hijos.add(new Tour(h2, problem));
    	return(hijos);
    	
    };
    
    public Vector<Tour> crossover (int[] parents_id,  CrossoverType ctype, boolean test) 
	/*
	 * FUNCTION: crossover: aplica el operador cruzamiento
	 * INPUT: index en la poblacio de los padres: parents_id, 
	 *        tipo de crossover: ctype
	 * OUTPUT: vector con 2 individuos hijos
	 * COMMENTS: 
     */
    {
    	Vector <Tour> offspring = new Vector<Tour> ();
    	Vector <Tour> parents = new Vector<Tour> ();
    	
    	/* Obtener indivuduos padres con los ids */
    	for (int i=0; i<parents_id.length; i++) {
    		parents.addAll(getIndividuals(parents_id));
    	}
    	
        /* Aplicar Crossover */
    	if (ctype == CrossoverType.PMX) {
    		offspring.addAll(PMXCrossover(parents));
    	} else if (ctype == CrossoverType.OX) {
    		offspring.addAll(OXCrossover(parents));
    	} else if (ctype == CrossoverType.OPX) {
    		offspring.addAll(OPXCrossover(parents, test));
    	} else {
    	    offspring.addAll(OXCrossover(parents));
    	}
    	
    	return(offspring);
    };
    

    /******************************************/
    /*      Metodos de seleccion padres (2)   */ 
    /******************************************/
    
    private int[] selectIRandom (int size) 
	/*
	 * FUNCTION: selectIRandom: selecciona individuos aleatoriamente
	 * INPUT: cantidad de individuos a seleccionar: size
	 * OUTPUT: arreglo de index de los individuos seleccionados
	 * COMMENTS: seleccion aleatoria uniforme
	 */
    {
    	Vector<Integer> elegible = new Vector<Integer> ();
    	int[] sel = new int[size]; 
    	int aux;
    	
    	if (size > pop_size) {
    		System.err.println("Error: No es posible seleccionar " +
    	                       size + " de una poblacion con "+
    				           pop_size + "individuos");
    		System.exit(1);
    	} 
    	
    	/* Si todos son seleccionados */
    	if (size == pop_size) {
    		for (int i=0; i<size; i++) {
    			sel[i] = i;
    		}
    		return(sel);
    	}
    	
    	/* Añadir a todos los candidatos a ser seleccionados */
    	for (int i=0; i<pop.size(); i++) elegible.add(i);
    	
    	/* Seleccionar aleatoriamente */
    	for (int i=0; i<size; i++) {
    		aux = Utilities.random_n(elegible.size());
    		sel[i]= elegible.get(aux);
    		elegible.remove(aux);
    	}
    	return(sel);
    };
    
    private int[] selectIRoulette () 
	/*
	 * FUNCTION: selectIRoulette: selecciona individuos en base a 
	 *           la ruleta
	 * INPUT: ninguno
	 * OUTPUT: arreglo de index de 2 individuos seleccionados
	 * COMMENTS: seleccion proporcional al fitness (solo dos individuos)
	 */
    {
    	Vector<Tour> candidates = new Vector<Tour> ();
    	Vector<Integer> ids = new Vector<Integer>();
    	int[] sel = new int[2]; 
    	double[] roulette;
    	double r;
    	boolean flag;
    	
    	/* Si todos son seleccionados */
    	if (pop_size == 2) {
    		sel[0] = 0;
    		sel[1] = 1;
    		return(sel);
    	}
    	
    	/* Anadir candidatos a ser seleccionados*/
    	candidates.addAll(pop);
    	for (int i=0; i<pop_size; i++) {
    		ids.add(i);
    	}
    	
    	/* Primer padre seleccionado */
    	/* generar ruleta */
    	roulette = generateRouletteWheel(candidates);
    	/* seleccionar */
		r = Utilities.ran01();
		flag = true;
		for (int i=0; i<candidates.size() && flag; i++) {
			if (r < roulette[i]) {
				sel[0] = sel[1] = ids.get(i);
				candidates.remove(i);
				ids.remove(i);
				flag = false;
			}
		}
		
		/* Segundo padre seleccionado*/
		/* generar ruleta */
		roulette = generateRouletteWheel(candidates);
		/* seleccionar */
    	r = Utilities.ran01();
    	flag = true;
    	for(int i=0; i < candidates.size() && flag; i++) {
    		if (r < roulette[i]) {
    			sel[1] = ids.get(i);
    			flag = false;
    		}
    	}
    	
    	return(sel);
    };
    
    private int[] selectIBest (int size) 
	/*
	 * FUNCTION: selectIBest: selecciona individuos en base al fitness
	 * INPUT: cantidad de individuos a seleccionar: size
	 * OUTPUT: arreglo de index de individuos seleccionados
	 * COMMENTS: seleccion elitista 
	 */
    {
    	int[] sel = new int[size];
    	
    	/* Si todos son seleccionados */
    	if (size == pop_size) {
    		for (int i=0; i<size; i++) {
    			sel[i] = i;
    		}
    		return(sel);
    	}
    	
    	/* ordenar poblacion */
    	orderPopulation();
    	
    	/* seleccionar */
    	for (int i=0; i<size; i++) { 
    		sel[i] = i;
    	}
    	return (sel);
    };
    
    private int[] selectITournament () 
	/*
	 * FUNCTION: selectITournament: selecciona individuos en base al fitness
	 * INPUT: ninguno
	 * OUTPUT: arreglo de index de 2 individuos seleccionados
	 * COMMENTS: seleccion torneo k=3 (solo 2 individuos)
	 */
    {
    	int tsize = 3;
    	int[] sel = new int[2];
    	int[] tsel;
    	int aux;
    	 
    	/* reducir el tamaño del torneo si es necesario */
    	while (tsize > pop_size) {
    		tsize = tsize -1;
    	}
    	
    	/* Si todos son seleccionados */
    	if (pop_size == 2) {
    		for (int i=0; i<pop_size; i++) {
    			sel[i] = i;
    		}
    		return(sel);
    	}
    	
    	/* seleccion del primer padre */
    	/* seleccionar individuos torneo*/
    	tsel = selectIRandom(tsize);
    	aux = 0;
    	/* seleccionar mejor */
    	for (int j=1; j<tsize; j++) {
    		if (pop.get(tsel[0]).getCost() > pop.get(tsel[j]).getCost()) {
    			aux = j;
    		}
    	}
    	sel[0] = sel[1] = tsel[aux];	
    	
    	/* seleccion del segundo padre */
    	do {
    		/* seleccionar individuos torneo */
    		tsel = selectIRandom(tsize);
    		aux = 0;
    		/* seleccionar mejor */
    		for (int j=1; j<tsize; j++) {
    			if (pop.get(tsel[0]).getCost() > pop.get(tsel[j]).getCost()) {
    				aux = j;
    			}
    		}
    		sel[1] = tsel[aux];
    	} while (sel[0] == sel[1]);
    	
    	return(sel);
    };
    
    public int[] selectParents (SelectionType stype) 
	/*
	 * FUNCTION: selectParents: selecciona individuos
	 * INPUT: tipo de seleccion: stype
	 * OUTPUT: arreglo de index de 2 individuos seleccionados
	 * COMMENTS: 
	 */
    {
    	int[] sel_index = new int[2];
    	
    	if (stype == SelectionType.BEST) {
    		sel_index = selectIBest(2);
    	} else if (stype == SelectionType.ROULETTE) {
    		sel_index = selectIRoulette();
    	} else if (stype == SelectionType.TOURNAMENT) {
    		sel_index = selectITournament();
    	} else if (stype == SelectionType.RANDOM) {
    		sel_index = selectIRandom(2);
    	} else {
    		sel_index = selectIBest(2);
    	}
    	return(sel_index);
    };
    
    
    /******************************************/
    /*        Seleccion de poblacion          */ 
    /******************************************/
    
    private void selectPopBest (int size) 
	/*
	 * FUNCTION: selectPopBest: selecciona mejores individuos 
	 *           para permanecer en la poblacion
	 * INPUT: tamaño de la poblacion seleccionada: size
	 * OUTPUT: ninguno
	 * COMMENTS: seleccion elitista, elimina de la poblacion
	 *           individuos no seleccioandos
	 */
    {
    	Vector<Tour> selected = new Vector<Tour> ();
    	
    	/* Si todos son seleccionados */
    	if (size == pop_size) {
    		return;
    	}
    	
    	/* ordernar poblacion */
    	orderPopulation();
    	
    	/* seleccionar los mejores */
    	for (int i=0; i<size; i++) {
    		selected.add(pop.get(i));
    	}
    	
    	/* eliminar todos los individuos */
    	pop.removeAllElements();
    	
    	/* añadir seleccionados */
    	pop.addAll(selected);
    	
    	/* actualizar poblacion */
    	pop_size = size;
    	best_index = -1;
    	searchBest();
    };
    
    private void selectPopRoulette (int size) 
	/*
	 * FUNCTION: selectPopRoulette: selecciona en base a la ruleta
	 *           individuos para permanecer en la poblacion
	 * INPUT: tamaño de la poblacion seleccionada: size
	 * OUTPUT: ninguno
	 * COMMENTS: seleccion proporcional al fitness , elimina de la poblacion
	 *           individuos no seleccioandos
	 */
    {
    	Vector<Tour> selected = new Vector<Tour> ();
    	double[] roulette;
    	double r;
    	boolean flag;
    	
    	/* Si todos son seleccionados */
    	if (size == pop_size) {
    		return;
    	}
    	
    	for (int i =0; i<size; i++) {
    		/* generar ruleta */
    		roulette = generateRouletteWheel(pop);
    		/* seleccionar */
    		r = Utilities.ran01();
    		flag = true;
    		for(int j=0; j<pop.size() && flag; j++) {
    			if (r < roulette[j]) {
    				selected.add(pop.get(j));
    				pop.remove(j);
    				flag = false;
    			}
    		}
    	}
    	
    	/* eliminar todos los individuos */
    	pop.removeAllElements();
    	
    	/* añadir seleccionados */
    	pop.addAll(selected);
    	
    	/* actualizar poblacion */
    	pop_size = size;
    	best_index = -1;
    	searchBest();
    };
    
    private void selectPopTournament (int size) 
	/*
	 * FUNCTION: selectPopTournament: selecciona por torneo individuos 
	 *           para permanecer en la poblacion
	 * INPUT: tamaño de la poblacion seleccionada: size
	 * OUTPUT: ninguno
	 * COMMENTS: seleccion de torneos k=3, elimina de la poblacion
	 *           individuos no seleccioandos
	 */
    {
    	int tsize = 3;
    	int[] sel;
    	int aux;
    	Vector <Tour> selected = new Vector<Tour> ();

    	for (int i=0; i<size; i++) {
    		/* reduce el tamaño del torneo si es necesario */
    		while (tsize > pop_size) {
    			tsize = tsize-1;
    		}
    		
    		/* si todos son seleccionados */
    		if (tsize == pop_size) {
    			return;
    		}
    		
    		/* seleccionar inviduos torneo */
    		sel = selectIRandom(tsize);
    		
    		/* seleccionar mejores */
    		aux = 0;
    		for (int j=1; j<tsize; j++) {
    		    if (pop.get(sel[aux]).getCost() > pop.get(sel[j]).getCost()) {
    			    aux = j;
    		    }
    		}
    		
    		/* añadir a seleccionados */
    		selected.add(pop.get(sel[aux]));
    		
    		/* eliminar de la poblacion */
    		pop.remove(sel[aux]);
    	}
    	
    	/* eliminar todos los individuos de la poblacion */
    	pop.removeAllElements();
    	
    	/* añadir los seleccionados */
    	pop.addAll(selected);
    	
    	/* actualizar poblacion */
    	pop_size = size;
    	best_index = -1;
    	searchBest();
    };
    
    private void selectPopRandom (int size) 
	/*
	 * FUNCTION: selectPopRandom: selecciona aleatoriamente individuos 
	 *           para permanecer en la poblacion
	 * INPUT: tamaño de la poblacion seleccionada: size
	 * OUTPUT: ninguno
	 * COMMENTS: seleccion aleatoria uniforme, elimina de la poblacion
	 *           individuos no seleccioandos
	 */
    {
    	Vector<Tour> selected = new Vector<Tour> ();
    	int r;
    	
    	/* Si todos son seleccionados */
    	if (size == pop_size) {
    		return;
    	}
    	
    	/* seleccionar */
    	for (int i =0; i<size; i++) {
    		r = Utilities.random_n(pop.size()-1);
    		selected.add(pop.get(r));
    		pop.remove(r);
    	}
    	
    	/* eliminar todos los individuos */
    	pop.removeAllElements();
    	
    	/* añadir los seleccionados */
    	pop.addAll(selected);
    	
    	/* actualizar poblacion */
    	pop_size = size;
    	best_index = -1;
    	searchBest();
    };
    
    public void selectPopulation (int size, SelectionType stype) 
	/*
	 * FUNCTION: selectPopulation: selecciona  individuos 
	 *           para permanecer en la poblacion
	 * INPUT: tamaño de la poblacion seleccionada: size,
	 *        tipo de seleccion: stype
	 * OUTPUT: ninguno
	 * COMMENTS: elimina de la poblacion individuos no seleccioandos
	 */
    {
    	
    	if (size > pop_size) {
    		System.err.println("Error: No es posible seleccionar " +
    	                       size + " de una poblacion con "+
    				           pop_size + "individuos");
    		System.exit(1);
    	}
    	
    	if (stype == SelectionType.BEST) {
    		selectPopBest(size);
    	} else if (stype == SelectionType.ROULETTE) {
    		selectPopRoulette(size);
    	} else if (stype == SelectionType.TOURNAMENT) {
    		selectPopTournament(size);
    	} else if (stype == SelectionType.RANDOM) {
    		selectPopRandom(size);
    	} else {
    		selectPopBest(size);
    	}
    };


}
