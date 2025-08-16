# ⚔️ SkillForge

**SkillForge** is a comprehensive LitRPG plugin for Minecraft that transforms your server into a skill-based adventure. Players level up by performing actions, unlock powerful abilities, and form parties to conquer the world together. Designed for immersive progression, cinematic feedback, and modular expansion.

---

## ✨ Features

- **Skill System** – Level up skills like mining, combat, fishing, taming, and more
- **Ability Unlocks** – Trigger mythic powers as you reach new milestones
- **Party System** – Invite friends, share XP, and coordinate attacks
- **Leaderboards** – Track top players by skill and flex your grind
- **Titles & Action Bars** – Cinematic feedback for every achievement
- **Database Integration** – Persistent player data via SQLite or MySQL
- **Modular Managers** – Clean architecture for easy expansion and customization

---

## 📦 Installation

1. Drop the `SkillForge-x.x.x.jar` into your server’s `/plugins` folder
2. Start the server to generate config and database files
3. Customize `config.yml` and `abilities.yml` to suit your server
4. Assign permissions to players or groups

---

## 🧠 Commands

| Command       | Description                             | Permission           |
|---------------|-----------------------------------------|----------------------|
| `/sf`         | Main command hub                        | `skillforge.use`     |
| `/inspect`    | View another player’s skill stats       | `skillforge.inspect` |
| `/leaderboard`| View top players by skill               | `skillforge.leaderboard` |
| `/party`      | Manage party invites and members        | `skillforge.party`   |

---

## 🛠 Configuration

SkillForge generates multiple config files:

- `config.yml` – General settings and toggles
- `abilities.yml` – Define ability unlocks per skill and level
- `database.yml` – Choose between SQLite or MySQL

All configs are modular and reloadable.

---

## 🧩 Developers

SkillForge is built with modular managers and clean command/listener separation. Want to extend it?

- Fork the repo
- Add new skills or abilities via `SkillManager`
- Create custom events or overlays
- Submit pull requests with flair

---

## 🪪 License

MIT – Free to use, modify, and redistribute. Credit appreciated but not required.

---

## 🧙 Author

Crafted by **James**, a mythic-minded automation visionary.  
For plugin support, ideas, or collaboration, open an issue or reach out.

---

> “Forge your path. Level your legend.”
