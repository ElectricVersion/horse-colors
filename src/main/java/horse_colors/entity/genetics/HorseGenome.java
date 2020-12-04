package sekelsta.horse_colors.entity.genetics;

import java.util.*;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import sekelsta.horse_colors.HorseColors;
import sekelsta.horse_colors.client.renderer.TextureLayer;
import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.entity.*;
import sekelsta.horse_colors.entity.genetics.breed.Breed;
import sekelsta.horse_colors.util.RandomSupplier;
import sekelsta.horse_colors.util.Util;

public class HorseGenome extends Genome {

    /* Extension is the gene that determines whether black pigment can extend
    into the hair, or only reach the skin. 0 is red, 1 can have black. */

    /* Agouti controls where black hairs are placed. 0 is for black, 1 for seal 
    brown, 2 for bay, and, if I ever draw the pictures, 3 for wild bay. They're
    in order of least dominant to most. */

    /* Dun dilutes pigment (by restricting it to a certain part of each hair 
    shaft) and also adds primative markings such as a dorsal stripe. It's
    dominant, so dun (wildtype) is 11 or 10, non-dun1 (with dorsal stripe) is
    01, and non-dun2 (without dorsal stripe) is 00. ND1 and ND2 are
    codominate: a horse with both will have a fainter dorsal stripe. */

    /* Gray causes rapid graying with age. Here, it will simply mean the
    horse is gray. It is epistatic to every color except white. Gray is 
    dominant, so 0 is for non-gray, and 1 is for gray. */

    /* Cream makes red pigment a lot lighter and also dilutes black a 
    little. It's incomplete dominant, so here 0 is wildtype and 1 is cream. */

    /* Silver makes black manes and tails silvery, while lightening a black
    body color to a more chocolatey one, sometimes with dapples. Silver
    is dominant, so 0 for wildtype, 1 for silver. */

    /* Liver recessively makes chestnut darker. 0 for liver, 1 for non-liver. */

    /* Either flaxen gene makes the mane lighter; both in combination
    make the mane almost white. They're both recessive, so 0 for flaxen,
    1 for non-flaxen. */

    /* Sooty makes a horse darker, sometimes smoothly or sometimes in a 
    dapple pattern. */

    /* Mealy turns some red hairs to white, generally on the belly or
    undersides. It's a polygenetic trait. */
    public static final ImmutableList<String> genes = ImmutableList.of(
        "extension", 
        "agouti", 
        "dun", 
        "gray", 
        "cream", 
        "liver", 
        "flaxen1", 
        "flaxen2", 
        "dapple", 
        "sooty1", 
        "sooty2", 
        "sooty3", 
        // I'm treating this as the agouti promoter region responsible for 
        // white bellied agouti in mice
        "light_belly",
        "mealy1", 
        "mealy2", 
        "KIT", 
        "MITF", 
        "leopard",
        "PATN1", 
        "PATN2", 
        "PATN3", 
        "gray_suppression",
        "slow_gray1", 
        "slow_gray2", 
        "slow_gray3",
        "white_star",
        "white_forelegs",
        "white_hindlegs",
        "gray_melanoma",
        "gray_mane1",
        "gray_mane2",
        "rufous",
        "dense",
        "champagne", // TODO
        "cameo",
        "ivory",
        "donkey_dark",
        "cross",
        "reduced_points",
        "light_legs",
        "less_light_legs",
        "donkey_dun",
        "flaxen_boost",
        "light_dun",
        "marble",
        "leopard_suppression",
        "leopard_suppression2",
        "PATN_boost1",
        "PATN_boost2",
        "PAX3", 
        "white_suppression", 
        "frame", 
        "silver", 
        "dark_red",
        "LCORL",
        "HMGA2",
        "mushroom",
        "speed0",
        "speed1",
        "speed2",
        "speed3",
        "speed4",
        "speed5",
        "speed6",
        "speed7",
        "speed8",
        "speed9",
        "speed10",
        "speed11",
        "athletics0",
        "athletics1",
        "athletics2",
        "athletics3",
        "athletics4",
        "athletics5",
        "athletics6",
        "athletics7",
        "jump0",
        "jump1",
        "jump2",
        "jump3",
        "jump4",
        "jump5",
        "jump6",
        "jump7",
        "jump8",
        "jump9",
        "jump10",
        "jump11",
        "health0",
        "health1",
        "health2",
        "health3",
        "health4",
        "health5",
        "health6",
        "health7",
        "health8",
        "health9",
        "health10",
        "health11",
        "immune0",
        "immune1",
        "immune2",
        "immune3",
        "immune4",
        "immune5",
        "immune6",
        "immune7",
        "mhc0",
        "mhc1",
        "mhc2",
        "mhc3",
        "mhc4",
        "mhc5",
        "mhc6",
        "mhc7",
        "leg_stripes",   // TODO
        "stripe_spacing" // TODO
    );

