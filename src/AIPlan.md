# Plan:

## MrX AI
- What it should do:  
	- Create a tree of possible moves for Mr X and Detectives based on start location.  
	- Based on each Detective's current position, find the point furthest away from them.  
	- Update tree of possible moves for Mr X and Detectives based on tickets used and current position.  
	- "Guess" Detectives possible moves based on their current location, number of tickets available and MrX's current/last known location.  
	- Move to point furthest away, without going near a corner or being surrounded.  

## Detective AI
- What it should do: 
	- Create a tree of Mr X's possible moves, based on move tickets used and current/last known location.   
	- Find shortest path to each of those possible points, create a tree of possible moves.  
	- Record which point that Detective is going towards (so other Detectives can use that info).  
	- Optimise path for tickets available.  
	
	- Play moves to corner or restrict MrX.  
	- Try to keep MrX near Taxi/Bus stations only.  
	
## Overall ToDo	
- Recursive Tree generator  
-  
