package delphi.net.lights;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class LightsBlockListener extends BlockListener {

	private Lights lights;
	
	public LightsBlockListener(Lights lights) {
		this.lights = lights;
	}
	
	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		Block b = event.getBlock();
		Player p = event.getPlayer();
		String n = p.getDisplayName().toString();
		String EN = lights.playerEditing;
		boolean edt = lights.editing;
		boolean plm = lights.placeMode;
		if(n.equals(EN)){
			if(plm){
				if(edt && b.getType().equals(Material.GLOWSTONE)){
					lights.addLightPM(p, b);
				}
				if(edt && b.getType().equals(Material.STONE_BUTTON)){
					lights.addSwitchPM(p, b);
				}
			}
		}
		
		
		
		
	}

			

}