    public HorseGenome(Species species, IGeneticEntity entityIn) {
        super(species, entityIn, new RandomSupplier(ImmutableList.of("leg_white", "face_white", "star_choice", "roan_density", "liver_darkness", "shade")));
    }

    public HorseGenome(Species species) {
        this(species, new FakeGeneticEntity());
    }

    @Override
    public ImmutableList<String> listGenes() {
        return genes;
    }

    @Override
    public List<Genome.Linkage> listLinkages() {
        List<Genome.Linkage> linkages = super.listLinkages();
        // It doesn't matter if some appear twice, the last will be used
        linkages.add(new Genome.Linkage("extension", 0.015f));
        linkages.add(new Genome.Linkage("KIT"));

        linkages.add(new Genome.Linkage("agouti", 0.0f));
        linkages.add(new Genome.Linkage("light_belly"));

        for (int i = 0; i < 7; ++i) {
            linkages.add(new Genome.Linkage("mhc" + i, 0.2f));
        }
        linkages.add(new Genome.Linkage("mhc7"));

        return linkages;
    }

    /* For named genes, this returns the number of bits needed to store one allele. 
    For stats, this returns the number of genes that contribute to the stat. */
    @Override
    public int getGeneSize(String gene)
    {
        switch(gene) 
        {
            case "KIT": return 6;

            case "MITF":
            case "PAX3": return 4;

            case "cream":
            case "extension":
            case "agouti":
            case "LCORL":
            case "HMGA2": return 3;

            case "dun": return 2;

            default: return 1;
        }
    }

    @Deprecated
    public void printGeneLocations() {
        for (String gene : genes) {
            System.out.println(gene + ": size=" + getGeneSize(gene) + ", pos=" + getGenePos(gene) + ", chr=" + getGeneChromosome(gene));
        }
    }

    public void printGeneData() {
        String g = entity.getGeneData();
        String genedebug = "";
        for (int i = 0; i < g.length(); ++i) {
            genedebug += (short)g.charAt(i) + " ";
        }
        System.out.println(genedebug);
    }

    public boolean isChestnut()
    {
        return this.isHomozygous("extension", HorseAlleles.E_RED);
    }

    public boolean hasCream() {
        return this.hasAllele("cream", HorseAlleles.CREAM);
    }

    public boolean isPearl() {
        return this.isHomozygous("cream", HorseAlleles.PEARL);
    }

    public boolean isDoubleCream() {
        return this.isHomozygous("cream", HorseAlleles.CREAM) 
            || this.isHomozygous("cream", HorseAlleles.SNOWDROP)
            || (this.hasAllele("cream", HorseAlleles.CREAM)
                && this.hasAllele("cream", HorseAlleles.SNOWDROP));
    }

    public boolean isCreamPearl() {
        return (this.hasAllele("cream", HorseAlleles.CREAM)
                || this.hasAllele("cream", HorseAlleles.SNOWDROP))
            && this.hasAllele("cream", HorseAlleles.PEARL);
    }

    public boolean isMushroom() {
        return this.isHomozygous("mushroom", 1);
    }

