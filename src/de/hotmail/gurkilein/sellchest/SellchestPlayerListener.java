package de.hotmail.gurkilein.sellchest;


import java.text.DecimalFormat;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class SellchestPlayerListener
  implements Listener
{
  public double stacksell(Player p, ItemStack stack) {
	  
	  for (int j = 0; j < Sellchest.priceArray.length; j++) {
		  String priceArrayLine = (String) Sellchest.priceArray[j];
      if (stack != null) {
          Integer id = Integer.valueOf(stack.getTypeId());
          Integer amount = Integer.valueOf(stack.getAmount());
          if (id.toString().equals(priceArrayLine.split("=")[0].split(":")[0]))
          {
            Integer data;
            if (!priceArrayLine.split("=")[0].split(":")[0].equals(priceArrayLine.split("=")[0]))
              data = new Integer(priceArrayLine.split("=")[0].split(":")[1]);
            else {
              data = null;
            }

            if ((data == null) || (stack.getDurability() == data.intValue())) {
              return  new Double(priceArrayLine.split("=")[1]).doubleValue() * amount.intValue();
            }
          }
        }
	  }
      return -1;
  }
  public void chestsell(Player p, Inventory inv)
  {
    Double betrag = Double.valueOf(0.0D);

      for (int i = 0; i < inv.getSize(); i++) {
          ItemStack stack = inv.getItem(i);
          double result = stacksell(p, stack);
          if (!(result == -1)) {
        	  inv.clear(i);
        	  betrag = betrag + result;
          }
      }
      DecimalFormat df = new DecimalFormat("#0.00");
      String betragstring = df.format(betrag);
      betrag = Double.valueOf(Double.parseDouble(betragstring));
      p.sendMessage(Sellchest.success + " " + betrag);
      Sellchest.econ.depositPlayer(p.getName(), betrag.doubleValue());
  }
  
  public boolean chestsell(Player p, ItemStack stack) {
	  double betrag = stacksell(p, stack);
	  if (!(betrag == -1)) {
      DecimalFormat df = new DecimalFormat("#0.00");
      String betragstring = df.format(betrag);
      betrag = Double.valueOf(Double.parseDouble(betragstring));
      p.sendMessage(Sellchest.success + " " + betrag);
      Sellchest.econ.depositPlayer(p.getName(), betrag); 
	  }
	  return !(betrag == -1);
  }

  @EventHandler
  public void onklick(PlayerInteractEvent event) throws Exception
  {
	if (Sellchest.fastsell) return;
    Player p = event.getPlayer();
    Block sb = event.getClickedBlock();
    if (event.getAction() == Action.RIGHT_CLICK_BLOCK && sb != null && sb.getRelative(BlockFace.DOWN) instanceof Chest) {
    	Inventory inv = ((Chest)sb.getRelative(BlockFace.DOWN)).getBlockInventory();
    	chestSellTry(p, inv, sb);
    }
  }
  
  @EventHandler
  public void onItemPut(InventoryDragEvent event) {
	  if (!Sellchest.fastsell) return;
	  Player p = (Player) event.getWhoClicked();
	  InventoryHolder ih = (InventoryHolder)event.getInventory().getHolder();
	  if (ih instanceof Chest) {
		  Block b = ((BlockState)ih).getBlock();
		   if (chestSellTry(p, event.getCursor(), b)) {
			   event.setCursor(new ItemStack (Material.AIR));
		   }
	  }
  }

private boolean chestSellTry(Player p, ItemStack stack, Block sb) {
	BlockState bs = sb.getState();
	if (bs instanceof Sign && ((Sign) bs).getLine(0).contains(Sellchest.signtag)) {
		if (Sellchest.perms.has(p, "sellchest.use")) {
                  chestsell(p, stack);
                  return true;
		} else {
			p.sendMessage(Sellchest.disallow);
			return false;
		}
	}
	return false;
}
private boolean chestSellTry(Player p, Inventory inv, Block sb) {
	BlockState bs = sb.getState();
	if (bs instanceof Sign && ((Sign) bs).getLine(0).contains(Sellchest.signtag)) {
		if (Sellchest.perms.has(p, "sellchest.use")) {
                  chestsell(p, inv);
                  return true;
		} else {
			p.sendMessage(Sellchest.disallow);
			return false;
		}
	}
	return false;
}
}