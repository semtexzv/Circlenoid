
package com.semtexzv.circlenoid2

import aurelienribon.tweenengine.Tween
import com.badlogic.gdx.graphics.{OrthographicCamera, Color, GL20, Texture}
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx._
import com.semtexzv.circlenoid2.GameState._
import com.semtexzv.circlenoid2.objects.BlockLayer
import com.semtexzv.circlenoid2.screens.{LostScreen, PausedScreen, MenuScreen, GameScreen}



class CirclenoidGame extends Game {


  lazy val gameScreen = new GameScreen(this)
  lazy val menuScreen = new MenuScreen(this,Manager.batch)
  lazy val pauseScreen = new PausedScreen(this)
  lazy val lostScreen = new LostScreen(this)
  lazy val bg = new GradientBG

  var state: GameState = GameState.Menu

  lazy val level = new Level

  override def create(): Unit =
  {
    Gdx.input.setCatchBackKey(true)
    Manager.game = this
    Manager.init()
    Tween.registerAccessor(classOf[BlockLayer],new LayerAccessor)

    menu()
  }

  var w = 0f
  var h = 0f



  override def resize(width: Int, height: Int): Unit = {
    Manager.resize(width,height)

    w= Manager.uiPort.getWorldWidth
    h =Manager.uiPort.getWorldHeight

    bg.redraw(Manager.gradCenter,Color.BLACK)

    screen.resize(w.toInt,h.toInt)
  }


  override def setScreen(screen: Screen): Unit = {
    if(this.screen != null) {
      this.screen.hide()
    }
    this.screen = screen
    this.screen.resize(w.toInt,h.toInt)
    this.screen.show()
  }

  override def pause(): Unit = {
    pauseGame()

  }

  override def resume(): Unit = {

  }

  override def render(): Unit ={
    Gdx.gl.glClearColor(1,0.8f,0,0.5f)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    Manager.batch.begin()
    bg.render(Manager.batch,w,h)
     Manager.batch.end()
    screen.render(Gdx.graphics.getDeltaTime)
    if(level!=null && level.lost && state == GameState.Running){
      lose()
    }
  }
  def play(): Unit ={
    Manager.resetScore()
    level.set()
    state = GameState.Running
    setScreen(gameScreen)
  }
  def menu(): Unit ={
    Manager.resetScore()
    state = GameState.Menu
    setScreen(menuScreen)
  }
  def lose(): Unit ={
    state = GameState.Lost
    setScreen(lostScreen)
  }
  def restart(): Unit = {
    level.clear()
    play()
  }
  def pauseGame(): Unit ={
    if(state == GameState.Running) {
      state = GameState.Paused
      setScreen(pauseScreen)
    }
  }
  def resumeGame(): Unit = {
    if (state == GameState.Paused) {
      state = GameState.Running
      setScreen(gameScreen)
    }
  }
  def quit(): Unit ={
    Manager.saveScore
    Gdx.app.exit()
  }

  override def dispose(): Unit = {
    Manager.saveScore
  }
}