    public boolean isSilver() {
        return this.hasAllele("silver", HorseAlleles.SILVER);
    }

    public boolean isGray() {
        return this.hasAllele("gray", HorseAlleles.GRAY);
    }

    // Arbitrarily decide homozygous donkey nondun breaks horse dun.
    // Obviously there's no way to check this in real life except by
    // theorizing once we know more about donkey dun.
    public boolean isDun() {
        return this.hasAllele("donkey_dun", HorseAlleles.DONKEY_DUN)
            && (this.hasAllele("dun", HorseAlleles.DUN)
                || this.isHomozygous("dun", HorseAlleles.DUN_OTHER));
    }

    // Whether the horse shows primitive markings such as the dorsal stripe.
    public boolean hasStripe() {
        // Some confusion here to account for "donkey dun" mules
        if (isHomozygous("dun", HorseAlleles.NONDUN2)) {
            return false;
        }
        if (isHomozygous("donkey_dun", HorseAlleles.DONKEY_NONDUN)) {
            return false;
        }
        if (hasAllele("dun", HorseAlleles.DUN)) {
            return true;
        }
        if (hasAllele("donkey_dun", HorseAlleles.DONKEY_DUN)) {
            return true;
        }
        if (hasAllele("dun", HorseAlleles.NONDUN2)) {
            return false;
        }
        return hasAllele("dun", HorseAlleles.DUN_OTHER);
    }

    public boolean isMealy() {
        return (this.getAllele("light_belly", 0) == HorseAlleles.MEALY 
                    && this.getAllele("agouti", 0) != HorseAlleles.A_BLACK)
                || (this.getAllele("light_belly", 1) == HorseAlleles.MEALY 
                    && this.getAllele("agouti", 1) != HorseAlleles.A_BLACK);
    }

    // The MC1R ("extension") gene seems to be associated with white
    // patterning. For now I assume this is caused by MC1R itself,
    // but if it turns out to be a different gene that's just very
    // closely linked, I can change this.
    public boolean hasMC1RWhiteBoost() {
        return isChestnut();
    }

    public boolean isTobiano() {
        return HorseAlleles.isTobianoAllele(getAllele("KIT", 0))
            || HorseAlleles.isTobianoAllele(getAllele("KIT", 1));
    }

    public boolean isWhite() {
        return this.hasAllele("KIT", HorseAlleles.KIT_DOMINANT_WHITE)
            || this.isLethalWhite()
            || this.isHomozygous("KIT", HorseAlleles.KIT_SABINO1)
            || (this.hasAllele("KIT", HorseAlleles.KIT_SABINO1)
                && (this.hasAllele("frame", HorseAlleles.FRAME)
                    || this.isHomozygous("MITF", HorseAlleles.MITF_SW1))
                && this.isTobiano());
    }

    public boolean showsLegMarkings() {
        return !isWhite() && !isTobiano();
    }

    public boolean isDappleInclined() {
        // Recessive so that mules are not dappled
        return this.isHomozygous("dapple", 1);
    }

    public boolean isLethalWhite() {
        return this.isHomozygous("frame", HorseAlleles.FRAME);
    }

    public boolean isEmbryonicLethal() {
        return this.isHomozygous("KIT", HorseAlleles.KIT_DOMINANT_WHITE);
    }

    public boolean hasERURiskFactor() {
        return this.getAllele("mhc1", 0) % 4 == 3 
                && this.getAllele("mhc1", 1) % 4 == 3;
    }

    public int getSootyLevel() {
        // sooty1 and 2 dominant, 3 recessive
        return 1 + getMaxAllele("sooty1") + getMaxAllele("sooty2") 
                        - getMaxAllele("sooty3");
    }

