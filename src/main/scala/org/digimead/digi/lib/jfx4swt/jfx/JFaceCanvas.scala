/**
 * JFX4SWT - JavaFX library adapter for SWT framework.
 *
 * Copyright (c) 2014 Alexey Aksenov ezh@ezh.msk.ru
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.digimead.digi.lib.jfx4swt.jfx

import javafx.application.Platform
import javafx.scene.Scene
import org.digimead.digi.lib.jfx4swt.JFX
import org.digimead.digi.lib.log.api.XLoggable
import scala.ref.WeakReference

/**
 * Embedded JFX Window
 */
abstract class JFaceCanvas(host: WeakReference[FXHost]) extends javafx.stage.Window {
  @volatile protected var disposed = false

  /**
   * Close embedded frame.
   *
   * @param runnable run after close
   */
  def close(onDispose: JFaceCanvas ⇒ _) {
    if (disposed)
      throw new IllegalStateException(s"$this is disposed.")
    if (Platform.isFxApplicationThread())
      closeExec(onDispose)
    else
      JFX.exec { closeExec(onDispose) }
    disposed = true
  }
  /**
   * Open embedded frame.
   *
   * @param scene scene to render
   * @param runnable run after open
   */
  def open(scene: Scene, onReady: JFaceCanvas ⇒ _) {
    if (disposed)
      throw new IllegalStateException(s"$this is disposed.")
    if (Platform.isFxApplicationThread())
      openExec(scene, onReady)
    else
      JFX.exec { openExec(scene, onReady) }
  }

  /** Execute close sequence within Java FX thread. */
  protected def closeExec(onDispose: JFaceCanvas ⇒ _)
  /** Execute open sequence within Java FX thread. */
  protected def openExec(scene: Scene, onReady: JFaceCanvas ⇒ _)
}

object JFaceCanvas extends XLoggable
