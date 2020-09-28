package sekelsta.horse_colors.genetics;
import java.util.*;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.google.common.collect.ImmutableList;

import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.renderer.TextureLayer;
import sekelsta.horse_colors.util.RandomSupplier;

public class HorseColorCalculator
{
    static final RandomSupplier randSource = new RandomSupplier(ImmutableList.of("leg_white", "face_white", "star_choice"));

    private static final int GRAY_BODY_STAGES = 19;
    private static final int GRAY_MANE_STAGES = 20;

    public static String fixPath(String inStr) {
        if (inStr == null || inStr.contains(".png")) {
            return inStr;
        }
        else if (inStr == "")
        {
            return null;
        }
        else {
            return "horse_colors:textures/entity/horse/" + inStr +".png";
        }
    }

    public static void adjustConcentration(TextureLayer layer, float power) {

        float r = layer.red / 255.0F;
        float g = layer.green / 255.0F;
        float b = layer.blue / 255.0F;

        float red = (float)Math.pow(r, power) * 255.0F;
        float green = (float)Math.pow(g, power) * 255.0F;
        float blue = (float)Math.pow(b, power) * 255.0F;

        layer.red = Math.max(0, Math.min(255, (int)red));
        layer.green = Math.max(0, Math.min(255, (int)green));
        layer.blue = Math.max(0, Math.min(255, (int)blue));
    }

    public static void addWhite(TextureLayer layer, float white) {
        layer.red = (int)(255. * white + layer.red * (1f - white));
        layer.green = (int)(255. * white + layer.green * (1f - white));
        layer.blue = (int)(255. * white + layer.blue * (1f - white));
    }

    public static void setPheomelanin(TextureLayer layer, float concentration, float white) {
        layer.red = 0xe4;
        layer.green = 0xc0;
        layer.blue = 0x77;
        adjustConcentration(layer, concentration);
        addWhite(layer, white);
    }

    public static void setEumelanin(TextureLayer layer, float concentration, float white) {
        layer.red = 0xc0;
        layer.green = 0x9a;
        layer.blue = 0x5f;
        adjustConcentration(layer, concentration);
        addWhite(layer, white);
    }

    public static void colorRedBody(HorseGenome horse, TextureLayer layer) {
        // 5, 0.2 looks haflingerish
        // 5, 0.1 looks medium chestnut
        // 6, 0.1 looks liver chestnutish
        float concentration = 5f;
        float white = 0.08f;

        if (horse.isDoubleCream() || horse.isHomozygous("ivory", HorseAlleles.IVORY)) {
            concentration *= 0.05f;
            white += 0.4f;
        }
        else if (horse.isCreamPearl()) {
            concentration *= 0.1f;
        }
        else if (horse.hasCream()) {
            concentration *= 0.6f;
            white += 0.15f;
            if (horse.hasAllele("cream", HorseAlleles.MATP_MINOR)) {
                concentration *= 0.6f;
                white += 0.04;
            }
        }
        else if (horse.isPearl()) {
            concentration *= 0.6f;
            white += 0.15f;
            if (horse.hasAllele("cream", HorseAlleles.MATP_MINOR)) {
                concentration *= 0.9f;
                white += 0.04f;
            }
        }
        else if (horse.isHomozygous("cream", HorseAlleles.MATP_MINOR)) {
            concentration *= 0.9f;
            white += 0.04f;
        }

        if (horse.hasAllele("cameo", HorseAlleles.CAMEO)) {
            concentration *= 0.3f;
            white += 0.25f;
        }

        if (horse.hasAllele("rufous", 1)) {
            concentration *= 1.1f;
        }

        if (horse.isHomozygous("dark_red", 1)) {
            concentration *= 1.2f;
        }

        if (horse.isHomozygous("dense", 1)) {
            concentration *= 1.1f;
            white -= 0.03f;
        }

        white = Math.max(white, 0);
        setPheomelanin(layer, concentration, white);

        // Treat liver like it leaks some eumelanin into the coat
        if (horse.isChestnut() 
                && horse.isHomozygous("liver", HorseAlleles.LIVER)) {
            TextureLayer dark = new TextureLayer();
            setPheomelanin(dark, concentration * 5f, white);
            float a = 0.4f;
            layer.red = (int)(dark.red * a + layer.red * (1 - a));
            layer.green = (int)(dark.green * a + layer.green * (1 - a));
            layer.blue = (int)(dark.blue * a + layer.blue * (1 - a));
            layer.clamp();
        }
    }

