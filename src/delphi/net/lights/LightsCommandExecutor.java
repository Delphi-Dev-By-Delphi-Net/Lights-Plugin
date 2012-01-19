package delphi.net.lights;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
		
		if(command.getName().equalsIgnoreCase("lfinnish")){
			lights.stopEditing(p);
			return true;
		}
		
		if(command.getName().equalsIgnoreCase("lon")){
			if(arg3.length ==1){
				String name = arg3[0];
				lights.turnON(name, p);
				return true;
			}else{
				return false;
			}
		}
		
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

	/*
	 * Block t = p.getTargetBlock(null, 10);
			al.add(t);
			Location l = p.getLocation();
			World w = l.getWorld();
			Block b = w.getBlockAt(t.getX()+1, t.getY()+1, t.getZ());
			if(p.getTargetBlock(null, 10).getType().equals(Material.GLOWSTONE)){
				b.setType(Material.COBBLESTONE);
			}else{
				b.setType(Material.GLOWSTONE);
			}
	 */
	
}