    // Number of years to turn fully gray
    public float getGrayRate() {
        // Starting age should vary from around 1 to 5 years
        // Ending age from 3 to 20
        int gray = countAlleles("gray", HorseAlleles.GRAY);
        float rate = 3f * (3 - gray);
        if (this.isHomozygous("slow_gray1", 1)) {
            rate *= 1.5f;
        }
        else if (this.hasAllele("slow_gray1", 1)) {
            rate *= 1.2f;
        }

        if (this.hasAllele("slow_gray2", 1)) {
            rate *= 1.3f;
        }

        if (this.isHomozygous("slow_gray3", 1)) {
            rate *= 1.2f;
        }

        if (this.hasAllele("gray_mane1", 1)) {
            rate *= 1.2f;
        }
        return rate;
    }

    // Number of years for the mane and tail to turn fully gray
    public float getGrayManeRate() {
        float rate = getGrayRate();
        if (this.hasAllele("gray_mane1", 0)) {
            rate *= 0.9f;
        }

        if (this.isHomozygous("gray_mane2", 0)) {
            rate *= 0.9f;
        }
        // Adjust so mane grays slightly before the body finishes
        return rate * 17f / 19f;
    }

    public float getImmuneHealth() {
        float scale = 8f;
        // Sum of heterozygosity of the 16 immune diversity genes
        int diffs = 0;
        for (int i = 0; i < 8; ++i) {
            if (getAllele("immune" + i, 0) != getAllele("immune" + i, 1)) {
                diffs++;
            }
            if (getAllele("mhc" + i, 0) != getAllele("mhc" + i, 1)) {
                diffs++;
            }
        }
        // 16 genes each with 16 alleles, makes total expected heterozygosity 15
        // But horses from older versions had fewer, and allow for some bad 
        // luck, so use 12
        float heterozygosity = diffs / 12f;
        // Adjust so super outbreeding gives 1.25 advantage, not double advantage
        if (heterozygosity > 1f) {
            heterozygosity = 0.25f * (heterozygosity - 1) + 1;
        }
        return scale * heterozygosity;
    }

    public float getGrayHealthLoss() {
        // Count zygosity, mitigate from protective gene
        // Agouti may also have an effect on prevalence/severity,
        // but I'm not sufficiently convinced
        float base = countAlleles("gray", HorseAlleles.GRAY);
        if (isHomozygous("gray_melanoma", 0)) {
            base -= 1f;
        }
        // Horses without melanocytes in the skin should be much
        // less likely to get melanomas
        if (isWhite()) {
            base -= 1.5f;
        }
        return Math.max(0f, base);
    }

    public float getSilverHealthLoss() {
        if (isHomozygous("silver", HorseAlleles.SILVER)) {
            return 1f;
        }
        else if (hasAllele("silver", HorseAlleles.SILVER)) {
            return 0.5f;
        }
        else {
            return 0;
        }
    }

    public float getDeafHealthLoss() {
        if (HorsePatternCalculator.hasPigmentInEars(this)) {
            return 0f;
        }
        else {
            return 1f;
        }
    }

    public float getERUHealthLoss() {
        if (hasERURiskFactor()) {
            return 0.5f * countAlleles("leopard", HorseAlleles.LEOPARD);
        }
        return 0;
    }

    public float getBaseHealth() {
        if (HorseConfig.GENETICS.enableHealthEffects.get()) {
            return -getGrayHealthLoss() - getSilverHealthLoss() - getDeafHealthLoss() - getERUHealthLoss();
        }
        else {
            return 0;
        }
    }

    public float getHealth() {
        // Default horse health ranges from 15 to 30, but ours goes from
        // 15 to 31
        float healthStat = this.sumGenes("health", 0, 4)
                            + this.sumGenes("health", 4, 8)
                            + this.sumGenes("health", 8, 12)
                            + this.getImmuneHealth();
        float maxHealth = 15.0F + healthStat * 0.5F;
        maxHealth += this.getBaseHealth();
        return maxHealth;
    }

    // A special case because it has two different alleles
    public int countW20() {
        return countAlleles("KIT", HorseAlleles.KIT_W20) 
                + countAlleles("KIT", HorseAlleles.KIT_TOBIANO_W20);
    }

