# Plumax

A board game, inspired by Saboteur and Chinese Checker. Created by Kyle and Cindy from No. 2 High School of ECNU. 

## Basics

 - Plumax is a multi-player board game which can host 2, 3, 4, or 6 players.
 - The objective of the game is to connect two opposite ends of matching color on the hexagram board using the pieces in hand. 
 - Players will be divided into teams when the game begins. Each team will need to connect the ends of their assigned color to win. 
 - Players take turns to put pieces on the board to create paths on the board.
 - When a player successfully connects his the two ends of the board, his team wins.


## The Board

<p align="center">
<img src="/../images/Board.png?raw=true"/>
</p>

 - Plumax has a board similar to the Chinese checkers. The six nodes at the ends of the hexagram are starting/end points and should not have pieces on them. 
 - The players put pieces on the empty triangular areas on the board.


## The Pieces

<p align="center">
<img src="/../images/Pieces.png?raw=true"/>
</p>

 - Every player will have four pieces in hand each turn. 
 - In each turn, the player puts a piece on the board or uses the destroyer to remove a piece on the board. 
 - The player will draw pieces until he has four pieces when his turn begins.

### The Singo & The Trigo

<p align="center">
<img src="/../images/Singo.png?raw=true"/> <img src="/../images/Trigo.png?raw=true"/>
</p>

 - The Singo and the Trigo connects neighboring triangles on the board.
 - The player can decide which direction to put the Singo in. That is to say, the player can decide which triangles to connect.  

### The Oneway

<p align="center">
<img src="/../images/Oneway.png?raw=true"/>
</p>

 - The Oneway is similar to the Trigo, but only allows "in" from one direction. The player can decide which side to allow "in" and which sides to allow "out" only.

### The Destroyer

<p align="center">
<img src="/../images/Destroyer.png?raw=true"/>
</p>

 - The destroyer is pretty simple. Use it to remove any pieces on the board. 

## Winning Condition and Tie Breaker

- When any team has the ends of their color connected, they win. That is to say, there could be a tie if two pairs of ends are connected when the last piece is put on the board. 
- Tie breaker: When two pairs of ends are connected at the same time, the team using a shorter path to connect the ends win. (Haven't started working on that.)
