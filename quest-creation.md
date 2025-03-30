# Documentation for quest creators

## Actions

| Actions         | Parameter name  | Description                                                                                                                                                        |
| --------------- | --------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Catch           | catch           | Catch a Pokémon.                                                                                                                                                   |
| Defeat          | defeat          | Defeat a Pokémon.                                                                                                                                                  |
| Defeat Player   | defeat_player   | [Defeat a player](#defeat-player-and-npc)                                                                                                                          |
| Defeat NPC      | defeat_npc      | [Defeat an NPC](#defeat-player-and-npc)                                                                                                                            |
| Evolve          | evolve          | Triggers on a Pokémon being evolved. Ex. Bulbasaur -> Ivysaur counts as Bulbasaur.                                                                                 |
| Evolve into     | evolve_into     | Triggers on a Pokémon being evolved into. The example above would count as Ivysaur. Also triggers `catch` event.                                                   |
| Kill            | kill            | Kill a Pokémon.                                                                                                                                                    |
| Level up        | level_up        | Triggers on the delta level of a Pokémon. Ex. lvl 10 -> 13 would increase the task with 3.                                                                         |
| Level up to     | level_up_to     | Triggers on the resulting level. Ex. lvl 10 -> 13 would set the task completion to 13 out of `amount`.                                                             |
| Release         | release         | Go into the pc and release Pokémon into the wild.                                                                                                                  |
| Throw Poké Ball | throw_ball      | Throw the selected ball at a Pokémon.                                                                                                                              |
| Trade away      | trade_away      | Triggers on a Pokémon leaving the players possession.                                                                                                              |
| Trade for       | trade_for       | Triggers on a Pokémon entering the players possession.                                                                                                             |
| Obtain          | obtain          | Any of `catch, trade_for, revive_fossil`.                                                                                                                          |
| Select starter  | select_starter  | Select starter. Also triggers `catch`                                                                                                                              |
| Revive fossil   | revive_fossil   | Revive fossil in the resurrection machine.                                                                                                                         |
| Scan            | scan            | Use a Pokédex to scan a Pokémon.                                                                                                                                   |
| Reel            | reel            | Use a Poké Rod to reel in a Pokémon.                                                                                                                               |
| Have registered | have_registered | Pokémon registered in the Pokédex. Checked on player login and dex update. Level of registration (seen/caught) can be selected using the `dex_progress` condition. |

## Conditions

All the conditions below stack on top of each other, meaning that

| Condition                | Parameter name      | Type           | Description                                                                                                                                                     |
| ------------------------ | ------------------- | -------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Actions                  | action              | List\<String\> | The action performed by the player for the quest to trigger.                                                                                                    |
| Amount                   | amount              | long           | The amount of pokemon to defeat/catch etc.                                                                                                                      |
| Biomes                   | biome               | List\<String\> | The biome the player is in when triggering the quest.                                                                                                           |
| Pokédex progress         | dex_progress        | String         | Specifies if the Pokédex has a Pokémon registered as `seen` or `caught`, used with the actions `register` and `have registered`.                                |
| Dimensions               | dimension           | List\<String\> | The dimension the player is in when triggering the quest.                                                                                                       |
| Forms                    | form                | List\<String\> | Matches the aspects of the pokemon. Used primarily for checking pokemon forms but can also be used to check for aspects. See [Custom aspects](#custom-aspects). |
| Genders                  | gender              | List\<String\> | The gender of the pokemon                                                                                                                                       |
| Min/max time of day      | time_min/time_max   | long           | The time of which the action is executed. `time_min <= time < time_max`.                                                                                        |
| Min/max level of pokemon | min_level/max_level | int            | The Pokémon's min/max level (inclusive). If both are set to the same nonzero value, the Pokémon is required to have that level.                                 |
| Poké Balls used          | poke_ball_used      | List\<String\> | The poke ball used. Also applicable on tasks such as `trade` and `evolve` where the pokemon has a ball.                                                         |
| Pokemon                  | pokemon             | List\<String\> | The pokemon that count for the task.                                                                                                                            |
| Pokémon types            | pokemon_type        | List\<String\> | The type of the pokemon. (fire, water, earth, etc.)                                                                                                             |
| Regions                  | region              | List\<String\> | The region/generation the pokemon is from. For custom regions, see [Custom regions](#custom-regions-pokemon-and-dimensions).                                    |
| Shiny                    | shiny               | boolean        | If the pokemon is shiny.                                                                                                                                        |

## Custom aspects

When spawning a pokemon with the `/spawnpokemon <pokemon> aspect=<aspect>` command, custom aspects for that pokemon can be added. If you want a quest to
trigger when a pokemon with a custom aspect is caught, add this value to the quests `form` field. See [How to edit the chapter files](#how-to-edit-the-chapter-files) for
help with editing the tasks manually.

<details>
  <summary>Example</summary>

```yml
id: "1234567890ABCDEF"
    tasks: [{
        action: "catch"
        amount: 1L
        biome: ""
        dimension: ""
        form: "custom_aspect, custom_aspect2"
        gender: ""
        id: "1234567890ABCDEF"
        poke_ball_used: ""
        pokemon: ""
        pokemon_type: ""
        region: ""
        shiny: false
        time_max: 24000L
        time_min: 0L
        type: "cobblemon_tasks:cobblemon_task"
        }]
    x: 0.0d
    y: 0.0d
}
```

</details>

## Custom regions, pokemon and dimensions

If you have a mod that adds more regions, biomes or anything else that cannot be found in the default task creation screen, you need to edit the chapter files manually.
Simply add a new entry on the form `<namespace>:<name>` to the specified field. See [How to edit the chapter files](#how-to-edit-the-chapter-files) for help with editing the tasks manually.

<details>
  <summary>Example</summary>

```yml
id: "1234567890ABCDEF"
    tasks: [{
        action: "catch"
        amount: 1L
        biome: "custom_mod:biome"
        dimension: "minecraft:the_nether, custom_mod:dimension"
        form: ""
        gender: ""
        id: "1234567890ABCDEF"
        poke_ball_used: ""
        pokemon: ""
        pokemon_type: ""
        region: "my_custom_region"
        shiny: false
        time_max: 24000L
        time_min: 0L
        type: "cobblemon_tasks:cobblemon_task"
        }]
    x: 0.0d
    y: 0.0d
}
```

</details>

## How to edit the chapter files

The easiest way to create a new custom quest is to create a blank quest in the quest creation screen. Afterwards, make a mental note of the chapter the quest was created in and right click the quest and select `Copy ID`.
Log out of the game/close the server to make sure your changes aren't being overridden.
Go into the default minecraft directory and navigate to`config/ftbquests/quests/chapters/<chapter_name>.snbt`. Open the file with whichever text editor of choice and search for the quest id you copied before.
Do the necessary changes and don't forget to save. Start the game and the quest should be working. The default name will most likely have some weird unmatched translations, so simply create your own title to finish things up.

## Defeat player and npc

The defeating of a player or npc does not follow the same set of parameters as the rest of the task triggers. The only parameters that matter are the ones in the `form`
condition and amount. All the other parameters have no effect on these quest triggers. All the comma separated names in there correlate to a name of a player or npc.
The task below would trigger when an npc or player is defeated with the name "Steve" or "Ash". Keep in mind that capitalization is important, meaning that a npc named
"ash" will not trigger the quest. See [How to edit the chapter files](#how-to-edit-the-chapter-files) for help with editing the tasks manually.

<details>
  <summary>Example</summary>

```yml
id: "1234567890ABCDEF"
    tasks: [{
        action: "defeat_npc, defeat_player"
        amount: 1L
        biome: ""
        dimension: ""
        form: "Steve, Ash"
        gender: ""
        id: "1234567890ABCDEF"
        poke_ball_used: ""
        pokemon: ""
        pokemon_type: ""
        region: ""
        shiny: false
        time_max: 24000L
        time_min: 0L
        type: "cobblemon_tasks:cobblemon_task"
        }]
    x: 0.0d
    y: 0.0d
}
```

</details>

## Custom pokemon icon

The default icon is the first pokemon in the `pokemon` list, or if empty, a standard poke ball. This does not reflect the form or gender of the pokemon. To set a custom pokemon icon, use the command

```bash
/give @s cobblemon:pokemon_model[cobblemon:pokemon_item={species:"<namespace>:<pokemon_name>",aspects:[]}]
```

and replace `<pokemon_name>` and `<namespace>`with the correct values and add the aspects of choice. To give yourself a galarian zigzagoon, execute the following command:

```bash
/give @s cobblemon:pokemon_model[cobblemon:pokemon_item={species:"cobblemon:zigzagoon",aspects:[galarian]}]
```

## Commands

### Blacklist pokemon

To blacklist a pokemon from counting towards quests you can add them with the command below.

```bash
/cobblemonquests blacklisted_pokemon [add/remove] <pokemon>
```

### Suppress errors being sent to the console

All errors being sent from cobblemon quests can be ignored by running the command below. Default is false, meaning that errors are being sent.

```bash
/cobblemonquests suppress_warnings [true/false]
```

### Give pokemon and make it count as a catch

The default /givepokemon command does not count the pokemon towards quests. This command allows admins to forge an event.

| Parameter   | Type            | Description                                                                                                   |
| ----------- | --------------- | ------------------------------------------------------------------------------------------------------------- |
| player      | player          | The player the event is triggered for.                                                                        |
| should_give | boolean         | If the player should receive the pokemon or just trigger the actions.                                         |
| amount      | integer         | The amount of steps to increase the task progress. (Can also be a negative integer to decrease task progress) |
| actions     | list of actions | A comma separated list of actions. Ex. `{catch, defeat, evolve}`.                                             |
| pokemon     | pokemon         | A pokemon builder as can be found in the standard /givepokemon command                                        |

```bash
/cobblemonquests givepokemon <player> <should_give> <amount> {<actions>} <pokemon>
```
