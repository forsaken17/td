package tim.towerdefence;

import java.awt.Image;

import javax.swing.ImageIcon;

public class Tower {

	public String textureFile = "";
	public Image texture;
	public static final Tower[] towerList = new Tower[200];
	public static final Tower lightningTower = new TowerLightning(0, 10).getTextureFile("commy");

	public int id;
	public int cost;
	public Tower(int id, int cost) {
		if (towerList[id] != null) {
			System.out.println("[Tower init] same id " + id);
		} else {
			towerList[id] = this;
			this.id = id;
			this.cost = cost;
		}
	}

	public Tower getTextureFile(String str) {
		this.textureFile = str;
		this.texture = new ImageIcon("res/tower/"+this.textureFile+".jpg").getImage();
		return null;
	}
}
