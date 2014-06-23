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

import com.sun.javafx.embed.HostInterface
import javafx.scene.{ Group, Scene }
import org.digimead.digi.lib.log.api.XLoggable
import scala.ref.WeakReference

/**
 * HostInterface implementation that connects scene, stage and adapter together.
 *
 * IMPORTANT JavaFX have HUGE performance loss with embedded content.
 * Those funny code monkeys from Oracle corporation redraw ALL content every time. :-/ Shit? Shit! Shit!!!
 * It is better to create few small contents than one huge.
 *
 * val adapter = new MyFXAdapter
 * val host = new FXHost(adapter)
 * val stage = new FXEmbedded(host)
 * stage.open(scene)
 */
abstract class FXHost(adapter: WeakReference[FXAdapter]) extends HostInterface {
  /** Redraw. */
  def sceneNeedsRepaint()
}

object FXHost extends XLoggable {
  lazy val scene = new Scene(new Group)
}
