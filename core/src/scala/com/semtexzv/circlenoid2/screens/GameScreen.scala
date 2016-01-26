package com.semtexzv.circlenoid2.screens

import com.badlogic.gdx.Application.ApplicationType
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx._
import com.badlogic.gdx.scenes.scene2d.{InputEvent, InputListener, Stage}
import com.badlogic.gdx.scenes.scene2d.ui.{ImageButton, Label, TextButton, Table}
import com.semtexzv.circlenoid2.GameState.GameState
import com.semtexzv.circlenoid2.{CirclenoidGame, Manager, GameState, Level}
import com.semtexzv.circlenoid2.GameState.GameState


/**
  * Created by Semtexzv on 1/23/2016.
  */

class GameScreen(game: CirclenoidGame) extends Screen{
  var stage = new Stage(Manager.uiPort)
  var lbutton = new ImageButton(Manager.skin,"left")
  var rbutton = new ImageButton(Manager.skin,"right")
  var scoreLabel = new Label("00",Manager.skin,"score")
  var pauseButton = new ImageButton(Manager.skin,"pause")

  Manager.onBlockDestroyed += this.updateScore
  lbutton.addListener(new InputListener(){
    override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean = {
      game.level.left = true

      true
    }

    override def touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Unit = {
      game.level.left = false
    }
  })
  rbutton.addListener(new InputListener(){
    override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean = {
      game.level.right = true
      true
    }
    override def touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Unit = {
      game.level.right = false
    }
  })
  pauseButton.addListener(new InputListener(){
    override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean = true

    override def touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Unit = {
      game.pauseGame()
    }
  })

  override def show(): Unit = {
    Manager.multiplexer.addProcessor(stage)
    updateScore()
  }

  override def dispose(): Unit = {
    stage.dispose()
  }
  override def resize(width: Int, height: Int): Unit = {
    stage.clear()
    var scale = Math.min(width,height);
    var scaleP = Math.min(width,height);
    pauseButton.setSize(scaleP/8,scaleP/8)
    pauseButton.setPosition(width-pauseButton.getWidth-10,height-pauseButton.getHeight-10)

    lbutton.setSize(scale/6,scale/6)
    rbutton.setSize(scale/6,scale/6)
    lbutton.setPosition(20,20)
    rbutton.setPosition(width-20-rbutton.getWidth,20)
    scoreLabel.setPosition(Manager.uiPort.getWorldWidth/2-scoreLabel.getWidth/2,
      Manager.uiPort.getWorldHeight/2-scoreLabel.getHeight/2);
    //if(Gdx.app.getType == ApplicationType.Android || Gdx.app.getType == ApplicationType.iOS)
    {
      stage.addActor(pauseButton)
      stage.addActor(lbutton)
      stage.addActor(rbutton)
    }
    stage.addActor(scoreLabel)
  }
  def updateScore(): Unit ={
    scoreLabel.setText(f"%%02d" format Manager.score)
    scoreLabel.setPosition(Manager.uiPort.getWorldWidth/2-scoreLabel.getPrefWidth/2,
      Manager.uiPort.getWorldHeight/2-scoreLabel.getPrefHeight/2)
  }
  override def pause(): Unit = {

  }

  override def resume(): Unit = {

  }

  override def render(delta: Float): Unit = {

      stage.act(delta)
      stage.draw()

    if(Gdx.input.isKeyJustPressed(Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Keys.BACK)){
      game.pauseGame()
    }
    game.level.render(delta)
  }

  override def hide(): Unit = {
    Manager.multiplexer.removeProcessor(stage)
  }
}
