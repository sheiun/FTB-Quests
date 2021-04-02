package dev.ftb.mods.ftbquests.integration.kubejs;

import dev.ftb.mods.ftbquests.FTBQuests;
import dev.ftb.mods.ftbquests.quest.ChangeProgress;
import dev.ftb.mods.ftbquests.quest.QuestFile;
import dev.ftb.mods.ftbquests.quest.QuestObjectBase;
import dev.ftb.mods.ftbquests.quest.QuestObjectType;
import dev.ftb.mods.ftbquests.quest.QuestShape;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbteams.FTBTeamsAPI;
import dev.latvian.kubejs.player.PlayerJS;
import dev.latvian.kubejs.world.WorldJS;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class FTBQuestsKubeJSWrapper {
	public Map<String, QuestShape> getQuestShapes() {
		return QuestShape.MAP;
	}

	public Map<String, QuestObjectType> getQuestObjectTypes() {
		return QuestObjectType.NAME_MAP.map;
	}

	public Map<String, ChangeProgress> getChangeProgressTypes() {
		return ChangeProgress.NAME_MAP.map;
	}

	public QuestFile getFile(WorldJS world) {
		return FTBQuests.PROXY.getQuestFile(world.minecraftWorld.isClientSide());
	}

	@Nullable
	public TeamData getData(WorldJS world, UUID uuid) {
		return getFile(world).getData(FTBTeamsAPI.getPlayerTeamID(uuid));
	}

	@Nullable
	public TeamData getData(PlayerJS player) {
		return getFile(player.getWorld()).getData(player.minecraftPlayer);
	}

	@Nullable
	public QuestObjectBase getObject(WorldJS world, Object id) {
		QuestFile file = getFile(world);
		return file.getBase(file.getID(id));
	}
}