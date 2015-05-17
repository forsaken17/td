package tim.towerdefence;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

public class LevelFile {

	FileInputStream file;
	InputStreamReader reader;
	Scanner scanner;
	Level level = new Level();

	public Level getLevel(String filename) {
		try {
			file = new FileInputStream("level/" + filename + ".txt");
			reader = new InputStreamReader(file);
			scanner = new Scanner(reader);
			level.map = new int[22][18];
			int x = 0;
			int y = 0;
			while (scanner.hasNext()) {
				level.map[x][y] = scanner.nextInt();
				if (x < 22 - 1) {
					x++;
				} else {
					y++;
					x = 0;
				}

			}
			return level;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
