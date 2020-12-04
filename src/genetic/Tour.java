package genetic;
/* Tour class
* Clase que representa una solucion del TSP
*
* @autor  Leslie Perez Caceres
* @version 1.0
*
*/
import java.util.Arrays;
import java.util.ArrayList;
import solver.SRFLP;
public class Tour {
  enum InitialSolution
  /* Metodos disponibles para crear una solucion inicial
   *   RANDOM: solucion alearia
   *   NEAREST_N: solucion creada con la heuristica del vecino mas cercano
   *   DETERMINISTIC: solucion creada deterministicamente para testing
   */
  {
      RANDOM, NEAREST_N, DETERMINISTIC
  };
  
 
  /* instancia del tsp */
	private static SRFLP problem;
	
	/* solucion actual */
	private int [] current;
	private double cost;
	
	public Tour (InitialSolution initial_sol, SRFLP _problem) {
    /*
     * FUNCTION: Constructor clase Tour
     * INPUT: metodo para generar la solucion inicial: initial_sol, instancia
     *        del TSP: _problem
     */
		problem = _problem;
		
		/* solucion inicial */
	
		current = problem.random_tour();
        
		/* calcular costo */
		cost = problem.getTotalDistance(current);
	};
	
	public Tour (int[] tour, SRFLP _problem) {
	    /*
	     * FUNCTION: Constructor clase Tour
	     * INPUT: metodo para generar la solucion inicial: initial_sol, instancia
	     *        del TSP: _problem
	     */
			problem = _problem;
			
			current = new int[tour.length];
			for (int i=0; i<tour.length; i++) {
				current[i]= tour[i];
			}
	       
	        
			/* calcular costo */
			cost = problem.getTotalDistance(current);
		};
		
	public Tour(Tour _tour) {
    /*
     * FUNCTION: Constructor de copia clase TSP
     */
		problem = _tour.problem;
		current = Arrays.copyOf(_tour.current, _tour.current.length);
		cost    = _tour.cost;
	};
	
	public void Copy(Tour _tour) 
    /* 
     * FUNCTION: Copy: copia el contenido de una solucion
     * INPUT: instancia de tour inicializada
     * OUTPUT: ninguno
     */
	{
		
		System.arraycopy(_tour.current, 0, current, 0, _tour.current.length);
		cost = _tour.cost;
	};
	
	public double getCost () 
    /* 
     * FUNCTION: getCost
     * INPUT: ninguno
     * OUTPUT: costo del tour actual
     * COMMENT: 
     */
	{
		return(cost);
	};
	
	public void print() {
		problem.print_solution_and_cost(current);
		
	};
	
	public void lprint() {
		problem.lprint_solution_and_cost(current);
	};
	
	public void printCost() {
		System.out.print("cost: "+ cost);
	};
	
    private double delta_cost_swap (int[] tour, double cost2, int n1, int n2)
    /*
     * FUNCTION: delta_cost_swap: recalcula el nuevo costo de un tour despues
     *           de una aplicacion del movimiento swap
     * INPUT: arreglo del tour a modificar (sin haber sido modificado aun): tour,
     *        el costo actual del tour: cost, indices de los nodos
     *        para hacer swap: n1 y n2
     * OUTPUT: el nuevo costo despues del swap.
     * COMMENT: esta funcion no modifica el tour actual
     *
     */
    {
        int s, e, s_prev, s_next, e_prev, e_next;
        if (n1 == n2) return cost2;
        
        /* Identify the smaller index */
        s = Math.min(n1,n2);
        e = Math.max(n1,n2);
        
        /* Identify the previous and posterior nodes to form the
         * added and removed edges */
        s_prev = s - 1;
        s_next = s + 1;
        e_prev = e - 1;
        e_next = e + 1;
        
        if (s == 0)
            s_prev = problem.getSize() - 1;
        if (e == (problem.getSize() - 1))
            e_next = 0;
        
        /* Calculate new cost */
        if (s_prev != e) {
            cost2 = cost2 - problem.getWeight(tour[s_prev], tour[s])
                        - problem.getWeight(tour[e], tour[e_next])
                        + problem.getWeight(tour[s_prev], tour[e])
                        + problem.getWeight(tour[s], tour[e_next]);
        } else {
            cost2 = cost2 - problem.getWeight(tour[s], tour[s_next])
                        - problem.getWeight(tour[e_prev], tour[e])
                        + problem.getWeight(tour[e], tour[s_next])
                        + problem.getWeight(tour[e_prev], tour[s]);
        }
        
        if (s_next!=e_prev && s_next!=e && s_prev != e) {
            cost2 = cost2 - problem.getWeight(tour[s], tour[s_next])
                        - problem.getWeight(tour[e_prev], tour[e])
                        + problem.getWeight(tour[e], tour[s_next])
                        + problem.getWeight(tour[e_prev], tour[s]);
        }

        return cost2;
    };
    
