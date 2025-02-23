# Fill Hole Plugin

**Fill Hole** is a Minecraft plugin designed to efficiently fill holes in a selected region.  
It integrates with **WorldEdit** and leverages the **FAWE API** for optimal performance.

## Main Command

### `/fillhole <pattern>`
This command detects and fills holes in the selected region with the specified pattern.  
- **Usage**: Select a region using WorldEdit, then execute the command with a pattern (e.g., `stone`, `dirt`, or combinations like `stone,dirt`).  
- **Undoable**: Changes made by the command can be reverted using WorldEdit's **undo** command.  
- **Task-Based Execution**: Uses Bukkit tasks to handle large regions without crashing.  
- **Progress Bar**: Displays completion status while filling holes.  

You can also use the alias `/fh`.

## New Masks

### `#translucent`
Filters out translucent blocks when selecting terrain.

### `#full_cube`
Filters blocks to include only full cubes, useful for terrain cleanup.

These masks are useful when starting **Terraforming** or fixing broken terrain.

---

## Configuration

### `max_selection_size` (default: **10,000,000**)  
Defines the maximum allowed selection size.

### `reload_delay` (default: **5 minutes**)  
Controls how often the configuration auto-reloads.

### Task Control Settings
These options optimize performance by controlling processing cycles:  
- `filter_processed_cycle`
- `filter_tick_cycle`
- `fill_hole_processed_cycle`
- `fill_hole_tick_cycle`

---

## Installation

### Requirements
- **Minecraft Paper Server**: Version **1.20.6 or later** *(not fully tested)*.
- **FAWE (Fast Async WorldEdit)**: Ensure **FAWE** is installed.

### Steps
1. Download the `.jar` file from [the releases page](https://modrinth.com/plugin/fill-hole/versions).
2. Place the file in your server’s `plugins` folder.
3. Restart your server.

---

## Permissions

- **`fillhole.use`**: Required to execute the `/fillhole` command.  
  Players without this permission will receive an error message.

---

## Useful Links

- **Issue Tracker**: [Report a bug or suggest a feature](https://github.com/Nacharon/Fill-Hole/issues).  
- **Source Code**: [View the GitHub repository](https://github.com/Nacharon/Fill-Hole/).  
- **Releases**: [Download the plugin](https://modrinth.com/plugin/fill-hole/versions).  

---

## Author

Developed by **Nacharon**.  
For questions or support, please use the **issue tracker**.
