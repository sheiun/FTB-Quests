package com.feed_the_beast.ftbquests.quest.reward;

import com.feed_the_beast.ftbquests.FTBQuests;
import com.feed_the_beast.ftbquests.gui.GuiSelectQuestObject;
import com.feed_the_beast.ftbquests.quest.Quest;
import com.feed_the_beast.ftbquests.quest.QuestObjectType;
import com.feed_the_beast.ftbquests.quest.loot.RewardTable;
import com.feed_the_beast.ftbquests.util.ConfigQuestObject;
import com.feed_the_beast.mods.ftbguilibrary.config.ConfigGroup;
import com.feed_the_beast.mods.ftbguilibrary.config.gui.GuiEditConfig;
import com.feed_the_beast.mods.ftbguilibrary.icon.Icon;
import com.feed_the_beast.mods.ftbguilibrary.widget.GuiIcons;
import me.shedaniel.architectury.annotations.ExpectPlatform;
import me.shedaniel.architectury.core.RegistryEntry;
import me.shedaniel.architectury.platform.Platform;
import me.shedaniel.architectury.registry.Registries;
import me.shedaniel.architectury.registry.Registry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public final class RewardType extends RegistryEntry<RewardType>
{
	private static Registry<RewardType> REGISTRY;

	public static void createRegistry()
	{
		if (Platform.isForge())
			postRegistryEvent(getRegistry());
	}

	@ExpectPlatform
	private static void postRegistryEvent(Registry<RewardType> REGISTRY)
	{
		throw new AssertionError();
	}

	public static Registry<RewardType> getRegistry()
	{
		if (REGISTRY == null)
		{
			ResourceLocation registryName = new ResourceLocation(FTBQuests.MOD_ID, "rewards");
			REGISTRY = Registries.get(FTBQuests.MOD_ID).<RewardType>builder(registryName)
					.saveToDisc()
					.syncToClients()
					.build();
		}
		return REGISTRY;
	}

	@Nullable
	public static Reward createReward(Quest quest, String id)
	{
		if (id.isEmpty())
		{
			id = FTBQuests.MOD_ID + ":item";
		}
		else if (id.indexOf(':') == -1)
		{
			id = FTBQuests.MOD_ID + ':' + id;
		}

		RewardType type = REGISTRY.get(new ResourceLocation(id));

		if (type == null)
		{
			return null;
		}

		return type.provider.create(quest);
	}

	@FunctionalInterface
	public interface Provider
	{
		Reward create(Quest quest);
	}

	public interface GuiProvider
	{
		@Environment(EnvType.CLIENT)
		void openCreationGui(Runnable gui, Quest quest, Consumer<Reward> callback);
	}

	public final Provider provider;
	private MutableComponent displayName;
	private Icon icon;
	private GuiProvider guiProvider;
	private boolean excludeFromListRewards;

	public RewardType(Provider p)
	{
		provider = p;
		displayName = null;
		icon = GuiIcons.MONEY_BAG;
		guiProvider = new GuiProvider()
		{
			@Override
			@Environment(EnvType.CLIENT)
			public void openCreationGui(Runnable gui, Quest quest, Consumer<Reward> callback)
			{
				Reward reward = provider.create(quest);

				if (reward instanceof RandomReward)
				{
					ConfigQuestObject<RewardTable> config = new ConfigQuestObject<>(QuestObjectType.REWARD_TABLE);
					new GuiSelectQuestObject<>(config, accepted -> {
						if (accepted)
						{
							((RandomReward) reward).table = config.value;
							callback.accept(reward);
						}
						gui.run();
					}).openGui();
					return;
				}

				ConfigGroup group = new ConfigGroup(FTBQuests.MOD_ID);
				reward.getConfig(reward.createSubGroup(group));
				group.savedCallback = accepted -> {
					if (accepted)
					{
						callback.accept(reward);
					}
					gui.run();
				};
				new GuiEditConfig(group).openGui();
			}
		};
	}

	public String getTypeForNBT()
	{
		return getRegistryName().getNamespace().equals(FTBQuests.MOD_ID) ? getRegistryName().getPath() : getRegistryName().toString();
	}

	public ResourceLocation getRegistryName()
	{
		return getRegistry().getId(this);
	}

	public RewardType setDisplayName(MutableComponent name)
	{
		displayName = name;
		return this;
	}

	public MutableComponent getDisplayName()
	{
		if (displayName == null)
		{
			ResourceLocation id = getRegistryName();
			displayName = id == null ? new TextComponent("error") : new TranslatableComponent("ftbquests.reward." + id.getNamespace() + '.' + id.getPath());
		}

		return displayName;
	}

	public RewardType setIcon(Icon i)
	{
		icon = i;
		return this;
	}

	public Icon getIcon()
	{
		return icon;
	}

	public RewardType setGuiProvider(GuiProvider p)
	{
		guiProvider = p;
		return this;
	}

	public GuiProvider getGuiProvider()
	{
		return guiProvider;
	}

	public RewardType setExcludeFromListRewards(boolean v)
	{
		excludeFromListRewards = v;
		return this;
	}

	public boolean getExcludeFromListRewards()
	{
		return excludeFromListRewards;
	}
}