    // Genetic-based size, which unlike age-based size should affect the hitbox
    // This is a multiplier for both width and height, so adjust for that when
    // calculating weight.
    public float getGeneticScale() {
        if (!HorseConfig.COMMON.enableSizes.get()) {
            return 1f;
        }
        float size = 1f;
        size *= this.entity.isMale() ? 1.01f : 0.99f;
        // LCORL is based off of information from the Center for Animal Genetics
        // They list T/T warmbloods as ~159 cm, T/C warmbloods as ~164 cm, and
        // C/C warmbloods as ~169 cm.
        // I've assumed the relationship is multiplicative.
        // 0 is T, 1 is C
        for (int i = 0; i < this.countAlleles("LCORL", 1); ++i) {
            size *= 1.03f;
        }
        // HMGA2 is based off of information from the Center for Animal Genetics
        // They list G/G ponies as 104 cm tall at the withers, G/A as 98 cm,
        // and A/A as 84 cm.
        // Again, I'm assuming the relationship is multiplicative.
        // 0 is G, 1 is A
        if (this.isHomozygous("HMGA2", 1)) {
            size *= 0.81f;
        }
        else if (this.hasAllele("HMGA2", 1)) {
            size *= 0.94f;
        }
        // Donkeys are smaller
        if (this.species == Species.DONKEY) {
            size *= 0.9f;
        }
        // Weighted arithmetic average with mother's size
        size = (float)(Math.pow(size, 0.97) * Math.pow(entity.getMotherSize(), 0.03));
        return size;
    }

    // Return true if the client needs to know the age to render properly,
    // aside from just whether the animal is a child
    public boolean clientNeedsAge() {
        return isGray() 
            || (HorseConfig.GROWTH.growGradually.get() 
                && entity instanceof AbstractHorseGenetic 
                && ((AbstractHorseGenetic)entity).isChild());
    }

    public int getAge() {
        if (entity instanceof AbstractHorseGenetic) {
            return ((AbstractHorseGenetic)entity).getDisplayAge();
        }
        else {
            return 0;
        }
    }

    // Distribution should be a series of floats increasing from
    // 0.0 to 1.0, where the probability of choosing allele i is
    // the chance that a random uniform number between 0 and 1
    // is greater than distribution[i-1] but less than distribution[i].
    protected int chooseRandomAllele(List<Float> distribution) {
        float n = this.entity.getRand().nextFloat();
        for (int i = 0; i < distribution.size(); ++i) {
            if (n < distribution.get(i)) {
                return i;
            }
        }
        // In case of floating point rounding errors
        return distribution.size() - 1;
    }

    protected void randomizeGenes(Map<String, List<Float>> map) {
        for (String gene : genes) {
            if (map.containsKey(gene)) {
                List<Float> distribution = map.get(gene);
                int allele0 = chooseRandomAllele(distribution);
                int allele1 = chooseRandomAllele(distribution);
                setAllele(gene, 0, allele0);
                setAllele(gene, 1, allele1);
            }
            else {
                HorseColors.logger.debug(gene + " is not in the given map");
                setAllele(gene, 0, 0);
                setAllele(gene, 1, 0);
            }
        }
    }

    /* Make the horse have random genetics. */
    public void randomize(Breed breed)
    {
        randomizeGenes(breed.colors);

        // Replace lethal white overos with heterozygotes
        if (isHomozygous("frame", HorseAlleles.FRAME))
        {
            setAllele("frame", 0, 0);
        }

        // Homozygote dominant whites will be replaced with heterozygotes
        if (isHomozygous("KIT", HorseAlleles.KIT_DOMINANT_WHITE))
        {
            setAllele("KIT", 0, 0);
        }

        entity.setSeed(this.entity.getRand().nextInt());
        this.entity.setMale(this.rand.nextBoolean());
    }

    public String judgeStatRaw(int val) {
        if (val <= 0) {
            return "worst";
        }
        else if (val <= 2) {
            return "bad";
        }
        else if (val <= 5) {
            return "avg";
        }
        else if (val <= 7) {
            return "good";
        }
        else {
            return "best";
        }
    }

