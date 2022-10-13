package sekelsta.horse_colors.entity;
import net.minecraft.world.entity.animal.horse.*;

import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import sekelsta.horse_colors.breed.Breed;
import sekelsta.horse_colors.breed.BreedManager;
import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.entity.genetics.EquineGenome.Gene;
import sekelsta.horse_colors.entity.genetics.Species;
import sekelsta.horse_colors.util.Util;

public class DonkeyGeneticEntity extends AbstractHorseGenetic {
    private static final ResourceLocation LOOT_TABLE = new ResourceLocation("minecraft", "entities/donkey");

    public DonkeyGeneticEntity(EntityType<? extends DonkeyGeneticEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public ResourceLocation getDefaultLootTable() {
        return this.LOOT_TABLE;
    }

    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        return SoundEvents.DONKEY_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        super.getDeathSound();
        return SoundEvents.DONKEY_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        super.getHurtSound(damageSourceIn);
        return SoundEvents.DONKEY_HURT;
    }

    @Override
    public boolean fluffyTail() {
        return false;
    }

    @Override
    public boolean longEars() {
        return true;
    }

    @Override
    public boolean thinMane() {
        return true;
    }

    @Override
    public Species getSpecies() {
        return Species.DONKEY;
    }

   /**
    * Returns true if the mob is currently able to mate with the specified mob.
    */
    @Override
    public boolean canMate(Animal otherAnimal) {
        if (otherAnimal == this)
        {
            return false;
        }
        if (otherAnimal instanceof AbstractHorseGenetic) {
            if (!this.isOppositeGender((AbstractHorseGenetic)otherAnimal)) {
                return false;
            }
        }
        if (otherAnimal instanceof DonkeyGeneticEntity 
                || otherAnimal instanceof HorseGeneticEntity
                || otherAnimal instanceof Donkey 
                || otherAnimal instanceof Horse)
        {
            return this.canParent() && Util.horseCanMate((AbstractHorse)otherAnimal);
        }
        else
        {
            return false;
        }
    }

    // Helper function for createChild that creates and spawns an entity of the 
    // correct species
    @Override
    public AbstractHorse getChild(ServerLevel world, AgeableMob ageable)
    {
        if (ageable instanceof Horse) {
            return EntityType.MULE.create(this.level);
        }
        else if (ageable instanceof Donkey) {
            return EntityType.DONKEY.create(this.level);
        }
        return null;
    }

    @Override
    public Breed getDefaultBreed() {
        return BreedManager.DONKEY.getBreed("default_donkey");
    }

    @Override
    public Collection<Breed<Gene>> getBreeds() {
        return BreedManager.DONKEY.getAllBreeds();
    }

    @Override
    public int getPopulation() {
        return 40000000;
    }
}
