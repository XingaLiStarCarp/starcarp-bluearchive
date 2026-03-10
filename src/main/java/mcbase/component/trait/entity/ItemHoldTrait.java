package mcbase.component.trait.entity;

import mcbase.component.TraitProvider.TraitComponent;
import mcbase.registry.Registers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * 手持物品
 * 
 * @param <_T>
 */
public class ItemHoldTrait implements TraitComponent<LivingEntity> {
	private LivingEntity entity;

	private String mainHandItem;
	private String offhandItem;

	private ItemStack originalMainHandItem;
	private ItemStack originalOffHandItem;

	/**
	 * 空物品的ID
	 */
	public static final String EMPTY = Items.AIR.toString();

	public ItemHoldTrait(String mainHandItem, String offhandItem) {
		this.mainHandItem = mainHandItem;
		this.offhandItem = offhandItem;
	}

	public ItemHoldTrait(String mainHandItem) {
		this(mainHandItem, EMPTY);
	}

	public ItemStack getMainHandItem() {
		if (this.entity != null) {
			return this.entity.getItemInHand(InteractionHand.MAIN_HAND);
		}
		return null;
	}

	public ItemStack getOffHandItem() {
		if (this.entity != null) {
			return this.entity.getItemInHand(InteractionHand.OFF_HAND);
		}
		return null;
	}

	public void setMainHandItem(String mainHandItem) {
		this.mainHandItem = mainHandItem;
		if (this.entity != null) {
			this.entity.setItemInHand(InteractionHand.MAIN_HAND, Registers.item(mainHandItem).getDefaultInstance());
		}
	}

	public void setOffHandItem(String offhandItem) {
		this.offhandItem = offhandItem;
		if (this.entity != null) {
			this.entity.setItemInHand(InteractionHand.OFF_HAND, Registers.item(offhandItem).getDefaultInstance());
		}
	}

	@Override
	public void init(LivingEntity entity) {
		this.entity = entity;
		this.originalMainHandItem = this.getMainHandItem();
		this.originalOffHandItem = this.getOffHandItem();
		this.setMainHandItem(mainHandItem);
		this.setOffHandItem(offhandItem);
	}

	@Override
	public void uninit(LivingEntity mob) {
		this.entity.setItemInHand(InteractionHand.MAIN_HAND, originalMainHandItem);
		this.entity.setItemInHand(InteractionHand.OFF_HAND, originalOffHandItem);
	}
}