    public void swap (int n1, int n2)
    /*
     * FUNCTION: swap: aplica el operador swap entre dos nodos
     * INPUT:  indices de dos nodos para set intercambiados con swap: n1 y n2
     * OUTPUT: ninguno
     * COMMENT: esta funcion modica el tour de la clase (tour) y el costo (cost)
     */
    {
        int[] tour = Arrays.copyOf(current, current.length);
        int aux;
        
        /* do swap */
        aux = tour[n1];
        tour[n1] = tour[n2];
        tour[n2] = aux;
       // tour[tour.length-1] = tour[0];
        
        cost = delta_cost_swap (current, cost, n1, n2);
        //assert current_cost == problem.compute_tour_length(tour);
        System.arraycopy(tour, 0, current, 0, tour.length);
    };

    public void random_swap ()
    /*
     * FUNCTION: random_swap: aplica el operador swap entre dos nodos aleatorios
     * INPUT:  ninguno
     * OUTPUT: ninguno
     * COMMENT: esta funcion modica el tour de la clase (tour) y el costo (cost)
     */
    {
        int n1 = Utilities.random_n(problem.getSize());
        int n2 = n1;
        
        while (n2 == n1) {
        	n2 =  Utilities.random_n(problem.getSize());
        }
          
        /* do swap */
        swap(n1,n2);
    };
  
    private double delta_cost_two_opt (int[] tour, double cost2, int s, int e)
    /*
     * FUNCTION: recalcula el nuevo costo de un tour despues
     *           de una aplicacion del movimiento 2-opt
     * INPUT: indices de dos nodos para set intercambiados con 2-opt: s y e
     * OUTPUT: el nuevo costo despues de 2-opt.
     * COMMENT: esta funcion no modifica el tour actual
     */
    {
        int s_prev, e_next;
        
        /* El mismo nodo, no hay swap */
        if (e == s) return cost2;
        
        /* identificar el nodo anterior y posterior para formar los caminos eliminados */
        s_prev = s - 1;
        e_next = e + 1;
        if (s == 0) {
            if (e == (problem.getSize())) return cost2;
            s_prev = problem.getSize() ;
        }

        cost2 = cost2 - problem.getWeight(tour[s_prev], tour[s])
                    - problem.getWeight(tour[e], tour[e_next])
                    + problem.getWeight(tour[s_prev], tour[e])
                    + problem.getWeight(tour[s], tour[e_next]);
        return cost2;
    };
	
    public void twoOptSwap (int n1, int n2)
    /*
     * FUNCTION: twoOptSwap: aplica movimiento 2-opt entre 2 nodos
     * INPUT: indices de dos nodos para set intercambiados con 2-opt: n1 y n2
     * OUTPUT: ninguno
     * COMMENT: esta funcion modica el tour de la clase (tour) y el costo (cost)
     */
    {
        int s, e, aux;
        int[] new_tour = new int[current.length]; /* tour nuevo */
        
        /* no es posible aplicar el movimiento en el mismo nodo */
        if (n1 == n2) return;
        /* indice fuera de los limites */
        if (n1 >= problem.getSize() || n2 >= problem.getSize()) return;
        if (n1 < 0 || n2 < 0) return;
        
        /* identificar el indice mas pequeño */
        s = Math.min(n1,n2);
        e = Math.max(n1,n2);
        
        /* copiar la primera parte del tour no modificado por 2-opt */
        for (int i=0; i < s; i++)
            new_tour[i] = current[i];

        /* invertir el orden del tour entre [s,e] */
        aux = 0;
        for (int i=s; i <=e; i++) {
            new_tour[i] = current[e-aux]; /* intercambia nodos */
            aux = aux + 1;
        }

        /* copiar la parte final del tour no modificado por 2-opt */
        for (int i=(e+1); i < current.length; i++)
            new_tour[i] = current[i];

       // new_tour[current.length-1] = new_tour[0];
        
        cost = delta_cost_two_opt (current, cost, s, e);
        //assert current_cost == problem.compute_tour_length(new_tour);
        System.arraycopy(new_tour, 0, current, 0, new_tour.length);
    };
	
    public void random_two_opt ()
    /*
     * FUNCTION: random_two_opt: aplica el operador two_opt entre dos nodos aleatorios
     * INPUT:  ninguno
     * OUTPUT: ninguno
     * COMMENT: esta funcion modica el tour de la clase (tour) y el costo (cost)
     */
    {
        int n1 = Utilities.random_n(problem.getSize());
        int n2 = n1;
        
        while (n2 == n1 | Math.abs(n2-n1) < 2 ) {
        	n2 =  Utilities.random_n(problem.getSize());
        }
        
        twoOptSwap(n1, n2);
    };
    
    
    public int getPosition (int node) {
    	for (int i=0; i<problem.getSize(); i++) {
    		if (current[i] == node) return(i);
    	}
    	return(-1);
    };

    public int getNode (int pos) {
    	return (current[pos]);
    };

}
