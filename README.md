# Fill Hole Plugin

**Fill Hole** is a Minecraft plugin designed to make filling holes in a selected region easy and efficient. It integrates with WorldEdit and leverages the FAWE API for optimal performance.

## Main Command

### `/fillhole <pattern>`
This command fills the detected holes in the selected region with the specified pattern.  
- **Usage**: Select a region using WorldEdit, then execute the command with a pattern (e.g., `stone`, `dirt`, or combinations like `stone,dirt`).  
- **Undoable**: Changes made by the command can be reverted using WorldEdit's **undo** command.  

You can also use the alias `fh`.

---

## Installation

### Requirements
- **Minecraft Paper Server**: Version 1.20.6 or later (not test).
- **FAWE (Fast Async WorldEdit)**: Ensure FAWE is installed.

### Steps
1. Download the `.jar` file from [the releases page](https://modrinth.com/plugin/fill-hole/versions).
2. Place the file in your server’s `plugins` folder.
3. Restart your server.

---

## Permissions

- **`fillhole.use`**: Required to execute the `/fillhole` command.  
  Players without this permission will receive a message indicating they lack the required permissions.

---

## Useful Links

- **Issue Tracker**: [Report a bug or suggest a feature](https://github.com/Nacharon/Fill-Hole/issues).  
- **Source Code**: [View the GitHub repository](https://github.com/Nacharon/Fill-Hole/).  
- **Releases**: [Download the plugin](https://modrinth.com/plugin/fill-hole/versions).  

---

## Author

Developed by **Nacharon**.  
For questions or support, please use the issue tracker.