    public String judgeStat(int val, String loc) {
        return Util.translate(loc + judgeStatRaw(val));
    }

    public String judgeStat(String name, int min, int max) {
        return Util.translate("stats." + judgeStatRaw(sumGenes(name, min, max)));
    }

    private void listGenes(ArrayList<String> list, List<String> genelist) {
        for (String gene : genelist) {
            if (gene.equals("KIT") && this.species != Species.DONKEY) {
                String tobianoLocation = "genes.tobiano";
                String tobi = Util.translate(tobianoLocation + ".name") + ": ";
                String a1 = HorseAlleles.isTobianoAllele(getAllele("KIT", 0))? "Tobiano" : "Wildtype";
                String a2 = HorseAlleles.isTobianoAllele(getAllele("KIT", 1))? "Tobiano" : "Wildtype";
                tobi += Util.translate(tobianoLocation + ".allele" + a1) + "/";
                tobi += Util.translate(tobianoLocation + ".allele" + a2);
                list.add(tobi);
            }
            String translationLocation = "genes." + gene;
            String s = Util.translate(translationLocation + ".name") + ": ";
            s += Util.translate(translationLocation + ".allele" + getAllele(gene, 0)) + "/";
            s += Util.translate(translationLocation + ".allele" + getAllele(gene, 1));
            list.add(s);
        }
    }

    public List<List<String>> getBookContents() {
        List<List<String>> contents = new ArrayList<List<String>>();
        List<String> physical = new ArrayList<String>();
        physical.add(Util.translate("book.physical"));
        String health = Util.translate("stats.health") + "\n";
        health += "  " + Util.translate("stats.health1") + ": " + judgeStat("health", 0, 4) + "\n";
        health += "  " + Util.translate("stats.health2") + ": " + judgeStat("health", 4, 8) + "\n";
        health += "  " + Util.translate("stats.health3") + ": " + judgeStat("health", 8, 12) + "\n";
        health += "  " + Util.translate("stats.immune") + ": " + judgeStat((int)getImmuneHealth(), "stats.immune.");
        String healthEffects = "";
        if (HorseConfig.GENETICS.enableHealthEffects.get()) {
            if (getDeafHealthLoss() > 0.5f) {
                healthEffects += "\n" + Util.translate("stats.health.deaf");
            }
            float h = getHealth() + getSilverHealthLoss();
            if ((int)getHealth() != (int)h) {
                healthEffects += "\n" + Util.translate("stats.health.MCOA");
            }
            float h2 = h + getGrayHealthLoss();
            if ((int)h != (int)h2) {
                healthEffects += "\n" + Util.translate("stats.health.melanoma");
            }
            if ((int)h2 != (int)(h2 + getERUHealthLoss())) {
                healthEffects += "\n" + Util.translate("stats.health.ERU");
            }
            if (isHomozygous("leopard", HorseAlleles.LEOPARD)) {
                healthEffects += "\n" + Util.translate("stats.health.CSNB");
            }
        }
        physical.add(health);
        String athletics = Util.translate("stats.athletics") + "\n";
        athletics += "  " + Util.translate("stats.athletics1") + ": " + judgeStat("athletics", 0, 4) + "\n";
        athletics += "  " + Util.translate("stats.athletics2") + ": " + judgeStat("athletics", 4, 8);
        physical.add(athletics);
        String speed = Util.translate("stats.speed") + "\n";
        speed += "  " + Util.translate("stats.speed1") + ": " + judgeStat("speed", 0, 4) + "\n";
        speed += "  " + Util.translate("stats.speed2") + ": " + judgeStat("speed", 4, 8) + "\n";
        speed += "  " + Util.translate("stats.speed3") + ": " + judgeStat("speed", 8, 12);
        physical.add(speed);
        String jump = Util.translate("stats.jump") + "\n";
        jump += "  " + Util.translate("stats.jump1") + ": " + judgeStat("jump", 0, 4) + "\n";
        jump += "  " + Util.translate("stats.jump2") + ": " + judgeStat("jump", 4, 8) + "\n";
        jump += "  " + Util.translate("stats.jump3") + ": " + judgeStat("jump", 8, 12);
        physical.add(jump);
        physical.add(healthEffects);
        if (HorseConfig.GENETICS.useGeneticStats.get() 
            && HorseConfig.GENETICS.bookShowsTraits.get()) {
            contents.add(physical);
        }

        List<String> colorgenelist = ImmutableList.of("extension", "agouti", "dun", 
            "gray", "cream", "silver", "KIT", "frame", "MITF", "leopard", "PATN1", 
            "mushroom");
        if (this.species == Species.DONKEY) {
            colorgenelist = ImmutableList.of("extension", "agouti", "KIT");
        }
        ArrayList<String> genetic = new ArrayList<>();
        genetic.add(Util.translate("book.genetic_color"));
        listGenes(genetic, colorgenelist);
        ArrayList<String> sizes = new ArrayList<>();
        sizes.add(Util.translate("book.genetic_size"));
        listGenes(sizes, ImmutableList.of("LCORL", "HMGA2"));
        // TODO: add a note that unknown genes and envorinmental factors may affect size
        if (HorseConfig.GENETICS.bookShowsGenes.get()) {
            contents.add(genetic);
        }
        if (HorseConfig.COMMON.enableSizes.get()) {
            contents.add(sizes);
        }
        return contents;
    }