    public static TextureLayer getRedBody(HorseGenome horse) {
        TextureLayer layer = new TextureLayer();
        layer.name = fixPath("base");
        colorRedBody(horse, layer);
        setGrayConcentration(horse, layer);
        return layer;
    }

    public static void colorBlackBody(HorseGenome horse, TextureLayer layer) {
        float concentration = 20f;
        float white = 0.02f;
        if (horse.isDoubleCream() || horse.isHomozygous("ivory", HorseAlleles.IVORY)) {
            concentration *= 0.02f;
        }
        else if (horse.isCreamPearl()) {
            concentration *= 0.025f;
        }
        else if (horse.hasCream()) {
            concentration *= 0.5f;
        }
        else if (horse.isPearl()) {
            concentration *= 0.25f;
            white += 0.18f;
        }

        if (horse.hasAllele("cameo", HorseAlleles.CAMEO)) {
            concentration *= 0.2f;
            white += 0.2f;
        }

        if (horse.hasAllele("silver", HorseAlleles.SILVER)) {
            concentration *= 0.4f;
        }

        if (horse.isHomozygous("dense", 1)) {
            concentration *= 1.1f;
            white -= 0.01f;
        }
 
        white = Math.max(white, 0);
        setEumelanin(layer, concentration, white);
    }

    public static TextureLayer getBlackBody(HorseGenome horse) {
        if (horse.isChestnut()) {
            return null;
        }
        TextureLayer layer = new TextureLayer();
        layer.description = "black body";

        if (horse.getMaxAllele("agouti") == HorseAlleles.A_BLACK) {
            layer.name = fixPath("base");
        }
        else if (horse.getMaxAllele("agouti") == HorseAlleles.A_SEAL
                || horse.getMaxAllele("agouti") == HorseAlleles.A_BROWN) {
            layer.name = fixPath("brown");
        }
        else {
            return getSooty(horse);
        }
        colorBlackBody(horse, layer);
        setGrayConcentration(horse, layer);
        return layer;
    }

    public static void addRedManeTail(HorseGenome horse, List<TextureLayer> layers) {
        final float PALOMINO_POWER = 0.2f;
        if (!horse.isChestnut()) {
            return;
        }

        if (horse.hasAllele("cream", HorseAlleles.CREAM)) {
            TextureLayer palomino_mane = new TextureLayer();
            palomino_mane.description = "palomino mane";
            palomino_mane.name = fixPath("manetail");
            colorRedBody(horse, palomino_mane);
            adjustConcentration(palomino_mane, PALOMINO_POWER);
            setGrayConcentration(horse, palomino_mane);
            layers.add(palomino_mane);
        }

        if (!horse.isHomozygous("flaxen1", HorseAlleles.FLAXEN)
                && !horse.isHomozygous("flaxen2", HorseAlleles.FLAXEN)) {
            // No flaxen, nothing to do
            return;
        }

        TextureLayer flaxen = new TextureLayer();
        flaxen.name = fixPath("flaxen");
        flaxen.description = "flaxen";
        colorRedBody(horse, flaxen);
        float power = 1f;
        if (horse.hasAllele("cream", HorseAlleles.CREAM)) {
            power *= PALOMINO_POWER;
        }
        float white = 0f;
        if (horse.isHomozygous("flaxen1", HorseAlleles.FLAXEN)) {
            power *= 0.5f;
            white += 0.2f;
        }
        if (horse.isHomozygous("flaxen2", HorseAlleles.FLAXEN)) {
            power *= 0.8f;
            white += 0.1f;
        }
        if (horse.hasAllele("flaxen_boost", 1)) {
            Math.pow(power, 1.5);
            white *= 1.5;
        }
        adjustConcentration(flaxen, power);
        setGrayConcentration(horse, flaxen);
        addWhite(flaxen, white);
        layers.add(flaxen);
    }

