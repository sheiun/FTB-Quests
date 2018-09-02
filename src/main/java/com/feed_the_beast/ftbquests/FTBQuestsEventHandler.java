package com.feed_the_beast.ftbquests;

import com.feed_the_beast.ftblib.events.FTBLibPreInitRegistryEvent;
import com.feed_the_beast.ftbquests.block.BlockProgressDetector;
import com.feed_the_beast.ftbquests.block.BlockProgressScreen;
import com.feed_the_beast.ftbquests.block.BlockProgressScreenPart;
import com.feed_the_beast.ftbquests.block.BlockQuestChest;
import com.feed_the_beast.ftbquests.block.BlockScreen;
import com.feed_the_beast.ftbquests.block.BlockScreenPart;
import com.feed_the_beast.ftbquests.block.ItemBlockProgressScreen;
import com.feed_the_beast.ftbquests.block.ItemBlockScreen;
import com.feed_the_beast.ftbquests.item.ItemQuestBook;
import com.feed_the_beast.ftbquests.item.ItemScript;
import com.feed_the_beast.ftbquests.item.ItemXPVial;
import com.feed_the_beast.ftbquests.quest.tasks.DependencyTask;
import com.feed_the_beast.ftbquests.quest.tasks.FluidTask;
import com.feed_the_beast.ftbquests.quest.tasks.ForgeEnergyTask;
import com.feed_the_beast.ftbquests.quest.tasks.ItemTask;
import com.feed_the_beast.ftbquests.quest.tasks.QuestTaskType;
import com.feed_the_beast.ftbquests.tile.TileProgressDetector;
import com.feed_the_beast.ftbquests.tile.TileProgressScreenCore;
import com.feed_the_beast.ftbquests.tile.TileProgressScreenPart;
import com.feed_the_beast.ftbquests.tile.TileQuestChest;
import com.feed_the_beast.ftbquests.tile.TileScreenCore;
import com.feed_the_beast.ftbquests.tile.TileScreenPart;
import com.feed_the_beast.ftbquests.util.ConfigQuestObject;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBQuests.MOD_ID)
public class FTBQuestsEventHandler
{
	private static Block withName(Block block, String name)
	{
		block.setCreativeTab(FTBQuests.TAB);
		block.setRegistryName(name);
		block.setTranslationKey(FTBQuests.MOD_ID + "." + name);
		return block;
	}

	private static Item withName(Item item, String name)
	{
		item.setCreativeTab(FTBQuests.TAB);
		item.setRegistryName(name);
		item.setTranslationKey(FTBQuests.MOD_ID + "." + name);
		return item;
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event)
	{
		event.getRegistry().registerAll(
				withName(new BlockScreen(), "screen"),
				withName(new BlockScreenPart(), "screen_part"),
				withName(new BlockProgressDetector(), "progress_detector"),
				withName(new BlockProgressScreen(), "progress_screen"),
				withName(new BlockProgressScreenPart(), "progress_screen_part"),
				withName(new BlockQuestChest(), "chest")
		);

		GameRegistry.registerTileEntity(TileScreenCore.class, new ResourceLocation(FTBQuests.MOD_ID, "screen_core"));
		GameRegistry.registerTileEntity(TileScreenPart.class, new ResourceLocation(FTBQuests.MOD_ID, "screen_part"));
		GameRegistry.registerTileEntity(TileProgressDetector.class, new ResourceLocation(FTBQuests.MOD_ID, "progress_detector"));
		GameRegistry.registerTileEntity(TileProgressScreenCore.class, new ResourceLocation(FTBQuests.MOD_ID, "progress_screen_core"));
		GameRegistry.registerTileEntity(TileProgressScreenPart.class, new ResourceLocation(FTBQuests.MOD_ID, "progress_screen_part"));
		GameRegistry.registerTileEntity(TileQuestChest.class, new ResourceLocation(FTBQuests.MOD_ID, "chest"));
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().registerAll(
				new ItemBlockScreen(FTBQuestsBlocks.SCREEN).setRegistryName("screen"),
				new ItemBlock(FTBQuestsBlocks.PROGRESS_DETECTOR).setRegistryName("progress_detector"),
				new ItemBlockProgressScreen(FTBQuestsBlocks.PROGRESS_SCREEN).setRegistryName("progress_screen"),
				new ItemBlock(FTBQuestsBlocks.CHEST).setRegistryName("chest"),

				withName(new ItemQuestBook(), "book"),
				withName(new ItemXPVial(), "xp_vial"),
				withName(new ItemScript(), "script")
		);
	}

	@SubscribeEvent
	public static void registerFTBLib(FTBLibPreInitRegistryEvent event)
	{
		event.getRegistry().registerConfigValueProvider(ConfigQuestObject.ID, () -> new ConfigQuestObject(""));
	}

	@SubscribeEvent
	public static void registerTasks(RegistryEvent.Register<QuestTaskType> event)
	{
		event.getRegistry().registerAll(
				new QuestTaskType(ItemTask.class, ItemTask::new).setRegistryName("item"),
				new QuestTaskType(FluidTask.class, FluidTask::new).setRegistryName("fluid"),
				new QuestTaskType(ForgeEnergyTask.class, ForgeEnergyTask::new).setRegistryName("forge_energy"),
				new QuestTaskType(DependencyTask.TaskDep.class, DependencyTask.TaskDep::new).setRegistryName("task"),
				new QuestTaskType(DependencyTask.QuestDep.class, DependencyTask.QuestDep::new).setRegistryName("quest"),
				new QuestTaskType(DependencyTask.ChapterDep.class, DependencyTask.ChapterDep::new).setRegistryName("chapter"),
				new QuestTaskType(DependencyTask.VariableDep.class, DependencyTask.VariableDep::new).setRegistryName("variable")
		);
	}
}