    @OnlyIn(Dist.CLIENT)
    public void setTexturePaths()
    {
        this.textureLayers = HorseColorCalculator.getTexturePaths(this);
        this.textureCacheName = "horse/cache_" + this.textureLayers.getUniqueName();
    }

    public String genesToString() {
        String answer = entity.isMale()? "M" : "F";
        String genes = entity.getGeneData();
        for (int i = 0; i < genes.length(); ++i) {
            answer += String.format("%1$02X", (int)genes.charAt(i));
        }
        return answer;
    }

    public void genesFromString(String s) {
        // Before M/F was added, all strings were multiples of 8 long
        if (s.length() % 8 != 0) {
            String g = s.substring(0, 1);
            entity.setMale(g.equals("M"));
            s = s.substring(1);
        }

        if (s.length() <= 8 * 12) {
            Map<String, Integer> map = parseLegacyGenes(s);
            setLegacyGenes(map);
        }
        else {
            String genes = "";
            for (int i = 0; i < s.length() / 4; ++i) {
                for (int n = 0; n < 2; ++n) {                
                    String c = s.substring(4 * i + 2 * n, 4 * i + 2 * n + 2);
                    genes += (char)Short.parseShort(c, 16);
                }
            }
            entity.setGeneData(genes);
        }
    }
        

