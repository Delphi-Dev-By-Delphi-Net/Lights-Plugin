package delphi.net.lights;

import java.util.ArrayList;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LightsCommandExecutor implements CommandExecutor {

	private Lights lights;
	ArrayList<Block> al = new ArrayList<Block>();
	
	public LightsCommandExecutor(Lights lights) {
		this.lights = lights;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] arg3) {
		Player p = (Player)sender;
		
		//create
		if(command.getName().equalsIgnoreCase("lcreate")){
			if(arg3.length ==1){
				String name = arg3[0];
				lights.createArray(name, p);
				return true;
			}else{
				return false;
			}
	
		}
		
		//add light
		if(command.getName().equalsIgnoreCase("ladd")){
			lights.addLight(p);
			return true;
		}
		
		//add switch
		if(command.getName().equalsIgnoreCase("lswadd")){
			lights.addSwitch(p);
			return true;
		}
		
		//add switch
		if(command.getName().equalsIgnoreCase("llist")){
			lights.listArrays(p);
			return true;
		}
		
		//finnish creating
		if(command.getName().equalsIgnoreCase("lfinish")){
			lights.stopEditing(p);
			return true;
		}
		
		/*
		//initialize command
		if(command.getName().equalsIgnoreCase("linit")){
			lights.loadSwitchBlocks(p);
			return true;
		}
		*/
		
		if(command.getName().equalsIgnoreCase("lplacemode")){
			lights.togglePlaceMode(p);
			return true;
		}
		
		if(command.getName().equalsIgnoreCase("ledit")){
			if(arg3.length ==1){
				String name = arg3[0];
				lights.editArray(p, name);
				return true;
			}else{
				return false;
			}
	
		}
		
		//turn on an array
		if(command.getName().equalsIgnoreCase("lon")){
			if(arg3.length ==1){
				String name = arg3[0];
				lights.turnON(name, p);
				return true;
			}else{
				return false;
			}
		}
		
		//turn off an array
		if(command.getName().equalsIgnoreCase("loff")){
			if(arg3.length ==1){
				String name = arg3[0];
				lights.turnOFF(name, p);
				return true;
			}else{
				return false;
			}
			
		}else{
				return false;
		}
		
	}	
}
