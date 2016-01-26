package com.semtexzv.circlenoid2

import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.graphics.{Color, Camera, OrthographicCamera, Texture}
import com.badlogic.gdx.graphics.g2d._
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.{TextureRegionDrawable, NinePatchDrawable}
import com.badlogic.gdx.utils.viewport.{FitViewport, ScreenViewport, ExtendViewport, Viewport}
import com.badlogic.gdx.{InputMultiplexer, Preferences, Gdx}
import com.semtexzv.circlenoid2.GameState._
import com.semtexzv.circlenoid2.screens.{LostScreen, PausedScreen, MenuScreen, GameScreen}

import scala.collection.mutable

/**
  * Created by Semtexzv on 1/23/2016.
  */

object Manager {
  var scale = 24f
  var Speed = 3f
  var RingSpeed = 0f
  var atlas: TextureAtlas = null
  var ballRegion: TextureRegion = null
  var blackRegion: TextureRegion = null

  var game: CirclenoidGame = null

  lazy val  prefs: Preferences = Gdx.app.getPreferences("Circlenoid")
  var uiPort: Viewport = null
  var gamePort: Viewport = null

  var multiplexer: InputMultiplexer = null

  var skin: Skin = null
  var program: ShaderProgram = null
  var batch: SpriteBatch = null

  var colors = Array[Color](Color.WHITE,Color.ORANGE,Color.RED,Color.YELLOW)
  var gradCenter= Color.CYAN.cpy().lerp(Color.BLUE,0.3f)

  lazy val collisionSound = Gdx.audio.newSound(Gdx.files.internal("data/collision.ogg"))
  var left : ImageButtonStyle = null
  var right : ImageButtonStyle = null
  var sound: ImageButtonStyle = null
  var soundDisabled: Boolean = true

  def init(): Unit = {
    highscore = prefs.getInteger("highscore_classic")
    soundDisabled = prefs.getBoolean("sound")

    atlas = new TextureAtlas("data/circlenoid.pack")
    program = new ShaderProgram(Gdx.files.internal("data/shad.vert"), Gdx.files.internal("data/shad.frag"))
    ballRegion = atlas.findRegion("ball")
    blackRegion = atlas.findRegion("black")
    val up = new NinePatch(atlas.findRegion("up"),10,10,10,10)
    val down = new NinePatch(atlas.findRegion("down"),10,10,10,10)
    batch= new SpriteBatch()

    skin = new Skin()


    left = new ImageButtonStyle()
    left.up = new NinePatchDrawable(up)
    left.down = new NinePatchDrawable(down)
    left.imageUp  = new TextureRegionDrawable(atlas.findRegion("anticlockwise-rotation"))
    skin.add("left",left,classOf[ImageButtonStyle])

    val pause = new ImageButtonStyle(left)
    pause.imageUp = new TextureRegionDrawable(atlas.findRegion("pause"))
    skin.add("pause",pause,classOf[ImageButtonStyle])

    right = new ImageButtonStyle(left)
    right.imageUp  = new TextureRegionDrawable(atlas.findRegion("clockwise-rotation"))
    skin.add("right",right,classOf[ImageButtonStyle])

    sound = new ImageButtonStyle(left)
    sound.imageUp = new TextureRegionDrawable(atlas.findRegion("speaker"))
    sound.imageChecked = new TextureRegionDrawable(atlas.findRegion("speaker-off"))
    skin.add("sound",sound,classOf[ImageButtonStyle])

    //TODO, fix the Icon
    val fontGen = new FreeTypeFontGenerator(Gdx.files.internal("data/Play.ttf"))
    val fParam = new FreeTypeFontParameter
    fParam.genMipMaps =true
    fParam.kerning=true
    fParam.minFilter = Texture.TextureFilter.MipMapLinearLinear
    fParam.magFilter = TextureFilter.Linear
    fParam.color = Color.WHITE
    fParam.size = 80

    val font = fontGen.generateFont(fParam)
    fParam.size = 96
    fParam.borderWidth = 5f
    fParam.borderColor = Color.BLACK

    val outlineFont =  fontGen.generateFont(fParam)

    fParam.borderWidth =0
    fParam.size = 64
    fParam.color = Color.CYAN.cpy().lerp(Color.WHITE,0.7f)
    fParam.characters += "0123456789"
    val scoreFont = fontGen.generateFont(fParam);


    skin.add("default",font, classOf[BitmapFont])
    skin.add("outline",outlineFont, classOf[BitmapFont])
    skin.add("score",scoreFont,classOf[BitmapFont])

    val butStyle = new TextButtonStyle()
    butStyle.up = new NinePatchDrawable(up)
    butStyle.down = new NinePatchDrawable(down)
    butStyle.font = font
    butStyle.fontColor = Color.WHITE
    skin.add("default",butStyle)
    val lstyle = new LabelStyle()
    lstyle.font = outlineFont

    lstyle.background = null
    skin.add("default",lstyle)
    val scoreStyle = new LabelStyle

    scoreStyle.font = scoreFont
    scoreStyle.background = null
    skin.add("score",scoreStyle,classOf[LabelStyle])

    uiPort = new ExtendViewport(800,800)
    gamePort = new ExtendViewport(14,14)

    multiplexer = new InputMultiplexer()
    Gdx.input.setInputProcessor(multiplexer)
  }
  def resize(width: Int, height: Int): Unit = {
    uiPort.update(width,height,true)
    gamePort.getCamera.position.set(0,0,0)
    gamePort.update(width,height,false)
    batch.setProjectionMatrix(uiPort.getCamera.projection)
    batch.setTransformMatrix(uiPort.getCamera.view)
  }



  def resetScore(): Unit ={
    if (isHighscore) {
      highscore = score
      prefs.putInteger("highscore_classic",highscore)
    }
    score =0
  }
  def saveScore(): Unit ={
    prefs.putInteger("highscore_classic",highscore)
    prefs.putBoolean("sound",soundDisabled)
    prefs.flush()
  }
  var score :Int = 0
  var highscore: Int = 0

  var blocks: Int = 0
  var layers: Int = 0

  var onBlockDestroyed: mutable.MutableList[()=>Unit] = new mutable.MutableList[()=>Unit]()
  var onLayerDestroyed: mutable.MutableList[()=>Unit] = new mutable.MutableList[()=>Unit]()

  def blockDestroyed(): Unit = {
    blocks += 1
    score += 1
    onBlockDestroyed.foreach(_.apply())
    if (!soundDisabled) {
      collisionSound.play()
    }
  }
  def layerDestroyed(): Unit ={
    layers+=1
    Speed += 0.20f *(1/ (layers/5+1))
    RingSpeed += 0.05f *(1/(layers/5+1))
    onLayerDestroyed.foreach(_.apply())
  }

  def isHighscore: Boolean = score > highscore
}
