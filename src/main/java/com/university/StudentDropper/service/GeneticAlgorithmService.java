package com.university.StudentDropper.service;

import com.university.StudentDropper.exception.InsufficientStudentsException;
import com.university.StudentDropper.genAlgorithm.Chromosome;
import com.university.StudentDropper.model.Destination;
import com.university.StudentDropper.model.Preference;
import com.university.StudentDropper.model.Student;
import com.university.StudentDropper.model.StudentAllocation;
import com.university.StudentDropper.repository.DestinationRepository;
import com.university.StudentDropper.repository.PreferenceRepository;
import com.university.StudentDropper.repository.StudentAllocationRepository;
import com.university.StudentDropper.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GeneticAlgorithmService {

    private final Random rand = new Random();

    private static final int POPULATION_SIZE = 100; // This is just a sample size, adjust as necessary
    private static final int PARENTS_COUNT = 10;    // The number of parents to select for mating
    public static final int PREFERENCE_SCALE = 4; // Scale for ranking preferences, assuming 1 is high and 4 is low
    private static final int MAX_GENERATIONS = 100; // Maximum number of generations before stopping the GA
    private static final int BONUS_FOR_TOP_PREFERENCE = PREFERENCE_SCALE * 10;

    private final PreferenceRepository preferenceRepository;
    private final DestinationRepository destinationRepository;
    private final StudentRepository studentRepository;
    private final StudentAllocationRepository studentAllocationRepository;

    public GeneticAlgorithmService(PreferenceRepository preferenceRepository, DestinationRepository destinationRepository, StudentRepository studentRepository, StudentAllocationRepository studentAllocationRepository) {
        this.preferenceRepository = preferenceRepository;
        this.destinationRepository = destinationRepository;
        this.studentRepository = studentRepository;
        this.studentAllocationRepository = studentAllocationRepository;
    }

    public void startAllocationProcess() {
        log.info("Starting allocation process");
        List<Destination> destinations = destinationRepository.findAll();
        List<Student> studentsWithPreference = studentRepository.findAll().stream()
                .filter(student -> !preferenceRepository.findByStudent(student).isEmpty())
                .toList();

        if (studentsWithPreference.size() < 10) {
            throw new InsufficientStudentsException("Process requires at least 10 students with preferences to make sure Genetic Algorithm can evaluate fitness function which designed for larger population.");
        }

        if (hasAllocatedStudents()) {
            throw new IllegalStateException("Students already allocated.");
        }

        Chromosome bestSolution = runGeneticAlgorithm(studentsWithPreference, destinations);
        saveAllocationResults(bestSolution);
    }

    public Chromosome runGeneticAlgorithm(List<Student> students, List<Destination> destinations) {
        log.info("Running GeneticAlgorithm");
        List<Chromosome> population = generateInitialPopulation(students, destinations);

        int currentGeneration = 0;
        int stagnationCount = 0;
        int maxStagnation = 20; // Number of generations without improvement before termination
        double bestFitness = Double.NEGATIVE_INFINITY;

        while (currentGeneration < MAX_GENERATIONS && stagnationCount < maxStagnation) {
            for (Chromosome chromosome : population) {
                calculateFitness(chromosome);
            }

            population.sort(Comparator.comparingDouble(Chromosome::getFitness).reversed());
            double currentBestFitness = population.get(0).getFitness();

            if (currentBestFitness > bestFitness) {
                bestFitness = currentBestFitness;
                stagnationCount = 0;
            } else {
                stagnationCount++;
            }

            List<Chromosome> parents = selectParents(population);

            List<Chromosome> newPopulation = new ArrayList<>();
            while (newPopulation.size() < POPULATION_SIZE) {
                Chromosome parent1 = parents.get(rand.nextInt(parents.size()));
                Chromosome parent2 = parents.get(rand.nextInt(parents.size()));
                Chromosome offspring = crossover(parent1, parent2, destinations);
                mutate(offspring);
                newPopulation.add(offspring);
            }

            population = newPopulation;
            currentGeneration++;
        }

        population.sort(Comparator.comparingDouble(Chromosome::getFitness).reversed());
        return population.get(0);
    }

    private List<Chromosome> generateInitialPopulation(List<Student> students, List<Destination> destinations) {
        log.info("Generating initial population");
        List<Chromosome> population = new ArrayList<>();

        Map<Student, Destination> heuristicAllocation = new HashMap<>();
        Map<Destination, Integer> destinationCounts = new HashMap<>();
        for (Destination dest : destinations) {
            destinationCounts.put(dest, 0);
        }

        for (Student student : students) {
            boolean assigned = false;
            List<Preference> sortedPreferences = student.getPreferences().stream()
                    .sorted(Comparator.comparingInt(Preference::getRank))
                    .toList();

            for (Preference preference : sortedPreferences) {
                Destination dest = preference.getDestination();
                if (destinationCounts.get(dest) < dest.getMaxNumStudents()) {
                    heuristicAllocation.put(student, dest);
                    destinationCounts.put(dest, destinationCounts.get(dest) + 1);
                    assigned = true;
                    break;
                }
            }

            if (!assigned) {
                for (Destination dest : destinations) {
                    if (destinationCounts.get(dest) < dest.getMaxNumStudents()) {
                        heuristicAllocation.put(student, dest);
                        destinationCounts.put(dest, destinationCounts.get(dest) + 1);
                        break;
                    }
                }
            }
        }
        population.add(new Chromosome(heuristicAllocation));

        for (int i = 1; i < POPULATION_SIZE; i++) {
            Map<Student, Destination> allocation = new HashMap<>();
            destinationCounts.clear();
            for (Destination dest : destinations) {
                destinationCounts.put(dest, 0);
            }

            for (Student student : students) {
                Destination destination;
                do {
                    destination = destinations.get(rand.nextInt(destinations.size()));
                } while (destinationCounts.get(destination) >= destination.getMaxNumStudents());

                allocation.put(student, destination);
                destinationCounts.put(destination, destinationCounts.get(destination) + 1);
            }
            population.add(new Chromosome(allocation));
        }
        return population;
    }

    private double calculateFitness(Chromosome chromosome) {
        log.info("Calculating fitness");
        double fitness = 0.0;
        for (Map.Entry<Student, Destination> entry : chromosome.getAllocation().entrySet()) {
            Student student = entry.getKey();
            Destination destination = entry.getValue();
            int preferenceRank = student.getPreferencesRank(destination);

            if (preferenceRank > PREFERENCE_SCALE) {
                fitness -= 10;
            } else {
                fitness += Math.pow(PREFERENCE_SCALE + 1 - preferenceRank, 2);
                if (preferenceRank == 1) {
                    fitness += BONUS_FOR_TOP_PREFERENCE;
                }
            }
        }
        chromosome.setFitness(fitness);
        return fitness;
    }

    private List<Chromosome> selectParents(List<Chromosome> population) {
        List<Chromosome> parents = new ArrayList<>();
        double totalFitness = population.stream().mapToDouble(Chromosome::getFitness).sum();
        for (int i = 0; i < PARENTS_COUNT; i++) {
            double randFitness = rand.nextDouble() * totalFitness;
            double cumulativeFitness = 0;
            for (Chromosome chromosome : population) {
                cumulativeFitness += chromosome.getFitness();
                if (cumulativeFitness >= randFitness) {
                    parents.add(chromosome);
                    break;
                }
            }
        }
        return parents;
    }

    private Chromosome crossover(Chromosome parent1, Chromosome parent2, List<Destination> destinations) {
        log.info("Starting Crossover");
        Map<Student, Destination> newAllocation = new HashMap<>();
        List<Student> students = new ArrayList<>(parent1.getAllocation().keySet());
        Collections.shuffle(students);

        for (Student student : students) {
            Destination destination1 = parent1.getAllocation().get(student);
            Destination destination2 = parent2.getAllocation().get(student);

            Destination chosenDestination = rand.nextBoolean() ? destination1 : destination2;

            if (Collections.frequency(newAllocation.values(), chosenDestination) < chosenDestination.getMaxNumStudents()) {
                newAllocation.put(student, chosenDestination);
            } else {
                Destination alternativeDestination = findAvailableDestination(newAllocation, destinations);
                if (alternativeDestination != null) {
                    newAllocation.put(student, alternativeDestination);
                } else {
                    log.warn("No available destinations for student {}", student.getId());
                }
            }
        }

        return new Chromosome(newAllocation);
    }

    private Destination findAvailableDestination(Map<Student, Destination> allocation, List<Destination> destinations) {
        for (Destination dest : destinations) {
            if (Collections.frequency(allocation.values(), dest) < dest.getMaxNumStudents()) {
                return dest;
            }
        }
        return null;
    }

    private void mutate(Chromosome chromosome) {
        log.info("Starting mutating...");
        double mutationRate = 0.05; // 5% mutation rate
        List<Student> students = new ArrayList<>(chromosome.getAllocation().keySet());

        Map<Destination, Long> destinationCounts = chromosome.getAllocation().values().stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        List<Destination> destinations = destinationRepository.findAll();

        for (Student student : students) {
            if (rand.nextDouble() < mutationRate) {
                List<Destination> availableDestinations = destinations.stream()
                        .filter(d -> destinationCounts.getOrDefault(d, 0L) < d.getMaxNumStudents())
                        .toList();

                if (!availableDestinations.isEmpty()) {
                    Destination newDestination = availableDestinations.get(rand.nextInt(availableDestinations.size()));
                    Destination oldDestination = chromosome.getAllocation().put(student, newDestination);

                    destinationCounts.put(newDestination, destinationCounts.getOrDefault(newDestination, 0L) + 1);
                    destinationCounts.put(oldDestination, destinationCounts.get(oldDestination) - 1);
                } else {
                    log.warn("No available destinations to allocate for mutation.");
                }
            }
        }
    }

    // Deprecated
    private boolean shouldTerminate(int currentGeneration, int maxGenerations) {
        log.info("Terminating GeneticAlgorithm");
        return currentGeneration >= maxGenerations;
    }

    private void saveAllocationResults(Chromosome bestSolution) {
        log.info("Saving allocation results");
        List<StudentAllocation> allocationsToSave = bestSolution.getAllocation().entrySet().stream()
                .map(entry -> {
                    Student student = entry.getKey();
                    Destination destination = entry.getValue();
                    int preferenceRank = student.getPreferencesRank(destination);

                    StudentAllocation allocation = new StudentAllocation();
                    allocation.setStudent(student);
                    allocation.setDestination(destination);
                    allocation.setStudentName(student.getName() + " " + student.getSurname());
                    allocation.setAllocatedDestination(destination.getName());
                    allocation.setRank(preferenceRank);
                    return allocation;
                })
                .toList();

        studentAllocationRepository.saveAll(allocationsToSave);
    }

    public boolean hasAllocatedStudents() {
        List<StudentAllocation> allocations = studentAllocationRepository.findAll();
        return !allocations.isEmpty();
    }}
