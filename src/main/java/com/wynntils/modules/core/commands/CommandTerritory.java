/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.core.commands;

import com.wynntils.ModCore;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.TerritoryProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.init.SoundEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.IClientCommand;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandTerritory extends CommandBase implements IClientCommand {

    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return false;
    }

    @Override
    public String getName() {
        return "territory";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return I18n.format("wynntils.commands.territory.usage");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length <= 0) {
            Minecraft.getMinecraft().player.playSound(SoundEvents.BLOCK_ANVIL_PLACE, 1.0f, 1.0f);

            throw new WrongUsageException(I18n.format("wynntils.commands.territory.error.wrong_usage"));
        }
        String territoryName = StringUtils.join(args, " ");
        Collection<TerritoryProfile> territories = WebManager.getTerritories().values();

        Optional<TerritoryProfile> selectedTerritory = territories.stream().filter(c -> c.getName().equalsIgnoreCase(territoryName)).findFirst();
        if(!selectedTerritory.isPresent()) {
            Minecraft.getMinecraft().player.playSound(SoundEvents.BLOCK_ANVIL_PLACE, 1.0f, 1.0f);

            throw new CommandException(I18n.format("wynntils.commands.territory.error.territory_not_valid"));
        }
        Minecraft.getMinecraft().player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0f, 10.0f);

        TerritoryProfile tp = selectedTerritory.get();

        int xMiddle = tp.getStartX() + ((tp.getEndX() - tp.getStartX())/2);
        int zMiddle = tp.getStartZ() + ((tp.getEndZ() - tp.getStartZ())/2);

        ModCore.mc().world.setSpawnPoint(new BlockPos(xMiddle, 0, zMiddle));

        TextComponentTranslation success = new TextComponentTranslation("wynntils.commands.territory.now_pointing", territoryName, xMiddle, zMiddle);
        success.getStyle().setColor(TextFormatting.GREEN);

        TextComponentTranslation warn = new TextComponentTranslation("wynntils.commands.territory.compass_warning");
        warn.getStyle().setColor(TextFormatting.AQUA);

        success.appendSibling(warn);

        TextComponentString separator = new TextComponentString("-----------------------------------------------------");
        separator.getStyle().setColor(TextFormatting.DARK_GRAY).setStrikethrough(true);

        sender.sendMessage(separator);
        sender.sendMessage(success);
        sender.sendMessage(separator);
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length >= 1) {
            return getListOfStringsMatchingLastWord(args, WebManager.getTerritories().values().stream().map(TerritoryProfile::getName).collect(Collectors.toList()));
        }
        return Collections.emptyList();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

}