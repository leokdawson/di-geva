/*
Grammatical Evolution in Java
Release: GEVA-v2.0.zip
Copyright (C) 2008 Michael O'Neill, Erik Hemberg, Anthony Brabazon, Conor Gilligan 
Contributors Patrick Middleburgh, Eliott Bartley, Jonathan Hugosson, Jeff Wrigh

Separate licences for asm, bsf, antlr, groovy, jscheme, commons-logging, jsci is included in the lib folder. 
Separate licence for rieps is included in src/com folder.

This licence refers to GEVA-v2.0.

This software is distributed under the terms of the GNU General Public License.


This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
/>.
*/

package Operator.Operations;

import Exceptions.BadParameterException;
import Individuals.FitnessPackage.BasicFitness;
import Individuals.FitnessPackage.Fitness;
import Individuals.*;
import Mapper.GEGrammar;
import Util.Constants;
import Util.Random.RandomNumberGenerator;
import Util.Random.Stochastic;
import java.util.Arrays;

import java.util.List;
import java.util.Properties;

/**
 * Create an Individual with a randomly generated chromosome
 * @author erikhemberg
 */
public class RandomInitialiser implements CreationOperation, Stochastic {

    protected RandomNumberGenerator rng;
    protected GEGrammar grammar;
    protected int initChromSize;

    /**
     * New instance
     * @param rng random number generator
     * @param g grammatical evolution grammar
     * @param initChromSize initial input size (Chromosome length)
     */
    public RandomInitialiser(RandomNumberGenerator rng, GEGrammar g, int initChromSize) {
        this.rng = rng;
        this.grammar = g;
        this.initChromSize = initChromSize;
    }

    /**
     * New instance
     * @param rng random number generator
     * @param g grammatical evolution grammar
     * @param p properties
     */
    public RandomInitialiser(RandomNumberGenerator rng, GEGrammar g, Properties p) {
        this.rng = rng;
        this.grammar = g;
        this.setProperties(p);
    }

    public void setProperties(Properties p) {
        int value;
        String std = Constants.DEFAULT_CHROMOSOME_SIZE;
        String key = Constants.INITIAL_CHROMOSOME_SIZE;
        try {
            value = Integer.parseInt(p.getProperty(key, std));
            if (value < 1) {
                throw new BadParameterException(key);
            }
        } catch (Exception e) {
            value = Integer.parseInt(std);
            System.out.println(e + " for " + key + " using default: " + value);
        }
        this.initChromSize = value;
    }

    public RandomNumberGenerator getRNG() {
        return this.rng;
    }

    public void setRNG(RandomNumberGenerator rng) {
        this.rng = rng;
    }

    /** Creates an Individual
     *  @return A new individual
     **/
    public Individual createIndividual() {
        GEGrammar gram = GEGrammar.getGrammar(this.grammar);
        Phenotype phenotype = new Phenotype();
        
        int[] codons = new int[this.initChromSize]; //literally just creates an (zeroed) array of size initChromSize
        GEChromosome chrom = new GEChromosome(this.initChromSize, codons);  //creates (zeroed) chromosome of size initChromSize
        //Set the maximum chromosome length
        chrom.setMaxChromosomeLength(gram.getMaxChromosomeLengthByDepth()); //not exactly sure yet what this is doing, or more so, how it is doing it
        GEChromosome chrom_b = new GEChromosome(this.initChromSize, codons);
        chrom_b.setMaxChromosomeLength(gram.getMaxChromosomeLengthByDepth());
        
        Genotype genotype = new Genotype(1, chrom);
        Genotype genotype_b = new Genotype(1, chrom_b);
        
        Fitness fitness = new BasicFitness();
        //this.doOperation(ind);
        GEIndividual individual = new GEIndividual(gram, phenotype, genotype, genotype_b, fitness); //individual created, with two chromosomes
//      Genotypes have been created but not actually initialised with any sort of integers:  
//        System.out.println(genotype);
//        System.out.println(genotype_b);
        return individual;
    }

    /**
     * Set 2 different integer chromosome of initChromSize filled with random integers
     * in the incoming individual.
     * Not sure yet were this is actually being called from during program execution
     * @param operand Individual to get the new chromosome
     **/
    public void doOperation(Individual operand) {
        //creates int array of same length as genotype/chromosome, filled with random integers up to a maximum value
        int[] chr = new int[operand.getGenotype().get(0).getLength()];
        for (int i = 0; i < operand.getGenotype().get(0).getLength(); i++) {
            chr[i] = rng.nextInt(Integer.MAX_VALUE);
        }
        //set the new genotype
        ((GEChromosome) operand.getGenotype().get(0)).setAll(chr);
        
        int[] chr_b = new int[operand.getGenotype_b().get(0).getLength()];
        for (int i = 0; i < operand.getGenotype_b().get(0).getLength(); i++) {
            chr_b[i] = rng.nextInt(Integer.MAX_VALUE);
        }
        ((GEChromosome) operand.getGenotype_b().get(0)).setAll(chr_b);
        
        //print out the 2 chromosomes in our new individual
        System.out.println(operand.getGenotype());
        System.out.println(Arrays.toString(chr_b));
        System.out.println(operand.getGenotype_b());
    }

    /**
     * Calls doOperation(Individual operand)
     * @param operands list of individuals
     */
    public void doOperation(List<Individual> operands) {
        for (Individual operand : operands) {
            this.doOperation(operand);
        }
    }
}