    private Map<String, Integer> parseLegacyGenes(String s) {
        ImmutableList<String> chromosomes = ImmutableList.of("0", "1", "2", "3", "speed", "jump", "health", "mhc1", "mhc2", "immune", "random", "4");
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < chromosomes.size(); ++i) {
            // This will be the default value if there are parsing errors
            int val = 0;
            try {
                String c = s.substring(8 * i, 8 * (i + 1));
                val = (int)Long.parseLong(c, 16);
            }
            catch (IndexOutOfBoundsException e) {}
            catch (NumberFormatException e) {}
            map.put(chromosomes.get(i), val);
        }
        if (s.length() <= 11 * 8) {
            datafixAddingFourthChromosome(map);
        }
        return map;
    }

    private void setGenericGenes(String name, int len, int val) {
        for (int i = 0; i < len; ++i) {
            setAllele(name + i, 0, val & 1);
            val = val >>> 1;
            setAllele(name + i, 1, val & 1);
            val = val >>> 1;
        }
    }

    // Convert from the format used by version 1.4 and earlier
    public void setLegacyGenes(Map<String, Integer> map) {
        // Convert the named genes
        for (String gene : listGenes()) {
            // Stop at the end of the "named genes." The others followed a 
            // different format.
            if (gene.equals("speed0")) {
                break;
            }
            // Use legacy access method and updated setter
            int allele0 = getAlleleOld(gene, 0, map);
            int allele1 = getAlleleOld(gene, 1, map);
            if (gene.equals("extension")) {
                allele0 = allele0 >= 4? 1 : 0;
                allele1 = allele1 >= 4? 1 : 0;
            }
            else if (gene.equals("agouti")) {
                allele0 = Math.min(4, allele0);
                allele1 = Math.min(4, allele1);
            }
            setAllele(gene, 0, allele0);
            setAllele(gene, 1, allele1);
        }
        // Convert speed, health, and jump genes
        int speed = map.get("speed");
        int jump = map.get("jump") >>> 8;
        int health = map.get("health");
        int athletics = (speed >>> 24) | ((map.get("jump") & 255) << 8);
        setGenericGenes("speed", 12, speed);
        setGenericGenes("health", 12, health);
        setGenericGenes("jump", 12, jump);
        setGenericGenes("athletics", 8, athletics);
        // Convert immune diversity genes
        long mhc1 = map.get("mhc1");
        long mhc2 = map.get("mhc2");
        long mhc = mhc1 | (mhc2 << 32);
        int immune = map.get("immune");
        for (int i = 0; i < 8; ++i) {
            for (int n = 0; n < 2; ++n) {
                // 3 == 0b11
                setAllele("immune" + i, n, immune & 3);
                immune = immune >>> 2;
                // 15 == 0b1111
                setAllele("mhc" + i, n, (int)(mhc & 15));
                mhc = mhc >>> 4;
            }
        }
    }

    public boolean isValidGeneString(String s) {
        if (s.length() < 2) {
            return false;
        }
        // Genderless from older version
        if (s.length() % 8 == 0) {
            return s.matches("[0-9a-fA-F]*");
        }
        String g = s.substring(0, 1);
        if (!g.equals("M") && !g.equals("F")) {
            return false;
        }
        s = s.substring(1);
        // Two hexadecimal characters per 1-byte allele, two alleles per gene
        if (s.length() % 4 != 0) {
            return false;
        }
        return s.matches("[0-9a-fA-F]*");
    }

    public void datafixAddingFourthChromosome(Map<String, Integer> map) {
        // MITF and PAX3 were next to each other and were 2 bits each,
        // now PAX3 moved and they are 4 bits each
        int prevSplash = this.getNamedGene("MITF", map);
        this.setAlleleOld("MITF", 0, prevSplash & 3, map);
        this.setAlleleOld("MITF", 1, (prevSplash >>> 2) & 3, map);
        this.setAlleleOld("PAX3", 0, (prevSplash >>> 4) & 3, map);
        this.setAlleleOld("PAX3", 1, (prevSplash >>> 6) & 3, map);
        // There was 1 bit for each allele of white_suppression, 
        // then 4 for KIT, then 1 for frame
        // Those were all merged into KIT and the other genes were
        // moved elsewhere
        int prevKIT = this.getNamedGene("KIT", map);
        this.setAlleleOld("white_suppression", 0, prevKIT & 1, map);
        this.setAlleleOld("white_suppression", 1, (prevKIT >>> 1) & 1, map);
        this.setAlleleOld("KIT", 0, (prevKIT >>> 2) & 15, map);
        this.setAlleleOld("KIT", 1, (prevKIT >>> 6) & 15, map);
        this.setAlleleOld("frame", 0, (prevKIT >>> 10) & 1, map);
        this.setAlleleOld("frame", 1, (prevKIT >>> 11) & 1, map);
        // Used to be 2 bits of cream and 1 of silver, 
        // now cream is merged to where silver was
        int prevCream = this.getNamedGene("cream", map);
        this.setAlleleOld("cream", 0, prevCream & 3, map);
        this.setAlleleOld("cream", 1, (prevCream >>> 2) & 3, map);
        this.setAlleleOld("silver", 0, (prevCream >>> 4) & 1, map);
        this.setAlleleOld("silver", 1, (prevCream >>> 5) & 1, map);
    }
}
