package openperipheral.meta;

import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import openperipheral.ApiImplementation;
import openperipheral.api.IItemStackMetaBuilder;
import openperipheral.api.IItemStackMetaProvider;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;

@ApiImplementation
public class ItemStackMetadataBuilder implements IItemStackMetaBuilder {

	private static String getNameForItemStack(ItemStack is) {
		try {
			return is.getDisplayName();
		} catch (Exception e) {}

		try {
			return is.getUnlocalizedName();
		} catch (Exception e2) {}

		return "unknown";
	}

	public static String getRawNameForStack(ItemStack is) {
		try {
			return is.getUnlocalizedName().toLowerCase();
		} catch (Exception e) {}

		return "unknown";
	}

	private static final Map<String, Object> NULL;

	static {
		ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
		builder.put("id", "invalid");
		NULL = builder.build();
	}

	@Override
	public Map<String, Object> getItemStackMetadata(ItemStack itemstack) {
		if (itemstack == null) return NULL;
		Item item = itemstack.getItem();

		Map<String, Object> map = Maps.newHashMap();
		fillBasicProperties(map, item, itemstack);
		fillCustomProperties(map, item, itemstack);
		return map;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void fillCustomProperties(Map<String, Object> map, Item item, ItemStack itemstack) {
		final Iterable<IItemStackMetaProvider<?>> providers = MetaProvidersRegistry.ITEMS.getProviders(item.getClass());

		for (IItemStackMetaProvider provider : providers) {
			Object converted = provider.getMeta(item, itemstack);
			if (converted != null) {
				final String key = provider.getKey();
				map.put(key, converted);
			}
		}
	}

	private static void fillBasicProperties(Map<String, Object> map, Item item, ItemStack itemstack) {
		UniqueIdentifier id = GameRegistry.findUniqueIdentifierFor(item);

		map.put("id", id != null? id.toString() : "?");
		map.put("name", id != null? id.name : "?");
		map.put("mod_id", id != null? id.modId : "?");

		map.put("display_name", getNameForItemStack(itemstack));
		map.put("raw_name", getRawNameForStack(itemstack));
		map.put("qty", itemstack.stackSize);
		map.put("dmg", itemstack.getItemDamage());
		map.put("max_dmg", itemstack.getMaxDamage());
		map.put("max_size", itemstack.getMaxStackSize());
	}

	@Override
	public void register(IItemStackMetaProvider<?> provider) {
		MetaProvidersRegistry.ITEMS.addProvider(provider);
	}
}
