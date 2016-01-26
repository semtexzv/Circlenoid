package com.semtexzv.circlenoid2.screens

import com.badlogic.gdx.Screen
import com.badlogic.gdx.scenes.scene2d.{InputEvent, InputListener, Stage}
import com.badlogic.gdx.scenes.scene2d.ui.{Label, TextButton, Table}
import com.semtexzv.circlenoid2.{CirclenoidGame, Manager}


/**
  * Created by Semtexzv on 1/24/2016.
  */
class LostScreen(game: CirclenoidGame) extends Screen {

  var stage = new Stage(Manager.uiPort)
  var table = new Table(Manager.skin)
  var restarButton = new TextButton("Restart", Manager.skin)
  var quitButton = new TextButton("Menu", Manager.skin)
  var lostLabel = new Label("Lost", Manager.skin)
  var scoreLabel = new Label("Score: 00",Manager.skin)
  restarButton.addListener(new InputListener() {
    override def touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Unit = {
      game.restart()
    }

    override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean = true
  })
  quitButton.addListener(new InputListener() {
    override def touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Unit = {
     game.menu()
    }

    override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean = true
  })
  stage.addActor(table)

  override def render(delta: Float): Unit = {
    game.level.render(0)

    Manager.batch.begin()
    Manager.batch.draw(Manager.blackRegion,0,0,Manager.uiPort.getWorldWidth,Manager.uiPort.getWorldHeight)
    Manager.batch.end()
    stage.act(delta)
    stage.draw()
  }

  override def hide(): Unit = {
    Manager.multiplexer.removeProcessor(stage)
  }

  override def resize(width: Int, height: Int): Unit = {
    table.setSize(width, height)
    table.setFillParent(true)
    table.clear()
    table.center()
    table.add(lostLabel).center().expand(0, 1)
    table.row()
    table.add(restarButton).center().pad(20).width(width * 0.75f)
    table.row()
    table.add(quitButton).center().pad(20).width(width * 0.75f)
    table.row()
    scoreLabel.setText(
      (if(Manager.isHighscore) {
        f"New Highscore: %%02d"
      } else {
        f"Score: %%02d"
      }).format(Manager.score)
    )
    table.add(scoreLabel).center().expand(0,1)
    table.layout()
  }

  override def show(): Unit = {
    Manager.multiplexer.addProcessor(stage)
  }

  override def dispose(): Unit = {
    stage.dispose()
  }

  override def pause(): Unit = {

  }


  override def resume(): Unit = {

  }
}
