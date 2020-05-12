package sekelsta.horse_colors.genetics;

import sekelsta.horse_colors.HorseColors;
import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.entity.HorseGeneticEntity;
import sekelsta.horse_colors.renderer.TextureLayer;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        "silver", 
        "liver", 
        "flaxen1", 
        "flaxen2", 
        "dapple", 
        "sooty1", 
        "sooty2", 
        "sooty3", 
        "mealy1", 
        "mealy2", 
        "mealy3", 
        "white_suppression", 
        "KIT", 
        "frame", 
        "MITF", 
        "PAX3", 
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
        "gray_mane2"
    );

    public static final ImmutableList<String> genericChromosomes = ImmutableList.of(
        "speed",
        "jump",
        "health"        
    );

    public static final ImmutableList<String> stats = ImmutableList.of(
        "speed1",
        "speed2",
        "speed3",
        "athletics1",
        "athletics2",
        "jump1",
        "jump2",
        "jump3",
        "health1",
        "health2",
        "health3",
        "stamina"
    );

    public static final ImmutableList<String> chromosomes = ImmutableList.of("0", "1", "2", "speed", "jump", "health", "random");

    public HorseGenome(IGeneticEntity entityIn) {
        super(entityIn);
    }

    @Override
    public ImmutableList<String> listGenes() {
        return genes;
    }

    @Override
    public ImmutableList<String> listGenericChromosomes() {
        return genericChromosomes;
    }

    @Override
    public ImmutableList<String> listStats() {
        return stats;
    }

    /* For named genes, this returns the number of bits needed to store one allele. 
    For stats, this returns the number of genes that contribute to the stat. */
    @Override
    public int getGeneSize(String gene)
    {
        switch(gene) 
        {
            case "KIT":
            case "speed1":
            case "speed2":
            case "speed3":
            case "athletics1":
            case "athletics2":
            case "jump1":
            case "jump2":
            case "jump3":
            case "health1":
            case "health2":
            case "health3":
            case "stamina": return 4;

            case "extension":
            case "agouti": return 3;

            case "MITF":
            case "PAX3":
            case "cream":
            case "dun": return 2;

            default: return 1;
        }
    }

    public boolean isChestnut()
    {
        int e = getMaxAllele("extension");
        return e == HorseAlleles.E_RED 
                || e == HorseAlleles.E_RED2
                || e == HorseAlleles.E_RED3
                || e == HorseAlleles.E_RED4;
    }

    public boolean hasCream() {
        return this.hasAllele("cream", HorseAlleles.CREAM);
    }

    public boolean isPearl() {
        return this.isHomozygous("cream", HorseAlleles.PEARL);
    }

    public boolean isDoubleCream() {
        return this.isHomozygous("cream", HorseAlleles.CREAM);
    }

    public boolean isCreamPearl() {
        return this.hasAllele("cream", HorseAlleles.CREAM)
            && this.hasAllele("cream", HorseAlleles.PEARL);
    }

    public boolean isSilver() {
        return this.hasAllele("silver", HorseAlleles.SILVER);
    }

    public boolean isGray() {
        return this.hasAllele("gray", HorseAlleles.GRAY);
    }

    public boolean isDun() {
        return this.hasAllele("dun", HorseAlleles.DUN)
            || this.hasAllele("dun", HorseAlleles.DUN_UNUSED);
    }

    // The MC1R ("extension") gene seems to be associated with white
    // patterning. For now I assume this is caused by MC1R itself,
    // but if it turns out to be a different gene that's just very
    // closely linked, I can change this.
    public boolean hasMC1RWhiteBoost() {
        return isChestnut();
    }

    public boolean isTobiano() {
        return this.hasAllele("KIT", HorseAlleles.KIT_TOBIANO)
            || this.hasAllele("KIT", HorseAlleles.KIT_TOBIANO_W20);
    }

    public boolean isWhite() {
        return this.hasAllele("KIT", HorseAlleles.KIT_DOMINANT_WHITE)
            || this.isLethalWhite()
            || this.isHomozygous("KIT", HorseAlleles.KIT_SABINO1)
            || (this.hasAllele("KIT", HorseAlleles.KIT_SABINO1)
                && this.hasAllele("frame", HorseAlleles.FRAME)
                && this.isTobiano());
    }

    public boolean showsLegMarkings() {
        return !isWhite() && !isTobiano();
    }

    public boolean isDappleInclined() {
        return this.hasAllele("dapple", 1);
    }

    public boolean isLethalWhite() {
        return this.isHomozygous("frame", HorseAlleles.FRAME);
    }

    public boolean isEmbryonicLethal() {
        return this.isHomozygous("KIT", HorseAlleles.KIT_DOMINANT_WHITE);
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
        int white = HorseColorCalculator.getFaceWhiteLevel(this);
        if (white > 18) {
            return 1f;
        }
        else {
            return 0f;
        }
    }

    public float getBaseHealth() {
        if (HorseConfig.COMMON.enableHealthEffects.get()) {
            return -getGrayHealthLoss() - getSilverHealthLoss() - getDeafHealthLoss();
        }
        else {
            return 0;
        }
    }

    // A special case because it has two different alleles
    public int countW20() {
        return countAlleles("KIT", HorseAlleles.KIT_W20) 
                + countAlleles("KIT", HorseAlleles.KIT_TOBIANO_W20);
    }

    // Return true if the client needs to know the age to render properly,
    // aside from just whether the animal is a child
    public boolean clientNeedsAge() {
        return isGray();
    }

    public int getAge() {
        if (entity instanceof HorseGeneticEntity) {
            return ((HorseGeneticEntity)entity).getDisplayAge();
        }
        else {
            return 0;
        }
    }

    public int inheritStats(HorseGenome other, String chromosome) {
            int mother = this.getRandomGenericGenes(1, this.getChromosome(chromosome));
            int father = other.getRandomGenericGenes(0, other.getChromosome(chromosome));
            return mother | father;
    }

    // Distribution should be a series of floats increasing from
    // 0.0 to 1.0, where the probability of choosing allele i is
    // the chance that a random uniform number between 0 and 1
    // is greater than distribution[i-1] but less than distribution[i].
    public int chooseRandomAllele(List<Float> distribution) {
        float n = this.entity.getRand().nextFloat();
        for (int i = 0; i < distribution.size(); ++i) {
            if (n < distribution.get(i)) {
                return i;
            }
        }
        // In case of floating point rounding errors
        return distribution.size() - 1;
    }

    public int chooseRandom(List<Float> distribution) {
        int left = chooseRandomAllele(distribution);
        int right = chooseRandomAllele(distribution);
        // Log 2
        int size = 8 * Integer.BYTES - 1 - Integer.numberOfLeadingZeros(distribution.size());
        // Round up
        if (distribution.size() != 1 << size) {
            size += 1;
        }
        return (left << size) | right;
    }

    public void randomizeNamedGenes() {
        HashMap<String, ImmutableList<Float>> map = HorseBreeds.DEFAULT;
        for (String gene : genes) {
            setNamedGene(gene, chooseRandom(map.get(gene)));
        }
    }

    /* Make the horse have random genetics. */
    public void randomize()
    {
        randomizeNamedGenes();

        // Replace lethal white overos with heterozygotes
        if (isHomozygous("frame", HorseAlleles.FRAME))
        {
            setNamedGene("frame", 1);
        }

        // Homozygote dominant whites will be replaced with heterozygotes
        if (isHomozygous("KIT", HorseAlleles.KIT_DOMINANT_WHITE))
        {
            setNamedGene("KIT", 15);
        }

        for (String stat : this.listGenericChromosomes()) {
            entity.setChromosome(stat, this.entity.getRand().nextInt());
        }
        entity.setChromosome("random", this.entity.getRand().nextInt());
    }

    private String getAbv(TextureLayer layer) {
        if (layer == null || layer.name == null) {
            return "";
        }        
        String abv = layer.toString() + "_";
        if (layer.next != null) {
            abv += ".-" + getAbv(layer.next) + "-.";
        }
        // Upper case characters will cause a crash
        return abv.toLowerCase();
    }
    public ArrayList<String> humanReadableNamedGenes(boolean showAll) {
        List<String> genelist = genes;
        if (!showAll) {
            genelist = ImmutableList.of("extension", "agouti", "dun", "gray", "cream", "silver", "KIT", "frame", "MITF");
        }
        ArrayList<String> list = new ArrayList<String>();
        for (String gene : genelist) {
            String translationLocation = HorseColors.MODID + ".genes." + gene;
            TranslationTextComponent translation = new TranslationTextComponent(translationLocation + ".name");
            String s = translation.getFormattedText() + ": ";
            TranslationTextComponent allele1 = new TranslationTextComponent(translationLocation + ".allele" + getAllele(gene, 0));
            TranslationTextComponent allele2 = new TranslationTextComponent(translationLocation + ".allele" + getAllele(gene, 1));
            s += allele1.getFormattedText() + "/";
            s += allele2.getFormattedText();
            list.add(s);
        }
        return list;
    }/*
    public ArrayList<String> humanReadableStats(boolean showAll) {
        ArrayList<String> list = new ArrayList<String>();
        for (String stat : stats) {
            TranslationTextComponent translation = new TranslationTextComponent(HorseColors.MODID + ".stats." + stat);
            String s = translation.getFormattedText();
            s += ": " + this.getStat(stat);
            s += " (";
            int val = this.getChromosome(stat);
            for (int i = 16; i >0; i--) {
                s += (val >>> (2 * i - 1)) & 1;
                s += (val >>> (2 * i - 2)) & 1;
                if (i > 1) {
                    s += " ";
                }
            }
            s += ")";
            list.add(s);
        }
        return list;
    }*/

    @OnlyIn(Dist.CLIENT)
    public void setTexturePaths()
    {
        this.textureLayers = new ArrayList();
        TextureLayer red = HorseColorCalculator.getRedBody(this);
        TextureLayer black = HorseColorCalculator.getBlackBody(this);
        this.textureLayers.add(red);
        HorseColorCalculator.addRedManeTail(this, this.textureLayers);
        this.textureLayers.add(black);
        this.textureLayers.add(HorseColorCalculator.getBlackManeTail(this));
        this.textureLayers.add(HorseColorCalculator.getSooty(this));
        HorseColorCalculator.addDun(this, this.textureLayers);
        HorseColorCalculator.addGray(this, this.textureLayers);
        this.textureLayers.add(HorseColorCalculator.getNose(this));
        this.textureLayers.add(HorseColorCalculator.getHooves(this));

        if (this.hasAllele("KIT", HorseAlleles.KIT_ROAN)) {
            TextureLayer roan = new TextureLayer();
            roan.name = HorseColorCalculator.fixPath("roan/roan");
            this.textureLayers.add(roan);
        }

        this.textureLayers.add(HorseColorCalculator.getFaceMarking(this));
        if (showsLegMarkings())
        {
            String[] leg_markings = HorseColorCalculator.getLegMarkings(this);
            for (String marking : leg_markings) {
                TextureLayer layer = new TextureLayer();
                layer.name = marking;
                this.textureLayers.add(layer);
            }
        }

        this.textureLayers.add(HorseColorCalculator.getPinto(this));

        TextureLayer highlights = new TextureLayer();
        highlights.name = HorseColorCalculator.fixPath("base");
        highlights.type = TextureLayer.Type.HIGHLIGHT;
        highlights.alpha = (int)(255f * 0.2f);
        this.textureLayers.add(highlights);

        TextureLayer shading = new TextureLayer();
        shading.name = HorseColorCalculator.fixPath("shading");
        shading.type = TextureLayer.Type.SHADE;
        shading.alpha = (int)(255 * 0.5);
        this.textureLayers.add(shading);

        TextureLayer common = new TextureLayer();
        common.name = HorseColorCalculator.fixPath("common");
        this.textureLayers.add(common);

        this.textureCacheName = "horse/cache_";

        for (int i = 0; i < textureLayers.size(); ++i) {
            this.textureCacheName += getAbv(this.textureLayers.get(i));
        }
    }

    public String genesToString() {
        String answer = "";
        for (String chr : chromosomes) {
            answer += String.format("%1$08X", getChromosome(chr));
        }
        return answer;
    }

    public void genesFromString(String s) {
        for (int i = 0; i < chromosomes.size(); ++i) {
            String c = s.substring(8 * i, 8 * (i + 1));
            entity.setChromosome(chromosomes.get(i), (int)Long.parseLong(c, 16));
        }
    }

    public boolean isValidGeneString(String s) {
        if (s.length() != 8 * chromosomes.size()) {
            return false;
        }
        if (!s.matches("[0-9a-fA-F]*")) {
            return false;
        }
        return true;
    }

    public void setChildGenes(HorseGenome other, IGeneticEntity childEntity) {

        int mother = this.getRandomGenes(1, 0);
        int father = other.getRandomGenes(0, 0);
        int i = mother | father;
        childEntity.setChromosome("0", i);

        mother = this.getRandomGenes(1, 1);
        father = other.getRandomGenes(0, 1);
        i = mother | father;
        childEntity.setChromosome("1", i);


        childEntity.setChromosome("2", rand.nextInt());
        mother = this.getRandomGenes(1, 2);
        father = other.getRandomGenes(0, 2);
        i = mother | father;
        childEntity.setChromosome("2", i);

        for (String stat : this.listGenericChromosomes()) {
            int val = inheritStats(other, stat);
            childEntity.setChromosome(stat, val);
        }
        childEntity.getGenes().mutate();
    }
}
