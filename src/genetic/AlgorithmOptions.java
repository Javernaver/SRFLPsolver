package genetic;
/* AlgorithmOptions class
 * Clase que lee los argumentos proporcionados para ejecutar el algoritmo
 *
 * @autor  Leslie Perez Caceres
 * @version 1.0
 */
import java.util.HashMap;

import java.util.Map;
import org.apache.commons.cli.Option;
import java.util.Comparator;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import genetic.GeneticAlgorithm.SelectionStrategy;

public class AlgorithmOptions {
	
    /* archivo de la instancia */
	public String filename = "instances/QAP_sko56_04_n";
	//public String filename = "instances/peque�as/S10";
	//public String filename = "instances/grandes/SRFLP_Rubio_AnKeVa_500_03.txt";
    
    /* semilla para el generador de numero aleatorios */
    public long seed;
    
	/* Tamaño de la poblacion */
	public int pop_size = 15;
	
	/* Cantidad de hijos */
	public int offspring_size = 30;
    
    /* Seleccion de padres */
    //public Population.SelectionType pselection_type = Population.SelectionType.BEST;
   // public Population.SelectionType pselection_type = Population.SelectionType.RANDOM;
	//public Population.SelectionType pselection_type = Population.SelectionType.ROULETTE;
     public Population.SelectionType pselection_type = Population.SelectionType.TOURNAMENT;
    
	/* Cruzamiento */
    public Population.CrossoverType crossover_type = Population.CrossoverType.OX;
    //public Population.CrossoverType crossover_type = Population.CrossoverType.OPX;
	//public Population.CrossoverType crossover_type = Population.CrossoverType.PMX;
	
	
	/* Mutacion */
    public Population.MutationType mutation_type = Population.MutationType.SWAP;
    //public Population.MutationType mutation_type = Population.MutationType.TWO_OPT;
	
	/* probabilidad de mutacion */
    public Double mutation_prob = 0.1;
	
    /* Estrategia de seleccion de la nueva poblacion */
    public GeneticAlgorithm.SelectionStrategy selection_strategy = GeneticAlgorithm.SelectionStrategy.MULAMBDA;
	//public GeneticAlgorithm.SelectionStrategy selection_strategy = GeneticAlgorithm.SelectionStrategy.MUPLUSLAMBDA;
	
	/* Seleccion de la nueva poblacion */
    public Population.SelectionType gselection_type = Population.SelectionType.BEST;
	//Population.SelectionType gselection_type = Population.SelectionType.RANDOM;
	//public Population.SelectionType gselection_type = Population.SelectionType.ROULETTE;
	//public Population.SelectionType gselection_type = Population.SelectionType.TOURNAMENT;
	
	/* Numero de evaluaciones maximas */
    public int max_evaluations = 0;
    
	/* Numero de iteraciones maximas */
    public int max_iterations = 350;
    
    
    static class OptComparator implements Comparator<Option> {
        Map<String, Integer> opt = new HashMap<String, Integer>();
        
        public OptComparator() {
            int i = 0;
            opt.put("seed", i++);
            opt.put("i", i++);
            opt.put("h", i++);
        }
        
        @Override
        public int compare(Option o1, Option o2) {
            if (o1.getValue() == null || o2.getValue() == null)
                return 0;
            else
                return (opt.get(o1.getOpt()) - opt.get(o2.getOpt()));
        }
    };
    
    public AlgorithmOptions (String args[]) {
        /* Semilla para el generador de numeros aleatorios */
        seed = System.currentTimeMillis();
        /* Leer argumentos */
        readOptions(args);
        /* Setear semillas en el generador de numeros aleatorio */
        Utilities.seed = seed;
    };
    
