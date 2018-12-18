# Settlers of Catan on Android

Famous Settlers of Catan board game made into a an Android app. This app supports single player, and local multiplayer. In single player mode you can play against computers of varying difficulty. And with local mulitplayer, you can play with up to 4 players in a single game.

## Introduction

[Official Settlers of Catan site](https://www.catan.com/game/catan)

### Rules

[Official Game Rules (PDF)](https://www.catan.com/files/downloads/catan_5th_ed_rules_eng_150303.pdf)

## Authors

- [Alex Weininger](https://github.com/alexweininger)
- [Andrew Lang](https://github.com/AndrewLang98)
- [Daniel Borg](https://github.com/dborg291)
- [Niraj Mali](https://github.com/malin383)

## Linked repositories

Repositories that hold smaller parts of this proejct, that were later combined into this reposity.

- <https://github.com/alexweininger/game-state>
- <https://github.com/alexweininger/android-catan-old>
- <https://github.com/alexweininger/android-catan-layout>

## About the game

### Board Data Structures

The creation of the board and logic is the largest part of our application. The creation of the board was not simply drawing multiple hexagons on a SurfaceView; it involved finding the correct algorithm to create the correct number of tiles in each row as well as an algorithm to assign values to each of these hexagons which represented tiles in the actual Catan game. Additionally, the board logic itself was extremely difficult; Not only did we need to figure out the adjacent of each hexagon on the board, but also of intersection to intersection, intersection to ports, and intersection to hexagons. This required the creation of an adjacency matrix which they researched

### The sheer size of the game

Catan arguably had the most unique features and rules of any game. On the exterior, Catan can be broken down into four core actions: trading, building, using development cards, and using the robber. However, within each were multiple and specific algorithms for various situations in the game. Building, which is the main feature of Catan, involved taking into account intersection adjacency and each building also needed to take into account hexagon adjacency for resource production. 

### Robber

The Robber is one of the most unique features of Catan and unique in how it needs to be implemented. To be short, the robber is activated whenever a player rolls a seven and there are three phases: the discard phase, the moving phase, and the steal phase. The hardest part to implement is the discard phase and this feature discerns apart from other games. This is because it requires players to take action when it is not their turn, which is extremely dangerous when implemented with the game framework because the framework is really meant for turn-based actions. This means not only do we have to bypass the turn checking, but we also have to make sure that each player is updating the game state correctly. In order to get the robber correct

### Custom Images

Many of the images used to make the game were made by us.  For example, with the help of some inspiration from Catan and other mainstream games, we made the resource icons as well as the buildings and ports.

### Dumb Computer Player
	
The dumb computer player is solely based on randomness and current resources. During the setup phase, it chooses intersections to build on randomly and does not take chit number or resource into account. During the normal phase it randomly chooses to try one action; build a road, build a settlement, build a city or to do nothing. If it tries to build something, the game then checks to see if the computer player has enough resources and if it does the action is completed, otherwise, it is not and the computer player ends its turn. When it comes to moving the robber, it moves it to a randomly valid tile and then steals from a randomly valid opponent.

### Smart Computer Player

The smart computer player first starts by improving the selection of the AI’s building locations in the setup phase by taking into account that they build on one of the inner hexagon corners.  Then, the smart computer also trades during the main action phase, by checking to see if they have enough resources to complete a trade to gain either a brick or lumber.  The smart computer can also purchase and use development cards, and picks what cards to gain (for monopoly and year of plenty) based off the least amount of resource type they have.  The building is done in the following order if they are a possibility to be built: settlements, city from a settlement, and the roads.  This allows the smart computer to gain more victory points at a faster rate than its counterpart.
