package dev.ftb.mods.ftbquests.gui.quests;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.*;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.ui.misc.ButtonListBaseScreen;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import dev.ftb.mods.ftbquests.net.EditObjectMessage;
import dev.ftb.mods.ftbquests.quest.task.ItemTask;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.theme.property.ThemeProperties;
import dev.latvian.mods.itemfilters.api.IStringValueFilter;
import dev.latvian.mods.itemfilters.api.ItemFiltersAPI;
import dev.latvian.mods.itemfilters.api.ItemFiltersItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class TaskButton extends Button {
	public final QuestScreen questScreen;
	public Task task;

	public TaskButton(Panel panel, Task t) {
		super(panel, t.getTitle(), Icons.ACCEPT);
		questScreen = (QuestScreen) panel.getGui();
		task = t;
	}

	@Override
	public boolean mousePressed(MouseButton button) {
		if (isMouseOver()) {
			if (button.isRight() || getWidgetType() != WidgetType.DISABLED) {
				onClicked(button);
			}

			return true;
		}

		return false;
	}

	@Override
	public void onClicked(MouseButton button) {
		if (button.isLeft()) {
			task.onButtonClicked(this, !(task.invalid || !questScreen.file.self.canStartTasks(task.quest) || questScreen.file.self.isCompleted(task)));
		} else if (button.isRight() && questScreen.file.canEdit()) {
			playClickSound();
			List<ContextMenuItem> contextMenu = new ArrayList<>();

			if (task instanceof ItemTask) {
				ItemTask i = (ItemTask) task;
				var tags = i.item.getItem().builtInRegistryHolder().tags().map(TagKey::location).toList();

				if (!tags.isEmpty() && !ItemFiltersAPI.isFilter(i.item)) {
					contextMenu.add(new ContextMenuItem(Component.translatable("ftbquests.task.ftbquests.item.convert_tag"), ThemeProperties.RELOAD_ICON.get(), () -> {
						ItemStack tagFilter = new ItemStack(ItemFiltersItems.TAG.get());

						if (tags.size() == 1) {
							String tag = tags.iterator().next().toString();
							((IStringValueFilter) tagFilter.getItem()).setValue(tagFilter, tag);
							i.item = tagFilter;

							if (i.title.isEmpty()) {
								i.title = "Any #" + tag;
							}

							new EditObjectMessage(i).sendToServer();
						} else {
							new ButtonListBaseScreen() {
								@Override
								public void addButtons(Panel panel) {
									for (ResourceLocation s : tags) {
										panel.add(new SimpleTextButton(panel, Component.literal(s.toString()), Icon.EMPTY) {
											@Override
											public void onClicked(MouseButton button) {
												questScreen.openGui();
												((IStringValueFilter) tagFilter.getItem()).setValue(tagFilter, s.toString());
												i.item = tagFilter;

												if (i.title.isEmpty()) {
													i.title = "Any #" + s;
												}

												new EditObjectMessage(i).sendToServer();
											}
										});
									}
								}
							}.openGui();
						}
					}));

					contextMenu.add(ContextMenuItem.SEPARATOR);
				}
			}

			questScreen.addObjectMenuItems(contextMenu, getGui(), task);
			getGui().openContextMenu(contextMenu);
		}
	}

	@Override
	@Nullable
	public Object getIngredientUnderMouse() {
		return task.getIngredient();
	}

	@Override
	public void addMouseOverText(TooltipList list) {
		questScreen.addInfoTooltip(list, task);

		if (task.addTitleInMouseOverText()) {
			list.add(getTitle());
		}

		if (questScreen.file.self.canStartTasks(task.quest)) {
			long maxp = task.getMaxProgress();
			long progress = questScreen.file.self.getProgress(task);

			if (maxp > 1L) {
				if (task.hideProgressNumbers()) {
					list.add(Component.literal("[" + task.getRelativeProgressFromChildren(questScreen.file.self) + "%]").withStyle(ChatFormatting.DARK_GREEN));
				} else {
					String max = isShiftKeyDown() ? Long.toUnsignedString(maxp) : task.formatMaxProgress();
					String prog = isShiftKeyDown() ? Long.toUnsignedString(progress) : task.formatProgress(questScreen.file.self, progress);

					if (maxp < 100L) {
						list.add(Component.literal((progress > maxp ? max : prog) + " / " + max).withStyle(ChatFormatting.DARK_GREEN));
					} else {
						list.add(Component.literal((progress > maxp ? max : prog) + " / " + max).withStyle(ChatFormatting.DARK_GREEN).append(Component.literal(" [" + task.getRelativeProgressFromChildren(questScreen.file.self) + "%]").withStyle(ChatFormatting.DARK_GRAY)));
					}

				}
			}
		} else {
			//list.add(TextFormatting.DARK_GRAY + "[0%]");
		}

		task.addMouseOverText(list, questScreen.file.self);
	}

	@Override
	public void drawBackground(PoseStack matrixStack, Theme theme, int x, int y, int w, int h) {
		if (isMouseOver()) {
			super.drawBackground(matrixStack, theme, x, y, w, h);
		}
	}

	@Override
	public void drawIcon(PoseStack matrixStack, Theme theme, int x, int y, int w, int h) {
		task.drawGUI(questScreen.file.self, matrixStack, x, y, w, h);
	}

	@Override
	public void draw(PoseStack matrixStack, Theme theme, int x, int y, int w, int h) {
		int bs = h >= 32 ? 32 : 16;
		GuiHelper.setupDrawing();
		drawBackground(matrixStack, theme, x, y, w, h);
		drawIcon(matrixStack, theme, x + (w - bs) / 2, y + (h - bs) / 2, bs, bs);

		if (questScreen.file.self == null) {
			return;
		} else if (questScreen.contextMenu != null) {
			//return;
		}

		if (questScreen.file.self.isCompleted(task)) {
			matrixStack.pushPose();
			matrixStack.translate(0, 0, 200);
			RenderSystem.enableBlend();
			ThemeProperties.CHECK_ICON.get().draw(matrixStack, x + w - 9, y + 1, 8, 8);
			matrixStack.popPose();
		} else {
			MutableComponent s = task.getButtonText();

			if (s.getContents() != ComponentContents.EMPTY) {
				matrixStack.pushPose();
				matrixStack.translate(x + 19F - theme.getStringWidth(s) / 2F, y + 15F, 200F);
				matrixStack.scale(0.5F, 0.5F, 1F);
				RenderSystem.enableBlend();
				theme.drawString(matrixStack, s, 0, 0, Color4I.WHITE, Theme.SHADOW);
				matrixStack.popPose();
			}
		}
	}
}
