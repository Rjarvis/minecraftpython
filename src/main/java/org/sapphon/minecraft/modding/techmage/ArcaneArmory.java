package org.sapphon.minecraft.modding.techmage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.apache.commons.io.FilenameUtils;
import org.sapphon.minecraft.modding.base.BasenameFinder;
import org.sapphon.minecraft.modding.minecraftpython.MinecraftPythonCreativeTab;
import org.sapphon.minecraft.modding.minecraftpython.ScriptLoaderConstants;
import org.sapphon.minecraft.modding.minecraftpython.problemhandlers.PythonProblemHandler;
import org.sapphon.minecraft.modding.minecraftpython.spells.CachingSpell;
import org.sapphon.minecraft.modding.minecraftpython.spells.ISpell;
import org.sapphon.minecraft.modding.minecraftpython.spells.NeverCachingSpell;
import org.sapphon.minecraft.modding.techmage.MagicWand;

import cpw.mods.fml.common.registry.LanguageRegistry;

public class ArcaneArmory {
	private List<MagicWand> wands = new ArrayList<MagicWand>();


	public static final int GLOBAL_WAND_COOLDOWN = 1500;
	
	
	private static ArcaneArmory SINGLETON;
	public static MinecraftPythonCreativeTab spellcraftersWandCreativeTab = new MinecraftPythonCreativeTab("arcane_armory", Items.blaze_rod);
	
	
	public static ArcaneArmory SINGLETON(){
		if(SINGLETON == null)
			SINGLETON = new ArcaneArmory(new File(ScriptLoaderConstants.TECHMAGE_SCRIPTS_PATH));
		return SINGLETON;
	}
	
	private ArcaneArmory(File scriptsDirectory){
		addWands(scriptsDirectory, spellcraftersWandCreativeTab);

	}

	private void addWands(File scriptsDirectory, MinecraftPythonCreativeTab creativeTab) {
		if(scriptsDirectory.canRead() && scriptsDirectory.isDirectory()){
			File[] unparsedScripts = scriptsDirectory.listFiles();
			
			for (File script : unparsedScripts) {
				if(isAPythonScript(script)){
					try{
						ISpell spellForWand = new CachingSpell(FilenameUtils.getBaseName(script.getAbsolutePath()).toLowerCase(), script);
						MagicWand magicWandToAdd = new MagicWand(spellForWand, SpellExperienceLevelArbiter.getRequiredExperienceLevelForSpell(spellForWand), creativeTab);
						wands.add(magicWandToAdd);
					}catch(Exception e){
						PythonProblemHandler.printErrorMessageToDialogBox(e);
					}
				}
				else if(script.isDirectory()){
					String directoryName = script.getName();
					String directoryNiceName = directoryName.substring(0, 1).toUpperCase() + directoryName.substring(1);
					LanguageRegistry.instance().addStringLocalization(
							"itemGroup."+directoryName, "en_US",
							directoryNiceName);
					addWands(script, new MinecraftPythonCreativeTab(directoryName, Items.blaze_rod));
				}
			}
			
		}
	}
	
	public List<String> getUniqueAuthorNames(){
		HashSet<String> listOfUniqueNames = new HashSet<String>();
		for (MagicWand wand : this.wands) {
			listOfUniqueNames.add(wand.getSpell().getAuthorName());
		}
		return new ArrayList<String>(listOfUniqueNames);
	}
	
	

	private boolean isAPythonScript(File script) {
		return FilenameUtils.getExtension(script.getAbsolutePath()).matches("[Pp][Yy][Cc]?");
	}
	
    public List<MagicWand> getWands(){
    	return new ArrayList<MagicWand>(wands);
    }
    
    private List<MagicWand> getWandsWrittenByAuthor(String authorName){
    	ArrayList<MagicWand> toReturn = new ArrayList<MagicWand>();
    	for (MagicWand wand : this.wands) {
			if(wand.getSpell().getAuthorName().equals(authorName)){
				toReturn.add(wand);
			}
		}
    	return toReturn;
    }
    
    public Map<String, List<MagicWand>> getWandsGroupedByAuthorName(){
    	HashMap<String, List<MagicWand>> toReturn = new HashMap<String, List<MagicWand>>();
    	for (String name : this.getUniqueAuthorNames()) {
			toReturn.put(name, this.getWandsWrittenByAuthor(name));
		}
    	return toReturn;
    }

	public boolean hasWandWithSpellNamed(String name) {
		for (MagicWand wand : wands) {
			if(wand.getSpell().getSpellShortName().equals(name)){
				return true;
			}
		}
		return false;
	}
	
	public MagicWand getWandBySpellName(String name){
		for (MagicWand wand : wands) {
			if(wand.getSpell().getSpellShortName().equals(name)){
				return wand;
			}
		}
		return null;	//TODO DANGER
	}
}
