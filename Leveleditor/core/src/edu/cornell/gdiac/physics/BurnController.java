/** 
 * Due to AidenController (previously platformController) integrates collision
 * controller and it's hard to single it out, I implemented the burning controller code
 * inside Update() and beginContact(). 
 * AidenModel is modified with simple fuel system.
 * 
 * Also I have modified FlammableBlock's UpdateBurningState() to take in float since
 * counting with frame rates are less reliable than with time. 
 * 
 * FlammaBlocks should probably override the draw() method in simpleObstacles or load
 * different texture when burning
 */