    public static TextureLayer getBlackManeTail(HorseGenome horse) {
        if (horse.isChestnut()) {
            return null;
        }
        if (!horse.hasAllele("silver", HorseAlleles.SILVER)) {
            return null;
        }
        TextureLayer layer = new TextureLayer();
        layer.name = fixPath("flaxen");
        layer.description = "silver dapple mane";
        setEumelanin(layer, 0.3f, 0.0f);
        setGrayConcentration(horse, layer);
        return layer;
    }

    public static void colorSkin(HorseGenome horse, TextureLayer layer) {
        if (horse.isCreamPearl() || horse.hasAllele("cameo", HorseAlleles.CAMEO)) {
            // Light skin
            setEumelanin(layer, 5f, 0.2f);
        }
        else if (!(horse.isDoubleCream() || horse.isHomozygous("ivory", HorseAlleles.IVORY))) {
            // Black skin
            setEumelanin(layer, 18f, 0.1f);
        }
        // White to pink (red is unchanged)
        int old = layer.green;
        layer.green = (int)(layer.green * 0xd6 / 255f);
        old = layer.blue;
        layer.blue = (int)(layer.blue * 0xb6 / 255f);
    }

    public static void colorGray(HorseGenome horse, TextureLayer layer) {
        // Show skin very faintly through the white hairs
        colorSkin(horse, layer);
        addWhite(layer, 0.99f);
    }

    public static TextureLayer getNose(HorseGenome horse) {
        TextureLayer layer = new TextureLayer();
        layer.name = fixPath("nose");
        colorSkin(horse, layer);
        return layer;
    }

    public static TextureLayer getHooves(HorseGenome horse) {
        TextureLayer layer = new TextureLayer();
        layer.name = fixPath("hooves");
        colorSkin(horse, layer);
        addWhite(layer, 0.4f);
        // Multiply by the shell color of hooves
        layer.red = (int)((float)layer.red * 255f / 255f);
        layer.green = (int)((float)layer.green * 229f / 255f);
        layer.blue= (int)((float)layer.blue * 184f / 255f);
        layer.clamp();
        return layer;
    }

    public static void addDun(HorseGenome horse, List<TextureLayer> layers) {
        if (!horse.hasStripe()) {
            return;
        }
        TextureLayer white = new TextureLayer();
        white.name = fixPath("dun");
        white.alpha = (int)(0.1f * 255f);
        if (!horse.isDun()) {
            white.alpha = (int)(white.alpha * 0.1);
        }
        if (horse.isHomozygous("light_dun", 1)) {
            white.alpha *= 2;
        }
        white.type = TextureLayer.Type.SHADE;
        layers.add(white);

        TextureLayer layer = new TextureLayer();
        layer.name = fixPath("dun");
        layer.type = TextureLayer.Type.ROOT;
        float dunpower = 0.6f;
        if (!horse.isDun()) {
            dunpower = 0.9f;
        }
        int val = (int)(dunpower * 255);
        layer.red = val;
        layer.green = val;
        layer.blue = val;
        layers.add(layer);
    }

    public static TextureLayer getSooty(HorseGenome horse)
    {
        TextureLayer layer = new TextureLayer();

        int sooty_level = horse.getSootyLevel();
        switch (sooty_level) {
            case 0:
                return null;
            case 1:
                layer.alpha = (int)(0.4F * 255.0F);
                break;
            case 2:
                layer.alpha = (int)(0.8F * 255.0F);
                break;
            case 3:
                layer.alpha = 255;
                break;
            default:
                layer.alpha = 255;
        }

        // TODO: replace this with something that actually looks good
        if (horse.hasAllele("donkey_dark", 1) && !horse.isChestnut()) {
            layer.alpha = 255;
        }

        layer.name = fixPath("sooty_countershade");
        if (horse.isDappleInclined()) {
            layer.name = fixPath("sooty_dapple");
        }
        else if (horse.isChestnut()) {
            layer.name = fixPath("base");
            layer.alpha /= 2;
        }

        colorBlackBody(horse, layer);
        setGrayConcentration(horse, layer);

        return layer;
    }