    private void readOptions (String args[]) {
        
        if (args.length == 0) {
            System.out.println("Use el argumento `--help' para mayor informacion");
        }
        
        Options options = new Options();
        options.addOption("h", "help", false, "muestra esta ayuda");
        options.addOption("i", "instance", true, "achivo de instancia (formato TSPLIB)");
        options.addOption("p", "psize", true, "tamaño de la poblacion ]0,INT_MAX]");
        options.addOption("o", "osize", true, "cantidad de hijos a generar ]0,INT_MAX]");
        options.addOption("ps", "pselection", true, "Operador de seleccion de padres [ random | best | roulette | tournament ]");
        options.addOption("c", "crossover", true, "Operador de crossover [ox, opx, pmx]");
        options.addOption("m", "mutation", true, "Operador de mutacion [swap | two_opt]");
        options.addOption("mp", "mprobability", true, "Probabilidad de muracion [0.0,1.0]");
        options.addOption("gs", "gselection", true, "Operador de seleccion de poblacion [ random | best | roulette | tournament ]");
        options.addOption("g", "gstrategy", true, "Estrategia de seleccion de padres [ mu,lambda | mu+lambda]");
        options.addOption("e", "evaluations", true, "maximo numero de soluciones a evaluar");
        options.addOption("t", "iterations", true, "maximo numero de iteraciones a realizar");
        options.addOption("seed", true, "numero para ser usado como semilla para el generador de numeros aleatorios");
        
        
        
        CommandLine cmd = null;
        CommandLineParser parser = new BasicParser();
        try {
            cmd = parser.parse(options, args);
            System.out.println("\nLeyendo argumentos ...");
        } catch (ParseException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
        
        if (cmd.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.setSyntaxPrefix("Usage: ");
            formatter.setOptionComparator(new OptComparator());
            formatter.printHelp("Runner [OPTION]... [ARGUMENT]...", "Options:", options, "");
            System.exit(0);
        }
        
        /* Semilla */
        if (cmd.hasOption("seed")) {
            seed = Integer.parseInt(cmd.getOptionValue("seed"));
            System.out.println("-seed con valor " + seed);
        } else {
            System.out.println("-seed " + seed + " (auto)");
        }
        
        /* Archivo de instancia */
        if (cmd.hasOption("i")) {
            filename = cmd.getOptionValue("i");
            System.out.println("-i/instance filename " + filename);
        } else {
            System.out.println("-i/instance " + filename + " (auto)");
        }
        
        /* Tamaño de la poblacion */
        if (cmd.hasOption("p")) {
            pop_size = Integer.parseInt(cmd.getOptionValue("p"));
            System.out.println("-p/psize " + pop_size);
        } else {
            System.out.println("-p/psize " + pop_size + " (default)");
        }
        
        /* Tamaño de la poblacion de hijos */
        if (cmd.hasOption("o")) {
            pop_size = Integer.parseInt(cmd.getOptionValue("o"));
            System.out.println("-o/osize " + offspring_size);
        } else {
            System.out.println("-o/osize " + offspring_size + " (default)");
        }
        
        /* Seleccion de padres */
        if (cmd.hasOption("ps")){
            String value;
            value = cmd.getOptionValue("ps");
            if (value.equals("random")) {
                pselection_type = Population.SelectionType.RANDOM;
            } else if (value.equals("best")) {
            	pselection_type = Population.SelectionType.BEST;
            } else if (value.equals("roulette")) {
            	pselection_type = Population.SelectionType.ROULETTE;
            } else if (value.equals("tournament")) {
            	pselection_type = Population.SelectionType.TOURNAMENT;
            }else {
                System.err.println("Error: opcion no reconocida -ps "+ value);
                System.exit(1);
            }
            System.out.println("-ps/pselection " + pselection_type);
        } else {
        	System.out.println("-ps/pselection " + pselection_type + " (default)");
        }
        
        /* Cruzamiento */
        if (cmd.hasOption("c")){
            String value;
            value = cmd.getOptionValue("c");
            if (value.equals("pmx")) {
                crossover_type = Population.CrossoverType.PMX;
            } else if (value.equals("ox")) {
            	crossover_type = Population.CrossoverType.OX;
            } else if (value.equals("opx")) {
            	crossover_type = Population.CrossoverType.OPX;
            } else {
                System.err.println("Error: opcion no reconocida -c "+ value);
                System.exit(1);
            }
            System.out.println("-c/crossover " + crossover_type);
        } else {
        	System.out.println("-c/crossover " + crossover_type + " (default)");
        }
        
        /* Mutacion */
        if (cmd.hasOption("m")){
            String value;
            value = cmd.getOptionValue("m");
            if (value.equals("swap")) {
                mutation_type = Population.MutationType.SWAP;
            } else if (value.equals("two_opt")) {
            	mutation_type = Population.MutationType.TWO_OPT;
            } else {
                System.err.println("Error: opcion no reconocida -m "+ value);
                System.exit(1);
            }
            System.out.println("-m/mutation " + mutation_type);
        } else {
        	System.out.println("-m/mutation " + mutation_type + " (default)");
        }
        
        /* Probabilidad de mutacion */
        if (cmd.hasOption("mp")) {
            mutation_prob = Double.parseDouble(cmd.getOptionValue("mp"));
            System.out.println("-mp/mprobability " + mutation_prob);
        } else {
            System.out.println("-mp/mprobability " + mutation_prob + " (default)");
        }
        
        /* Population selection strategy */
        if (cmd.hasOption("g")){
            String value;
            value = cmd.getOptionValue("g");
            if (value.equals("mu,lambda")) {
                selection_strategy = GeneticAlgorithm.SelectionStrategy.MULAMBDA;
            } else if (value.equals("mu+lambda")) {
                selection_strategy = GeneticAlgorithm.SelectionStrategy.MUPLUSLAMBDA;
            } else {
                System.err.println("Error: opcion no reconocida -g "+ value);
                System.exit(1);
            }
            System.out.println("-g/gstrategy " + selection_strategy);
        } else {
        	System.out.println("-g/gstrategy " + selection_strategy + " (default)");
        }
        
        /* Seleccion de padres */
        if (cmd.hasOption("gs")){
            String value;
            value = cmd.getOptionValue("gs");
            if (value.equals("random")) {
                gselection_type = Population.SelectionType.RANDOM;
            } else if (value.equals("best")) {
            	gselection_type = Population.SelectionType.BEST;
            } else if (value.equals("roulette")) {
            	gselection_type = Population.SelectionType.ROULETTE;
            } else if (value.equals("tournament")) {
            	gselection_type = Population.SelectionType.TOURNAMENT;
            }else {
                System.err.println("Error: opcion no reconocida -gs "+ value);
                System.exit(1);
            }
            System.out.println("-gs/gselection " + gselection_type);
        } else {
        	System.out.println("-gs/gselection " + gselection_type + " (default)");
        }
        
        /* Numero maximo de iteraciones */
        if (cmd.hasOption("t")) {
            max_iterations = Integer.parseInt(cmd.getOptionValue("t"));
            System.out.println("-t/iterations " + max_iterations);
        } else {
            System.out.println("-t/iterations " + max_iterations + " (default)");
        }

        /* Numero maximo de evaluaciones */
        if (cmd.hasOption("e")) {
            max_evaluations = Integer.parseInt(cmd.getOptionValue("e"));
            System.out.println("-e/evaluations " + max_evaluations);
        } else {
            System.out.println("-e/evaluations " + max_evaluations+ " (default)");
        }
        
        validateOptions();
    }
    
    private void validateOptions() {
    	
    	if (max_evaluations<=0 && max_iterations <=0) {
    		System.err.println("Error: iteraciones o evaluaciones maximas deben ser > 0");
    		System.exit(1);
        }
    	if (mutation_prob > 1.0 || mutation_prob < 0.0 ) {
    		System.err.println("Error: la probabilidad de mutacion debe ser [0.0, 1.0]");
    		System.exit(1);
    	}
    	
    	if (selection_strategy == GeneticAlgorithm.SelectionStrategy.MULAMBDA &&
    			pop_size > offspring_size) {
        	  System.err.println("Error: con (mu,lambda) la poblacion (-p/psize)"
        	  		+ " debe ser >= que los hijos (-o/osize)");
          	  System.exit(1);
    	}
    	
    	if (pop_size <= 1) {
    		System.err.println("Error: tamaño de la poblacion (-p/psize)"+
    	                       " debe ser > 1");
        	System.exit(1);    		
    	}
    };

}
