package gtneioreplugin.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import bartworks.system.material.Werkstoff;
import bwcrossmod.galacticgreg.VoidMinerUtility;
import gregtech.api.util.GTLanguageManager;
import gregtech.api.util.GTUtility;
import gtneioreplugin.Config;
import gtneioreplugin.GTNEIOrePlugin;
import gtneioreplugin.plugin.gregtech5.PluginGT5VeinStat;
import gtneioreplugin.util.GT5OreLayerHelper.OreLayerWrapper;

import static bwcrossmod.galacticgreg.VoidMinerUtility.dropMapsByDimId;
import static bwcrossmod.galacticgreg.VoidMinerUtility.dropMapsByChunkProviderName;
import static bwcrossmod.galacticgreg.VoidMinerUtility.extraDropsDimMap;

// todo: yeet any opencsv usage.
public class CSVMaker implements Runnable {

    public void runSmallOres() {
        try {
            Iterator<Map.Entry<String, GT5OreSmallHelper.OreSmallWrapper>> it = GT5OreSmallHelper.mapOreSmallWrapper
                .entrySet()
                .iterator();
            List<SmallOre> SmallOreVeins = new ArrayList<>();
            while (it.hasNext()) {
                SmallOre oremix = new SmallOre();

                Map.Entry<String, GT5OreSmallHelper.OreSmallWrapper> pair = it.next();
                GT5OreSmallHelper.OreSmallWrapper oreLayer = pair.getValue();

                Map<String, Boolean> Dims = GT5OreSmallHelper.bufferedDims.get(oreLayer);

                oremix.setOreName(oreLayer.oreGenName);
                oremix.setOreMeta(oreLayer.oreMeta);
                oremix.setHeight(oreLayer.worldGenHeightRange);
                oremix.setAmount(oreLayer.amountPerChunk);
                oremix.setDims(Dims);

                SmallOreVeins.add(oremix);

                it.remove(); // avoids a ConcurrentModificationException
            }

            BufferedWriter one = Files.newBufferedWriter(
                GTNEIOrePlugin.instanceDir.toPath()
                    .resolve(Config.CSVnameSmall));
            Collections.sort(SmallOreVeins);

            // header first
            one.write(SmallOre.getCsvHeader());
            one.newLine();
            for (SmallOre ore : SmallOreVeins) {
                one.write(ore.getCsvEntry());
                one.newLine();
            }
            one.flush();
            one.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        runVeins();
        runSmallOres();
        runVoidMiner();
    }

    private static String getBWOreName(short meta) {
        final Werkstoff werkstoff = Werkstoff.werkstoffHashMap.getOrDefault(meta, null);
        return GTLanguageManager.getTranslation("bw.blocktype.ore")
            .replace("%material", werkstoff.getLocalizedName());
    }

    private static String getOreName(OreLayerWrapper oreLayer, int veinLayer) {
        final String oreName;
        if ((oreLayer.bwOres & 0b1000 >> veinLayer) != 0) oreName = getBWOreName(oreLayer.Meta[veinLayer]);
        else oreName = PluginGT5VeinStat.getGTOreLocalizedName(oreLayer.Meta[veinLayer]);
        return oreName;
    }
    
    public void runVeins() {
        try {
            Iterator<Map.Entry<String, OreLayerWrapper>> it = GT5OreLayerHelper.mapOreLayerWrapper.entrySet()
                .iterator();
            List<Oremix> OreVeins = new ArrayList<>();
            while (it.hasNext()) {
                Oremix oremix = new Oremix();

                Map.Entry<String, OreLayerWrapper> pair = it.next();
                Map<String, Boolean> Dims = GT5OreLayerHelper.bufferedDims.get(pair.getValue());
                OreLayerWrapper oreLayer = pair.getValue();
                oremix.setOreMixName(oreLayer.localizedName);
                oremix.setPrimary(getOreName(oreLayer,0));
                oremix.setSecondary(getOreName(oreLayer,1));
                oremix.setInbetween(getOreName(oreLayer,2));
                oremix.setSporadic(getOreName(oreLayer,3));
                oremix.setSize(oreLayer.size);
                oremix.setHeight("H" + oreLayer.worldGenHeightRange);
                oremix.setDensity(oreLayer.density);
                oremix.setWeight(oreLayer.randomWeight);
                oremix.setOreMixIDs(
                    Integer.toString(oreLayer.Meta[0]) + "|"
                        + Integer.toString(oreLayer.Meta[1])
                        + "|"
                        + Integer.toString(oreLayer.Meta[2])
                        + "|"
                        + Integer.toString(oreLayer.Meta[3]));
                oremix.setDims(Dims);
                OreVeins.add(oremix);

                it.remove(); // avoids a ConcurrentModificationException
            }
            BufferedWriter one = Files.newBufferedWriter(
                GTNEIOrePlugin.instanceDir.toPath()
                    .resolve(Config.CSVName));
            Collections.sort(OreVeins);

            // header first
            one.write(Oremix.getCsvHeader());
            one.newLine();
            for (Oremix ore : OreVeins) {
                one.write(ore.getCsvEntry());
                one.newLine();
            }
            one.flush();
            one.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void runVoidMiner() {try {
        BufferedWriter one = Files.newBufferedWriter(
                GTNEIOrePlugin.instanceDir.toPath()
                    .resolve("VoidMiner.csv"));

        one.write(new VoidMinerLine("Item", "Weight").toString());
        one.newLine();
        
        dropMapsByDimId.forEach((dimID, map) -> {
            one.write(new VoidMinerLine("!!dimID!!", dimID.toString()).toString());
            one.newLine();
            
            List<VoidMinerLine> list = solveDropMap(one, map);
            list.forEach((line) -> {
                one.write(line.toString());
                one.newLine();
            });
        });

        dropMapsByChunkProviderName.forEach((chunkProviderName, map) -> {
            one.write(new VoidMinerLine("chunkProviderName", chunkProviderName).toString());
            one.newLine();
            
            List<VoidMinerLine> list = solveDropMap(one, map);
            list.forEach((line) -> {
                one.write(line.toString());
                one.newLine();
            });
        });

        extraDropsDimMap.forEach((dimID, map) -> {
            one.write(new VoidMinerLine("EXTRA!!dimID!!", dimID.toString()).toString());
            one.newLine();
            
            List<VoidMinerLine> list = solveDropMap(one, map);
            list.forEach((line) -> {
                one.write(line.toString());
                one.newLine();
            });
        });

        one.write(new VoidMinerLine("", "").toString());
        one.newLine();

        one.write(new VoidMinerLine("Item", "Name").toString());
        one.newLine();
        map_ItemID_ItemName.forEach((itemID, name) -> {
            one.write(new VoidMinerLine(itemID, name).toString());
            one.newLine();
        });

        one.flush();
        one.close();
        } catch (IOException e) {throw new IOException(e);}
    }

    private static Map<String, String> map_ItemID_ItemName = new HashMap<>();
    
    private static List<VoidMinerLine> solveDropMap(BufferedWriter one, VoidMinerUtility.DropMap map) {
        List<VoidMinerLine> list = new ArrayList<>();

        map.getInternalMap().forEach((GTItemId, weight) -> {
            String unLocName = GTItemId.getItemStack().getUnlocalizedName();
            map_ItemID_ItemName.putIfAbsent(unLocName, GTItemId.getItemStack().getDisplayName());
            list.add(new VoidMinerLine(unLocName, weight.toString()).toString());
        });

        return list;
    }

    private static class VoidMinerLine {
        public String[] values = new String[2];
        public VoidMinerLine(String a, String b){
            this.values[0] = a;
            this.values[1] = b;
        }
        
        @Override
        public String toString(){
            return String.join(",", values);
        }
    }
}