    public static TextureLayer getMealy(HorseGenome horse)
    {
        // Agouti black hides mealy
        if (!horse.isMealy()) {
            return null;
        }

        TextureLayer light_belly = new TextureLayer();
        int spread = 1;
        int color = 0;
        if (horse.hasAllele("mealy1", HorseAlleles.MEALY)) {
            spread += 2;
        }
        if (horse.hasAllele("mealy2", HorseAlleles.MEALY)) {
            color += 1;
        }
        if (horse.isHomozygous("flaxen2", 0)) {
            spread += 1;
        }
        
        String prefix = "";
        if (horse.isHomozygous("light_legs", 1)) {
            // Use version with darker legs
            prefix = "l";
        }
        else if (horse.hasAllele("less_light_legs", 0)) {
            // Set light_belly texture to leave the legs dark and be one 
            // shade darker as a whole, and add a thin layer with light legs
            prefix = "l";
            if (spread > 1) {
                spread -= 1;
                light_belly.next = new TextureLayer();
                light_belly.next.name = fixPath("mealy/mealy1");
                colorRedBody(horse, light_belly.next);
                adjustConcentration(light_belly.next, 0.04f * (2 - color));
            }
        }

        light_belly.name = fixPath("mealy/" + prefix + "mealy" + spread);
        colorRedBody(horse, light_belly);
        adjustConcentration(light_belly, 0.04f * (2 - color));



        return light_belly;
    }

    public static void addPoints(HorseGenome horse, List<TextureLayer> layers) {
        String prefix = "";
        if (horse.hasAllele("reduced_points", 1)) {
            prefix = "wild_";
        }
        if (horse.hasStripe()) {
            TextureLayer stripe = new TextureLayer();
            if (horse.hasAllele("cross", 1)) {
                stripe.name = fixPath("marks/" + prefix + "cross");
            }
            else {
                stripe.name = fixPath("marks/" + prefix + "dorsal");
            }
            if (horse.isChestnut()) {
                colorRedBody(horse, stripe);
            }
            else {
                colorBlackBody(horse, stripe);
            }
            adjustConcentration(stripe, 1.2f);
            layers.add(stripe);
        }
        else if (!horse.isChestnut()) {
            TextureLayer points = new TextureLayer();
            points.name = fixPath(prefix + "bay");
            colorBlackBody(horse, points);
            layers.add(points);
        }
    }

    public static void addGray(HorseGenome horse, List<TextureLayer> layers) {
        if (!horse.isGray()) {
            return;
        }
        float rate = horse.getGrayRate();
        float mane_rate = horse.getGrayManeRate();

        int body_stage = grayStage(horse, rate, GRAY_BODY_STAGES, 0.25f);
        int mane_stage = grayStage(horse, mane_rate, GRAY_MANE_STAGES, 0.3f);

        if (body_stage > 0) {
            TextureLayer body = new TextureLayer();
            if (body_stage > GRAY_BODY_STAGES) {
                body.name = fixPath("body");
            }
            else {
                body.name = fixPath("gray/dapple" + body_stage);
            }
            colorGray(horse, body);
            layers.add(body);
        }

        if (mane_stage > 0) {
            TextureLayer mane = new TextureLayer();
            if (mane_stage > GRAY_MANE_STAGES) {
                mane.name = fixPath("manetail");
            }
            else {
                mane.name = fixPath("gray/mane" + mane_stage);
            }
            colorGray(horse, mane);
            layers.add(mane);
        }
    }

