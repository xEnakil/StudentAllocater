package com.university.StudentDropper.genAlgorithm;

import com.university.StudentDropper.model.Destination;
import com.university.StudentDropper.model.Student;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Chromosome {
    private Map<Student, Destination> allocation;
    private double fitness;

    public Chromosome(Map<Student, Destination> allocation) {
        this.allocation = allocation;
        this.fitness = 0.0;
    }

    public Map<Student, Destination> getAllocation() {
        return allocation;
    }

    public void setAllocation(Map<Student, Destination> allocation) {
        this.allocation = allocation;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }
}
