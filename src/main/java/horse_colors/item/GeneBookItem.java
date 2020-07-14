package sekelsta.horse_colors.item;

import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import sekelsta.horse_colors.client.GeneBookScreen;
import sekelsta.horse_colors.entity.*;
import sekelsta.horse_colors.genetics.Genome;
import sekelsta.horse_colors.genetics.HorseGenome;
import sekelsta.horse_colors.genetics.IGeneticEntity;

public class GeneBookItem extends Item {
    public GeneBookItem(Item.Properties properties) {
        super(properties);
    }

    public static boolean validBookTagContents(CompoundNBT nbt) {
        if (nbt == null) {
            return false;
        }
        // 8 is string type
        if (!nbt.contains("species", 8)) {
            return false;
        }
        if (!nbt.contains("genes", 8)) {
            return false;
        }
        try {
            Species sp = Species.valueOf(nbt.getString("species"));
        }
        catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTag()) {
            CompoundNBT compoundnbt = stack.getTag();
            String s = compoundnbt.getString("species");
            if (!StringUtils.isNullOrEmpty(s)) {
                String translation = null;
                switch (Species.valueOf(s)) {
                    case HORSE:
                        translation = ModEntities.HORSE_GENETIC.getTranslationKey();
                        break;
                    case DONKEY:
                        translation = ModEntities.DONKEY_GENETIC.getTranslationKey();
                        break;
                    case MULE:
                        translation = ModEntities.MULE_GENETIC.getTranslationKey();
                        break;
                }
                if (translation != null) {
                    tooltip.add(new TranslationTextComponent(translation).applyTextStyle(TextFormatting.GRAY));
                }
            }
        }
    }

    /**
     * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
     * {@link #onItemUse}.
     */
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        if (validBookTagContents(itemstack.getTag())) {
            if (worldIn.isRemote()) {
                openGeneBook(itemstack.getTag());
            }
            return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
        }
        System.out.println("Gene book has invalid NBT");
        return new ActionResult<>(ActionResultType.FAIL, itemstack);
    }

    @OnlyIn(Dist.CLIENT)
    public void openGeneBook(CompoundNBT nbt) {
        Minecraft mc = Minecraft.getInstance();
        Genome genome = new HorseGenome();
        genome.genesFromString(nbt.getString("genes"));
        mc.displayGuiScreen(new GeneBookScreen(genome));
    }

    public static enum Species {
        HORSE,
        DONKEY,
        MULE
    }
}
