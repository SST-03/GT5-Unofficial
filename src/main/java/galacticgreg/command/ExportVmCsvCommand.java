package galacticgreg.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import gregtech.commands.GTBaseCommand;
import gtneioreplugin.util.CSVMaker;

public class ExportVmCsvCommand extends GTBaseCommand {

    public ExportVmCsvCommand() {
        super("exportvmcsv");
    }

    @Override
    public String getCommandName() {
        return "exportvmcsv";
    }

    @Override
    public String getCommandUsage(ICommandSender pCommandSender) {
        return "exportvmcsv";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        try {
            if (sender instanceof EntityPlayer player) {
                new CSVMaker().runVoidMiner();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        // Command is only enabled for actual players and only if they are OP-ed
        return isOpedPlayer(sender);
    }
}
