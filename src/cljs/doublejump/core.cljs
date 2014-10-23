(ns doublejump.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))


(defn GameState [game]
  (let [max_speed 500
        acceleration 1500
        drag 600
        gravity 2600
        jump_speed -1000]
    (reify
      Object
      (create [this]
        (set! (.. game -stage -backgroundColor) 0x4488cc)

        (set! (.-player this) (.sprite (.. this -game -add) 
                                       (/ (.-width game) 2) 
                                       (/ (.-height game) 2) 
                                       "player"))

        (.enable (.. game -physics) (.-player this) js/Phaser.Physics.ARCADE)

        (set! (.. this -player -body -collideWorldBounds) true)

        (.setTo (.. this -player -body -maxVelocity) max_speed (* max_speed 10))

        (.setTo (.. this -player -body -drag) drag 0)

        (set! (.. game -physics -arcade -gravity -y) gravity)

        (set! (.-canDoubleJump this) true)

        (set! (.-ground this) (.group (.-add game)))
        (loop [x 0]
          (let [groundBlock (.sprite (.-add game) 
                                     x 
                                     (- (.-height game) 32)
                                     "ground")]
            (.enable (.-physics game) groundBlock js/Phaser.Physics.ARCADE)
            (set! (.. groundBlock -body -immovable) true)
            (set! (.. groundBlock -body -allowGravity) false)
            (.add (.-ground this) groundBlock))
          (if (< x (.-width game))
            (recur (+ x 32))))

        (.addKeyCapture (.. game -input -keyboard) 
                        (clj->js 
                         '(js/Phaser.Keyboard.LEFT
                           js/Phaser.Keyboard.RIGHT
                           js/Phaser.Keyboard.UP
                           js/Phaser.Keyboard.DOWN)))

        (.drawHeightMarkers this)

        (set! (.. game -time -advancedTiming) true)
        (set! (.-fpsText this) (.text (.. game -add) 20 20 "Init" 
                                      (clj->js {:font "16px Arial" 
                                                :fill "#ffffff"}))))

      (drawHeightMarkers [this]
        (let [bitmap (.bitmapData (.-add game) (.-width game) (.-height game))
              ctx (.-context bitmap)]
          (loop [y (.-height game)]
            (.beginPath ctx)
            (set! (.-strokeStyle ctx) "rgba(255, 255, 255, 0.2)")
            (.moveTo ctx 0 y)
            (.lineTo ctx (.-width game) y)
            (.stroke ctx)
            (if (> y 64)
              (recur (- y 32))))
          (.image (.-add game) 0 0 bitmap)))
               
      (preload [this]
        (.image (.-load game) "ground" "/assets/gfx/ground.png")
        (.image (.-load game) "player" "/assets/gfx/player.png"))

      (update [this]
        (if (not= 0 (.. game -time -fps))
          (.setText (.-fpsText this) (str (.. game -time -fps) "FPS")))
        (.collide (.. game -physics -arcade) (.-player this) (.-ground this))

        (if (.leftInputIsActive this)
          (set! (.. this -player -body -acceleration -x) (- acceleration))
          (if (.rightInputIsActive this)
            (set! (.. this -player -body -acceleration -x) acceleration)
            (set! (.. this -player -body -acceleration -x) 0)))

        (let [onTheGround (.. this -player -body -touching -down)]
          (if (.upInputIsActive this 5)
            (set! (.. this -player -body -velocity -y) jump_speed)))) 

      (leftInputIsActive [this]
        (let [isActive (.isDown (.. this -input -keyboard) js/Phaser.Keyboard.LEFT)
              inPlace (not (and (.. game -input -activePointer -isDown)
                                (> (.. game -input -activePointer -x)
                                   (/ (.-width game) 4))))]
          
          (= isActive inPlace)))

      (rightInputIsActive [this]
        (let [isActive (.isDown (.. this -input -keyboard) js/Phaser.Keyboard.RIGHT)
              inPlace (not (and (.. game -input -activePointer -isDown)
                                (< (.. game -input -activePointer -x)
                                   (+ (/ (.-width game) 2)
                                      (/ (.-width game) 4)))))]
          
          (= isActive inPlace)))
      
      (upInputIsActive [this duration]
        (let [isActive (.justPressed (.. this -input -keyboard) js/Phaser.Keyboard.UP duration)
              inPlace (not (and 
                            (.justPressed (.. this -input -activePointer) (+ duration (/ 1000 60)))
                            (> (.. game -input -activePointer -x) (/ (.-width game) 4))
                            (< (.. game -input -activePointer -x)
                               (+ (/ (.-width game) 2)
                                  (/ (.-width game) 4)))))]
          (= isActive inPlace))))))

(defonce game (js/Phaser.Game. 848 450 js/Phaser.AUTO "app"))

(defn main []
  (.add (.-state game) "game" GameState true))