    // num_stages does not count the starting and ending stages
    public static int grayStage(HorseGenome horse, float rate, int num_stages, float delay) {
        final int YEAR_TICKS = HorseConfig.getYearLength();
        final int MAX_AGE = HorseConfig.GROWTH.getMaxAge();
        int age = horse.getAge() + 24000;
        age = Math.min(age, MAX_AGE);
        if (!HorseConfig.GROWTH.grayGradually) {
            // If horses should not gray gradually, treat them as being 8 years old
            age = (int)(MAX_AGE * 0.5f);
        }
        float gray_age = (float)age / (float)(YEAR_TICKS * rate);
        gray_age = (gray_age - delay) / (1f - delay);
        if (gray_age <= 0) {
            return 0;
        }
        if (gray_age >= 1f) {
            return num_stages + 1;
        }
        return (int)(gray_age * num_stages);
    }

    public static float grayConcentration(HorseGenome horse, float rate) {
        int stage = grayStage(horse, rate, 50, 0f);
        double val = 1.1 + Math.pow(1.06, stage) * stage / 50. * stage / 50.;
        return (float)val;
    }

    public static void setGrayConcentration(HorseGenome horse, TextureLayer layer) {
        if (horse.isGray()) {
            float prevRed = layer.red;
            float prevGreen = layer.green;
            float prevBlue = layer.blue;
            float concentration = grayConcentration(horse, horse.getGrayRate());
            adjustConcentration(layer, concentration);
            // Blacken the color a bit so it does not look so odd
            float lightnessDiff = (float)(layer.red + layer.green + layer.blue) / (prevRed + prevGreen + prevBlue);
            layer.red = (int)((layer.red + lightnessDiff * prevRed) / 2f);
            layer.green = (int)((layer.green + lightnessDiff * prevGreen) / 2f);
            layer.blue = (int)((layer.blue + lightnessDiff * prevBlue) / 2f);
        }
    }


    @SideOnly(Side.CLIENT)
    public static List<TextureLayer> getTexturePaths(HorseGenome horse) {
        List<TextureLayer> textureLayers = new ArrayList<TextureLayer>();
        TextureLayer red = HorseColorCalculator.getRedBody(horse);
        textureLayers.add(red);
        textureLayers.add(HorseColorCalculator.getMealy(horse));
        TextureLayer black = HorseColorCalculator.getBlackBody(horse);
        textureLayers.add(black);
        HorseColorCalculator.addDun(horse, textureLayers);
        addPoints(horse, textureLayers);
        HorseColorCalculator.addRedManeTail(horse, textureLayers);
        textureLayers.add(HorseColorCalculator.getBlackManeTail(horse));
        HorseColorCalculator.addGray(horse, textureLayers);
        textureLayers.add(HorseColorCalculator.getNose(horse));
        textureLayers.add(HorseColorCalculator.getHooves(horse));

        if (horse.hasAllele("KIT", HorseAlleles.KIT_ROAN)) {
            TextureLayer roan = new TextureLayer();
            roan.name = HorseColorCalculator.fixPath("roan/roan");
            textureLayers.add(roan);
        }

        HorsePatternCalculator.addFaceMarkings(horse, textureLayers);
        if (horse.showsLegMarkings())
        {
            String[] leg_markings = HorsePatternCalculator.getLegMarkings(horse);
            for (String marking : leg_markings) {
                TextureLayer layer = new TextureLayer();
                layer.name = marking;
                textureLayers.add(layer);
            }
        }

        textureLayers.add(HorsePatternCalculator.getPinto(horse));
        HorsePatternCalculator.addLeopard(horse, textureLayers);

        TextureLayer highlights = new TextureLayer();
        highlights.name = HorseColorCalculator.fixPath("base");
        highlights.type = TextureLayer.Type.HIGHLIGHT;
        highlights.alpha = (int)(255f * 0.2f);
        textureLayers.add(highlights);

        TextureLayer shading = new TextureLayer();
        shading.name = HorseColorCalculator.fixPath("shading");
        shading.type = TextureLayer.Type.SHADE;
        shading.alpha = (int)(255 * 0.5);
        textureLayers.add(shading);

        TextureLayer common = new TextureLayer();
        common.name = HorseColorCalculator.fixPath("common");
        textureLayers.add(common);
        return textureLayers;
    }
}
