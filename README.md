# Aris - Realistic World Generation Mod

![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21.11-brightgreen)
![License](https://img.shields.io/badge/License-GNU%20GPL%20v3-blue)

Aris — мод для реалистичной генерации мира в Minecraft. Проект предоставляет кастомные биомы, систему дорог между деревнями, новые блоки и полностью переработанную систему глобального террейна.

## 🎯 Возможности

### 🗺️ Система Биомов
- **ArisBiomeSource** — кастомный источник биомов с поддержкой:
    - Равнин (Plains)
    - Тайги (Taiga)
    - Холмов (Hills)
    - Пустынь (Desert)
    - Океанов (Ocean)

### 🛣️ Генерация Дорог
- Автоматическая генерация дорог между деревнями
- Алгоритм **RoadGraph** для расчета оптимальных путей
- Динамическое создание:
    - Мостов над водой
    - Фонарных столбов на краях дорог
    - Указателей расстояний до деревень
    - Разнообразных дорожных материалов (булыжник, гравий, земля)

### 🧱 Кастомные Блоки
- **Grass Slab / Dirt Slab / Sand Slab** — плиты из травы, земли и песка
- **Pebble** — декоративная галька с XZ-смещением
- **Taiga Leaves** — листва для тайги с кастомным цветом
- **Stick** — веточки для украшения ландшафта

### 🎨 Генерация Террейна
- Кастомная система плотности с использованием Perlin noise
- **Terrain Height** — слои для континентальности, эрозии и гор
- **Surface Rules** — правила создания верхнего слоя земли
- Динамическая регулировка высот с учётом биомов

### 🌳 Дополнительные Фичи
- **Slope Slabs Feature** — автоматическое создание плит на склонах
- **Pebble Patches** — разбросанная галька по ландшафту
- **Stick Patches** — природные скопления веток
- **Plains Rocks** — мох-булыжник в равнинах

## 📦 Требования

- **Java 21+**
- **Minecraft 1.21.11**
- Fabric Loader или NeoForge

### Зависимости
- Fabric API / NeoForge
- Architectury
- Knot (Networking)
- TerraBlender
- Lithostitched
- SmartBrainLib
- Geckolib

## 🚀 Установка

### Для игроков
1. Скачайте последний релиз из [Releases](../../releases)
2. Поместите JAR в папку `mods/`
3. Запустите Minecraft с Fabric или NeoForge

### Для разработчиков

#### Клонирование репозитория
```bash
git clone https://github.com/GrindlesStudio/Aris.git
cd Aris
```

#### Сборка проекта
```bash
# Linux / macOS
./gradlew build

# Windows
gradlew.bat build
```

Артефакты будут в `build/libs/`

#### Запуск в IDE

**IntelliJ IDEA:**
1. Откройте проект
2. Дождитесь индексирования
3. Запустите конфигурацию `Fabric Client` или `NeoForge Client`

## 📁 Структура Проекта

```
aris/
├── common/                          # Общий код для Fabric и NeoForge
│   ├── src/main/kotlin/
│   │   ├── worldgen/               # Системы генерации мира
│   │   │   ├── biome/              # Биомы и климат
│   │   │   ├── terrain/            # Террейн и регионы
│   │   │   ├── feature/            # Возможности (дороги, плиты)
│   │   │   └── ArisSurfaceRules.kt
│   │   ├── block/                  # Кастомные блоки
│   │   ├── skill/                  # Система скиллов игрока
│   │   └── Aris.kt                 # Основной класс мода
│   └── src/main/resources/
│       └── data/aris/
│           └── worldgen/           # JSON файлы для генерации
├── fabric/                          # Специфичный для Fabric код
│   └── src/main/kotlin/
│       ├── ArisFabric.kt
│       └── client/
├── neoforge/                        # Специфичный для NeoForge код
│   └── src/main/kotlin/
│       └── ArisNeoForge.kt
└── gradle/                          # Gradle конфигурация
```

## 🛠️ Разработка

### Основные классы

#### RoadGraph (`worldgen/feature/road/RoadGraph.kt`)
Управление графом дорог между деревнями. Использует кеш для оптимизации.

#### ArisBiomeSource (`worldgen/biome/ArisBiomeSource.kt`)
Источник биомов с логикой выбора на основе noise-генератора.

#### ArisBiomeRegion (`worldgen/biome/ArisBiomeRegion.kt`)
2D noise функция для определения типа региона.

#### VillageRoadFeature (`worldgen/feature/road/RoadFeature.kt`)
Основная фича для генерации дорог с мостами и элементами декора.

### Добавление нового блока

```kotlin
// 1. Определить блок в ModBlocks.kt
val CUSTOM_BLOCK: Block by lazy(LazyThreadSafetyMode.NONE) {
  Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE))
}

// 2. Создать JSON модель в assets/aris/models/block/
// 3. Зарегистрировать в ArisFabric.kt или ArisNeoForge.kt
```

## 📊 Конфигурация

### Параметры RoadGraph
```kotlin
const val REGION_SIZE = 34               // Размер региона в чанках
const val VILLAGE_SEPARATION = 8         // Минимальное расстояние между деревнями
const val MIN_ROAD_LENGTH = 300.0        // Минимальная длина дороги (блоков)
const val MAX_ROAD_LENGTH = 1200.0       // Максимальная длина дороги
const val MAX_CONNECTIONS_PER_VILLAGE = 2 // Макс. связей на деревню
```

### Параметры VillageRoadFeature
```kotlin
const val ROAD_RADIUS = 2.2              // Радиус дороги
const val VILLAGE_SAFE_RADIUS = 40.0     // Радиус без дорог вокруг деревни
const val LAMP_POST_CHANCE = 100         // Редкость фонарных столбов
```

## 🎮 Система Скиллов

Мод включает встроенную систему скиллов игрока:
- **Speed** — ускорение передвижения
- **Night Vision** — видение в темноте
- **Jump** — повышенный прыжок
- **Haste** — ускорение добычи

Скиллы разблокируются через интерфейс и синхронизируются с сервером.

## 📝 Лицензия

Этот проект распространяется под лицензией **GNU General Public License v3.0**.
См. [LICENSE](LICENSE) для деталей.

## 👥 Авторы

- **Tenakt** — основной разработчик
- **yorrudebug** — дизайнер, тестер

## 🤝 Контрибьютинг

Приветствуются pull requests! Для больших изменений сначала откройте issue для обсуждения.

## 📞 Поддержка

Если у вас есть вопросы или вы нашли баг:
1. Проверьте [Issues](../../issues)
2. Создайте новый issue с описанием проблемы
3. Включите лог ошибки и версию Minecraft

---

**Версия мода:** 1.0.0
**Версия Minecraft:** 1.21.11