package delphi.net.lights;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

public class LightsPlayerListener extends PlayerListener {

	public Lights l;
	
	public LightsPlayerListener(Lights lights) {
		this.l = lights;
	}


	public void onBlockRedstoneChange(BlockRedstoneEvent event) {
		if(event.getBlock().equals(Material.STONE_BUTTON)){
			Player p = ((Player) event).getPlayer();
			l.checkButton(p);
			
		}
	}


	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		Action act = event.getAction();
		Block b = event.getClickedBlock();
		if(act.equals(Action.RIGHT_CLICK_BLOCK) && b.getType().equals(Material.STONE_BUTTON)){
			l.checkButton(p);
		}
	}
	
	
	
	
	
	
	
	

}
