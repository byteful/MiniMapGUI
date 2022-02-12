[![JitPack](https://jitpack.io/v/byteful/MiniMapGUI.svg)](https://jitpack.io/#byteful/MiniMapGUI)

# MiniMapGUI

An easy-to-use Spigot library that implements a GUI interface within offhand maps.

# Maven/Gradle Dependencies

Replace `Tag` with the latest version. (Latest version is in the `Releases` tab.)

## Gradle

```groovy
repositories {
  maven { url 'https://jitpack.io' }
}

dependencies {
  implementation 'com.github.byteful:MiniMapGUI:Tag'
}
```

## Maven

```xml

<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
<groupId>com.github.byteful</groupId>
<artifactId>MiniMapGUI</artifactId>
<version>Tag</version>
</dependency>
```

# Example Usage
```java
import me.byteful.lib.mmgui.MiniMapGUI;
import me.byteful.lib.mmgui.api.option.impl.ImageOption;
import me.byteful.lib.mmgui.api.stage.impl.SimpleStage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.MalformedURLException;
import java.net.URL;

public class WarpGUI extends MiniMapGUI {
  private final JavaPlugin plugin;

  public WarpGUI(JavaPlugin plugin, Player player) {
    super(plugin, player);
    this.plugin = plugin;

    wraparoundStages = false;
  }

  @Override
  public void setup() {
    try {
      addStage(
          0,
          SimpleStage.newBuilder()
              .wraparoundOptions(true)
              .addOption(
                  new ImageOption(
                      getPlayer().getWorld(),
                      new URL("someimgurlink.com/image.png"),
                      gui -> {
                        gui.close();
                        gui.getPlayer().teleport(Bukkit.getWorld("world").getSpawnLocation());
                      }))
              .addOption(
                  new ImageOption(
                      getPlayer().getWorld(),
                      new URL("someimgurlink.com/image2.png"),
                      gui -> {
                        gui.close();
                        gui.getPlayer()
                            .teleport(Bukkit.getWorld("world_nether").getSpawnLocation());
                      }))
              .build());
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onLeftClick() {
    if (getCurrentStage() != null) {
      getCurrentStage().nextOption();
      render();
    }
  }

  @Override
  public void onRightClick() {
    selectCurrentOption();
  }

  //  @Override
  //  public void onDrop() {
  //    if(getCurrentStageIndex() <= 0) {
  //      close();
  //    } else {
  //      previousStage();
  //      render();
  //    }
  //  }
}
```
