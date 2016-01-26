package com.semtexzv.circlenoid2.screens

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.{ImageButton, Label, Table, TextButton}
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.badlogic.gdx.scenes.scene2d.{Actor, InputEvent, InputListener, Stage}
import com.badlogic.gdx.{Gdx, Screen}
import com.semtexzv.circlenoid2.{CirclenoidGame, Manager}

/**
  * Created by Semtexzv on 1/23/2016.
  */
class MenuScreen(game: CirclenoidGame,batch: SpriteBatch) extends Screen {
  var stage: Stage = null
  var table: Table = null
  var title: Label = null
  var startButton: TextButton = null
  var quitButton: TextButton = null
  var highscoreLabel: Label = null
  var soundButton: ImageButton = null

  stage = new Stage(Manager.uiPort)
  table = new Table(Manager.skin)
  title = new Label("Circlenoid", Manager.skin)
  startButton = new TextButton("Play", Manager.skin)
  quitButton = new TextButton("Quit", Manager.skin)
  highscoreLabel = new Label(f"Highscore: %%02d", Manager.skin)
  soundButton = new ImageButton(Manager.skin,"sound")

  stage.addActor(table)
  stage.addActor(soundButton)
  startButton.addListener(new InputListener() {
    override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean = true

    override def touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Unit = {
      game.play()

    }
  })
  quitButton.addListener(new InputListener() {
    override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean = {
      true
    }

    override def touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Unit = {
      Gdx.app.exit()
    }
  })
  soundButton.addListener(new ChangeListener {
    override def changed(event: ChangeEvent, actor: Actor): Unit = {
      Manager.soundDisabled = soundButton.isChecked
    }
  })


  def show(): Unit = {
    soundButton.setChecked(Manager.soundDisabled)
    Manager.multiplexer.addProcessor(stage)
  }

  def dispose() {
  }

  def hide: Unit = {
    Manager.multiplexer.removeProcessor(stage)
  }

  def resize(width: Int, height: Int): Unit = {

    highscoreLabel.setText(f"Highscore: %%02d".format(Manager.highscore))
    var scale = Math.min(width,height);
    soundButton.setSize(scale/8,scale/8)
    soundButton.setPosition(width-soundButton.getWidth-10,height-soundButton.getHeight-10)
    table.setSize(width, height)
    table.setFillParent(true)
    table.clear()
    table.center()
    table.add(title).center().pad(20).expand()
    table.row()
    table.add(startButton).center().pad(20).width(width * 0.75f)
    table.row()
    table.add(quitButton).center().pad(20).width(width * 0.75f)
    table.row()
    table.add(highscoreLabel).expand()

  }

  def render(delta: Float): Unit = {
    if (Gdx.input.isKeyJustPressed(Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Keys.BACK)) {
      game.quit()
    }
    stage.act(delta)
    stage.draw()
  }

  def pause {
  }

  def resume {
  }
}
