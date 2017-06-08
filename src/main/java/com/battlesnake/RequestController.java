/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.battlesnake;

import com.battlesnake.data.*;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@RestController
public class RequestController {

  @RequestMapping(value="/start", method=RequestMethod.POST, produces="application/json")
  public StartResponse start(@RequestBody StartRequest request) {
    return new StartResponse()
      .setName("Stargazer Snake")
      .setColor("#FF0000")
      .setHeadUrl("http://athenacinema.com/wp-content/uploads/2015/10/Snakes_on_a_Plane.jpg")
      .setHeadType(HeadType.DEAD)
      .setTailType(TailType.PIXEL)
      .setTaunt("I have had it with these motherf...ing snakes on this motherf...ing plane!!!");
  }

  @RequestMapping(value="/move", method=RequestMethod.POST, produces = "application/json")
  public MoveResponse move(@RequestBody MoveRequest request) {

    List<Move> moves = getPossibleMoves(request);
//    moves = filterByDistance(request, moves);

    return new MoveResponse()
      .setMove(filterByDistance(request, moves))
      .setTaunt("I have had it with these motherf...ing snakes on this motherf...ing plane!!!");
  }
    
  @RequestMapping(value="/end", method=RequestMethod.POST)
  public Object end() {
      // No response required
      Map<String, Object> responseObject = new HashMap<String, Object>();
      return responseObject;
  }

  public Move filterByDistance(MoveRequest moveRequest, List<Move> moves) {
    boolean[][] empty = new boolean[moveRequest.getWidth()][moveRequest.getHeight()];
    //Move resultDirection = moves.get(0);

    for (int i = 0 ; i < moveRequest.getHeight() ; i++) {
      for (int j = 0 ; j < moveRequest.getWidth() ; j++ ) {
        empty[i][j] = true;
      }
    }

    Snake mySnake = getMySnake(moveRequest.getYou(), moveRequest.getSnakes());
    Snake otherSnake = getOtherSnake(moveRequest.getYou(), moveRequest.getSnakes());

    for (int seg[] : mySnake.getCoords()) {
      empty[seg[0]][seg[1]] = false;
    }
    for (int seg[] : otherSnake.getCoords()) {
      empty[seg[0]][seg[1]] = false;
    }

//    int[][] head = new int[mySnake.getCoords()[0][0]][mySnake.getCoords()[0][1]];

    int currUp = 0;
    int currDown = 0;
    int currLeft = 0;
    int currRight = 0;

    for (Move direction : moves ) {
      if (direction.equals(Move.LEFT)) {
        for (int i = mySnake.getCoords()[0][0] ; i >= 0 ; i-- ) {
          if (empty[i][mySnake.getCoords()[0][1]]) {
            currLeft++;
          } else {
            break;
          }
        }
      }

      if (direction.equals(Move.RIGHT)) {
        for (int i = mySnake.getCoords()[0][0] ; i < moveRequest.getHeight() ; i++ ) {
          if (empty[i][mySnake.getCoords()[0][1]]) {
            currRight++;
          } else {
            break;
          }
        }
      }


      if (direction.equals(Move.UP)) {
        for (int j = mySnake.getCoords()[0][1] ; j >= 0 ; j-- ) {
          if (empty[mySnake.getCoords()[0][0]][j]) {
            currUp++;
          } else {
            break;
          }
        }
      }


      if (direction.equals(Move.DOWN)) {
        for (int j = mySnake.getCoords()[0][1] ; j < moveRequest.getWidth() ; j++ ) {
          if (empty[mySnake.getCoords()[0][0]][j]) {
            currDown++;
          } else {
            break;
          }
        }
      }
    }

    int max = Math.max(currUp, Math.max(currDown, Math.max(currLeft, currRight)));

    if (max == currUp) {
      return Move.UP;
    } else if (max == currDown) {
      return Move.DOWN;
    } else if (max == currLeft) {
      return Move.LEFT;
    } else {
      return Move.RIGHT;
    }
  }


  public List<Move> getPossibleMoves(MoveRequest moveRequest) {
    List<Move> results = Arrays.asList(Move.UP, Move.DOWN, Move.LEFT, Move.RIGHT);
    Snake mySnake = getMySnake(moveRequest.getYou(), moveRequest.getSnakes());

    int[][] coords = mySnake.getCoords();

    if (coords[0][0] == 0) {
      results.remove(Move.LEFT);
    }
    if (coords[0][0] == moveRequest.getWidth()-1) {
      results.remove(Move.RIGHT);
    }
    if (coords[0][1] == 0) {
      results.remove(Move.UP);
    }
    if (coords[0][1] == moveRequest.getHeight() - 1) {
      results.remove(Move.DOWN);
    }

    System.out.println("results: " + results);
    return results;
  }


  public Snake getMySnake(String you, ArrayList<Snake> snakes) {

    for (Snake s : snakes) {
      if (s.getId().equals(you)) {
        return s;
      }
    }
    return null;
  }
  public Snake getOtherSnake(String you, ArrayList<Snake> snakes) {
    for (Snake s : snakes) {
      if (!s.getId().equals(you)) {
        return s;
      }
    }
    return null;
  }
}
