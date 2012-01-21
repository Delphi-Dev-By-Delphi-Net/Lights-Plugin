package delphi.net.lights;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Lights extends JavaPlugin {
	
	public final String pName = "Lights: ";
	public final String pVersion = "0.1.0";
	Logger log = Logger.getLogger("Minecraft");
	public FileConfiguration config;
	private LightsCommandExecutor lce = new LightsCommandExecutor(this);
	PluginManager pm;
	private final LightsPlayerListener interactionListener = new LightsPlayerListener(this);
	private final LightsBlockListener blockListener = new LightsBlockListener(this);
	
	//Variables Loaded at startup
	public int totalArrays;
	public ArrayList<String> arrays = new ArrayList<String>();
	public ArrayList<Block> lightBlocks = new ArrayList<Block>();
	public ArrayList<Block> switchBlocks = new ArrayList<Block>();
	public ArrayList<Block> switchStore = new ArrayList<Block>();
	ArrayList<String> arrayNames;
	
	//edit mode stuff
	public boolean editing;
	private String curEditingArray;
	public String playerEditing;
	public boolean placeMode=false;
	private int totalL=0;
	private int totalS=0;
	
	// the onDisable method
	public void onDisable() {
		log.info(pName+"Disabled");
		
	}

	// The onEnebale method
	public void onEnable() {
		log.info(pName+"Starting");
		config = getConfig();
		pm = this.getServer().getPluginManager();
		//lightArrays = new String[1000];
		
		// set this boolean equal to the value of FIRST_RUN in the MAIN section
		boolean firstRun = config.getConfigurationSection("MAIN").getBoolean("FIRST_RUN");
		// if firstRun is true do the initial setup, if not just load the data
		if(firstRun){
			config.getConfigurationSection("MAIN").set("FIRST_RUN", false);
			config.getConfigurationSection("MAIN").set("TOTAL_ARRAYS", 0); // sets the total number of arrays to 0
			config.createSection("ARRAY_INDEXES");
			config.createSection("ARRAYS"); //create a section to hold all of the arrays
			saveConfig();
			//set variables to default values
			totalArrays =0;
			log.info(pName+"First Run Setup Done.OK");
		}else{
			loadArrayIndexes();
			totalArrays = config.getConfigurationSection("MAIN").getInt("TOTAL_ARRAYS");
			log.info(pName+"Load Complete,");
			log.info(pName+"Started.OK");
			
		}
		
		//set the exicutor for the commands
		getCommand("lcreate").setExecutor(lce);
		getCommand("ladd").setExecutor(lce);
		getCommand("lswadd").setExecutor(lce);
		getCommand("lfinish").setExecutor(lce);
		getCommand("lon").setExecutor(lce);
		getCommand("loff").setExecutor(lce);
		getCommand("linit").setExecutor(lce);
		getCommand("llist").setExecutor(lce);
		getCommand("lplacemode").setExecutor(lce);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, interactionListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Event.Priority.Normal, this);
		log.info(pName+"Enabled");
	}
	
	// Creates a new Light Array
	public void createArray(String arrayName, Player p) {
		if(editing && playerEditing.equals(p.getDisplayName().toString())){
			p.sendMessage("You are allready creating an array");
		}else if (editing) {
			p.sendMessage(playerEditing+" is allready creating an array");
			p.sendMessage("Please wait for them to finish");
		}else{
			if (config.getConfigurationSection("ARRAYS").getConfigurationSection(arrayName) != null) {
				p.sendMessage("An array called " + arrayName+ " Allready Exists");
			} else {
				String playerName = p.getDisplayName().toString();
				config.getConfigurationSection("ARRAYS").createSection(arrayName);
				config.getConfigurationSection("ARRAYS").getConfigurationSection(arrayName).set("INDEX", totalArrays);
				config.getConfigurationSection("ARRAYS").getConfigurationSection(arrayName).set("OWNER", playerName);
				config.getConfigurationSection("ARRAYS").getConfigurationSection(arrayName).set("STATE", true);//true for on false for off, must be true initialy
				config.getConfigurationSection("ARRAYS").getConfigurationSection(arrayName).set("TOTAL_LIGHTS", 0);
				config.getConfigurationSection("ARRAYS").getConfigurationSection(arrayName).createSection("LIGHTS");
				config.getConfigurationSection("ARRAYS").getConfigurationSection(arrayName).set("TOTAL_SWITCHES", 0);
				config.getConfigurationSection("ARRAYS").getConfigurationSection(arrayName).createSection("SWITCHES");
				config.getConfigurationSection("ARRAYS").getConfigurationSection(arrayName).createSection("SENSOR");
				config.getConfigurationSection("ARRAYS").getConfigurationSection(arrayName).set("IGNORE", false);
				addArrayIndex(arrayName);
				totalArrays++;
				config.getConfigurationSection("MAIN").set("TOTAL_ARRAYS", totalArrays);
				saveConfig();
				playerEditing = p.getDisplayName().toString();
				p.sendMessage("Array created and is now in edit mode");
				startEditing(arrayName);
			}
		}
	}

	//add array to indexes string array
	public void addArrayIndex(String name) {
		//lightArrays[totalArrays] = name;
		arrays.add(name);
		saveArrayIndexes();
	}

	// Add a light to the currently editing array
	public void addLight(Player player) {
		if (editing) {
			String n = player.getDisplayName().toString();
			if(n.endsWith(playerEditing)){
				Player p = player;
				Block targetBlock = p.getTargetBlock(null, 10);
				if (targetBlock.getType().equals(Material.GLOWSTONE)) {
					if (lightBlocks.contains(targetBlock)) {
						p.sendMessage("Light allready in this array");
					} else {
						lightBlocks.add(targetBlock);
						p.sendMessage("Light Added");
					}

				} else {
					p.sendMessage("Only Glowstone can be added as a light");
				}
			}else{
				player.sendMessage(playerEditing+" is editing u can't use this");
			}
			
		} else {
			player.sendMessage("You can only add a light when you are editing an array");
		}
	}

	// Add switch to the currently editing array
	public void addSwitch(Player player) {
		if (editing) {
			String n = player.getDisplayName().toString();
			if(n.equals(playerEditing)){
				Player p = player;
				Block targetBlock = p.getTargetBlock(null, 10);
				if (targetBlock.getType().equals(Material.STONE_BUTTON)) {
					if (switchBlocks.contains(targetBlock)) {
						p.sendMessage("Switch allready in this array");
					} else {
						switchBlocks.add(targetBlock);
						p.sendMessage("Switch Added");
					}

				} else {
					p.sendMessage("Only Stone Buttons can be added as a switch");
				}
			}else{
				player.sendMessage(playerEditing+" is editing u can't use this");
			}
			
		} else {
			player.sendMessage("You can only add a switch when you are editing an array");
		}
	}

	// saves array indexes to file
	public void saveArrayIndexes() {
		for (int i = 0; i < arrays.size(); i++) {
			config.getConfigurationSection("ARRAY_INDEXES").set("INDEX_" + i,arrays.get(i));
		}
		saveConfig();
	}

	// load array indexes from file
	public void loadArrayIndexes() {
		for (int i = 0; config.getConfigurationSection("ARRAY_INDEXES").contains("INDEX_" + i); i++) {
			String name = config.getConfigurationSection("ARRAY_INDEXES").getString("INDEX_" + i);
			arrays.add(name);
		}
	}

	//method for saving a blocks location
	public void saveLightBlocks() {
		for(int i=0; i < lightBlocks.size(); i++){
			Block b = lightBlocks.get(i);
			int x = b.getX();
			int y = b.getY();
			int z = b.getZ();
			config.getConfigurationSection("ARRAYS").getConfigurationSection(curEditingArray).getConfigurationSection("LIGHTS").set("Light_"+i+"_x", x);
			config.getConfigurationSection("ARRAYS").getConfigurationSection(curEditingArray).getConfigurationSection("LIGHTS").set("Light_"+i+"_y", y);
			config.getConfigurationSection("ARRAYS").getConfigurationSection(curEditingArray).getConfigurationSection("LIGHTS").set("Light_"+i+"_z", z);
			totalL++;
			saveConfig();
		}
		lightBlocks.clear();
		config.getConfigurationSection("ARRAYS").getConfigurationSection(curEditingArray).set("TOTAL_LIGHTS", totalL);
		saveConfig();
	}
	
	//method for saving a blocks location
	public void saveSwitchBlocks() {
		for(int i=0; i < switchBlocks.size(); i++){
				Block b = switchBlocks.get(i);
				int x = b.getX();
				int y = b.getY();
				int z = b.getZ();
				config.getConfigurationSection("ARRAYS").getConfigurationSection(curEditingArray).getConfigurationSection("SWITCHES").set("Switch_"+i+"_x", x);
				config.getConfigurationSection("ARRAYS").getConfigurationSection(curEditingArray).getConfigurationSection("SWITCHES").set("Switch_"+i+"_y", y);
				config.getConfigurationSection("ARRAYS").getConfigurationSection(curEditingArray).getConfigurationSection("SWITCHES").set("Switch_"+i+"_z", z);
				totalS++;
				saveConfig();
			}
			switchBlocks.clear();
			config.getConfigurationSection("ARRAYS").getConfigurationSection(curEditingArray).set("TOTAL_SWITCHES", totalS);
		}
	
	//enters edit mode
	public void startEditing(String array) {
		editing=true;
		curEditingArray = array;
	}
	
	// leves edit mode
	public void stopEditing(Player p) {
		String n = p.getDisplayName().toString();
		if(n.equals(playerEditing)){
			saveLightBlocks();
			saveSwitchBlocks();
			editing=false;
			placeMode=false;
			p.sendMessage("Light Array "+curEditingArray+" Created");
			p.sendMessage("Array contains "+totalL+" Lights");
			p.sendMessage("Array contains "+totalS+" Switches");
			log.info(p.getDisplayName().toString()+" Created light array "+curEditingArray);
			totalL=0;
			totalS=0;
			curEditingArray="";
			playerEditing="";
		}else{
			p.sendMessage(playerEditing+" is editing you can't use this");
		}
		
		
	}
	
	// turns an array on
	public void turnON(String name, Player p){
		ArrayList<Block> lightsToChange = new ArrayList<Block>();
		if(config.getConfigurationSection("ARRAYS").getConfigurationSection(name) !=null){
			if(!config.getConfigurationSection("ARRAYS").getConfigurationSection(name).getBoolean("STATE")){
				for(int i=0; config.getConfigurationSection("ARRAYS").getConfigurationSection(name).getConfigurationSection("LIGHTS").contains("Light_"+i+"_x"); i++){
					Location l = p.getLocation();
					World w = l.getWorld();
					int x = config.getConfigurationSection("ARRAYS").getConfigurationSection(name).getConfigurationSection("LIGHTS").getInt("Light_"+i+"_x");
					int y = config.getConfigurationSection("ARRAYS").getConfigurationSection(name).getConfigurationSection("LIGHTS").getInt("Light_"+i+"_y");
					int z = config.getConfigurationSection("ARRAYS").getConfigurationSection(name).getConfigurationSection("LIGHTS").getInt("Light_"+i+"_z");
					Block b = w.getBlockAt(x, y, z);
					lightsToChange.add(b);
				}
				for(int i=0; i < lightsToChange.size(); i++){
					Block b = lightsToChange.get(i);
					if(b.getType().equals(Material.OBSIDIAN)){
						b.setType(Material.GLOWSTONE);
					}
				}
				lightsToChange.clear();
				lightsToChange =null;
				config.getConfigurationSection("ARRAYS").getConfigurationSection(name).set("STATE", true);
				saveConfig();
			}else{
				p.sendMessage("Lights in array "+name+" are allready on");
			}
		}else{
			p.sendMessage("Array does not exist");
		}
	}
	
	//turn an array off
	public void turnOFF(String name, Player p){
		ArrayList<Block> lightsToChange = new ArrayList<Block>();
		if(config.getConfigurationSection("ARRAYS").getConfigurationSection(name) !=null){
			if(config.getConfigurationSection("ARRAYS").getConfigurationSection(name).getBoolean("STATE")){
				for(int i=0; config.getConfigurationSection("ARRAYS").getConfigurationSection(name).getConfigurationSection("LIGHTS").contains("Light_"+i+"_x"); i++){
					Location l = p.getLocation();
					World w = l.getWorld();
					int x = config.getConfigurationSection("ARRAYS").getConfigurationSection(name).getConfigurationSection("LIGHTS").getInt("Light_"+i+"_x");
					int y = config.getConfigurationSection("ARRAYS").getConfigurationSection(name).getConfigurationSection("LIGHTS").getInt("Light_"+i+"_y");
					int z = config.getConfigurationSection("ARRAYS").getConfigurationSection(name).getConfigurationSection("LIGHTS").getInt("Light_"+i+"_z");
					Block b = w.getBlockAt(x, y, z);
					lightsToChange.add(b);
				}
				for(int i=0; i < lightsToChange.size(); i++){
					Block b = lightsToChange.get(i);
					if(b.getType().equals(Material.GLOWSTONE)){
						b.setType(Material.OBSIDIAN);
					}
				}
				lightsToChange.clear();
				lightsToChange =null;
				config.getConfigurationSection("ARRAYS").getConfigurationSection(name).set("STATE", false);
				saveConfig();
			}else{
				p.sendMessage("Lights in array "+name+" are allready off");
			}
		}else{
			p.sendMessage("Array does not exist");
		}
	}

	//load array names
	public void loadArrayNames(){
		arrayNames = null;
		arrayNames = new ArrayList<String>();//holds the array names
		//get array names from index section
		for(int i=0; config.getConfigurationSection("ARRAY_INDEXES").contains("INDEX_"+i); i++){
			arrayNames.add(config.getConfigurationSection("ARRAY_INDEXES").getString("INDEX_"+i));
		}
	}
	
	//Initialize Method called by /linit and oneneble and /lfinnish
	public void loadSwitchBlocks(Player p) {
		loadArrayNames();
		for(int index=0; index <arrayNames.size(); index++){
			String name = arrayNames.get(index);
			// loop through the array names and get the Switches in each array
			for (int i=0; config.getConfigurationSection("ARRAYS").getConfigurationSection(name).getConfigurationSection("SWITCHES").contains("Switch_"+i+"_x"); i++){
				Location l = p.getLocation();
				World w = l.getWorld();
				int x = config.getConfigurationSection("ARRAYS").getConfigurationSection(name).getConfigurationSection("SWITCHES").getInt("Switch_"+i+"_x");
				int y = config.getConfigurationSection("ARRAYS").getConfigurationSection(name).getConfigurationSection("SWITCHES").getInt("Switch_"+i+"_y");
				int z = config.getConfigurationSection("ARRAYS").getConfigurationSection(name).getConfigurationSection("SWITCHES").getInt("Switch_"+i+"_z");
				Block b = w.getBlockAt(x, y, z);
				switchStore.add(b);
			}
		}
		log.info(pName+"All Swtch Blocks loaded .OK");		
	}

	//checks a button
	public void checkButton(Player p) {
		Block b = p.getTargetBlock(null, 10);
		int x = b.getX();
		int y = b.getY();
		int z = b.getZ();
		loadArrayNames();
		for(int index=0; index <arrayNames.size(); index++){
			String name = arrayNames.get(index);
			for (int i=0; config.getConfigurationSection("ARRAYS").getConfigurationSection(name).getConfigurationSection("SWITCHES").contains("Switch_"+i+"_x"); i++){
				int xb = config.getConfigurationSection("ARRAYS").getConfigurationSection(name).getConfigurationSection("SWITCHES").getInt("Switch_"+i+"_x");
				int yb = config.getConfigurationSection("ARRAYS").getConfigurationSection(name).getConfigurationSection("SWITCHES").getInt("Switch_"+i+"_y");
				int zb = config.getConfigurationSection("ARRAYS").getConfigurationSection(name).getConfigurationSection("SWITCHES").getInt("Switch_"+i+"_z");
				if(x==xb && y==yb && z==zb){
					String array = config.getConfigurationSection("ARRAY_INDEXES").getString("INDEX_"+index);
					bToggle(array, p);
				}
			}
		}
	}
	
	//toggles a light
	public void bToggle(String name, Player p) {
		Player play = p;
		if(config.getConfigurationSection("ARRAYS").getConfigurationSection(name).getBoolean("STATE")){
			turnOFF(name, play);
		}else{
			turnON(name,play);
		}
	}
	
	//lists arrays
	public void listArrays(Player p) {
		for(int i=0; i<arrays.size(); i++){
			String arrayN = arrays.get(i);
			p.sendMessage(arrayN);
		}
	}
	
	//enable/disable placemode
	public void togglePlaceMode(Player p){
		String n = p.getDisplayName().toString();
		if(editing){
			if(playerEditing.equals(n)){
				if(placeMode){
					p.sendMessage("Place Mode Disabled");
					placeMode=false;
				}else{
					p.sendMessage("Place Mode Enabled");
					placeMode=true;
				}
			}else{
				p.sendMessage(playerEditing+" is editing u can't change this");
			}
		}else{
			p.sendMessage("You are not editing an array");
		}
		
		
		
		
		
	}

	// Add a light to the currently editing array
	public void addLightPM(Player player, Block b) {
		lightBlocks.add(b);
		player.sendMessage("Light Added");
	}

	// Add switch to the currently editing array
	public void addSwitchPM(Player player, Block b) {
		switchBlocks.add(b);
		player.sendMessage("Switch Added");
	}
}	
