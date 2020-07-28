package sekelsta.horse_colors.genetics;

import net.minecraft.entity.AgeableEntity;
import sekelsta.horse_colors.genetics.Species;

public interface IGeneticEntity {
    Genome getGenes();
    int getChromosome(String name);
    void setChromosome(String name, int val);

    java.util.Random getRNG();

    default Species getSpecies() {
        return null;
    }

    boolean isMale();

    void setMale(boolean gender);

    int getRebreedTicks();

    int getBirthAge();

    // Return true if successful, false otherwise
    // Reasons for returning false could be if the animal is male or the mate is female
    // (This prevents spawn eggs from starting a pregnancy.)
    boolean setPregnantWith(AgeableEntity child, AgeableEntity otherParent);
}
