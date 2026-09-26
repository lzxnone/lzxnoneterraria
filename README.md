# 🌟 Lzxnone Terraria (泰拉瑞亚 Mod)

<div align="center">

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-brightgreen.svg?style=flat-square&logo=minecraft)
![NeoForge](https://img.shields.io/badge/NeoForge-21.1.200+-orange.svg?style=flat-square)
![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=flat-square)
![Version](https://img.shields.io/badge/Version-1.9.1-blue.svg?style=flat-square)
![Java](https://img.shields.io/badge/Java-21-red.svg?style=flat-square&logo=openjdk)

**将《泰拉瑞亚》（Terraria）宏大的战斗体验、职业体系与标志性武器装备深度还原至 Minecraft 1.21.1 NeoForge。**

[功能特性](#-功能特性) • [职业与武器](#-职业与武器) • [饰品体系](#-饰品体系-curios) • [快捷键](#-快捷键) • [安装与前置](#-安装与前置要求) • [构建指南](#-源码构建) • [开源协议](#-开源协议与鸣谢)

</div>

---

## 📖 模组简介 (Introduction)

**Lzxnone Terraria** 是一款面向 Minecraft 1.21.1 NeoForge 的大型内容与战斗模组。模组忠实再现了《泰拉瑞亚》四大经典职业（近战、远程、魔法、召唤）以及《灾厄》（Calamity）模组的特色内容。

模组不仅移植了武器的模型与伤害数值，更重构了**专属魔力系统 (Mana)**、**独立仆从召唤槽位**、**狙击变焦刻度**、**多段连续骨骼与着色器动画**、以及**完整的 Curios 饰品合成进化树**。配合原生 GLSL Core Shaders 与动态光源支持，为玩家带来爽快非凡的战斗体验。

---

## ✨ 核心特色 (Key Features)

- ⚔️ **全职业武库体系**：四大常规职业 + 灾厄分支，从新手短剑到天顶剑 (Zenith)、终极棱镜 (Last Prism)、星尘之龙法杖。
- 🔮 **完整魔力机制**：包含魔力星 UI、魔力水晶（提升至 200 上限）、魔力药水恢复、以及耐魔性（Mana Sickness）减伤机制。
- 🐉 **进阶仆从召唤引擎**：支持仆从栏位上限管理、多段长龙（星尘龙）身体协同轨迹平滑运算、泰拉棱镜自律飞剑、目标攻击标记等。
- 💍 **全套 Curios 饰品树**：全套十字章护盾合成线、各大职业徽章合成线、天界贝壳、黑腰带、强力手套、魔力花等。
- 🎨 **原色 GLSL 着色器 & 粒子系统**：自主实现光束衰减、多重采样辉光（Bloom）、圆环粒子、星云扭曲及魔王杀戮模式滤镜。
- 💡 **动态光源无缝联动**：原生集成兼容 LambDynamicLights，飞天光束、射弹与夜光武器均可实时照亮周围方块。
- ⚙️ **丰富的自定义配置**：支持游戏内灵活调整各项饰品数值、伤害减免、拾取范围及冷却时间。

---

## 🏹 职业与武器 (Classes & Arsenal)

### 1. 近战 (Melee)
经典且富有打击感的挥砍、弹道与全屏飞刃：
- **经典合成进阶**：铜短剑、附魔剑、星怒、草剑、村正、火山、魔光剑、血腥屠刀、永夜刃、真永夜刃、断钢剑、真断钢剑、泰拉刃、泰拉魔刃。
- **毕业终极神兵**：彩虹喵之刃（Meowmere）、狂星之怒（Star Wrath）、种子弯刀、波涌之刃、第一分形（First Fractal）、天顶剑（Zenith - 漫天飞舞各大宝剑幻影回旋收割）。
- **灾厄模组特选**：
  - **破灭魔王剑 (Devil's Devastation)**：地狱硫磺火之力的具现化，按下专属快捷键进入「杀戮模式」，激活全屏粒子与高能破坏力。

### 2. 魔法 (Magic)
绚丽而强力的法术弹幕与持续光束打击：
- **标志性法宝**：
  - **终极棱镜 (Last Prism)**：按住蓄力逐渐收束六道光谱光束，融合成无坚不摧的贯穿毁灭光柱。
  - **月耀 (Lunar Flare)**：从天而降轰击目标地面的多道苍穹之雨。
  - **夜光 (Nightglow)** / **星云烈焰 (Nebula Blaze)** / **星云奥秘 (Nebula Arcanum)** / **双足翼龙怒气 (Betsy's Wrath)** / **星星吉他 (Stellar Tune)** / **泡泡枪 (Bubble Gun)** / **电弧涌动 (Arc Surge)**。
- **法师护甲**：紫晶、黄玉、蓝玉、翡翠、红玉、琥珀、钻石法袍，降低魔力消耗并增强法术表现。

### 3. 远程 (Ranged)
从早期火枪到高科技重火器与变焦狙击：
- **现代枪械与连发神器**：火枪、左轮手枪、三发猎枪、四管霰弹枪、红莱德枪、迷你鲨、巨兽鲨、鳄鱼机关枪、战术霰弹枪、链式机枪、太空海豚机枪 (S.D.M.G.)。
- **特殊发射器与神弓**：雪球炮、星星炮、超级星星炮、火焰喷射器、精灵熔枪、代达罗斯风暴弓（从天幕倾泻箭雨）。
- **瞄准镜与战术测距**：装备步枪瞄准镜 / 狙击镜 / 侦察镜后，支持右键精确缩放，显示相对/绝对距离与放大倍率。
- **特种弹药体系**：火枪子弹、灵液弹、诅咒弹、叶绿弹（全自动追踪敌怪）、流星弹、水晶子弹、夜明弹、无尽火枪袋等。

### 4. 召唤 (Summoner)
召唤自律仆从与强力长鞭协战：
- **星尘之龙法杖 (Stardust Dragon Staff)**：召唤多节巨龙盘旋跟随并自动穿透敌人，召唤次数越多巨龙身体越长、伤害越高。
- **泰拉棱镜 (Terraprisma)**：召唤纯净光之飞剑环绕自身，在检测到敌人时化作漫天流光极速刺杀。
- **星尘细胞法杖 (Stardust Cell Staff)** & **支配之鞭 (Possession)**。

---

## 🛡️ 饰品体系 (Curios Accessories)

完全兼容 **Curios API**，高度还原原版的合成进化体系：

| 饰品系列 | 包含饰品 | 核心效果 |
| :--- | :--- | :--- |
| **十字章护盾线** | 盔甲抛光剂、扩音器、邪眼、袖珍镜、反光墨镜、牛黄、蒙眼布、快走闹钟、维生素、粘性绷带、三折地图、十字章护身符、**十字章护盾** | 全面免疫中毒、黑暗、缓慢、虚弱、流血、困惑、凋零、挖掘疲劳等负面效果，并提供击退抗性与护甲 |
| **战术护盾线** | 钴护盾、黑曜石护盾、圣骑士护盾、英雄护盾、冰冻海龟壳、**冰冻护盾** | 击退免疫、点燃免疫、分摊友军伤害、低血量高额减伤 |
| **战斗手套线** | 猛爪手套、泰坦手套、强力手套、机械手套、血肉指虎、**烈火手套**、**狂战士手套** | 提升近战伤害、攻击击退、附加狱火燃烧 |
| **全职业徽章** | 战士/游侠/巫师/召唤师徽章 ➔ **复仇者徽章** ➔ **毁灭者徽章**、**天界徽章** | 全方位暴击与多乘区职业伤害提升 |
| **魔力与续航** | 魔力花、磁花、奥术花、魔力斗篷、天界手铐、神话护身符、点金石 | 自动消耗魔力药水、扩大拾取星辰范围、受击恢复魔力、减少药水冷却 |
| **探索与功能** | 天界贝壳（海神贝壳+月亮贝壳）、黑腰带（几率免伤闪避）、贪婪戒指（钱币掉落与折扣）、远古凿子（挖掘加速） | 昼夜及入水姿态转换与全属性增益 |

---

## 🔮 专属附魔 (Custom Enchantments)

可在附魔台或铁砧中获取专属附魔词条：
- **魔力效率 (Mana Efficiency)**：显著降低魔法武器的魔力消耗。
- **魔力泄漏 (Mana Leak)**：[诅咒/负面] 增加魔法武器的魔力消耗。
- **奥术增幅 (Arcane Amplification)**：直接提高魔法武器造成的伤害。
- **召唤增幅 (Summon Amplification)**：直接提高召唤物与仆从造成的伤害。
- **寸草不生 (Barren Land)**：赋予仆从穿墙隔空索敌的能力。
- **枪林弹雨 (Bullet Hell)**：射击时有几率不消耗弹药。
- **弹尽粮绝 (Ammo Exhaustion)**：[负面] 射击时额外消耗弹药。
- **火药 (Gunpowder)**：提升远程枪械的伤害。
- **屏息 (Steady Breath)**：大幅收束远程武器的散射与后坐散布。

---

## ⌨️ 快捷键 (Keybindings)

可在「按键绑定 - 泰拉瑞亚」分类中自定义：

| 快捷键 (默认) | 功能说明 |
| :--- | :--- |
| `C` | **杀戮模式 (Kill Mode)**：切换破灭魔王剑等特异武器的爆发状态 |
| `X` | **清除仆从 (Clear Summon)**：遣散单个当前选中的仆从 |
| `Ctrl + X` | **清除所有仆从 (Clear All Summons)**：一键遣散所有在场仆从 |
| `V` | **切换攻击模式 (Toggle Summon Attack Mode)**：在主动进攻与跟随模式间切换 |

---

## 📦 安装与前置要求 (Installation & Requirements)

### 必须依赖 (Dependencies)
- **Minecraft**：`1.21.1`
- **Mod Loader**：[NeoForge](https://neoforged.net/) (`21.1.200` 或更高版本)
- **Java**：`Java 21`
- [Curios API (NeoForge)](https://www.curseforge.com/minecraft/mc-mods/curios-continuation) (`>= 9.5.1`)
- [LDLib2 (LowDragLib2)](https://github.com/Low-Drag-MC/LDLib2) (`>= 2.2.29`)

### 推荐可选兼容 (Recommended Optional)
- [LambDynamicLights (NeoForge)](https://github.com/LambdAurora/LambDynamicLights)：开启武器光束与弹幕的实时动态发光。
- [JEI (Just Enough Items)](https://www.curseforge.com/minecraft/mc-mods/jei)：便捷查询所有武器、弹药及饰品配方。

---

## 🔨 源码构建 (Build from Source)

如果你希望自行克隆并构建本模组：

```bash
# 1. 克隆代码仓库
git clone https://github.com/lzxnone/lzxnoneterraria.git
cd lzxnoneterraria

# 2. 构建模组 Jar 包 (Windows PowerShell / CMD)
.\gradlew build

# Linux / macOS
./gradlew build
```

构建完成后，生成的 Jar 包将存放于 `build/libs/` 目录下。

---

## 📄 开源协议与鸣谢 (License & Credits)

- **模组代码与资源**遵循 [MIT License](LICENSE) 开源协议。
- **游戏原作**：本模组深受 **Re-Logic** 开发的经典游戏《Terraria》启发，模组中的武器设计、数值思路与音频源于原作设定。
- **灾厄模组**：部分近战衍生内容取材自著名的 Terraria 模组 **Calamity Mod**。
- 感谢 **NeoForged** 团队与相关开源生态库作者的杰出贡献。

---

<div align="center">
Made with ❤️ by <b>LZX</b>
</div>
